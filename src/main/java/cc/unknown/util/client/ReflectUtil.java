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
    
    public static int getMotionX() {
        return ObfuscationReflectionHelper.getPrivateValue(S12PacketEntityVelocity.class, new S12PacketEntityVelocity(), "motionX", "field_149415_b");
    }
    
    public static int getMotionY() {
    	return ObfuscationReflectionHelper.getPrivateValue(S12PacketEntityVelocity.class, new S12PacketEntityVelocity(), "motionY", "field_149416_c");
    }
    
    public static int getMotionZ() {
    	return ObfuscationReflectionHelper.getPrivateValue(S12PacketEntityVelocity.class, new S12PacketEntityVelocity(), "motionZ", "field_149414_d");
    }
    
    public static void setMotionX(int val) {
        ObfuscationReflectionHelper.setPrivateValue(S12PacketEntityVelocity.class, new S12PacketEntityVelocity(), val, "motionX", "field_149415_b");
    }
    
    public static void setMotionY(int val) {
    	ObfuscationReflectionHelper.setPrivateValue(S12PacketEntityVelocity.class, new S12PacketEntityVelocity(), val, "motionY", "field_149416_c");
    }
    
    public static void setMotionZ(int val) {
    	ObfuscationReflectionHelper.setPrivateValue(S12PacketEntityVelocity.class, new S12PacketEntityVelocity(), val, "motionZ", "field_149414_d");
    }
    
    public static void setServerSprintState(boolean bool) {
    	ObfuscationReflectionHelper.setPrivateValue(EntityPlayerSP.class, mc.thePlayer, bool, "serverSprintState", "field_175171_bO");
    }
    
    public static boolean isServerSprintState() {
    	return ObfuscationReflectionHelper.getPrivateValue(EntityPlayerSP.class, mc.thePlayer, "serverSprintState", "field_175171_bO");
    }
    
    public static void setYawC03(float flot) {
    	ObfuscationReflectionHelper.setPrivateValue(C03PacketPlayer.class, new C03PacketPlayer(), flot, "yaw", "field_149476_e");
    }
    
    public static void setPitchC03(float flot) {
    	ObfuscationReflectionHelper.setPrivateValue(C03PacketPlayer.class, new C03PacketPlayer(), flot, "pitch", "field_149473_f");
    }
    
    public static void setRotatingC03(boolean bool) {
    	ObfuscationReflectionHelper.setPrivateValue(C03PacketPlayer.class, new C03PacketPlayer(), bool, "rotating", "field_149481_i");
    }
    
    // block = 1
    // unblock = 0
    
    public static void setItemInUse(int block) {
    	ObfuscationReflectionHelper.setPrivateValue(EntityPlayerSP.class, mc.thePlayer, block, "itemInUseCount", "field_71072_f");
    }
    
    public static Vec3 getVectorForRotation(float pitch, float yaw) {
        try {
            Method method;
            try {
                method = Entity.class.getDeclaredMethod("func_174806_f", float.class, float.class);
            } catch (NoSuchMethodException e) {
                method = Entity.class.getDeclaredMethod("getVectorForRotation", float.class, float.class);
            }

            method.setAccessible(true);
            return (Vec3) method.invoke(mc.thePlayer, pitch, yaw);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static void rightClickMouse() {
    	try {
    		Method method;
    		try {
    			method = Minecraft.class.getDeclaredMethod("func_147121_ag");
    		} catch (NoSuchMethodException e) {
    			method = Minecraft.class.getDeclaredMethod("rightClickMouse");
    		}
    		
    		method.setAccessible(true);
    		method.invoke(mc);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    
    public static boolean isShaders() {
        try {
            Class<?> configClass = Class.forName("Config");
            return (boolean) configClass.getMethod("isShaders").invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void setGameSetting(Minecraft mc, String fieldName, boolean value) {
        try {
            try {
                ObfuscationReflectionHelper.setPrivateValue(GameSettings.class, mc.gameSettings, value, fieldName);
                return;
            } catch (Exception ignored) {}

            try {
                Class<?> configClass = Class.forName("Config");
                Field field = configClass.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.setBoolean(null, value);
            } catch (Exception ignored) {}

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}