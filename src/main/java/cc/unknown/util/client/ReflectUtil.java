package cc.unknown.util.client;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import cc.unknown.util.Accessor;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.shader.Shader;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;
import net.minecraft.util.Timer;
import net.minecraft.util.Vec3;

@SuppressWarnings("unchecked")
public class ReflectUtil implements Accessor {
    public static final boolean hasOptifine = Arrays.stream(GameSettings.class.getFields()).anyMatch(f -> f.getName().equals("ofFastRender"));

    public static void setBlockHitDelay(int val) {
        setPrivateField(PlayerControllerMP.class, mc.playerController, val, "blockHitDelay", "field_78781_i");
    }

    public static void setRightClickDelayTimer(int val) {
        setPrivateField(Minecraft.class, mc, val, "rightClickTimer", "field_71467_ac");
    }

    public static void setLeftClickCounter(int val) {
        setPrivateField(Minecraft.class, mc, val, "leftClickCounter", "field_71429_W");
    }

    public static void setJumpTicks(int val) {
        setPrivateField(EntityLivingBase.class, mc.thePlayer, val, "jumpTicks", "field_70773_bE");
    }

    public static void setCurBlockDamage(float val) {
        setPrivateField(PlayerControllerMP.class, mc.playerController, val, "curBlockDamageMP", "field_78770_f");
    }

    public static boolean isInWeb() {
        return getPrivateField(Entity.class, mc.thePlayer, "isInWeb", "field_70134_J");
    }

    public static void setInWeb(boolean bool) {
        setPrivateField(Entity.class, mc.thePlayer, bool, "isInWeb", "field_70134_J");
    }

    public static int getBlockHitDelay() {
        return getPrivateField(PlayerControllerMP.class, mc.playerController, "blockHitDelay", "field_78781_i");
    }

    public static float getCurBlockDamage() {
        return getPrivateField(PlayerControllerMP.class, mc.playerController, "curBlockDamageMP", "field_78770_f");
    }

    public static float getLastReportedYaw() {
        return getPrivateField(EntityPlayerSP.class, mc.thePlayer, "lastReportedYaw", "field_175164_bL");
    }

    public static float getLastReportedPitch() {
        return getPrivateField(EntityPlayerSP.class, mc.thePlayer, "lastReportedPitch", "field_175165_bM");
    }

    public static int getMotionX(S12PacketEntityVelocity packet) {
        return getPrivateField(S12PacketEntityVelocity.class, packet, "motionX", "field_149415_b");
    }

    public static int getMotionY(S12PacketEntityVelocity packet) {
        return getPrivateField(S12PacketEntityVelocity.class, packet, "motionY", "field_149416_c");
    }

    public static int getMotionZ(S12PacketEntityVelocity packet) {
        return getPrivateField(S12PacketEntityVelocity.class, packet, "motionZ", "field_149414_d");
    }

    public static void setMotionX(S12PacketEntityVelocity packet, int val) {
        setPrivateField(S12PacketEntityVelocity.class, packet, val, "motionX", "field_149415_b");
    }

    public static void setMotionY(S12PacketEntityVelocity packet, int val) {
        setPrivateField(S12PacketEntityVelocity.class, packet, val, "motionY", "field_149416_c");
    }

    public static void setMotionZ(S12PacketEntityVelocity packet, int val) {
        setPrivateField(S12PacketEntityVelocity.class, packet, val, "motionZ", "field_149414_d");
    }

    public static void setServerSprintState(boolean bool) {
        setPrivateField(EntityPlayerSP.class, mc.thePlayer, bool, "serverSprintState", "field_175171_bO");
    }

    public static boolean isServerSprintState() {
        return getPrivateField(EntityPlayerSP.class, mc.thePlayer, "serverSprintState", "field_175171_bO");
    }

    public static IInventory isLowerChestInventory() {
        return getPrivateField(GuiChest.class, ((GuiChest) mc.currentScreen), "lowerChestInventory", "field_147015_w");
    }

    public static void setYawC03(C03PacketPlayer packet, float flot) {
        setPrivateField(C03PacketPlayer.class, packet, flot, "yaw", "field_149476_e");
    }

    public static void setPitchC03(C03PacketPlayer packet, float flot) {
        setPrivateField(C03PacketPlayer.class, packet, flot, "pitch", "field_149473_f");
    }

    public static void setRotatingC03(C03PacketPlayer packet, boolean bool) {
        setPrivateField(C03PacketPlayer.class, packet, bool, "rotating", "field_149481_i");
    }

    public static void setItemInUse(int block) {
        setPrivateField(EntityPlayer.class, mc.thePlayer, block, "itemInUseCount", "field_71072_f");
    }

    public static boolean setItemInUse(boolean blocking) {
        setPrivateField(EntityPlayer.class, mc.thePlayer, blocking ? 1 : 0, "itemInUseCount", "field_71072_f");
        return blocking;
    }

    public static Vec3 getVectorForRotation(float pitch, float yaw) {
        return (Vec3) getPrivateMethod(Entity.class, mc.thePlayer, float.class, float.class, pitch, yaw, "getVectorForRotation", "func_174806_f");
    }

    public static void setSession(Session obj) {
        setPrivateField(Minecraft.class, mc, obj, "session", "field_178752_a");
    }

    public static void clickMouse() {
        getPrivateMethod(Minecraft.class, mc, "func_147116_af", "clickMouse");
    }

    public static void rightClickMouse() {
        getPrivateMethod(Minecraft.class, mc, "func_147121_ag", "rightClickMouse");
    }

    public static void setPressTime(KeyBinding key, int value) {
        setPrivateField(KeyBinding.class, key, value, "pressTime", "field_151474_i");
    }

    public static void loadShader(ResourceLocation shader) {
        getPrivateMethod(EntityRenderer.class, mc.entityRenderer, ResourceLocation.class, shader, "func_175069_a", "loadShader");
    }

    public static Timer getTimer() {
        return getPrivateField(Minecraft.class, mc, "timer", "field_71428_T");
    }

    public static double getRenderPosX() {
        return getPrivateField(RenderManager.class, mc.getRenderManager(), "renderPosX", "field_78725_b");
    }

    public static double getRenderPosY() {
        return getPrivateField(RenderManager.class, mc.getRenderManager(), "renderPosY", "field_78726_c");
    }

    public static double getRenderPosZ() {
        return getPrivateField(RenderManager.class, mc.getRenderManager(), "renderPosZ", "field_78723_d");
    }

    public static ShaderGroup isTheShaderGroup() {
        return getPrivateField(EntityRenderer.class, mc.entityRenderer, "theShaderGroup", "field_147707_d");
    }

    public static void setTheShaderGroup(ShaderGroup shaderGroup) {
        setPrivateField(EntityRenderer.class, mc.entityRenderer, shaderGroup,"theShaderGroup", "field_147707_d");
    }

    public static List<Shader> getListShaders(ShaderGroup shaderGroup) {
        return getPrivateField(ShaderGroup.class, shaderGroup, "listShaders", "field_148031_d");
    }

    public static void flushOutboundQueue(NetworkManager networkManager) {
        getPrivateMethod(NetworkManager.class, networkManager, "flushOutboundQueue", "func_150733_h");
    }

    public static void dispatchPacket(NetworkManager networkManager, Packet<?> packet, GenericFutureListener<?>[] listeners) {
        getPrivateMethod(NetworkManager.class, networkManager,Packet.class, GenericFutureListener[].class, packet, listeners, "dispatchPacket", "func_150732_b");
    }
    
    public static boolean isShaders() {
        try {
            Class<?> configClass = Class.forName("Config");
            return (boolean) configClass.getMethod("isShaders").invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void setGameSetting(Minecraft mc, String fieldName, boolean value) {
        try {
            setPrivateField(GameSettings.class, mc.gameSettings, value, fieldName);
            return;
        } catch (Exception ignored) {
        }

        try {
            Class<?> configClass = Class.forName("Config");
            Field field = configClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.setBoolean(null, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T> Object getPrivateMethod(Class<? super T> classToAccess, T instance, Object... values) {
        try {
            int stringIndex = -1;
            for (int i = 0; i < values.length; i++) {
                if (values[i] instanceof String) {
                    stringIndex = i;
                    break;
                }
            }

            if (stringIndex == -1 || stringIndex % 2 != 0) {
                throw new IllegalArgumentException("Invalid method call parameters. Expected: types, values, names.");
            }

            int paramCount = stringIndex / 2;
            Class<?>[] paramTypes = new Class<?>[paramCount];
            Object[] args = new Object[paramCount];

            for (int i = 0; i < paramCount; i++) {
                paramTypes[i] = (Class<?>) values[i];
                args[i] = values[i + paramCount];
            }

            String[] methodNames = Arrays.copyOfRange(values, stringIndex, values.length, String[].class);

            Method method = Arrays.stream(methodNames).map(name -> {
                try {
                    return classToAccess.getDeclaredMethod(name, paramTypes);
                } catch (NoSuchMethodException ignored) {
                    return null;
                }
            }).filter(m -> m != null).findFirst().orElseThrow(() -> new NoSuchMethodException("No matching method found in class: " + classToAccess.getName()));

            method.setAccessible(true);
            return method.invoke(instance, args);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T getPrivateField(Class<?> clazz, Object instance, String... fieldNames) {
        try {
            Field field = Arrays.stream(fieldNames).map(name -> {
                try {
                    return clazz.getDeclaredField(name);
                } catch (NoSuchFieldException e) {
                    return null;
                }
            }).filter(f -> f != null).findFirst().orElseThrow(() -> new NoSuchFieldException("No matching field found in class: " + clazz.getName()));

            field.setAccessible(true);
            Object value;

            Class<?> type = field.getType();
            if (type.isPrimitive()) {
                if (type == int.class) value = field.getInt(instance);
                else if (type == float.class) value = field.getFloat(instance);
                else if (type == double.class) value = field.getDouble(instance);
                else if (type == boolean.class) value = field.getBoolean(instance);
                else if (type == long.class) value = field.getLong(instance);
                else if (type == short.class) value = field.getShort(instance);
                else if (type == byte.class) value = field.getByte(instance);
                else if (type == char.class) value = field.getChar(instance);
                else throw new UnsupportedOperationException("Unsupported primitive type: " + type);
            } else {
                value = field.get(instance);
            }

            return (T) value;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> void setPrivateField(Class<? super T> classToAccess, T instance, Object value, String... fieldNames) {
        try {
            Field field = Arrays.stream(fieldNames).map(name -> {
                try {
                    return classToAccess.getDeclaredField(name);
                } catch (NoSuchFieldException e) {
                    return null;
                }
            }).filter(f -> f != null).findFirst().orElseThrow(() -> new NoSuchFieldException("No matching field found in class: " + classToAccess.getName()));

            field.setAccessible(true);
            field.set(instance, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}