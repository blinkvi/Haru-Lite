package cc.unknown;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.lwjgl.opengl.Display;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import cc.unknown.event.Event;
import cc.unknown.event.bus.EventBus;
import cc.unknown.handlers.AutoJoinHandler;
import cc.unknown.handlers.CPSHandler;
import cc.unknown.handlers.CommandHandler;
import cc.unknown.handlers.CosmeticHandler;
import cc.unknown.handlers.DiscordHandler;
import cc.unknown.handlers.DragHandler;
import cc.unknown.handlers.GuiMoveHandler;
import cc.unknown.handlers.IRCHandler;
import cc.unknown.handlers.KeyHandler;
import cc.unknown.handlers.SettingsHandler;
import cc.unknown.handlers.ShaderHandler;
import cc.unknown.handlers.SpoofHandler;
import cc.unknown.handlers.TransactionHandler;
import cc.unknown.managers.CommandManager;
import cc.unknown.managers.ConfigManager;
import cc.unknown.managers.CosmeticManager;
import cc.unknown.managers.DragManager;
import cc.unknown.managers.ModuleManager;
import cc.unknown.managers.PositionManager;
import cc.unknown.socket.WebSocketCore;
import cc.unknown.ui.click.DropGui;
import cc.unknown.util.client.ReflectUtil;
import cc.unknown.util.client.system.CustomLogger;
import cc.unknown.util.client.system.SystemUtil;
import cc.unknown.util.render.font.FontUtil;
import net.minecraft.client.Minecraft;

public class Haru {
    public static Haru instance = new Haru();

    public static final String NAME = "Haru";
    public static final String VERSION = "Reborn";

    public static ModuleManager modMngr;
    public static CosmeticManager cosmeMngr;
    public static CommandManager comMngr;
    public static ConfigManager cfgMngr;
    public static PositionManager posMngr;
    public static DragManager dragMngr;
    
    public static DropGui dropGui;
    public static WebSocketCore webSocket;
    
    public static final CustomLogger logger = new CustomLogger();
    public static final EventBus<Event> eventBus = new EventBus<>();
    public static final DiscordHandler discordHandler = new DiscordHandler();
    private final List<Object> registeredHandlers = Collections.synchronizedList(new ArrayList<>());
    public final ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(4);
    public final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static final File MAIN_DIR = new File(mc.mcDataDir, Haru.NAME);
    public static final File DLL_DIR = new File(MAIN_DIR, "dlls");
    public static final File CFG_DIR = new File(MAIN_DIR, "configs");
    public static final File DRAG_DIR = new File(MAIN_DIR, "draggable");
    public static final File CS_DIR = new File(MAIN_DIR, "cosmetics");
    
    public static boolean firstStart;
    
    public void init() {
    	Display.setTitle(NAME + " " + VERSION);
        createDirectories();
        
        FontUtil.initializeFonts();
        optimizeMinecraft();
        initializeManagers();
        registerHandlers();
        firstStart = SystemUtil.checkFirstStart();
        startDiscordPresence();
  
        logger.info("Initialized successfully.");
    }
    
    public void stop() {
        cfgMngr.saveFiles();
        cosmeMngr.saveFiles();
        posMngr.saveFiles();
        discordHandler.stop();
        logger.info("Rich Presence Terminated.");
        System.gc();
        logger.info("Client Terminated.");
    }

    private void register(Object... handlers) {
        Arrays.stream(handlers).forEach(handler -> {
            try {
                registeredHandlers.add(handler);
                eventBus.register(handler);
                logger.info(handler.getClass().getSimpleName() + " registered.");
            } catch (Exception e) {
                logger.error("Failed to register handler: " + handler.getClass().getSimpleName(), e);
            }
        });
    }

    private void registerHandlers() {
        logger.info("Initializing handlers...");
        register(
            new SpoofHandler(),
            new AutoJoinHandler(),
            new TransactionHandler(),
            new ShaderHandler(),
            new DragHandler(),
            new IRCHandler(),
            new SettingsHandler(),
            new CommandHandler(),
            new KeyHandler(),
            new CosmeticHandler(),
            new GuiMoveHandler(),
            new CPSHandler()
        );

        logger.info("Handlers registered.");
    }

    private void initializeManagers() {
        logger.info("Initializing managers...");

        modMngr = new ModuleManager();
        comMngr = new CommandManager();
        cfgMngr = new ConfigManager();
        posMngr = new PositionManager();
        dragMngr = new DragManager();
        cosmeMngr = new CosmeticManager();
        dropGui = new DropGui();

        cosmeMngr.init();
        posMngr.init();
        cfgMngr.init();
        comMngr.init();

        logger.info("Managers registered.");
    }

    private void optimizeMinecraft() {
        if (SystemUtil.isOptifineLoaded()) {
            try {
                Map<String, Boolean> settings = Stream.of(new Object[][]{
                    {"ofFastRender", !ReflectUtil.isShaders()},
                    {"ofChunkUpdatesDynamic", true},
                    {"ofSmartAnimations", true},
                    {"ofShowGlErrors", false},
                    {"ofRenderRegions", true},
                    {"ofSmoothFps", false},
                    {"ofFastMath", true}
                }).collect(Collectors.toMap(data -> (String) data[0], data -> (Boolean) data[1]));

                settings.forEach((key, value) -> ReflectUtil.setGameSetting(mc, key, value));
            } catch (Exception ignored) {}
        }
        mc.gameSettings.useVbo = true;
        logger.info("Minecraft optimization initialized.");
    }

    private void startDiscordPresence() {
        discordHandler.start();
        logger.info("Rich Presence initialized.");
    }
    
    public void createDirectories() {
    	Stream.of(MAIN_DIR, DLL_DIR, CFG_DIR, DRAG_DIR, CS_DIR)
        .filter(Objects::nonNull)
        .map(File::toPath)
        .forEach(dir -> {
            try {
                Files.createDirectories(dir);
            } catch (IOException e) {
                logger.error("Failed to create directory " + dir + ": " + e.getMessage(), e);
            }
        });

        logger.info("All directories were created successfully (or already existed).");
    }
	
	public static String getUser() {
		return mc.getSession().getUsername();
	}
}
