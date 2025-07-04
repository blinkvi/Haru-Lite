package cc.unknown.mixin.mixins;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.authlib.GameProfile;

import cc.unknown.event.player.ChatInputEvent;
import cc.unknown.event.player.LivingEvent;
import cc.unknown.event.player.PostUpdateEvent;
import cc.unknown.event.player.PrePositionEvent;
import cc.unknown.event.player.PreUpdateEvent;
import cc.unknown.event.player.PushOutOfBlockEvent;
import cc.unknown.event.player.SlowDownEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.potion.Potion;
import net.minecraft.util.MovementInput;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mixin(EntityPlayerSP.class)
@SideOnly(Side.CLIENT)
public abstract class MixinEntityPlayerSP extends AbstractClientPlayer {

	public MixinEntityPlayerSP(World worldIn, GameProfile playerProfile) {
		super(worldIn, playerProfile);
	}

	@Shadow
	public MovementInput movementInput;

	@Shadow
	@Final
	public NetHandlerPlayClient sendQueue;

	@Shadow
	private boolean serverSneakState;
	@Shadow
	private double lastReportedPosX;
	@Shadow
	private double lastReportedPosY;
	@Shadow
	private double lastReportedPosZ;
	@Shadow
	private float lastReportedPitch;
	@Shadow
	private int positionUpdateTicks;
	@Shadow
	private boolean serverSprintState;
	@Shadow
	public float lastReportedYaw;

	@Shadow
	protected abstract boolean isCurrentViewEntity();

	@Shadow
	public abstract boolean isSneaking();

	@Shadow
	public int sprintingTicksLeft;
	@Shadow
	public float timeInPortal;
	@Shadow
	public float prevTimeInPortal;

	@Shadow
	public float horseJumpPower;
	@Shadow
	public int horseJumpPowerCounter;
	@Shadow
	protected int sprintToggleTimer;
	@Shadow
	protected Minecraft mc;

	@Shadow
	public abstract void playSound(String name, float volume, float pitch);

	@Shadow
	public abstract void setSprinting(boolean sprinting);

	@Shadow
	protected abstract boolean pushOutOfBlocks(double x, double y, double z);

	@Shadow
	public abstract void sendPlayerAbilities();

	@Shadow
	protected abstract void sendHorseJump();

	@Shadow
	public abstract boolean isRidingHorse();

	@Unique
	public boolean omniSprint;

	@Inject(method = "onUpdate", at = @At("HEAD"), cancellable = true)
	public void onPreUpdate(CallbackInfo ci) {
		PreUpdateEvent event = new PreUpdateEvent();
		MinecraftForge.EVENT_BUS.post(event);

		if (event.isCanceled()) {
			ci.cancel();
		}
	}
	
	@Inject(method = {"sendChatMessage"}, at = {@At("HEAD")}, cancellable = true)
	public void sendChatMessage(String message, CallbackInfo ci) {
		ChatInputEvent event = new ChatInputEvent(message);
		MinecraftForge.EVENT_BUS.post(event);
		if (event.isCanceled()) {
			ci.cancel(); 
		}
	}

	@Inject(method = "onUpdate", at = @At("RETURN"))
	public void onPostUpdate(CallbackInfo ci) {
		MinecraftForge.EVENT_BUS.post(new PostUpdateEvent());
	}

    @Inject(method = "onUpdateWalkingPlayer", at = @At("HEAD"), cancellable = true)
    private void onUpdateWalkingPlayer(CallbackInfo ci) {
    	PrePositionEvent event = new PrePositionEvent(posX, posY, posZ, rotationYaw, rotationPitch, onGround, isSprinting(), isSneaking());

    	MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
        	return;
        }

        boolean flag = event.isSprinting;
        if (flag != serverSprintState) {
            if (flag) {
                sendQueue.addToSendQueue(new C0BPacketEntityAction(this, C0BPacketEntityAction.Action.START_SPRINTING));
            } else {
                sendQueue.addToSendQueue(new C0BPacketEntityAction(this, C0BPacketEntityAction.Action.STOP_SPRINTING));
            }

            serverSprintState = flag;
        }

        boolean flag1 = event.isSneaking;
        if (flag1 != serverSneakState) {
            if (flag1) {
                sendQueue.addToSendQueue(new C0BPacketEntityAction(this, C0BPacketEntityAction.Action.START_SNEAKING));
            } else {
                sendQueue.addToSendQueue(new C0BPacketEntityAction(this, C0BPacketEntityAction.Action.STOP_SNEAKING));
            }

            serverSneakState = flag1;
        }

        if (isCurrentViewEntity()) {
            double d0 = event.x - lastReportedPosX;
            double d1 = event.y - lastReportedPosY;
            double d2 = event.z - lastReportedPosZ;
            
            float yaw = event.yaw;
            float pitch = event.pitch;
            
            double d3 = yaw - lastReportedYaw;
            double d4 = pitch - lastReportedPitch;
            
            boolean flag2 = d0 * d0 + d1 * d1 + d2 * d2 > 9.0E-4 || positionUpdateTicks >= 20;
            boolean flag3 = d3 != 0.0 || d4 != 0.0;
            if (ridingEntity == null) {
                if (flag2 && flag3) {
                    sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(event.x, event.y, event.z, yaw, pitch, event.onGround));
                } else if (flag2) {
                    sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(event.x, event.y, event.z, event.onGround));
                } else if (flag3) {
                    sendQueue.addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(yaw, pitch, event.onGround));
                } else {
                    sendQueue.addToSendQueue(new C03PacketPlayer(event.onGround));
                }
            } else {
                sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(motionX, -999.0D, motionZ, yaw, pitch, event.onGround));
                flag2 = false;
            }

            ++positionUpdateTicks;

            if (flag2) {
                lastReportedPosX = event.x;
                lastReportedPosY = event.y;
                lastReportedPosZ = event.z;
                positionUpdateTicks = 0;
            }

            if (flag3) {
                lastReportedYaw = yaw;
                lastReportedPitch = pitch;
            }
        }
        
    	ci.cancel();
    }
    

	@Inject(method = "pushOutOfBlocks", at = @At("HEAD"), cancellable = true)
	private void onPushOutOfBlocks(CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
		PushOutOfBlockEvent event = new PushOutOfBlockEvent();
		if (noClip) {
			event.setCanceled(true);
		}
		MinecraftForge.EVENT_BUS.post(event);

		if (event.isCanceled()) {
			callbackInfoReturnable.setReturnValue(false);
		}
	}

    @Overwrite
    public void onLivingUpdate() {
    	MinecraftForge.EVENT_BUS.post(new LivingEvent());
        if (sprintingTicksLeft > 0) {
            --sprintingTicksLeft;

            if (sprintingTicksLeft == 0) setSprinting(false);
        }

        if (sprintToggleTimer > 0) --sprintToggleTimer;
        
        prevTimeInPortal = timeInPortal;

        if (inPortal) {
            if (mc.currentScreen != null && !mc.currentScreen.doesGuiPauseGame()) mc.displayGuiScreen((GuiScreen)null);
            if (timeInPortal == 0.0F) mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("portal.trigger"), rand.nextFloat() * 0.4F + 0.8F));
            
            timeInPortal += 0.0125F;

            if (timeInPortal >= 1.0F) timeInPortal = 1.0F;
            
            inPortal = false;
        } else if (isPotionActive(Potion.confusion) && getActivePotionEffect(Potion.confusion).getDuration() > 60) {
            timeInPortal += 0.006666667F;
            if (timeInPortal > 1.0F) timeInPortal = 1.0F;
        } else {
            if (timeInPortal > 0.0F) timeInPortal -= 0.05F;
            if (timeInPortal < 0.0F) timeInPortal = 0.0F;
        }

        if (timeUntilPortal > 0) --timeUntilPortal;

        boolean flag = movementInput.jump;
        boolean flag1 = movementInput.sneak;
        float f = 0.8F;
        boolean flag2 = movementInput.moveForward >= f;
        movementInput.updatePlayerMoveState();

        SlowDownEvent event = new SlowDownEvent(0.2F, 0.2F);
        MinecraftForge.EVENT_BUS.post(event);

        if (isUsingItem() && !isRiding()) {
            movementInput.moveStrafe *= event.strafeMultiplier;
            movementInput.moveForward *= event.forwardMultiplier;
            sprintToggleTimer = 0;
        }

        pushOutOfBlocks(posX - (double)width * 0.35D, getEntityBoundingBox().minY + 0.5D, posZ + (double)width * 0.35D);
        pushOutOfBlocks(posX - (double)width * 0.35D, getEntityBoundingBox().minY + 0.5D, posZ - (double)width * 0.35D);
        pushOutOfBlocks(posX + (double)width * 0.35D, getEntityBoundingBox().minY + 0.5D, posZ - (double)width * 0.35D);
        pushOutOfBlocks(posX + (double)width * 0.35D, getEntityBoundingBox().minY + 0.5D, posZ + (double)width * 0.35D);
        boolean flag3 = (float)getFoodStats().getFoodLevel() > 6.0F || capabilities.allowFlying;

        if (onGround && !flag1 && !flag2 && (omniSprint || movementInput.moveForward >= f) && !isSprinting() && flag3 && (!isUsingItem() || event.sprint) && !isPotionActive(Potion.blindness)) {
        	if (sprintToggleTimer <= 0 && !mc.gameSettings.keyBindSprint.isKeyDown()) {
        		sprintToggleTimer = 7;
        	} else {
        		setSprinting(true);
        	}
        }
        
        if (!isSprinting() && (omniSprint || movementInput.moveForward >= f) && flag3 && (!isUsingItem() || event.sprint) && !isPotionActive(Potion.blindness) && mc.gameSettings.keyBindSprint.isKeyDown()) setSprinting(true); 
        
        if (!event.sprint && isUsingItem() && !isRiding()) setSprinting(false); 

        if (isSprinting() && (!omniSprint && (movementInput.moveForward < f || isCollidedHorizontally || !flag3))) setSprinting(false);
        
        if (capabilities.allowFlying) {
            if (mc.playerController.isSpectatorMode()) {
                if (!capabilities.isFlying) {
                    capabilities.isFlying = true;
                    sendPlayerAbilities();
                }
            } else if (!flag && movementInput.jump) {
                if (flyToggleTimer == 0) {
                    flyToggleTimer = 7;
                } else {
                    capabilities.isFlying = !capabilities.isFlying;
                    sendPlayerAbilities();
                    flyToggleTimer = 0;
                }
            }
        }

        if (capabilities.isFlying && isCurrentViewEntity()) {
            if (movementInput.sneak) motionY -= (double)(capabilities.getFlySpeed() * 3.0F);
            if (movementInput.jump) motionY += (double)(capabilities.getFlySpeed() * 3.0F);
            
        }

        if (isRidingHorse()) {
            if (horseJumpPowerCounter < 0) {
                ++horseJumpPowerCounter;

                if (horseJumpPowerCounter == 0) {
                    horseJumpPower = 0.0F;
                }
            }

            if (flag && !movementInput.jump) {
                horseJumpPowerCounter = -10;
                sendHorseJump();
            } else if (!flag && movementInput.jump) {
                horseJumpPowerCounter = 0;
                horseJumpPower = 0.0F;
            } else if (flag) {
                ++horseJumpPowerCounter;

                if (horseJumpPowerCounter < 10) {
                    horseJumpPower = (float)horseJumpPowerCounter * 0.1F;
                } else {
                    horseJumpPower = 0.8F + 2.0F / (float)(horseJumpPowerCounter - 9) * 0.1F;
                }
            }
        } else {
            horseJumpPower = 0.0F;
        }

        super.onLivingUpdate();

        if (onGround && capabilities.isFlying && !mc.playerController.isSpectatorMode()) {
            capabilities.isFlying = false;
            sendPlayerAbilities();
        }        
    }
}