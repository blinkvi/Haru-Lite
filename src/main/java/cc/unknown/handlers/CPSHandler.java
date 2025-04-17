package cc.unknown.handlers;
import org.lwjgl.input.Mouse;

import cc.unknown.event.PreTickEvent;
import cc.unknown.util.Accessor;
import cc.unknown.util.structure.list.SList;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CPSHandler implements Accessor {
	
	private static SList<Long> leftPresses = new SList<Long>();
	private static SList<Long> rightPresses = new SList<Long>();

	@SubscribeEvent
	public void onClick(MouseEvent event) {
		if(Mouse.getEventButtonState()) {
			if(event.button == 0) {
				addLeftClicks();
			}
			
			if(event.button == 1) {
				addRightClicks();
			}
		}
	}
	
	@SubscribeEvent
	public void onPreTick(PreTickEvent event) {
		leftPresses.removeIf(t -> System.currentTimeMillis() - t > 1000);
		rightPresses.removeIf(t -> System.currentTimeMillis() - t > 1000);
	}

	public static int getLeftCps() {
		return leftPresses.size();
	}

	public static int getRightCps() {
		return rightPresses.size();
	}
	
	public static void addLeftClicks() {
		leftPresses.add(System.currentTimeMillis());
	}
	
	public static void addRightClicks() {
		rightPresses.add(System.currentTimeMillis());
	}
}
