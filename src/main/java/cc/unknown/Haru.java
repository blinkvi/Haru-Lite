package cc.unknown;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.lwjgl.opengl.Display;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import cc.unknown.handlers.AutoJoinHandler;
import cc.unknown.handlers.CPSHandler;
import cc.unknown.handlers.CommandHandler;
import cc.unknown.handlers.DiscordHandler;
import cc.unknown.handlers.DragHandler;
import cc.unknown.handlers.FixHandler;
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
import cc.unknown.managers.RotationManager;
import cc.unknown.module.Module;
import cc.unknown.socket.WebSocketCore;
import cc.unknown.ui.click.DropGui;
import cc.unknown.util.client.ReflectUtil;
import cc.unknown.util.client.system.CustomLogger;
import cc.unknown.util.client.system.SystemUtil;
import cc.unknown.util.render.font.FontUtil;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = "betterclouds", name = "better clouds", version = "Lite")
public class Haru {
    public static Haru instance = new Haru();

    public static final String NAME = "Haru";
    public static final String VERSION = "Lite";

    private ModuleManager moduleManager;
    private CosmeticManager cosmeticManager;
    private CommandManager cmdManager;
    private ConfigManager cfgManager;
    private PositionManager positionManager;
    private DragManager dragManager;
    private RotationManager rotationManager;
    
    private DropGui dropGui;
    private Module module;
    private WebSocketCore webSocket;
    
    private final CustomLogger logger = new CustomLogger();
    private final DiscordHandler discordHandler = new DiscordHandler();
    private final List<Object> registeredHandlers = Collections.synchronizedList(new ArrayList<>());
    public final ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(4);
    private final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    
    public static final File MAIN_DIR = new File(Minecraft.getMinecraft().mcDataDir, Haru.NAME);
    public static final File DLL_DIR = new File(MAIN_DIR, "dlls");
    public static final File CFG_DIR = new File(MAIN_DIR, "configs");
    public static final File DRAG_DIR = new File(MAIN_DIR, "draggable");
    public static final File CS_DIR = new File(MAIN_DIR, "cosmetics");
    
    private final Minecraft mc = Minecraft.getMinecraft();

    public boolean firstStart;

    @EventHandler
    public void startMod(FMLInitializationEvent ignored) { }
    
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
        cfgManager.saveFiles();
        cosmeticManager.saveFiles();
        positionManager.saveFiles();
        discordHandler.stop();
        logger.info("Rich Presence Terminated.");
        System.gc();
        logger.info("Client Terminated.");
    }

    private void register(Object... handlers) {
        for (Object handler : handlers) {
            try {
                registeredHandlers.add(handler);
                MinecraftForge.EVENT_BUS.register(handler);
                logger.info(handler.getClass().getSimpleName() + " registered.");
            } catch (Exception e) {
                logger.error("Failed to register handler: " + handler.getClass().getSimpleName(), e);
            }
        }
    }

    private void registerHandlers() {
        logger.info("Initializing handlers...");
        register(
            new SpoofHandler(),
            new AutoJoinHandler(),
            new TransactionHandler(),
            new FixHandler(),
            new ShaderHandler(),
            new DragHandler(),
            new IRCHandler(),
            new SettingsHandler(),
            new CommandHandler(),
            new KeyHandler(),
            new GuiMoveHandler(),
            new CPSHandler()
        );

        logger.info("Handlers registered.");
    }

    private void initializeManagers() {
        logger.info("Initializing managers...");

        moduleManager = new ModuleManager();
        cmdManager = new CommandManager();
        cfgManager = new ConfigManager();
        rotationManager = new RotationManager();
        positionManager = new PositionManager();
        dragManager = new DragManager();
        cosmeticManager = new CosmeticManager();
        dropGui = new DropGui();

        cosmeticManager.init();
        positionManager.init();
        cfgManager.init();
        cmdManager.init();

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
        try {
            Files.createDirectories(MAIN_DIR.toPath());
            Files.createDirectories(DLL_DIR.toPath());
            Files.createDirectories(CFG_DIR.toPath());
            Files.createDirectories(DRAG_DIR.toPath());
            Files.createDirectories(CS_DIR.toPath());
            logger.info("Dirs created successfully.");
        } catch (IOException e) {
            System.err.println("Error al crear directorios: " + e.getMessage());
        }
    }

    public WebSocketCore getWebSocket() { return webSocket; }
	public ModuleManager getModuleManager() { return moduleManager; }
	public CosmeticManager getCosmeticManager() { return cosmeticManager; }
	public RotationManager getRotationManager() { return rotationManager; }
    public CommandManager getCmdManager() { return cmdManager; }
    public ConfigManager getCfgManager() { return cfgManager; }
    public CustomLogger getLogger() { return logger; }
    public Gson getGSON() { return GSON; }
    public DragManager getDragManager() { return dragManager; }
    public DropGui getDropGui() { return dropGui; }
}
