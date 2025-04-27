package cc.unknown.mixin;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;

public class HaruTweaker implements ITweaker {

    private final List<String> launchArguments = new ArrayList<>();
    public static boolean hasOptifine = false;

    @Override
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile) {
        try {
            Class.forName("optifine.Patcher");
            hasOptifine = true;
            System.out.println("[HaruTweaker] Optifine detected.");
        } catch (ClassNotFoundException ignored) {
            System.out.println("[HaruTweaker] Optifine not detected.");
        }

        if (args != null) {
            launchArguments.addAll(args);
        }
        if (profile != null) {
            launchArguments.add("--version");
            launchArguments.add(profile);
        }
        if (assetsDir != null) {
            launchArguments.add("--assetsDir");
            launchArguments.add(assetsDir.getAbsolutePath());
        }
        if (gameDir != null) {
            launchArguments.add("--gameDir");
            launchArguments.add(gameDir.getAbsolutePath());
        }
    }

    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader) {
    	unlockLwjgl();
    	
        MixinBootstrap.init();
        Mixins.addConfiguration("mixins.haru.json");

        MixinEnvironment env = MixinEnvironment.getDefaultEnvironment();

        if (env.getObfuscationContext() == null) {
            env.setObfuscationContext("notch");
        }

        env.setSide(MixinEnvironment.Side.CLIENT);
    }

    @Override
    public String getLaunchTarget() {
        return "net.minecraft.client.main.Main";
    }

    @Override
    public String[] getLaunchArguments() {
        return launchArguments.toArray(new String[0]);
    }

    private void unlockLwjgl() {
        try {
            Field classLoaderExceptionsField = LaunchClassLoader.class.getDeclaredField("classLoaderExceptions");
            classLoaderExceptionsField.setAccessible(true);
            Object classLoaderExceptionsObj = classLoaderExceptionsField.get(Launch.classLoader);

            if (classLoaderExceptionsObj instanceof Set) {
                @SuppressWarnings("unchecked")
                Set<String> classLoaderExceptions = (Set<String>) classLoaderExceptionsObj;
                classLoaderExceptions.remove("org.lwjgl.");
            }

            Field transformerExceptionsField = LaunchClassLoader.class.getDeclaredField("transformerExceptions");
            transformerExceptionsField.setAccessible(true);
            Object transformerExceptionsObj = transformerExceptionsField.get(Launch.classLoader);

            if (transformerExceptionsObj instanceof Set) {
                @SuppressWarnings("unchecked")
                Set<String> transformerExceptions = (Set<String>) transformerExceptionsObj;
                transformerExceptions.remove("org.lwjgl.");
            }

            System.out.println("[HaruTweaker] Successfully unlocked LWJGL.");
        } catch (Exception e) {
            System.err.println("[HaruTweaker] Failed to unlock LWJGL.");
            e.printStackTrace();
        }
    }
}