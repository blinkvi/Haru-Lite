package cc.unknown.handlers;
import java.util.List;

import org.lwjgl.opengl.GL11;

import cc.unknown.event.player.JumpEvent;
import cc.unknown.event.player.PreAttackEvent;
import cc.unknown.event.player.PrePositionEvent;
import cc.unknown.module.impl.move.NoSlow;
import cc.unknown.module.impl.utility.NoItemRelease;
import cc.unknown.module.impl.visual.Interface;
import cc.unknown.util.Accessor;
import cc.unknown.util.client.ReflectUtil;
import cc.unknown.util.player.InventoryUtil;
import cc.unknown.util.structure.list.SList;
import cc.unknown.util.structure.vectors.Vec3;
import cc.unknown.value.impl.BoolValue;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemFood;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SettingsHandler implements Accessor {
	
	private SList<JumpCircle> circles = new SList<JumpCircle>();
	private boolean jumping;

    @SubscribeEvent
    public void onPreAttack(PreAttackEvent event) {
        if (!isInGame()) return;

        getDropGui().getWindows().stream().forEach(window -> {
            if (getName(window.getSettingBools(), "NoHitDelay")) {
                ReflectUtil.setLeftClickCounter(0);
            }

            if (getName(window.getSettingBools(), "NoJumpDelay")) {
                ReflectUtil.setJumpTicks(0);
            }
            
            if (getName(window.getSettingBools(), "JumpCircle")) {
        		if(jumping && mc.thePlayer.onGround) {
        			jumping = false;
        			circles.add(new JumpCircle(getPositionVector()));
        		}
        		
        		circles.removeIf(JumpCircle::update);
            }
        });
    }

    @SubscribeEvent
    public void onPrePosition(PrePositionEvent event) {
        if (!isInGame()) return;

        getDropGui().getWindows().stream()
            .filter(window -> getName(window.getSettingBools(), "NoUseDelay") && mc.thePlayer.isUsingItem() && InventoryUtil.getItemStack().getItem() instanceof ItemFood)
            .findFirst()
            .ifPresent(window -> {
                if (getModule(NoSlow.class).isEnabled() && getModule(NoItemRelease.class).isEnabled()) return;

                int foodUseDuration = mc.thePlayer.getItemInUseDuration();
                if (foodUseDuration >= 20) {
                    mc.thePlayer.stopUsingItem();
                }
            });
    }
	
	@SubscribeEvent
	public void onJump(JumpEvent event) {
        getDropGui().getWindows().stream().forEach(window -> {
        	if (getName(window.getSettingBools(), "JumpCircle")) {
            	jumping = true;
            }
        });
	}
    
    @SubscribeEvent
    public void onRender3D(RenderWorldLastEvent event) {
        getDropGui().getWindows().stream().forEach(window -> {
        	if (getName(window.getSettingBools(), "JumpCircle")) {
                GL11.glPushMatrix();
                GL11.glEnable(3042);
                GL11.glDisable(3008);
                GL11.glDisable(2884);
                GL11.glDisable(3553);
                GL11.glShadeModel(7425);
                
                circles.stream().forEach(circle -> {
                    GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
                    
                    for (int i = 0; i <= 360; i += 5) {
                        int color = getModule(Interface.class).color();
                        float red = (float) ((color >> 16) & 255) / 255.0F;
                        float green = (float) ((color >> 8) & 255) / 255.0F;
                        float blue = (float) (color & 255) / 255.0F;

                        Vec3 pos = circle.pos();
                        double anim = createAnimation(1.0 - circle.getAnimation(ReflectUtil.getTimer().renderPartialTicks));
                        double x = Math.cos(Math.toRadians(i)) * anim * 0.7;
                        double z = Math.sin(Math.toRadians(i)) * anim * 0.7;
                        double alpha = circle.getAnimation(ReflectUtil.getTimer().renderPartialTicks);

                        GL11.glColor4d(red, green, blue, 0.6 * alpha);
                        GL11.glVertex3d(pos.x + x, pos.y + 0.2, pos.z + z);

                        GL11.glColor4d(red, green, blue, 0.2 * alpha);
                        GL11.glVertex3d(pos.x + x * 1.4, pos.y + 0.2, pos.z + z * 1.4);
                    }

                    GL11.glEnd();
                });
                
                GL11.glEnable(3553);
                GL11.glDisable(3042);
                GL11.glEnable(3008);
                GL11.glShadeModel(7424);
                GL11.glEnable(2884);
                GL11.glPopMatrix();
                GlStateManager.resetColor();
            }
        });
    }
    
    private double createAnimation(double value) {
        return Math.sqrt(1.0 - Math.pow(value - 1.0, 2.0));
    }
    
    public Vec3 getPositionVector() {
        return new Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
    }
    
    private class JumpCircle {
    	
    	private Vec3 vector;
    	private int tick;
    	private int prevTick;
    	
    	public JumpCircle(Vec3 vector) {
    		this.vector = vector;
    		this.prevTick = 20;
            this.prevTick = this.tick = 20;
    	}
    	
        public double getAnimation(float pt) {
            return ((float)this.prevTick + (float)(this.tick - this.prevTick) * pt) / 20.0f;
        }

        public boolean update() {
            this.prevTick = this.tick;
            return this.tick-- <= 0;
        }

        public Vec3 pos() {
            return new Vec3(this.vector.x - ReflectUtil.getRenderPosX(), this.vector.y - ReflectUtil.getRenderPosY(), this.vector.z - ReflectUtil.getRenderPosZ());
        }
    }
    
    private boolean getName(List<BoolValue> value, String name) {
        return value.stream().filter(setting -> setting.getName().equalsIgnoreCase(name)).map(BoolValue::get).findFirst().orElse(false);
    }
}
