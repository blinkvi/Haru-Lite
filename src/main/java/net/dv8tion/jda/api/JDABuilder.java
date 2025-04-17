package net.dv8tion.jda.api;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.neovisionaries.ws.client.WebSocketFactory;

import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.exceptions.InvalidTokenException;
import net.dv8tion.jda.api.hooks.IEventManager;
import net.dv8tion.jda.api.hooks.VoiceDispatchInterceptor;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.RestConfig;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.api.utils.ConcurrentSessionController;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.SessionController;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.managers.PresenceImpl;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.IOUtil;
import net.dv8tion.jda.internal.utils.config.AuthorizationConfig;
import net.dv8tion.jda.internal.utils.config.MetaConfig;
import net.dv8tion.jda.internal.utils.config.SessionConfig;
import net.dv8tion.jda.internal.utils.config.ThreadingConfig;
import net.dv8tion.jda.internal.utils.config.flags.ConfigFlag;
import okhttp3.OkHttpClient;

public class JDABuilder
{
    protected final List<Object> listeners = new LinkedList<>();
    protected final EnumSet<CacheFlag> automaticallyDisabled = EnumSet.noneOf(CacheFlag.class);

    protected ScheduledExecutorService rateLimitScheduler = null;
    protected boolean shutdownRateLimitScheduler = true;
    protected ExecutorService rateLimitElastic = null;
    protected boolean shutdownRateLimitElastic = true;
    protected ScheduledExecutorService mainWsPool = null;
    protected boolean shutdownMainWsPool = true;
    protected ExecutorService callbackPool = null;
    protected boolean shutdownCallbackPool = true;
    protected ExecutorService eventPool = null;
    protected boolean shutdownEventPool = true;
    protected ScheduledExecutorService audioPool = null;
    protected boolean shutdownAudioPool = true;
    protected EnumSet<CacheFlag> cacheFlags = EnumSet.allOf(CacheFlag.class);
    protected ConcurrentMap<String, String> contextMap = null;
    protected SessionController controller = null;
    protected VoiceDispatchInterceptor voiceDispatchInterceptor = null;
    protected OkHttpClient.Builder httpClientBuilder = null;
    protected OkHttpClient httpClient = null;
    protected WebSocketFactory wsFactory = null;
    protected String token = null;
    protected IEventManager eventManager = null;
    protected JDA.ShardInfo shardInfo = null;
    protected Compression compression = Compression.ZLIB;
    protected Activity activity = null;
    protected OnlineStatus status = OnlineStatus.ONLINE;
    protected boolean idle = false;
    protected int maxReconnectDelay = 900;
    protected int largeThreshold = 250;
    protected int maxBufferSize = 2048;
    protected int intents = -1; // don't use intents by default
    protected EnumSet<ConfigFlag> flags = ConfigFlag.getDefault();
    protected ChunkingFilter chunkingFilter = ChunkingFilter.ALL;
    protected MemberCachePolicy memberCachePolicy = MemberCachePolicy.ALL;
    protected GatewayEncoding encoding = GatewayEncoding.JSON;
    protected RestConfig restConfig = new RestConfig();

    private JDABuilder(@Nullable String token, int intents)
    {
        this.token = token;
        this.intents = 1 | intents;
    }

    @Nonnull
    @CheckReturnValue
    public static JDABuilder createDefault(@Nullable String token)
    {
        return new JDABuilder(token, GatewayIntent.DEFAULT).applyDefault();
    }

    @Nonnull
    @CheckReturnValue
    public static JDABuilder createDefault(@Nullable String token, @Nonnull GatewayIntent intent, @Nonnull GatewayIntent... intents)
    {
        Checks.notNull(intent, "GatewayIntent");
        Checks.noneNull(intents, "GatewayIntent");
        return createDefault(token, EnumSet.of(intent, intents));
    }

    @Nonnull
    @CheckReturnValue
    public static JDABuilder createDefault(@Nullable String token, @Nonnull Collection<GatewayIntent> intents)
    {
        return create(token, intents).applyDefault();
    }

    private JDABuilder applyDefault()
    {
        return this.setMemberCachePolicy(MemberCachePolicy.DEFAULT)
                   .setChunkingFilter(ChunkingFilter.NONE)
                   .disableCache(CacheFlag.getPrivileged())
                   .setLargeThreshold(250);
    }

    @Nonnull
    @CheckReturnValue
    public static JDABuilder createLight(@Nullable String token)
    {
        return new JDABuilder(token, GatewayIntent.DEFAULT).applyLight();
    }

    @Nonnull
    @CheckReturnValue
    public static JDABuilder createLight(@Nullable String token, @Nonnull GatewayIntent intent, @Nonnull GatewayIntent... intents)
    {
        Checks.notNull(intent, "GatewayIntent");
        Checks.noneNull(intents, "GatewayIntent");
        return createLight(token, EnumSet.of(intent, intents));
    }

    @Nonnull
    @CheckReturnValue
    public static JDABuilder createLight(@Nullable String token, @Nonnull Collection<GatewayIntent> intents)
    {
        return create(token, intents).applyLight();
    }

    private JDABuilder applyLight()
    {
        return this.setMemberCachePolicy(MemberCachePolicy.NONE)
                   .setChunkingFilter(ChunkingFilter.NONE)
                   .disableCache(EnumSet.allOf(CacheFlag.class))
                   .setLargeThreshold(50);
    }

    @Nonnull
    @CheckReturnValue
    public static JDABuilder create(@Nonnull GatewayIntent intent, @Nonnull GatewayIntent... intents)
    {
        return create(null, intent, intents);
    }


    @Nonnull
    @CheckReturnValue
    public static JDABuilder create(@Nonnull Collection<GatewayIntent> intents)
    {
        return create(null, intents);
    }

    @Nonnull
    @CheckReturnValue
    public static JDABuilder create(@Nullable String token, @Nonnull GatewayIntent intent, @Nonnull GatewayIntent... intents)
    {
        return new JDABuilder(token, GatewayIntent.getRaw(intent, intents)).applyIntents();
    }

    @Nonnull
    @CheckReturnValue
    public static JDABuilder create(@Nullable String token, @Nonnull Collection<GatewayIntent> intents)
    {
        return new JDABuilder(token, GatewayIntent.getRaw(intents)).applyIntents();
    }

    private JDABuilder applyIntents()
    {
        EnumSet<CacheFlag> disabledCache = EnumSet.allOf(CacheFlag.class);
        for (CacheFlag flag : CacheFlag.values())
        {
            GatewayIntent requiredIntent = flag.getRequiredIntent();
            if (requiredIntent == null || (requiredIntent.getRawValue() & intents) != 0)
                disabledCache.remove(flag);
        }

        boolean enableMembers = (intents & GatewayIntent.GUILD_MEMBERS.getRawValue()) != 0;
        return setChunkingFilter(enableMembers ? ChunkingFilter.ALL : ChunkingFilter.NONE)
                .setMemberCachePolicy(enableMembers ? MemberCachePolicy.ALL : MemberCachePolicy.DEFAULT)
                .setDisabledCache(disabledCache);
    }

    private JDABuilder setDisabledCache(EnumSet<CacheFlag> flags)
    {
        disableCache(flags);
        this.automaticallyDisabled.addAll(flags);
        return this;
    }

    @Nonnull
    public JDABuilder setGatewayEncoding(@Nonnull GatewayEncoding encoding)
    {
        Checks.notNull(encoding, "GatewayEncoding");
        this.encoding = encoding;
        return this;
    }

    @Nonnull
    public JDABuilder setRawEventsEnabled(boolean enable)
    {
        return setFlag(ConfigFlag.RAW_EVENTS, enable);
    }

    @Nonnull
    public JDABuilder setEventPassthrough(boolean enable)
    {
        return setFlag(ConfigFlag.EVENT_PASSTHROUGH, enable);
    }

    @Nonnull
    public JDABuilder setRestConfig(@Nonnull RestConfig config)
    {
        Checks.notNull(config, "RestConfig");
        this.restConfig = config;
        return this;
    }

    @Nonnull
    public JDABuilder enableCache(@Nonnull Collection<CacheFlag> flags)
    {
        Checks.noneNull(flags, "CacheFlags");
        cacheFlags.addAll(flags);
        return this;
    }

    @Nonnull
    public JDABuilder enableCache(@Nonnull CacheFlag flag, @Nonnull CacheFlag... flags)
    {
        Checks.notNull(flag, "CacheFlag");
        Checks.noneNull(flags, "CacheFlag");
        cacheFlags.addAll(EnumSet.of(flag, flags));
        return this;
    }

    @Nonnull
    public JDABuilder disableCache(@Nonnull Collection<CacheFlag> flags)
    {
        Checks.noneNull(flags, "CacheFlags");
        automaticallyDisabled.removeAll(flags);
        cacheFlags.removeAll(flags);
        return this;
    }

    /**
     * Disable specific cache flags.
     * <br>This will not enable any currently unset cache flags.
     *
     * @param  flag
     *         {@link CacheFlag} to disable
     * @param  flags
     *         Other flags to disable
     *
     * @throws IllegalArgumentException
     *         If provided with null
     *
     * @return The JDABuilder instance. Useful for chaining.
     *
     * @see    #disableCache(Collection)
     * @see    #enableCache(CacheFlag, CacheFlag...)
     */
    @Nonnull
    public JDABuilder disableCache(@Nonnull CacheFlag flag, @Nonnull CacheFlag... flags)
    {
        Checks.notNull(flag, "CacheFlag");
        Checks.noneNull(flags, "CacheFlag");
        return disableCache(EnumSet.of(flag, flags));
    }

    @Nonnull
    public JDABuilder setMemberCachePolicy(@Nullable MemberCachePolicy policy)
    {
        if (policy == null)
            this.memberCachePolicy = MemberCachePolicy.ALL;
        else
            this.memberCachePolicy = policy;
        return this;
    }

    /**
     * Sets the {@link org.slf4j.MDC MDC} mappings to use in JDA.
     * <br>If sharding is enabled JDA will automatically add a {@code jda.shard} context with the format {@code [SHARD_ID / TOTAL]}
     * where {@code SHARD_ID} and {@code TOTAL} are the shard configuration.
     * Additionally it will provide context for the id via {@code jda.shard.id} and the total via {@code jda.shard.total}.
     *
     * <p>If provided with non-null map this automatically enables MDC context using {@link #setContextEnabled(boolean) setContextEnable(true)}!
     *
     * @param  map
     *         The <b>modifiable</b> context map to use in JDA, or {@code null} to reset
     *
     * @return The JDABuilder instance. Useful for chaining.
     *
     * @see    <a href="https://www.slf4j.org/api/org/slf4j/MDC.html" target="_blank">MDC Javadoc</a>
     * @see    #setContextEnabled(boolean)
     */
    @Nonnull
    public JDABuilder setContextMap(@Nullable ConcurrentMap<String, String> map)
    {
        this.contextMap = map;
        if (map != null)
            setContextEnabled(true);
        return this;
    }

    /**
     * Whether JDA should use a synchronized MDC context for all of its controlled threads.
     * <br>Default: {@code true}
     *
     * @param  enable
     *         True, if JDA should provide an MDC context map
     *
     * @return The JDABuilder instance. Useful for chaining.
     *
     * @see    <a href="https://www.slf4j.org/api/org/slf4j/MDC.html" target="_blank">MDC Javadoc</a>
     * @see    #setContextMap(java.util.concurrent.ConcurrentMap)
     */
    @Nonnull
    public JDABuilder setContextEnabled(boolean enable)
    {
        return setFlag(ConfigFlag.MDC_CONTEXT, enable);
    }

    /**
     * Sets the compression algorithm used with the gateway connection,
     * this will decrease the amount of used bandwidth for the running bot instance
     * for the cost of a few extra cycles for decompression.
     * Compression can be entirely disabled by setting this to {@link net.dv8tion.jda.api.utils.Compression#NONE}.
     * <br><b>Default: {@link net.dv8tion.jda.api.utils.Compression#ZLIB}</b>
     *
     * <p><b>We recommend to keep this on the default unless you have issues with the decompression.</b>
     * <br>This mode might become obligatory in a future version, do not rely on this switch to stay.
     *
     * @param  compression
     *         The compression algorithm to use with the gateway connection
     *
     * @throws java.lang.IllegalArgumentException
     *         If provided with null
     *
     * @return The JDABuilder instance. Useful for chaining
     *
     * @see    <a href="https://discord.com/developers/docs/topics/gateway#transport-compression" target="_blank">Official Discord Documentation - Transport Compression</a>
     */
    @Nonnull
    public JDABuilder setCompression(@Nonnull Compression compression)
    {
        Checks.notNull(compression, "Compression");
        this.compression = compression;
        return this;
    }

    /**
     * Whether the Requester should retry when
     * a {@link java.net.SocketTimeoutException SocketTimeoutException} occurs.
     * <br><b>Default</b>: {@code true}
     *
     * <p>This value can be changed at any time with {@link net.dv8tion.jda.api.JDA#setRequestTimeoutRetry(boolean) JDA.setRequestTimeoutRetry(boolean)}!
     *
     * @param  retryOnTimeout
     *         True, if the Request should retry once on a socket timeout
     *
     * @return The JDABuilder instance. Useful for chaining.
     */
    @Nonnull
    public JDABuilder setRequestTimeoutRetry(boolean retryOnTimeout)
    {
        return setFlag(ConfigFlag.RETRY_TIMEOUT, retryOnTimeout);
    }

    /**
     * Sets the token that will be used by the {@link net.dv8tion.jda.api.JDA} instance to log in when
     * {@link net.dv8tion.jda.api.JDABuilder#build() build()} is called.
     *
     * <p>To get a bot token:<br>
     * <ol>
     *     <li>Go to your <a href="https://discord.com/developers/applications/me">Discord Applications</a></li>
     *     <li>Create or select an already existing application</li>
     *     <li>Verify that it has already been turned into a Bot. If you see the "Create a Bot User" button, click it.</li>
     *     <li>Click the <i>click to reveal</i> link beside the <b>Token</b> label to show your Bot's {@code token}</li>
     * </ol>
     *
     * @param  token
     *         The token of the account that you would like to login with.
     *
     * @return The JDABuilder instance. Useful for chaining.
     */
    @Nonnull
    public JDABuilder setToken(@Nullable String token)
    {
        this.token = token;
        return this;
    }

    /**
     * Sets the {@link okhttp3.OkHttpClient.Builder Builder} that will be used by JDAs requester.
     * <br>This can be used to set things such as connection timeout and proxy.
     *
     * @param  builder
     *         The new {@link okhttp3.OkHttpClient.Builder Builder} to use
     *
     * @return The JDABuilder instance. Useful for chaining.
     */
    @Nonnull
    public JDABuilder setHttpClientBuilder(@Nullable OkHttpClient.Builder builder)
    {
        this.httpClientBuilder = builder;
        return this;
    }

    /**
     * Sets the {@link okhttp3.OkHttpClient OkHttpClient} that will be used by JDAs requester.
     * <br>This can be used to set things such as connection timeout and proxy.
     *
     * @param  client
     *         The new {@link okhttp3.OkHttpClient OkHttpClient} to use
     *
     * @return The JDABuilder instance. Useful for chaining.
     */
    @Nonnull
    public JDABuilder setHttpClient(@Nullable OkHttpClient client)
    {
        this.httpClient = client;
        return this;
    }

    /**
     * Sets the {@link com.neovisionaries.ws.client.WebSocketFactory WebSocketFactory} that will be used by JDA's websocket client.
     * This can be used to set things such as connection timeout and proxy.
     *
     * @param  factory
     *         The new {@link com.neovisionaries.ws.client.WebSocketFactory WebSocketFactory} to use.
     *
     * @return The JDABuilder instance. Useful for chaining.
     */
    @Nonnull
    public JDABuilder setWebsocketFactory(@Nullable WebSocketFactory factory)
    {
        this.wsFactory = factory;
        return this;
    }

    /**
     * Sets the {@link ScheduledExecutorService ScheduledExecutorService} that should be used in
     * the JDA rate-limit handler. Changing this can drastically change the JDA behavior for RestAction execution
     * and should be handled carefully. <b>Only change this pool if you know what you're doing.</b>
     * <br><b>This automatically disables the automatic shutdown of the rate-limit pool, you can enable
     * it using {@link #setRateLimitScheduler(ScheduledExecutorService, boolean) setRateLimitScheduler(executor, true)}</b>
     *
     * <p>This is used mostly by the Rate-Limiter to handle backoff delays by using scheduled executions.
     * Besides that it is also used by planned execution for {@link net.dv8tion.jda.api.requests.RestAction#queueAfter(long, TimeUnit)}
     * and similar methods. Requests are handed off to the {@link #setRateLimitElastic(ExecutorService) elastic pool} for blocking execution.
     *
     * <p>Default: {@link ScheduledThreadPoolExecutor} with 2 threads.
     *
     * @param  pool
     *         The thread-pool to use for rate-limit handling
     *
     * @return The JDABuilder instance. Useful for chaining.
     */
    @Nonnull
    public JDABuilder setRateLimitScheduler(@Nullable ScheduledExecutorService pool)
    {
        return setRateLimitScheduler(pool, pool == null);
    }

    /**
     * Sets the {@link ScheduledExecutorService ScheduledExecutorService} that should be used in
     * the JDA rate-limit handler. Changing this can drastically change the JDA behavior for RestAction execution
     * and should be handled carefully. <b>Only change this pool if you know what you're doing.</b>
     *
     * <p>This is used mostly by the Rate-Limiter to handle backoff delays by using scheduled executions.
     * Besides that it is also used by planned execution for {@link net.dv8tion.jda.api.requests.RestAction#queueAfter(long, TimeUnit)}
     * and similar methods. Requests are handed off to the {@link #setRateLimitElastic(ExecutorService) elastic pool} for blocking execution.
     *
     * <p>Default: {@link ScheduledThreadPoolExecutor} with 2 threads.
     *
     * @param  pool
     *         The thread-pool to use for rate-limit handling
     * @param  automaticShutdown
     *         Whether {@link JDA#shutdown()} should shutdown this pool
     *
     * @return The JDABuilder instance. Useful for chaining.
     */
    @Nonnull
    public JDABuilder setRateLimitScheduler(@Nullable ScheduledExecutorService pool, boolean automaticShutdown)
    {
        this.rateLimitScheduler = pool;
        this.shutdownRateLimitScheduler = automaticShutdown;
        return this;
    }

    /**
     * Sets the {@link ExecutorService ExecutorService} that should be used in
     * the JDA request handler. Changing this can drastically change the JDA behavior for RestAction execution
     * and should be handled carefully. <b>Only change this pool if you know what you're doing.</b>
     * <br><b>This automatically disables the automatic shutdown of the rate-limit elastic pool, you can enable
     * it using {@link #setRateLimitElastic(ExecutorService, boolean) setRateLimitElastic(executor, true)}</b>
     *
     * <p>This is used mostly by the Rate-Limiter to execute the blocking HTTP requests at runtime.
     *
     * <p>Default: {@link Executors#newCachedThreadPool()}.
     *
     * @param  pool
     *         The thread-pool to use for executing http requests
     *
     * @return The JDABuilder instance. Useful for chaining.
     */
    @Nonnull
    public JDABuilder setRateLimitElastic(@Nullable ExecutorService pool)
    {
        return setRateLimitElastic(pool, pool == null);
    }

    /**
     * Sets the {@link ExecutorService ExecutorService} that should be used in
     * the JDA request handler. Changing this can drastically change the JDA behavior for RestAction execution
     * and should be handled carefully. <b>Only change this pool if you know what you're doing.</b>
     *
     * <p>This is used mostly by the Rate-Limiter to execute the blocking HTTP requests at runtime.
     *
     * <p>Default: {@link Executors#newCachedThreadPool()}.
     *
     * @param  pool
     *         The thread-pool to use for executing http requests
     * @param  automaticShutdown
     *         Whether {@link JDA#shutdown()} should shutdown this pool
     *
     * @return The JDABuilder instance. Useful for chaining.
     */
    @Nonnull
    public JDABuilder setRateLimitElastic(@Nullable ExecutorService pool, boolean automaticShutdown)
    {
        this.rateLimitElastic = pool;
        this.shutdownRateLimitElastic = automaticShutdown;
        return this;
    }

    @Nonnull
    public JDABuilder setGatewayPool(@Nullable ScheduledExecutorService pool)
    {
        return setGatewayPool(pool, pool == null);
    }

    /**
     * Sets the {@link ScheduledExecutorService ScheduledExecutorService} used by
     * the main WebSocket connection for workers. These workers spend most of their lifetime
     * sleeping because they only activate for sending messages over the gateway.
     * <br><b>Only change this pool if you know what you're doing.</b>
     *
     * <p>This is used to send various forms of session updates such as:
     * <ul>
     *     <li>Voice States - (Dis-)Connecting from channels</li>
     *     <li>Presence - Changing current activity or online status</li>
     *     <li>Guild Setup - Requesting Members of newly joined guilds</li>
     *     <li>Heartbeats - Regular updates to keep the connection alive (usually once a minute)</li>
     * </ul>
     * When nothing has to be sent the pool will only be used every 500 milliseconds to check the queue for new payloads.
     * Once a new payload is sent we switch to "rapid mode" which means more tasks will be submitted until no more payloads
     * have to be sent.
     *
     * <p>Default: {@link ScheduledThreadPoolExecutor} with 1 thread
     *
     * @param  pool
     *         The thread-pool to use for WebSocket workers
     * @param  automaticShutdown
     *         Whether {@link JDA#shutdown()} should shutdown this pool
     *
     * @return The JDABuilder instance. Useful for chaining.
     */
    @Nonnull
    public JDABuilder setGatewayPool(@Nullable ScheduledExecutorService pool, boolean automaticShutdown)
    {
        this.mainWsPool = pool;
        this.shutdownMainWsPool = automaticShutdown;
        return this;
    }

    /**
     * Sets the {@link ExecutorService ExecutorService} that should be used in
     * the JDA callback handler which mostly consists of {@link net.dv8tion.jda.api.requests.RestAction RestAction} callbacks.
     * By default JDA will use {@link ForkJoinPool#commonPool()}
     * <br><b>Only change this pool if you know what you're doing.
     * <br>This automatically disables the automatic shutdown of the callback pool, you can enable
     * it using {@link #setCallbackPool(ExecutorService, boolean) setCallbackPool(executor, true)}</b>
     *
     * <p>This is used to handle callbacks of {@link RestAction#queue()}, similarly it is used to
     * finish {@link RestAction#submit()} and {@link RestAction#complete()} tasks which build on queue.
     *
     * <p>Default: {@link ForkJoinPool#commonPool()}
     *
     * @param  executor
     *         The thread-pool to use for callback handling
     *
     * @return The JDABuilder instance. Useful for chaining.
     */
    @Nonnull
    public JDABuilder setCallbackPool(@Nullable ExecutorService executor)
    {
        return setCallbackPool(executor, executor == null);
    }

    /**
     * Sets the {@link ExecutorService ExecutorService} that should be used in
     * the JDA callback handler which mostly consists of {@link net.dv8tion.jda.api.requests.RestAction RestAction} callbacks.
     * By default JDA will use {@link ForkJoinPool#commonPool()}
     * <br><b>Only change this pool if you know what you're doing.</b>
     *
     * <p>This is used to handle callbacks of {@link RestAction#queue()}, similarly it is used to
     * finish {@link RestAction#submit()} and {@link RestAction#complete()} tasks which build on queue.
     *
     * <p>Default: {@link ForkJoinPool#commonPool()}
     *
     * @param  executor
     *         The thread-pool to use for callback handling
     * @param  automaticShutdown
     *         Whether {@link JDA#shutdown()} should shutdown this executor
     *
     * @return The JDABuilder instance. Useful for chaining.
     */
    @Nonnull
    public JDABuilder setCallbackPool(@Nullable ExecutorService executor, boolean automaticShutdown)
    {
        this.callbackPool = executor;
        this.shutdownCallbackPool = automaticShutdown;
        return this;
    }

    /**
     * Sets the {@link ExecutorService ExecutorService} that should be used by the
     * event proxy to schedule events. This will be done on the calling thread by default.
     *
     * <p>The executor will not be shutdown automatically when JDA is shutdown.
     * To shut it down automatically use {@link #setEventPool(ExecutorService, boolean)}.
     *
     * @param  executor
     *         The executor for the event proxy, or null to use calling thread
     *
     * @return The JDABuilder instance. Useful for chaining.
     *
     * @since  4.2.0
     */
    @Nonnull
    public JDABuilder setEventPool(@Nullable ExecutorService executor)
    {
        return setEventPool(executor, executor == null);
    }

    /**
     * Sets the {@link ExecutorService ExecutorService} that should be used by the
     * event proxy to schedule events. This will be done on the calling thread by default.
     *
     * @param  executor
     *         The executor for the event proxy, or null to use calling thread
     * @param  automaticShutdown
     *         True, if the executor should be shutdown when JDA shuts down
     *
     * @return The JDABuilder instance. Useful for chaining.
     *
     * @since  4.2.0
     */
    @Nonnull
    public JDABuilder setEventPool(@Nullable ExecutorService executor, boolean automaticShutdown)
    {
        this.eventPool = executor;
        this.shutdownEventPool = automaticShutdown;
        return this;
    }

    /**
     * Sets the {@link ScheduledExecutorService ScheduledExecutorService} used by
     * the audio WebSocket connection. Used for sending keepalives and closing the connection.
     * <br><b>Only change this pool if you know what you're doing.</b>
     *
     * <p>Default: {@link ScheduledThreadPoolExecutor} with 1 thread
     *
     * @param  pool
     *         The thread-pool to use for the audio WebSocket
     *
     * @return The JDABuilder instance. Useful for chaining.
     *
     * @since  4.2.1
     */
    @Nonnull
    public JDABuilder setAudioPool(@Nullable ScheduledExecutorService pool)
    {
        return setAudioPool(pool, pool == null);
    }

    /**
     * Sets the {@link ScheduledExecutorService ScheduledExecutorService} used by
     * the audio WebSocket connection. Used for sending keepalives and closing the connection.
     * <br><b>Only change this pool if you know what you're doing.</b>
     *
     * <p>Default: {@link ScheduledThreadPoolExecutor} with 1 thread
     *
     * @param  pool
     *         The thread-pool to use for the audio WebSocket
     * @param  automaticShutdown
     *         Whether {@link JDA#shutdown()} should shutdown this pool
     *
     * @return The JDABuilder instance. Useful for chaining.
     *
     * @since  4.2.1
     */
    @Nonnull
    public JDABuilder setAudioPool(@Nullable ScheduledExecutorService pool, boolean automaticShutdown)
    {
        this.audioPool = pool;
        this.shutdownAudioPool = automaticShutdown;
        return this;
    }

    /**
     * If enabled, JDA will separate the bulk delete event into individual delete events, but this isn't as efficient as
     * handling a single event would be. It is recommended that BulkDelete Splitting be disabled and that the developer
     * should instead handle the {@link net.dv8tion.jda.api.events.message.MessageBulkDeleteEvent MessageBulkDeleteEvent}
     *
     * <p>Default: <b>true (enabled)</b>
     *
     * @param  enabled
     *         True - The MESSAGE_DELETE_BULK will be split into multiple individual MessageDeleteEvents.
     *
     * @return The JDABuilder instance. Useful for chaining.
     */
    @Nonnull
    public JDABuilder setBulkDeleteSplittingEnabled(boolean enabled)
    {
        return setFlag(ConfigFlag.BULK_DELETE_SPLIT, enabled);
    }

    /**
     * Enables/Disables the use of a Shutdown hook to clean up JDA.
     * <br>When the Java program closes shutdown hooks are run. This is used as a last-second cleanup
     * attempt by JDA to properly close connections.
     *
     * <p>Default: <b>true (enabled)</b>
     *
     * @param  enable
     *         True (default) - use shutdown hook to clean up JDA if the Java program is closed.
     *
     * @return Return the {@link net.dv8tion.jda.api.JDABuilder JDABuilder } instance. Useful for chaining.
     */
    @Nonnull
    public JDABuilder setEnableShutdownHook(boolean enable)
    {
        return setFlag(ConfigFlag.SHUTDOWN_HOOK, enable);
    }

    /**
     * Sets whether or not JDA should try to reconnect if a connection-error is encountered.
     * <br>This will use an incremental reconnect (timeouts are increased each time an attempt fails).
     *
     * <p>Default: <b>true (enabled)</b>
     *
     * @param  autoReconnect
     *         If true - enables autoReconnect
     *
     * @return The JDABuilder instance. Useful for chaining.
     */
    @Nonnull
    public JDABuilder setAutoReconnect(boolean autoReconnect)
    {
        return setFlag(ConfigFlag.AUTO_RECONNECT, autoReconnect);
    }

    /**
     * Changes the internally used EventManager.
     * <br>There are 2 provided Implementations:
     * <ul>
     *     <li>{@link net.dv8tion.jda.api.hooks.InterfacedEventManager InterfacedEventManager} which uses the Interface
     *     {@link net.dv8tion.jda.api.hooks.EventListener EventListener} (tip: use the {@link net.dv8tion.jda.api.hooks.ListenerAdapter ListenerAdapter}).
     *     <br>This is the default EventManager.</li>
     *
     *     <li>{@link net.dv8tion.jda.api.hooks.AnnotatedEventManager AnnotatedEventManager} which uses the Annotation
     *         {@link net.dv8tion.jda.api.hooks.SubscribeEvent @SubscribeEvent} to mark the methods that listen for events.</li>
     * </ul>
     * <br>You can also create your own EventManager (See {@link net.dv8tion.jda.api.hooks.IEventManager}).
     *
     * @param  manager
     *         The new {@link net.dv8tion.jda.api.hooks.IEventManager} to use.
     *
     * @return The JDABuilder instance. Useful for chaining.
     */
    @Nonnull
    public JDABuilder setEventManager(@Nullable IEventManager manager)
    {
        this.eventManager = manager;
        return this;
    }

    /**
     * Sets whether or not we should mark our session as afk
     * <br>This value can be changed at any time in the {@link net.dv8tion.jda.api.managers.Presence Presence} from a JDA instance.
     *
     * @param  idle
     *         boolean value that will be provided with our IDENTIFY package to mark our session as afk or not. <b>(default false)</b>
     *
     * @return The JDABuilder instance. Useful for chaining.
     *
     * @see    net.dv8tion.jda.api.managers.Presence#setIdle(boolean) Presence.setIdle(boolean)
     */
    @Nonnull
    public JDABuilder setIdle(boolean idle)
    {
        this.idle = idle;
        return this;
    }

    /**
     * Sets the {@link net.dv8tion.jda.api.entities.Activity Activity} for our session.
     * <br>This value can be changed at any time in the {@link net.dv8tion.jda.api.managers.Presence Presence} from a JDA instance.
     *
     * <p><b>Hint:</b> You can create an {@link net.dv8tion.jda.api.entities.Activity Activity} object using
     * {@link net.dv8tion.jda.api.entities.Activity#playing(String)} or {@link net.dv8tion.jda.api.entities.Activity#streaming(String, String)}.
     *
     * @param  activity
     *         An instance of {@link net.dv8tion.jda.api.entities.Activity Activity} (null allowed)
     *
     * @return The JDABuilder instance. Useful for chaining.
     *
     * @see    net.dv8tion.jda.api.managers.Presence#setActivity(net.dv8tion.jda.api.entities.Activity)  Presence.setActivity(Activity)
     */
    @Nonnull
    public JDABuilder setActivity(@Nullable Activity activity)
    {
        this.activity = activity;
        return this;
    }

    /**
     * Sets the {@link net.dv8tion.jda.api.OnlineStatus OnlineStatus} our connection will display.
     * <br>This value can be changed at any time in the {@link net.dv8tion.jda.api.managers.Presence Presence} from a JDA instance.
     *
     * @param  status
     *         Not-null OnlineStatus (default online)
     *
     * @throws IllegalArgumentException
     *         if the provided OnlineStatus is null or {@link net.dv8tion.jda.api.OnlineStatus#UNKNOWN UNKNOWN}
     *
     * @return The JDABuilder instance. Useful for chaining.
     *
     * @see    net.dv8tion.jda.api.managers.Presence#setStatus(OnlineStatus) Presence.setStatus(OnlineStatus)
     */
    @Nonnull
    @SuppressWarnings("ConstantConditions") // we have to enforce the nonnull at runtime
    public JDABuilder setStatus(@Nonnull OnlineStatus status)
    {
        if (status == null || status == OnlineStatus.UNKNOWN)
            throw new IllegalArgumentException("OnlineStatus cannot be null or unknown!");
        this.status = status;
        return this;
    }

    /**
     * Adds all provided listeners to the list of listeners that will be used to populate the {@link net.dv8tion.jda.api.JDA JDA} object.
     * <br>This uses the {@link net.dv8tion.jda.api.hooks.InterfacedEventManager InterfacedEventListener} by default.
     * <br>To switch to the {@link net.dv8tion.jda.api.hooks.AnnotatedEventManager AnnotatedEventManager},
     * use {@link #setEventManager(net.dv8tion.jda.api.hooks.IEventManager) setEventManager(new AnnotatedEventManager())}.
     *
     * <p><b>Note:</b> When using the {@link net.dv8tion.jda.api.hooks.InterfacedEventManager InterfacedEventListener} (default),
     * given listener(s) <b>must</b> be instance of {@link net.dv8tion.jda.api.hooks.EventListener EventListener}!
     *
     * @param   listeners
     *          The listener(s) to add to the list.
     *
     * @throws java.lang.IllegalArgumentException
     *         If either listeners or one of it's objects is {@code null}.
     *
     * @return The JDABuilder instance. Useful for chaining.
     *
     * @see    net.dv8tion.jda.api.JDA#addEventListener(Object...) JDA.addEventListener(Object...)
     */
    @Nonnull
    public JDABuilder addEventListeners(@Nonnull Object... listeners)
    {
        Checks.noneNull(listeners, "listeners");

        Collections.addAll(this.listeners, listeners);
        return this;
    }

    /**
     * Removes all provided listeners from the list of listeners.
     *
     * @param  listeners
     *         The listener(s) to remove from the list.
     *
     * @throws java.lang.IllegalArgumentException
     *         If either listeners or one of it's objects is {@code null}.
     *
     * @return The JDABuilder instance. Useful for chaining.
     *
     * @see    net.dv8tion.jda.api.JDA#removeEventListener(Object...) JDA.removeEventListener(Object...)
     */
    @Nonnull
    public JDABuilder removeEventListeners(@Nonnull Object... listeners)
    {
        Checks.noneNull(listeners, "listeners");

        this.listeners.removeAll(Arrays.asList(listeners));
        return this;
    }

    /**
     * Sets the maximum amount of time that JDA will back off to wait when attempting to reconnect the MainWebsocket.
     * <br>Provided value must be 32 or greater.
     *
     * <p>Default: {@code 900}
     *
     * @param  maxReconnectDelay
     *         The maximum amount of time that JDA will wait between reconnect attempts in seconds.
     *
     * @throws java.lang.IllegalArgumentException
     *         Thrown if the provided {@code maxReconnectDelay} is less than 32.
     *
     * @return The JDABuilder instance. Useful for chaining.
     */
    @Nonnull
    public JDABuilder setMaxReconnectDelay(int maxReconnectDelay)
    {
        Checks.check(maxReconnectDelay >= 32, "Max reconnect delay must be 32 seconds or greater. You provided %d.", maxReconnectDelay);

        this.maxReconnectDelay = maxReconnectDelay;
        return this;
    }

    /**
     * This will enable sharding mode for JDA.
     * <br>In sharding mode, guilds are split up and assigned one of multiple shards (clients).
     * <br>The shardId that receives all stuff related to given bot is calculated as follows: shardId == (guildId {@literal >>} 22) % shardTotal;
     * <br><b>PMs are only sent to shard 0.</b>
     *
     * <p>Please note, that a shard will not know about guilds which are not assigned to it.
     *
     * @param  shardId
     *         The id of this shard (starting at 0).
     * @param  shardTotal
     *         The number of overall shards.
     *
     * @throws java.lang.IllegalArgumentException
     *         If the provided shard configuration is invalid
     *         ({@code 0 <= shardId < shardTotal} with {@code shardTotal > 0})
     *
     * @return The JDABuilder instance. Useful for chaining.
     *
     * @see    net.dv8tion.jda.api.JDA#getShardInfo() JDA.getShardInfo()
     * @see    net.dv8tion.jda.api.sharding.ShardManager ShardManager
     */
    @Nonnull
    public JDABuilder useSharding(int shardId, int shardTotal)
    {
        Checks.notNegative(shardId, "Shard ID");
        Checks.positive(shardTotal, "Shard Total");
        Checks.check(shardId < shardTotal,
                "The shard ID must be lower than the shardTotal! Shard IDs are 0-based.");
        shardInfo = new JDA.ShardInfo(shardId, shardTotal);
        return this;
    }

    /**
     * Sets the {@link net.dv8tion.jda.api.utils.SessionController SessionController}
     * for this JDABuilder instance. This can be used to sync behaviour and state between shards
     * of a bot and should be one and the same instance on all builders for the shards.
     * <br>When {@link #useSharding(int, int)} is enabled, this is set by default.
     *
     * <p>When set, this allows the builder to build shards with respect to the login ratelimit automatically.
     *
     * @param  controller
     *         The {@link net.dv8tion.jda.api.utils.SessionController SessionController} to use
     *
     * @return The JDABuilder instance. Useful for chaining.
     *
     * @see    net.dv8tion.jda.api.utils.SessionControllerAdapter SessionControllerAdapter
     */
    @Nonnull
    public JDABuilder setSessionController(@Nullable SessionController controller)
    {
        this.controller = controller;
        return this;
    }

    /**
     * Configures a custom voice dispatch handler which handles audio connections.
     *
     * @param  interceptor
     *         The new voice dispatch handler, or null to use the default
     *
     * @return The JDABuilder instance. Useful for chaining.
     *
     * @since 4.0.0
     *
     * @see    VoiceDispatchInterceptor
     */
    @Nonnull
    public JDABuilder setVoiceDispatchInterceptor(@Nullable VoiceDispatchInterceptor interceptor)
    {
        this.voiceDispatchInterceptor = interceptor;
        return this;
    }

    /**
     * The {@link ChunkingFilter} to filter which guilds should use member chunking.
     *
     * <p>Use {@link #setMemberCachePolicy(MemberCachePolicy)} to configure which members to keep in cache from chunking.
     *
     * @param  filter
     *         The filter to apply
     *
     * @return The JDABuilder instance. Useful for chaining.
     *
     * @since  4.1.0
     *
     * @see    ChunkingFilter#NONE
     * @see    ChunkingFilter#include(long...)
     * @see    ChunkingFilter#exclude(long...)
     */
    @Nonnull
    public JDABuilder setChunkingFilter(@Nullable ChunkingFilter filter)
    {
        this.chunkingFilter = filter == null ? ChunkingFilter.ALL : filter;
        return this;
    }

    /**
     * Configures which events will be disabled.
     * Bots which did not enable presence/member updates in the developer dashboard are required to disable {@link GatewayIntent#GUILD_PRESENCES} and {@link GatewayIntent#GUILD_MEMBERS}!
     *
     * <p>It is not recommended to disable {@link GatewayIntent#GUILD_MEMBERS GatewayIntent.GUILD_MEMBERS} when
     * using {@link MemberCachePolicy#ALL MemberCachePolicy.ALL} as the members cannot be removed from cache by a leave event without this intent.
     *
     * <p>If you disable certain intents you also have to disable related {@link CacheFlag CacheFlags}.
     * This can be achieved using {@link #disableCache(CacheFlag, CacheFlag...)}. The required intents for each
     * flag are documented in the {@link CacheFlag} enum.
     *
     * @param  intent
     *         The first intent to disable
     * @param  intents
     *         Any other intents to disable
     *
     * @throws IllegalArgumentException
     *         If null is provided
     *
     * @return The JDABuilder instance. Useful for chaining.
     *
     * @see    #setMemberCachePolicy(MemberCachePolicy)
     *
     * @since  4.2.0
     */
    @Nonnull
    public JDABuilder setDisabledIntents(@Nonnull GatewayIntent intent, @Nonnull GatewayIntent... intents)
    {
        Checks.notNull(intent, "Intents");
        Checks.noneNull(intents, "Intents");
        return setDisabledIntents(EnumSet.of(intent, intents));
    }

    /**
     * Configures which events will be disabled.
     * Bots which did not enable presence/member updates in the developer dashboard are required to disable {@link GatewayIntent#GUILD_PRESENCES} and {@link GatewayIntent#GUILD_MEMBERS}!
     *
     * <p>It is not recommended to disable {@link GatewayIntent#GUILD_MEMBERS GatewayIntent.GUILD_MEMBERS} when
     * using {@link MemberCachePolicy#ALL MemberCachePolicy.ALL} as the members cannot be removed from cache by a leave event without this intent.
     *
     * <p>If you disable certain intents you also have to disable related {@link CacheFlag CacheFlags}.
     * This can be achieved using {@link #disableCache(CacheFlag, CacheFlag...)}. The required intents for each
     * flag are documented in the {@link CacheFlag} enum.
     *
     * @param  intents
     *         The intents to disable (default: none)
     *
     * @return The JDABuilder instance. Useful for chaining.
     *
     * @see    #setMemberCachePolicy(MemberCachePolicy)
     *
     * @since  4.2.0
     */
    @Nonnull
    public JDABuilder setDisabledIntents(@Nullable Collection<GatewayIntent> intents)
    {
        this.intents = GatewayIntent.ALL_INTENTS;
        if (intents != null)
            this.intents &= ~GatewayIntent.getRaw(intents);
        return this;
    }

    /**
     * Disable the specified {@link GatewayIntent GatewayIntents}.
     * <br>This will not enable any currently unset intents.
     *
     * <p>If you disable certain intents you also have to disable related {@link CacheFlag CacheFlags}.
     * This can be achieved using {@link #disableCache(CacheFlag, CacheFlag...)}. The required intents for each
     * flag are documented in the {@link CacheFlag} enum.
     *
     * @param  intents
     *         The intents to disable
     *
     * @throws IllegalArgumentException
     *         If provided with null
     *
     * @return The JDABuilder instance. Useful for chaining.
     *
     * @see    #enableIntents(Collection)
     */
    @Nonnull
    public JDABuilder disableIntents(@Nonnull Collection<GatewayIntent> intents)
    {
        Checks.noneNull(intents, "GatewayIntent");
        int raw = GatewayIntent.getRaw(intents);
        this.intents &= ~raw;
        return this;
    }

    /**
     * Disable the specified {@link GatewayIntent GatewayIntents}.
     * <br>This will not enable any currently unset intents.
     *
     * <p>If you disable certain intents you also have to disable related {@link CacheFlag CacheFlags}.
     * This can be achieved using {@link #disableCache(CacheFlag, CacheFlag...)}. The required intents for each
     * flag are documented in the {@link CacheFlag} enum.
     *
     * @param  intent
     *         The intent to disable
     * @param  intents
     *         Other intents to disable
     *
     * @throws IllegalArgumentException
     *         If provided with null
     *
     * @return The JDABuilder instance. Useful for chaining.
     *
     * @see    #enableIntents(GatewayIntent, GatewayIntent...)
     */
    @Nonnull
    public JDABuilder disableIntents(@Nonnull GatewayIntent intent, @Nonnull GatewayIntent... intents)
    {
        Checks.notNull(intent, "GatewayIntent");
        Checks.noneNull(intents, "GatewayIntent");
        int raw = GatewayIntent.getRaw(intent, intents);
        this.intents &= ~raw;
        return this;
    }

    /**
     * Configures which events will be enabled.
     * Bots which did not enable presence/member updates in the developer dashboard are required to disable {@link GatewayIntent#GUILD_PRESENCES} and {@link GatewayIntent#GUILD_MEMBERS}!
     *
     * <p>It is not recommended to disable {@link GatewayIntent#GUILD_MEMBERS GatewayIntent.GUILD_MEMBERS} when
     * using {@link MemberCachePolicy#ALL MemberCachePolicy.ALL} as the members cannot be removed from cache by a leave event without this intent.
     *
     * <p>If you disable certain intents you also have to disable related {@link CacheFlag CacheFlags}.
     * This can be achieved using {@link #disableCache(CacheFlag, CacheFlag...)}. The required intents for each
     * flag are documented in the {@link CacheFlag} enum.
     *
     * @param  intent
     *         The intent to enable
     * @param  intents
     *         Any other intents to enable
     *
     * @throws IllegalArgumentException
     *         If null is provided
     *
     * @return The JDABuilder instance. Useful for chaining.
     *
     * @see    #setMemberCachePolicy(MemberCachePolicy)
     *
     * @since  4.2.0
     */
    @Nonnull
    public JDABuilder setEnabledIntents(@Nonnull GatewayIntent intent, @Nonnull GatewayIntent... intents)
    {
        Checks.notNull(intent, "Intents");
        Checks.noneNull(intents, "Intents");
        EnumSet<GatewayIntent> set = EnumSet.of(intent, intents);
        return setDisabledIntents(EnumSet.complementOf(set));
    }

    /**
     * Configures which events will be enabled.
     * Bots which did not enable presence/member updates in the developer dashboard are required to disable {@link GatewayIntent#GUILD_PRESENCES} and {@link GatewayIntent#GUILD_MEMBERS}!
     *
     * <p>It is not recommended to disable {@link GatewayIntent#GUILD_MEMBERS GatewayIntent.GUILD_MEMBERS} when
     * using {@link MemberCachePolicy#ALL MemberCachePolicy.ALL} as the members cannot be removed from cache by a leave event without this intent.
     *
     * <p>If you disable certain intents you also have to disable related {@link CacheFlag CacheFlags}.
     * This can be achieved using {@link #disableCache(CacheFlag, CacheFlag...)}. The required intents for each
     * flag are documented in the {@link CacheFlag} enum.
     *
     * @param  intents
     *         The intents to enable (default: all)
     *
     * @return The JDABuilder instance. Useful for chaining.
     *
     * @see    #setMemberCachePolicy(MemberCachePolicy)
     *
     * @since  4.2.0
     */
    @Nonnull
    public JDABuilder setEnabledIntents(@Nullable Collection<GatewayIntent> intents)
    {
        if (intents == null || intents.isEmpty())
            setDisabledIntents(EnumSet.allOf(GatewayIntent.class));
        else if (intents instanceof EnumSet)
            setDisabledIntents(EnumSet.complementOf((EnumSet<GatewayIntent>) intents));
        else
            setDisabledIntents(EnumSet.complementOf(EnumSet.copyOf(intents)));
        return this;
    }

    /**
     * Enable the specified {@link GatewayIntent GatewayIntents}.
     * <br>This will not disable any currently set intents.
     *
     * @param  intents
     *         The intents to enable
     *
     * @throws IllegalArgumentException
     *         If provided with null
     *
     * @return The JDABuilder instance. Useful for chaining.
     *
     * @see    #disableIntents(Collection)
     */
    @Nonnull
    public JDABuilder enableIntents(@Nonnull Collection<GatewayIntent> intents)
    {
        Checks.noneNull(intents, "GatewayIntent");
        int raw = GatewayIntent.getRaw(intents);
        this.intents |= raw;
        return this;
    }

    /**
     * Enable the specified {@link GatewayIntent GatewayIntents}.
     * <br>This will not disable any currently set intents.
     *
     * @param  intent
     *         The intent to enable
     * @param  intents
     *         Other intents to enable
     *
     * @throws IllegalArgumentException
     *         If provided with null
     *
     * @return The JDABuilder instance. Useful for chaining.
     *
     * @see    #enableIntents(GatewayIntent, GatewayIntent...)
     */
    @Nonnull
    public JDABuilder enableIntents(@Nonnull GatewayIntent intent, @Nonnull GatewayIntent... intents)
    {
        Checks.notNull(intent, "GatewayIntent");
        Checks.noneNull(intents, "GatewayIntent");
        int raw = GatewayIntent.getRaw(intent, intents);
        this.intents |= raw;
        return this;
    }

    /**
     * Decides the total number of members at which a guild should start to use lazy loading.
     * <br>This is limited to a number between 50 and 250 (inclusive).
     * If the {@link #setChunkingFilter(ChunkingFilter) chunking filter} is set to {@link ChunkingFilter#ALL}
     * this should be set to {@code 250} (default) to minimize the amount of guilds that need to request members.
     *
     * @param  threshold
     *         The threshold in {@code [50, 250]}
     *
     * @return The JDABuilder instance. Useful for chaining.
     *
     * @since  4.1.0
     */
    @Nonnull
    public JDABuilder setLargeThreshold(int threshold)
    {
        this.largeThreshold = Math.max(50, Math.min(250, threshold)); // enforce 50 <= t <= 250
        return this;
    }

    /**
     * The maximum size, in bytes, of the buffer used for decompressing discord payloads.
     * <br>If the maximum buffer size is exceeded a new buffer will be allocated instead.
     * <br>Setting this to {@link Integer#MAX_VALUE} would imply the buffer will never be resized unless memory starvation is imminent.
     * <br>Setting this to {@code 0} would imply the buffer would need to be allocated again for every payload (not recommended).
     *
     * <p>Default: {@code 2048}
     *
     * @param  bufferSize
     *         The maximum size the buffer should allow to retain
     *
     * @throws IllegalArgumentException
     *         If the provided buffer size is negative
     *
     * @return The JDABuilder instance. Useful for chaining.
     */
    @Nonnull
    public JDABuilder setMaxBufferSize(int bufferSize)
    {
        Checks.notNegative(bufferSize, "The buffer size");
        this.maxBufferSize = bufferSize;
        return this;
    }

    /**
     * Builds a new {@link net.dv8tion.jda.api.JDA} instance and uses the provided token to start the login process.
     * <br>The login process runs in a different thread, so while this will return immediately, {@link net.dv8tion.jda.api.JDA} has not
     * finished loading, thus many {@link net.dv8tion.jda.api.JDA} methods have the chance to return incorrect information.
     * For example {@link JDA#getGuilds()} might return an empty list or {@link net.dv8tion.jda.api.JDA#getUserById(long)} might return null
     * for arbitrary user IDs.
     *
     * <p>If you wish to be sure that the {@link net.dv8tion.jda.api.JDA} information is correct, please use
     * {@link net.dv8tion.jda.api.JDA#awaitReady() JDA.awaitReady()} or register an
     * {@link net.dv8tion.jda.api.hooks.EventListener EventListener} to listen for the
     * {@link ReadyEvent ReadyEvent}.
     *
     * @throws InvalidTokenException
     *         If the provided token is invalid.
     * @throws IllegalArgumentException
     *         If the provided token is empty or null. Or the provided intents/cache configuration is not possible.
     *
     * @return A {@link net.dv8tion.jda.api.JDA} instance that has started the login process. It is unknown as
     *         to whether or not loading has finished when this returns.
     *
     * @see    net.dv8tion.jda.api.JDA#awaitReady()
     */
    @Nonnull
    public JDA build()
    {
        checkIntents();
        OkHttpClient httpClient = this.httpClient;
        if (httpClient == null)
        {
            if (this.httpClientBuilder == null)
                this.httpClientBuilder = IOUtil.newHttpClientBuilder();
            httpClient = this.httpClientBuilder.build();
        }

        WebSocketFactory wsFactory = this.wsFactory == null ? new WebSocketFactory() : this.wsFactory;

        if (controller == null && shardInfo != null)
            controller = new ConcurrentSessionController();

        AuthorizationConfig authConfig = new AuthorizationConfig(token);
        ThreadingConfig threadingConfig = new ThreadingConfig();
        threadingConfig.setCallbackPool(callbackPool, shutdownCallbackPool);
        threadingConfig.setGatewayPool(mainWsPool, shutdownMainWsPool);
        threadingConfig.setRateLimitScheduler(rateLimitScheduler, shutdownRateLimitScheduler);
        threadingConfig.setRateLimitElastic(rateLimitElastic, shutdownRateLimitElastic);
        threadingConfig.setEventPool(eventPool, shutdownEventPool);
        threadingConfig.setAudioPool(audioPool, shutdownAudioPool);
        SessionConfig sessionConfig = new SessionConfig(controller, httpClient, wsFactory, voiceDispatchInterceptor, flags, maxReconnectDelay, largeThreshold);
        MetaConfig metaConfig = new MetaConfig(maxBufferSize, contextMap, cacheFlags, flags);

        JDAImpl jda = new JDAImpl(authConfig, sessionConfig, threadingConfig, metaConfig, restConfig);
        jda.setMemberCachePolicy(memberCachePolicy);
        // We can only do member chunking with the GUILD_MEMBERS intent
        if ((intents & GatewayIntent.GUILD_MEMBERS.getRawValue()) == 0)
            jda.setChunkingFilter(ChunkingFilter.NONE);
        else
            jda.setChunkingFilter(chunkingFilter);

        if (eventManager != null)
            jda.setEventManager(eventManager);

        jda.addEventListener(listeners.toArray());
        jda.setStatus(JDA.Status.INITIALIZED);  //This is already set by JDA internally, but this is to make sure the listeners catch it.

        // Set the presence information before connecting to have the correct information ready when sending IDENTIFY
        ((PresenceImpl) jda.getPresence())
                .setCacheActivity(activity)
                .setCacheIdle(idle)
                .setCacheStatus(status);
        jda.login(shardInfo, compression, true, intents, encoding);
        return jda;
    }

    private JDABuilder setFlag(ConfigFlag flag, boolean enable)
    {
        if (enable)
            this.flags.add(flag);
        else
            this.flags.remove(flag);
        return this;
    }

    private void checkIntents()
    {
        boolean membersIntent = (intents & GatewayIntent.GUILD_MEMBERS.getRawValue()) != 0;
        if (!membersIntent && memberCachePolicy == MemberCachePolicy.ALL)
            throw new IllegalStateException("Cannot use MemberCachePolicy.ALL without GatewayIntent.GUILD_MEMBERS enabled!");
        else if (!membersIntent && chunkingFilter != ChunkingFilter.NONE)

        if (!automaticallyDisabled.isEmpty())
        {
            // List each missing intent
   

            // Tell user how to disable this warning

            // Only print this warning once
            automaticallyDisabled.clear();
        }

        if (cacheFlags.isEmpty())
            return;

        EnumSet<GatewayIntent> providedIntents = GatewayIntent.getIntents(intents);
        for (CacheFlag flag : cacheFlags)
        {
            GatewayIntent intent = flag.getRequiredIntent();
            if (intent != null && !providedIntents.contains(intent))
                throw new IllegalArgumentException("Cannot use CacheFlag." + flag + " without GatewayIntent." + intent + "!");
        }
    }
}
