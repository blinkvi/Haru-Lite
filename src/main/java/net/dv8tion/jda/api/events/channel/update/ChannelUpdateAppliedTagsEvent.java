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

package net.dv8tion.jda.api.events.channel.update;

import java.util.List;

import javax.annotation.Nonnull;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.ChannelField;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;

/**
 * Indicates that the tags applied to a {@link ThreadChannel forum post thread} have been updated.
 *
 * <p>Limited to {@link ThreadChannel ThreadChannels} inside of {@link ForumChannel ForumChannels}
 *
 * @see ThreadChannel#getAppliedTags()
 * @see ChannelField#APPLIED_TAGS
 */
public class ChannelUpdateAppliedTagsEvent extends GenericChannelUpdateEvent<List<Long>>
{
    public static final ChannelField FIELD = ChannelField.APPLIED_TAGS;
    public static final String IDENTIFIER = FIELD.getFieldName();

    public ChannelUpdateAppliedTagsEvent(@Nonnull JDA api, long responseNumber, @Nonnull ThreadChannel channel, @Nonnull List<Long> oldValue, @Nonnull List<Long> newValue)
    {
        super(api, responseNumber, channel, FIELD, oldValue, newValue);
    }

    @Nonnull
    @Override
    public List<Long> getOldValue()
    {
        return super.getOldValue();
    }

    @Nonnull
    @Override
    public List<Long> getNewValue()
    {
        return super.getNewValue();
    }
}
