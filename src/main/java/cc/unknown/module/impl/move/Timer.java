package cc.unknown.module.impl.move;

import java.text.DecimalFormat;
import java.util.concurrent.ThreadLocalRandom;

import cc.unknown.event.netty.OutgoingEvent;
import cc.unknown.event.player.LivingEvent;
import cc.unknown.event.player.PrePositionEvent;
import cc.unknown.event.render.Render2DEvent;
import cc.unknown.handlers.BadPacketsHandler;
import cc.unknown.handlers.PingSpoofHandler;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.ReflectUtil;
import cc.unknown.util.client.system.Clock;
import cc.unknown.util.render.font.FontUtil;
import cc.unknown.value.impl.Mode;
import cc.unknown.value.impl.Slider;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleInfo(name = "Timer", description = "Faster game.", category = Category.MOVE)
public class Timer extends Module {
	
	private final Mode mode = new Mode("Mode", this, "Vanilla", "Vanilla", "Ground", "Air", "Ground/Air", "Random", "Balance");
	
	private final Slider speed = new Slider("Speed", this, 1.5, 0.05, 25, 0.01, () -> !mode.is("Balance"));
	private final Slider variation = new Slider("Randomization", this, 15, 5, 50, 5, () -> mode.is("Random"));
	
	private final Slider ground = new Slider("GroundSpeed", this, 0.04, 0.05, 25, 0.01, () -> mode.is("Ground/Air"));
	private final Slider air = new Slider("AirSpeed", this, 2, 0.05, 25, 0.01, () -> mode.is("Ground/Air"));
	
	private final double balanceValue = 4230;
	private final boolean onlyWhenStill = true;
 
    public boolean blinked;
    private double balance = 0;
    private boolean speeding = false;
    private final Clock clock = new Clock();
	
	@Override
	public void onEnable() {
		if (!isInGame()) return;
		
        blinked = false;
        balance = 0;
        clock.reset();
        speeding = false;
	}
	
	@Override
	public void onDisable() {
		if (!isInGame()) return;
        if (mode.is("Balance")) {
            reset();
        }
		ReflectUtil.getTimer().timerSpeed = 1.0f;
	}
	
	@SubscribeEvent
    public void onUpdate(LivingEvent event) {
        if (mode.is("Balance")) {
            PingSpoofHandler.spoof(14000, true, false, false, false);
        }
    }
	
	@SubscribeEvent
    public void onOutgoing(OutgoingEvent event) {
        Packet<?> packet = event.packet;
        if (mode.is("Balance")) {
            if (packet instanceof C03PacketPlayer) {
            	C03PacketPlayer wrapper = (C03PacketPlayer) packet;

                if (!wrapper.getRotating() && !wrapper.isMoving() && (!onlyWhenStill || (mc.thePlayer.posX == mc.thePlayer.lastTickPosX && mc.thePlayer.posY == mc.thePlayer.lastTickPosY && mc.thePlayer.posZ == mc.thePlayer.lastTickPosZ))) {
                    event.setCanceled(true);
                }

                if (!event.isCanceled()) {
                    this.balance -= 50;
                }

                this.balance += clock.getTime();
                this.clock.reset();
            }
        }
    }

	@SubscribeEvent
    public void onRender2D(Render2DEvent event) {
        if (mode.is("Balance")) {
            final ScaledResolution resolution = new ScaledResolution(mc);
            final int x = resolution.getScaledWidth() / 2;
            final int y = resolution.getScaledHeight() - 75;

            float percentage = (float) Math.max(0.01, Math.min(1, balance / balanceValue));
            
            FontUtil.getConsolas(15).drawCentered("Balance", x, y - 1 - 11 + 3, -1);

            FontUtil.getConsolas(12).drawCentered(new DecimalFormat("0.0").format(percentage * 100) + "%", x, y + 2, -1);
        }
    }

	@SubscribeEvent
	public void onPrePosition(PrePositionEvent event) {
		if (!isInGame()) return;
		
		switch (mode.getMode()) {
		case "Vanilla":
			ReflectUtil.getTimer().timerSpeed = speed.getAsFloat();
			break;
		case "Ground":
			if (mc.thePlayer.onGround) 
				ReflectUtil.getTimer().timerSpeed = speed.getAsFloat();
			break;
		case "Air":
			if (!mc.thePlayer.onGround)
				ReflectUtil.getTimer().timerSpeed = speed.getAsFloat();
			break;
		case "Ground/Air":
			if (mc.thePlayer.onGround) 
				ReflectUtil.getTimer().timerSpeed = ground.getAsFloat();
			
			if (!mc.thePlayer.onGround)
				ReflectUtil.getTimer().timerSpeed = air.getAsFloat();
				break;
		case "Random":
			float randomization = variation.getAsInt();
		    float halfVariation = randomization / 2.0F;
		    float randomOffset = ThreadLocalRandom.current().nextFloat() * halfVariation * 2 - halfVariation;

		    ReflectUtil.getTimer().timerSpeed = Math.max(speed.getAsFloat() + randomOffset, 0.1F);
			break;
		case "Balance":
            if (balance > balanceValue) {
            	ReflectUtil.getTimer().timerSpeed = 3.0f;
                speeding = true;
            }
            if (speeding && balance <= 0) {
                reset();
            }
			break;
		}
	}
	
    private void reset() {
        this.balance = 0;
        this.clock.reset();
        ReflectUtil.getTimer().timerSpeed = 1.0f;
    }
}