package cc.unknown.util.client;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import cc.unknown.util.Accessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.util.Timer;
import net.minecraft.util.Vec3;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class ReflectUtil implements Accessor {
	
	public static void setBlockHitDelay(int val) {
		ObfuscationReflectionHelper.setPrivateValue(PlayerControllerMP.class, mc.playerController, val, "blockHitDelay", "field_78781_i");
	}

	public static void setRightClickDelayTimer(int val) {
		ObfuscationReflectionHelper.setPrivateValue(Minecraft.class, mc, val, "rightClickTimer", "field_71467_ac");
	}
	
	public static void setLeftClickCounter(int val) {
		ObfuscationReflectionHelper.setPrivateValue(Minecraft.class, mc, val, "leftClickCounter", "field_71429_W");
	}

	public static void setJumpTicks(int val) {
		ObfuscationReflectionHelper.setPrivateValue(EntityLivingBase.class, mc.thePlayer, val, "jumpTicks", "field_70773_bE");
	}

	public static void setCurBlockDamage(float val) {
        ObfuscationReflectionHelper.setPrivateValue(PlayerControllerMP.class, mc.playerController, val, "curBlockDamageMP", "field_78770_f");
	}
	
	public static boolean isInWeb() {
		return ObfuscationReflectionHelper.getPrivateValue(Entity.class, mc.thePlayer, "isInWeb", "field_70134_J");
	}

	public static void setInWeb(boolean bool) {
		ObfuscationReflectionHelper.setPrivateValue(Entity.class, mc.thePlayer, bool, "isInWeb", "field_70134_J");
	}

	public static double getRenderPosX() {
		return ObfuscationReflectionHelper.getPrivateValue(RenderManager.class, mc.getRenderManager(), "renderPosX", "field_78725_b");
	}
	
	public static double getRenderPosY() {
		return ObfuscationReflectionHelper.getPrivateValue(RenderManager.class, mc.getRenderManager(), "renderPosY", "field_78726_c");
	}

	public static double getRenderPosZ() {
    	return ObfuscationReflectionHelper.getPrivateValue(RenderManager.class, mc.getRenderManager(), "renderPosZ", "field_78723_d");
	}
	
	public static int getBlockHitDelay() {
    	return ObfuscationReflectionHelper.getPrivateValue(PlayerControllerMP.class, mc.playerController, "blockHitDelay", "field_78781_i");
	}
	
    public static float getCurBlockDamage() {
    	return ObfuscationReflectionHelper.getPrivateValue(PlayerControllerMP.class, mc.playerController, "curBlockDamageMP", "field_78770_f");
    }
	
    public static Timer getTimer() {
    	return ObfuscationReflectionHelper.getPrivateValue(Minecraft.class, mc, "timer", "field_71428_T");
    }

    public static float getLastReportedYaw() {
    	return ObfuscationReflectionHelper.getPrivateValue(EntityPlayerSP.class, mc.thePlayer, "lastReportedYaw", "field_175164_bL");
    }
    
    public static float getLastReportedPitch() {
    	return ObfuscationReflectionHelper.getPrivateValue(EntityPlayerSP.class, mc.thePlayer, "lastReportedPitch", "field_175165_bM");
    }
    
    public static int getMotionX(S12PacketEntityVelocity packet) {
        return ObfuscationReflectionHelper.getPrivateValue(S12PacketEntityVelocity.class, packet, "motionX", "field_149415_b");
    }
    
    public static int getMotionY(S12PacketEntityVelocity packet) {
    	return ObfuscationReflectionHelper.getPrivateValue(S12PacketEntityVelocity.class, packet, "motionY", "field_149416_c");
    }
    
    public static int getMotionZ(S12PacketEntityVelocity packet) {
    	return ObfuscationReflectionHelper.getPrivateValue(S12PacketEntityVelocity.class, packet, "motionZ", "field_149414_d");
    }
    
    public static void setMotionX(S12PacketEntityVelocity packet, int val) {
        ObfuscationReflectionHelper.setPrivateValue(S12PacketEntityVelocity.class, packet, val, "motionX", "field_149415_b");
    }
    
    public static void setMotionY(S12PacketEntityVelocity packet, int val) {
    	ObfuscationReflectionHelper.setPrivateValue(S12PacketEntityVelocity.class, packet, val, "motionY", "field_149416_c");
    }
    
    public static void setMotionZ(S12PacketEntityVelocity packet, int val) {
    	ObfuscationReflectionHelper.setPrivateValue(S12PacketEntityVelocity.class, packet, val, "motionZ", "field_149414_d");
    }
    
    public static void setServerSprintState(boolean bool) {
    	ObfuscationReflectionHelper.setPrivateValue(EntityPlayerSP.class, mc.thePlayer, bool, "serverSprintState", "field_175171_bO");
    }
    
    public static boolean isServerSprintState() {
    	return ObfuscationReflectionHelper.getPrivateValue(EntityPlayerSP.class, mc.thePlayer, "serverSprintState", "field_175171_bO");
    }
    
    public static void setYawC03(C03PacketPlayer packet, float flot) {
    	ObfuscationReflectionHelper.setPrivateValue(C03PacketPlayer.class, packet, flot, "yaw", "field_149476_e");
    }
    
    public static void setPitchC03(C03PacketPlayer packet, float flot) {
    	ObfuscationReflectionHelper.setPrivateValue(C03PacketPlayer.class, packet, flot, "pitch", "field_149473_f");
    }
    
    public static void setRotatingC03(C03PacketPlayer packet, boolean bool) {
    	ObfuscationReflectionHelper.setPrivateValue(C03PacketPlayer.class, packet, bool, "rotating", "field_149481_i");
    }
    
    public static void setItemInUse(int block) {
    	ObfuscationReflectionHelper.setPrivateValue(EntityPlayerSP.class, mc.thePlayer, block, "itemInUseCount", "field_71072_f");
    }
    
    public static Vec3 getVectorForRotation(float pitch, float yaw) {
        return (Vec3) getPrivateMethod(Entity.class, mc.thePlayer, float.class, float.class, pitch, yaw, "getVectorForRotation", "func_174806_f");
    }
    
    public static void clickMouse() {
    	getPrivateMethod(Minecraft.class, mc, "func_147116_af", "clickMouse");
    }
    
    public static void rightClickMouse() {
    	getPrivateMethod(Minecraft.class, mc, "func_147121_ag", "rightClickMouse");
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
            ObfuscationReflectionHelper.setPrivateValue(GameSettings.class, mc.gameSettings, value, fieldName);
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
    /**
     * This method is used to invoke a private method on a specified instance.
     * It searches for a method by its name from a list of possible names and invokes it with parameters.
     * 
     * @param <T> The type of the instance that the method is invoked on.
     * @param classToAccess The class to which the method belongs.
     * @param instance The instance of the class on which the method will be invoked.
     * @param values The list of values, where:
     *               - Any string elements represent method names.
     *               - Class elements represent the parameter types.
     *               - Object elements represent the argument values.
     * 
     * @throws Exception If the method invocation fails or the method doesn't exist.
     * 
     * @author xAmwy
     */
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
                throw new IllegalArgumentException("Invalid method call parameters.");
            }

            int paramCount = stringIndex / 2;
            Class<?>[] paramTypes = new Class<?>[paramCount];
            Object[] args = new Object[paramCount];

            for (int i = 0; i < paramCount; i++) {
                paramTypes[i] = (Class<?>) values[i];
                args[i] = values[i + paramCount];
            }

            String[] methodNames = new String[values.length - stringIndex];
            for (int i = stringIndex; i < values.length; i++) {
                methodNames[i - stringIndex] = (String) values[i];
            }

            Method method = null;
            for (String name : methodNames) {
                try {
                    method = classToAccess.getDeclaredMethod(name, paramTypes);
                    break;
                } catch (NoSuchMethodException ignored) {}
            }

            if (method == null) {
                throw new NoSuchMethodException("No matching method found in class: " + classToAccess.getName());
            }

            method.setAccessible(true);
            return method.invoke(instance, args);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}