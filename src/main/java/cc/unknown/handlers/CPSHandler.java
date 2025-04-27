package cc.unknown.handlers;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.MouseEvent;
import cc.unknown.util.Accessor;
import cc.unknown.util.player.InventoryUtil;
import cc.unknown.util.structure.list.SList;

public class CPSHandler implements Accessor {

	private static final SList<Long> leftClicks = new SList<>();
	private static final SList<Long> rightClicks = new SList<>();
	
	public static long leftClickTimer = 0L;
	public static long rightClickTimer = 0L;
	
	@EventLink
	public final Listener<MouseEvent> onMouse = event -> {
		if (event.buttonstate) {
			if (event.button == 0 && !mc.thePlayer.isBlocking()) {
				addLeftClick();
			}
			
			if (event.button == 1 || (InventoryUtil.getAnyBlock() || InventoryUtil.getProjectiles())) {
				addRightClick();
			}
		}
	};

	public static void addLeftClick() {
		leftClicks.add(leftClickTimer = System.currentTimeMillis());
	}

	public static void addRightClick() {
		rightClicks.add(rightClickTimer = System.currentTimeMillis());
	}

	public static int getLeftClickCounter() {
	    if (mc.thePlayer == null || mc.theWorld == null) return leftClicks.size();
	    leftClicks.removeIf(lon -> lon < System.currentTimeMillis() - 1000L);
	    return leftClicks.size();
	}

	public static int getRightClickCounter() {
	    if (mc.thePlayer == null || mc.theWorld == null) return rightClicks.size();
	    rightClicks.removeIf(lon -> lon < System.currentTimeMillis() - 1000L);
	    return rightClicks.size();
	}
}
