package cc.unknown.module.impl.combat;

import java.util.concurrent.ThreadLocalRandom;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.system.Clock;
import cc.unknown.value.impl.SliderValue;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

@ModuleInfo(name = "MoreKB", description = "Amplifies knockback effect on opponents during combat.", category = Category.COMBAT)
public class MoreKB extends Module {

	private final SliderValue minPostdelay = new SliderValue("MinDelay", this, 25, 1, 500, 5);
	private final SliderValue maxPostdelay = new SliderValue("MaxDelay", this, 55, 1, 500, 5);
	private final SliderValue minTime = new SliderValue("MinTime", this, 25, 1, 500, 5);
	private final SliderValue maxTime = new SliderValue("MaxTime", this, 55, 1, 500, 5);
	private final SliderValue minHits = new SliderValue("MinHits", this, 1, 1, 10, 1);
	private final SliderValue maxHits = new SliderValue("MaxHits", this, 1, 1, 10, 1);

	private boolean comboing, hitCoolDown, alreadyHit, waitingForPostDelay;
	private int hitTimeout, hitsWaited;
	private Clock actionTimer = new Clock(0), postDelayTimer = new Clock(0);

	@Override
	public void onEnable() {
		correctValues(minPostdelay, maxPostdelay);
		correctValues(minTime, maxTime);
		correctValues(minHits, maxHits);
	}

	@SubscribeEvent
	public void onPostTick(ClientTickEvent event) {
		if (event.phase == Phase.START) return;
		correctValues(minPostdelay, maxPostdelay);
		correctValues(minTime, maxTime);
		correctValues(minHits, maxHits);
	}

	@SubscribeEvent
	public void onRenderTick(TickEvent.RenderTickEvent event) {
		if (!isInGame()) return;

		if (waitingForPostDelay) {
			if (postDelayTimer.isFinished()) {
				waitingForPostDelay = false;
				comboing = true;
				startCombo();
				actionTimer.reset();
			}
			return;
		}

		if (comboing) {
			if (actionTimer.isFinished()) {
				comboing = false;
				finishCombo();
				return;
			} else {
				return;
			}
		}
		
		if (Mouse.isButtonDown(0)) {
		    MovingObjectPosition object = mc.objectMouseOver;
		    if (object != null && object.entityHit instanceof EntityPlayer) {
		        EntityPlayer target = (EntityPlayer) object.entityHit;
		        if (target.hurtResistantTime >= 10) {

					if (hitCoolDown && !alreadyHit) {
						hitsWaited++;
						if (hitsWaited >= hitTimeout) {
							hitCoolDown = false;
							hitsWaited = 0;
						} else {
							alreadyHit = true;
							return;
						}
					}

					if (!alreadyHit) {
						if (minHits.getValue() == maxHits.getValue()) {
							hitTimeout = (int) minHits.getValue();
						} else {

							hitTimeout = ThreadLocalRandom.current().nextInt((int) minHits.getValue(),
									(int) maxHits.getValue());
						}
						hitCoolDown = true;
						hitsWaited = 0;

						actionTimer.setStartTime((long) ThreadLocalRandom.current().nextDouble(minTime.getValue(),
								maxTime.getValue() + 0.01));

						if (minPostdelay.getValue() != 1 || maxPostdelay.getValue() != 1) {
							postDelayTimer.setStartTime((long) ThreadLocalRandom.current()
									.nextDouble(minPostdelay.getValue(), maxPostdelay.getValue() + 0.01));
							postDelayTimer.reset();
							waitingForPostDelay = true;
						} else {
							comboing = true;
							startCombo();
							actionTimer.reset();
							alreadyHit = true;
						}

						alreadyHit = true;
					}
				} else {
					if (alreadyHit) {
					}
					alreadyHit = false;
				}
		    }
		}
	}

	private void finishCombo() {
		if (!Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode())) {
			KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), false);
		}
	}

	private void startCombo() {
		if (Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode())) {
			KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), true);
			KeyBinding.onTick(mc.gameSettings.keyBindBack.getKeyCode());
		}
	}
}
