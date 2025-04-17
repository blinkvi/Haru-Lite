/*
 * Copyright 2015 Austin Keener, Michael Ritter, Florian Spieß, and the JDA contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.dv8tion.jda.internal.requests;

import java.util.Queue;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;


import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;

//Helper class delegated to WebSocketClient
class WebSocketSendingThread implements Runnable
{

    private final WebSocketClient client;
    private final JDAImpl api;
    private final ReentrantLock queueLock;
    private final Queue<DataObject> chunkQueue;
    private final Queue<DataObject> ratelimitQueue;
    private final ScheduledExecutorService executor;
    private Future<?> handle;

    private boolean needRateLimit = false;
    private boolean attemptedToSend = false;
    private boolean shutdown = false;

    WebSocketSendingThread(WebSocketClient client)
    {
        this.client = client;
        this.api = client.api;
        this.queueLock = client.queueLock;
        this.chunkQueue = client.chunkSyncQueue;
        this.ratelimitQueue = client.ratelimitQueue;
        this.executor = client.executor;
    }

    public void shutdown()
    {
        shutdown = true;
        if (handle != null)
            handle.cancel(false);
    }

    public void start()
    {
        shutdown = false;
        handle = executor.submit(this);
    }

    private void scheduleIdle()
    {
        if (shutdown)
            return;
        handle = executor.schedule(this, 500, TimeUnit.MILLISECONDS);
    }

    private void scheduleSentMessage()
    {
        if (shutdown)
            return;
        handle = executor.schedule(this, 10, TimeUnit.MILLISECONDS);
    }

    private void scheduleRateLimit()
    {
        if (shutdown)
            return;
        handle = executor.schedule(this, 1, TimeUnit.MINUTES);
    }

    @Override
    public void run()
    {
        //Make sure that we don't send any packets before sending auth info.
        if (!client.sentAuthInfo)
        {
            scheduleIdle();
            return;
        }

        DataObject chunkRequest = null;

        boolean hasLock = false;

        try
        {
            api.setContext();
            attemptedToSend = false;
            needRateLimit = false;
            // We do this outside of the lock because otherwise we could potentially deadlock here

            hasLock = queueLock.tryLock() || queueLock.tryLock(10, TimeUnit.SECONDS);
            if (!hasLock)
            {
                scheduleNext();
                return;
            }

            chunkRequest = chunkQueue.peek();
            if (chunkRequest != null)
                handleChunkSync(chunkRequest);
            else
                handleNormalRequest();
        }
        catch (InterruptedException ignored)
        {
            return;
        }
        catch (Throwable ex)
        {
            // Log error

            if (!attemptedToSend)
            {
                // Try to remove the failed request
                if (chunkRequest != null)
                    client.chunkSyncQueue.remove(chunkRequest);
            }

            // Rethrow if error to kill thread
            if (ex instanceof Error)
                throw (Error) ex;
        }
        finally
        {
            if (hasLock)
                queueLock.unlock();
        }

        scheduleNext();
    }

    private void scheduleNext()
    {
        try
        {
            if (needRateLimit)
                scheduleRateLimit();
            else if (!attemptedToSend)
                scheduleIdle();
            else
                scheduleSentMessage();
        }
        catch (RejectedExecutionException ex)
        {

        }
    }

    private void handleChunkSync(DataObject chunkOrSyncRequest)
    {
        boolean success = send(
            DataObject.empty()
                .put("op", WebSocketCode.MEMBER_CHUNK_REQUEST)
                .put("d", chunkOrSyncRequest)
        );

        if (success)
            chunkQueue.remove();
    }

    private void handleNormalRequest()
    {
        DataObject message = ratelimitQueue.peek();
        if (message != null)
        {
            if (send(message))
                ratelimitQueue.remove();
        }
    }

    //returns true if send was successful
    private boolean send(DataObject request)
    {
        needRateLimit = !client.send(request, false);
        attemptedToSend = true;
        return !needRateLimit;
    }

    protected DataObject newVoiceClose(long guildId)
    {
        return DataObject.empty()
            .put("op", WebSocketCode.VOICE_STATE)
            .put("d", DataObject.empty()
                .put("guild_id", Long.toUnsignedString(guildId))
                .putNull("channel_id")
                .put("self_mute", false)
                .put("self_deaf", false));
    }

    protected DataObject newVoiceOpen(AudioManager manager, long channel, long guild)
    {
        return DataObject.empty()
            .put("op", WebSocketCode.VOICE_STATE)
            .put("d", DataObject.empty()
                .put("guild_id", guild)
                .put("channel_id", channel)
                .put("self_mute", manager.isSelfMuted())
                .put("self_deaf", manager.isSelfDeafened()));
    }
}
