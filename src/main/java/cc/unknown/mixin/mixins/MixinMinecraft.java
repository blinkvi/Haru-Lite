package cc.unknown.mixin.mixins;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cc.unknown.Haru;
import cc.unknown.event.GameEvent;
import cc.unknown.event.player.AttackEvent;
import cc.unknown.mixin.impl.IMinecraft;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.resources.DefaultResourcePack;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.stream.IStream;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;
import net.minecraft.util.Util;
import net.minecraftforge.common.MinecraftForge;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft implements IMinecraft {

	@Shadow
	@Mutable
	@Final
	private Session session;

	@Shadow
	private boolean fullscreen;

	@Shadow
	public int leftClickCounter;

	@Shadow
	public PlayerControllerMP playerController;

	@Shadow
	public WorldClient theWorld;

	@Shadow
	public EntityPlayerSP thePlayer;

	@Shadow
	public MovingObjectPosition objectMouseOver;

	@Shadow
	public EntityRenderer entityRenderer;

	@Shadow
	public GuiScreen currentScreen;

	@Shadow
	@Final
	public DefaultResourcePack mcDefaultResourcePack;

	@Shadow
	private boolean enableGLErrorChecking = true;

	@Shadow
	public GameSettings gameSettings;

	@Shadow
	public abstract void updateDisplay();

	@Shadow
	public abstract ByteBuffer readImageToBuffer(InputStream imageStream) throws IOException;

	@Override
	public void setSession(final Session session) {
		this.session = session;
	}

	@Inject(method = "startGame", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;ingameGUI:Lnet/minecraft/client/gui/GuiIngame;", shift = At.Shift.AFTER))
	private void injectStartGame(CallbackInfo ci) {
		Haru.instance.init();
	}

	@Inject(method = "shutdownMinecraftApplet", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;info(Ljava/lang/String;)V", shift = At.Shift.BEFORE))
	private void shutdownMinecraftApplet(CallbackInfo ci) {
		Haru.instance.stop();
	}

	@Inject(method = "runGameLoop", at = @At(value = "INVOKE", target = "Lnet/minecraft/profiler/Profiler;startSection(Ljava/lang/String;)V", ordinal = 1))
	private void loopEvent(CallbackInfo ci) {
		MinecraftForge.EVENT_BUS.post(new GameEvent());
	}

	@Redirect(method = "runTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/EffectRenderer;updateEffects()V"))
	public void fixEffectRenderer(EffectRenderer effectRenderer) {
		try {
			effectRenderer.updateEffects();
		} catch (Exception e) {
		}
	}

	@Inject(method = "setIngameFocus", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/MouseHelper;grabMouseCursor()V"))
	public void fixKeyBinding(CallbackInfo callback) {
		for (KeyBinding keyBinding : gameSettings.keyBindings) {
			try {
				KeyBinding.setKeyBindState(keyBinding.getKeyCode(),
						keyBinding.getKeyCode() < 256 && Keyboard.isKeyDown(keyBinding.getKeyCode()));
			} catch (Exception e) {
			}
		}
	}

	@Inject(method = { "toggleFullscreen" }, at = { @At("RETURN") })
	public void toggleFullscreen(CallbackInfo info) {
		if (!this.fullscreen) {
			Display.setResizable(false);
			Display.setResizable(true);
		}

	}

	@Inject(method = "startGame", at = @At("TAIL"))
	private void disableGlErrorChecking(CallbackInfo ci) {
		this.enableGLErrorChecking = false;
	}

	@Overwrite
	private void setWindowIcon() {
		setWindowIcon("haru/icon/icon16.png", "haru/icon/icon32.png");
	}

	@Unique
	public void setWindowIcon(String icon16, String icon32) {
		if (Util.getOSType() == Util.EnumOS.OSX) {
			return;
		}

		try (InputStream input16 = mcDefaultResourcePack.getInputStream(new ResourceLocation(icon16));
				InputStream input32 = mcDefaultResourcePack.getInputStream(new ResourceLocation(icon32))) {

			if (input16 != null && input32 != null) {
				Display.setIcon(new ByteBuffer[] { readImageToBuffer(input16), readImageToBuffer(input32) });
			}
		} catch (IOException e) {
		}
	}

	@Overwrite
	public void clickMouse() {
		clickMouse(true, true);
	}

	@Unique
	public void clickMouse(boolean swing, boolean events) {
		if (this.leftClickCounter <= 0) {
			if (events) {
				if (this.objectMouseOver != null
						&& this.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY
						&& objectMouseOver.entityHit instanceof EntityLivingBase) {
					final AttackEvent event = new AttackEvent((EntityLivingBase) this.objectMouseOver.entityHit);
					MinecraftForge.EVENT_BUS.post(event);

					if (event.isCanceled())
						return;
				}
			}

			if (swing) {
				this.thePlayer.swingItem();
			}

			if (this.objectMouseOver == null) {
				if (this.playerController.isNotCreative()) {
					this.leftClickCounter = 10;
				}
			} else {
				switch (this.objectMouseOver.typeOfHit) {
				case ENTITY:
					this.playerController.attackEntity(this.thePlayer, this.objectMouseOver.entityHit);
					break;

				case BLOCK:
					final BlockPos blockpos = this.objectMouseOver.getBlockPos();

					if (this.theWorld.getBlockState(blockpos).getBlock().getMaterial() != Material.air) {
						this.playerController.clickBlock(blockpos, this.objectMouseOver.sideHit);
						break;
					}

				default:
					if (this.playerController.isNotCreative()) {
						this.leftClickCounter = 10;
					}
				}
			}
		}
	}

	@Inject(method = "createDisplay", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/Display;setTitle(Ljava/lang/String;)V", shift = At.Shift.AFTER))
	private void createDisplay(CallbackInfo callbackInfo) {
		Display.setTitle("Loading Haru...");
	}

	@Redirect(method = "loadWorld(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V", at = @At(value = "INVOKE", target = "Ljava/lang/System;gc()V"))
	private void optimizedWorldSwapping() {
	}

	@Redirect(method = "runGameLoop", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/stream/IStream;func_152935_j()V"))
	private void skipTwitchCode1(IStream instance) {
	}

	@Redirect(method = "runGameLoop", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/stream/IStream;func_152922_k()V"))
	private void skipTwitchCode2(IStream instance) {
	}

	@Redirect(method = "runTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/EntityRenderer;loadEntityShader(Lnet/minecraft/entity/Entity;)V"))
	private void keepShadersOnPerspectiveChange(EntityRenderer entityRenderer, Entity entityIn) {
	}

	@Inject(method = "loadWorld(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V", at = @At("HEAD"))
	private void clearLoadedMaps(WorldClient worldClientIn, String loadingMessage, CallbackInfo ci) {
		if (worldClientIn != this.theWorld) {
			this.entityRenderer.getMapItemRenderer().clearLoadedMaps();
		}
	}
	
    @Overwrite
    public void startTimerHackThread() { }

	@Inject(method = "startGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/SkinManager;<init>(Lnet/minecraft/client/renderer/texture/TextureManager;Ljava/io/File;Lcom/mojang/authlib/minecraft/MinecraftSessionService;)V"))
	public void splashSkinManager(CallbackInfo callback) {
		updateDisplay();
	}

	@Inject(method = "startGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/storage/AnvilSaveConverter;<init>(Ljava/io/File;)V"))
	public void splashSaveLoader(CallbackInfo callback) {
		updateDisplay();
	}

	@Inject(method = "startGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/audio/SoundHandler;<init>(Lnet/minecraft/client/resources/IResourceManager;Lnet/minecraft/client/settings/GameSettings;)V"))
	public void splashSoundHandler(CallbackInfo callback) {
		updateDisplay();
	}

	@Inject(method = "startGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/audio/MusicTicker;<init>(Lnet/minecraft/client/Minecraft;)V"))
	public void splashMusicTicker(CallbackInfo callback) {
		updateDisplay();
	}

	@Inject(method = "startGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;<init>(Lnet/minecraft/client/settings/GameSettings;Lnet/minecraft/util/ResourceLocation;Lnet/minecraft/client/renderer/texture/TextureManager;Z)V"))
	public void splashFontRenderer(CallbackInfo callback) {
		updateDisplay();
	}

	@Inject(method = "startGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/MouseHelper;<init>()V"))
	public void splashMouseHelper(CallbackInfo callback) {
		updateDisplay();
	}

	@Inject(method = "startGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/texture/TextureMap;<init>(Ljava/lang/String;)V"))
	public void splashTextureMap(CallbackInfo callback) {
		updateDisplay();
	}

	@Inject(method = "startGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/model/ModelManager;<init>(Lnet/minecraft/client/renderer/texture/TextureMap;)V"))
	public void splashModelManager(CallbackInfo callback) {
		updateDisplay();
	}

	@Inject(method = "startGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/RenderItem;<init>(Lnet/minecraft/client/renderer/texture/TextureManager;Lnet/minecraft/client/resources/model/ModelManager;)V"))
	public void splashRenderItem(CallbackInfo callback) {
		updateDisplay();
	}

	@Inject(method = "startGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/RenderManager;<init>(Lnet/minecraft/client/renderer/texture/TextureManager;Lnet/minecraft/client/renderer/entity/RenderItem;)V"))
	public void splashRenderManager(CallbackInfo callback) {
		updateDisplay();
	}

	@Inject(method = "startGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemRenderer;<init>(Lnet/minecraft/client/Minecraft;)V"))
	public void splashItemRenderer(CallbackInfo callback) {
		updateDisplay();
	}

	@Inject(method = "startGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/EntityRenderer;<init>(Lnet/minecraft/client/Minecraft;Lnet/minecraft/client/resources/IResourceManager;)V"))
	public void splashEntityRenderer(CallbackInfo callback) {
		updateDisplay();
	}

	@Inject(method = "startGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/BlockRendererDispatcher;<init>(Lnet/minecraft/client/renderer/BlockModelShapes;Lnet/minecraft/client/settings/GameSettings;)V"))
	public void splashBlockRenderDispatcher(CallbackInfo callback) {
		updateDisplay();
	}

	@Inject(method = "startGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderGlobal;<init>(Lnet/minecraft/client/Minecraft;)V"))
	public void splashRenderGlobal(CallbackInfo callback) {
		updateDisplay();
	}

	@Inject(method = "startGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/achievement/GuiAchievement;<init>(Lnet/minecraft/client/Minecraft;)V"))
	public void splashGuiAchivement(CallbackInfo callback) {
		updateDisplay();
	}

	@Inject(method = "startGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/EffectRenderer;<init>(Lnet/minecraft/world/World;Lnet/minecraft/client/renderer/texture/TextureManager;)V"))
	public void splashEffectRenderer(CallbackInfo callback) {
		updateDisplay();
	}

	@Inject(method = "startGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiIngame;<init>(Lnet/minecraft/client/Minecraft;)V"))
	public void splashGuiIngame(CallbackInfo callback) {
		updateDisplay();
	}
}
