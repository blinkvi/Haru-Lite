package cc.unknown.module.impl.visual;

import cc.unknown.event.render.Render2DEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.ReflectUtil;
import cc.unknown.util.render.Animation;
import cc.unknown.util.render.enums.animation.Easing;
import cc.unknown.util.render.progress.Progress;
import cc.unknown.util.render.progress.ProgressManager;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleInfo(name = "BreakProgress", description = "", category = Category.VISUAL)
public class BreakProgress extends Module {
	private final Animation progressAnimation = new Animation(Easing.EASE_OUT_CIRC, 200);
	private final Progress progressObj = new Progress("");
	private double progress;
	@SuppressWarnings("unused")
	private BlockPos block;
	private String progressStr;

	@SubscribeEvent
	public void onRender2D(Render2DEvent event) {
		synchronized (ProgressManager.progresses) {
			ProgressManager.progresses.forEach(Progress::render);
		}

		progressAnimation.run(progress);
		progressObj.setProgress(progressAnimation.getValue());
		progressObj.setText(progressStr);
		if (progress > 0)
			ProgressManager.add(progressObj);
		else
			ProgressManager.remove(progressObj);
	}

	@Override
	public void onUpdate() {
		try {
		if (mc.thePlayer.capabilities.isCreativeMode || !mc.thePlayer.capabilities.allowEdit) {
			this.resetVariables();
			return;
		}

		if (mc.objectMouseOver == null || mc.objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) {
			this.resetVariables();
			return;
		}
		
		this.progress = ReflectUtil.getCurBlockDamage();
		if (this.progress == 0.0f) {
			this.resetVariables();
			return;
		}
		
		this.block = mc.objectMouseOver.getBlockPos();
		this.setProgress();
    	} catch (NullPointerException e) {
    		
    	}
	}

	private void setProgress() {
		this.progressStr = (int) (100.0 * (this.progress)) + "%";
	}

	public void onDisable() {
		this.resetVariables();
	}

	private void resetVariables() {
		this.progress = 0.0f;
		this.block = null;
		this.progressStr = "";
	}
}