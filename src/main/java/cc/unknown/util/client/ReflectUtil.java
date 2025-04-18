package cc.unknown.util.client;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import cc.unknown.util.Accessor;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
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

@UtilityClass
public class ReflectUtil implements Accessor {
	
	public void setBlockHitDelay(int val) {
		ObfuscationReflectionHelper.setPrivateValue(PlayerControllerMP.class, mc.playerController, val, "blockHitDelay", "field_78781_i");
	}

	public void setRightClickDelayTimer(int val) {
		ObfuscationReflectionHelper.setPrivateValue(Minecraft.class, mc, val, "rightClickTimer", "field_71467_ac");
	}
	
	public void setLeftClickCounter(int val) {
		ObfuscationReflectionHelper.setPrivateValue(Minecraft.class, mc, val, "leftClickCounter", "field_71429_W");
	}

	public void setJumpTicks(int val) {
		ObfuscationReflectionHelper.setPrivateValue(EntityLivingBase.class, mc.thePlayer, val, "jumpTicks", "field_70773_bE");
	}

	public void setCurBlockDamage(float val) {
        ObfuscationReflectionHelper.setPrivateValue(PlayerControllerMP.class, mc.playerController, val, "curBlockDamageMP", "field_78770_f");
	}
	
	public boolean isInWeb() {
		return ObfuscationReflectionHelper.getPrivateValue(Entity.class, mc.thePlayer, "isInWeb", "field_70134_J");
	}

	public void setInWeb(boolean bool) {
		ObfuscationReflectionHelper.setPrivateValue(Entity.class, mc.thePlayer, bool, "isInWeb", "field_70134_J");
	}

	public double getRenderPosX() {
		return ObfuscationReflectionHelper.getPrivateValue(RenderManager.class, mc.getRenderManager(), "renderPosX", "field_78725_b");
	}
	
	public double getRenderPosY() {
		return ObfuscationReflectionHelper.getPrivateValue(RenderManager.class, mc.getRenderManager(), "renderPosY", "field_78726_c");
	}

	public double getRenderPosZ() {
    	return ObfuscationReflectionHelper.getPrivateValue(RenderManager.class, mc.getRenderManager(), "renderPosZ", "field_78723_d");
	}
	
	public int getBlockHitDelay() {
    	return ObfuscationReflectionHelper.getPrivateValue(PlayerControllerMP.class, mc.playerController, "blockHitDelay", "field_78781_i");
	}
	
    public float getCurBlockDamage() {
    	return ObfuscationReflectionHelper.getPrivateValue(PlayerControllerMP.class, mc.playerController, "curBlockDamageMP", "field_78770_f");
    }
	
    public Timer getTimer() {
    	return ObfuscationReflectionHelper.getPrivateValue(Minecraft.class, mc, "timer", "field_71428_T");
    }
    
    public float getLastReportedYaw() {
    	return ObfuscationReflectionHelper.getPrivateValue(EntityPlayerSP.class, mc.thePlayer, "lastReportedYaw", "field_175164_bL");
    }
    
    public float getLastReportedPitch() {
    	return ObfuscationReflectionHelper.getPrivateValue(EntityPlayerSP.class, mc.thePlayer, "lastReportedPitch", "field_175165_bM");
    }
    
    public int getMotionX() {
        return ObfuscationReflectionHelper.getPrivateValue(S12PacketEntityVelocity.class, new S12PacketEntityVelocity(), "motionX", "field_149415_b");
    }
    
    public int getMotionY() {
    	return ObfuscationReflectionHelper.getPrivateValue(S12PacketEntityVelocity.class, new S12PacketEntityVelocity(), "motionY", "field_149416_c");
    }
    
    public int getMotionZ() {
    	return ObfuscationReflectionHelper.getPrivateValue(S12PacketEntityVelocity.class, new S12PacketEntityVelocity(), "motionZ", "field_149414_d");
    }
    
    public void setMotionX(int val) {
        ObfuscationReflectionHelper.setPrivateValue(S12PacketEntityVelocity.class, new S12PacketEntityVelocity(), val, "motionX", "field_149415_b");
    }
    
    public void setMotionY(int val) {
    	ObfuscationReflectionHelper.setPrivateValue(S12PacketEntityVelocity.class, new S12PacketEntityVelocity(), val, "motionY", "field_149416_c");
    }
    
    public void setMotionZ(int val) {
    	ObfuscationReflectionHelper.setPrivateValue(S12PacketEntityVelocity.class, new S12PacketEntityVelocity(), val, "motionZ", "field_149414_d");
    }
    
    public void setServerSprintState(boolean bool) {
    	ObfuscationReflectionHelper.setPrivateValue(EntityPlayerSP.class, mc.thePlayer, bool, "serverSprintState", "field_175171_bO");
    }
    
    public boolean isServerSprintState() {
    	return ObfuscationReflectionHelper.getPrivateValue(EntityPlayerSP.class, mc.thePlayer, "serverSprintState", "field_175171_bO");
    }
    
    public void setYawC03(float flot) {
    	ObfuscationReflectionHelper.setPrivateValue(C03PacketPlayer.class, new C03PacketPlayer(), flot, "yaw", "field_149476_e");
    }
    
    public void setPitchC03(float flot) {
    	ObfuscationReflectionHelper.setPrivateValue(C03PacketPlayer.class, new C03PacketPlayer(), flot, "pitch", "field_149473_f");
    }
    
    public void setRotatingC03(boolean bool) {
    	ObfuscationReflectionHelper.setPrivateValue(C03PacketPlayer.class, new C03PacketPlayer(), bool, "rotating", "field_149481_i");
    }
    
    public void setItemInUse(int block) {
    	ObfuscationReflectionHelper.setPrivateValue(EntityPlayerSP.class, mc.thePlayer, block, "itemInUseCount", "field_71072_f");
    }
    
    public Vec3 getVectorForRotation(float pitch, float yaw) {
        return (Vec3) getPrivateMethod(Entity.class, mc.thePlayer, float.class, float.class, pitch, yaw, "getVectorForRotation", "func_174806_f");
    }
    
    public void clickMouse() {
    	getPrivateMethod(Minecraft.class, mc, "func_147116_af", "clickMouse");
    }
    
    public void rightClickMouse() {
    	getPrivateMethod(Minecraft.class, mc, "func_147121_ag", "rightClickMouse");
    }
    
    @SneakyThrows
    public boolean isShaders() {
        Class<?> configClass = Class.forName("Config");
        return (boolean) configClass.getMethod("isShaders").invoke(null);
    }

    @SneakyThrows
    public void setGameSetting(Minecraft mc, String fieldName, boolean value) {
        ObfuscationReflectionHelper.setPrivateValue(GameSettings.class, mc.gameSettings, value, fieldName);
        Class<?> configClass = Class.forName("Config");
        Field field = configClass.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.setBoolean(null, value);
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
    @SneakyThrows
    public <T> Object getPrivateMethod(Class<? super T> classToAccess, T instance, Object... values) {
        int stringIndex = -1;
        for (int i = 0; i < values.length; i++) {
            if (values[i] instanceof String) {
                stringIndex = i;
                break;
            }
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

        method.setAccessible(true);
        return method.invoke(instance, args);
    }
}