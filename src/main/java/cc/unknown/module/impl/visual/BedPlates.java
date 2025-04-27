package cc.unknown.module.impl.visual;

import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glNormal3f;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glScaled;
import static org.lwjgl.opengl.GL11.glTranslatef;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.EntityJoinWorldEvent;
import cc.unknown.event.impl.PreTickEvent;
import cc.unknown.event.impl.RenderWorldLastEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.system.Clock;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.value.impl.BoolValue;
import cc.unknown.value.impl.SliderValue;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;

@ModuleInfo(name = "BedPlates", description = "Show information about beds.", category = Category.VISUAL)
public class BedPlates extends Module {
    private final List<BlockPos> beds = new ArrayList<>();
    private final List<List<Block>> bedBlocks = new ArrayList<>();
    private BlockPos[] bed = null;
    private Clock stopWatch = new Clock();
    
    private final BoolValue firstBed = new BoolValue("RenderFirstBed", this, true);
    private final BoolValue showDistance = new BoolValue("ShowDistance", this, true);
    private final SliderValue range = new SliderValue("Range", this, 10, 2, 30);
    private final SliderValue layers = new SliderValue("Layers", this, 3, 1, 10);

    @Override
    public void onDisable() {
        this.beds.clear();
        this.bedBlocks.clear();
    }
    
    @EventLink
    public final Listener<PreTickEvent> onPreTick = event -> {
    	
    	try {
	        if (PlayerUtil.isInGame()) {
	            if (stopWatch.isFinished()) {
	            	stopWatch.setStartTime(1000);
	            	stopWatch.reset();
	            }
	            int i;
	            priorityLoop:
	            for (int n = i = (int) range.getValue(); i >= -n; --i) {
	                for (int j = -n; j <= n; ++j) {
	                    for (int k = -n; k <= n; ++k) {
	                        final BlockPos blockPos = new BlockPos(mc.thePlayer.posX + j, mc.thePlayer.posY + i, mc.thePlayer.posZ + k);
	                        final IBlockState getBlockState = mc.theWorld.getBlockState(blockPos);
	                        if (getBlockState.getBlock() == Blocks.bed && getBlockState.getValue(BlockBed.PART) == BlockBed.EnumPartType.FOOT) {
	                            if (firstBed.get()) {
	                                if (this.bed != null && isSamePos(blockPos, this.bed[0])) {
	                                    return;
	                                }
	                                this.bed = new BlockPos[]{blockPos, blockPos.offset(getBlockState.getValue(BlockBed.FACING))};
	                                return;
	                            } else {
	                                for (BlockPos pos : this.beds) {
	                                    if (isSamePos(blockPos, pos)) {
	                                        continue priorityLoop;
	                                    }
	                                }
	                                this.beds.add(blockPos);
	                                this.bedBlocks.add(new ArrayList<>());
	                            }
	                        }
	                    }
	                }
	            }
	        }
    	} catch (NullPointerException e) {
    		
    	}
    };
    
    @EventLink
    public final Listener<EntityJoinWorldEvent> onWorld = event -> {
        if (event.entity == mc.thePlayer) {
            this.beds.clear();
            this.bedBlocks.clear();
            this.bed = null;
        }
    };

    @EventLink
    public final Listener<RenderWorldLastEvent> onRender3D = event -> {
        if (PlayerUtil.isInGame()) {
            if (firstBed.get() && this.bed != null) {
                if (!(mc.theWorld.getBlockState(bed[0]).getBlock() instanceof BlockBed)) {
                    this.bed = null;
                    return;
                }
                findBed(bed[0].getX(), bed[0].getY(), bed[0].getZ(), 0);
                this.drawPlate(bed[0], 0);
            }
            if (this.beds.isEmpty()) {
                return;
            }
            Iterator<BlockPos> iterator = this.beds.iterator();
            while (iterator.hasNext()) {
                BlockPos blockPos = iterator.next();
                if (!(mc.theWorld.getBlockState(blockPos).getBlock() instanceof BlockBed)) {
                    iterator.remove();
                    continue;
                }
                findBed(blockPos.getX(), blockPos.getY(), blockPos.getZ(), this.beds.indexOf(blockPos));
                this.drawPlate(blockPos, this.beds.indexOf(blockPos));
            }
        }
    };

    private void drawPlate(BlockPos blockPos, int index) {
        float rotateX = mc.gameSettings.thirdPersonView == 2 ? -1.0F : 1.0F;
        glPushMatrix();
        glDisable(GL_DEPTH_TEST);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glTranslatef((float) (blockPos.getX() - mc.getRenderManager().viewerPosX + 0.5), (float) (blockPos.getY() - mc.getRenderManager().viewerPosY + 2), (float) (blockPos.getZ() - mc.getRenderManager().viewerPosZ + 0.5));
        glNormal3f(0.0F, 1.0F, 0.0F);
        glRotatef(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
        glRotatef(mc.getRenderManager().playerViewX, rotateX, 0.0F, 0.0F);
        glScaled(-0.01666666753590107D * Math.sqrt(mc.thePlayer.getDistance(blockPos.getX(), blockPos.getY(), blockPos.getZ())), -0.01666666753590107D * Math.sqrt(mc.thePlayer.getDistance(blockPos.getX(), blockPos.getY(), blockPos.getZ())), 0.01666666753590107D * Math.sqrt(mc.thePlayer.getDistance(blockPos.getX(), blockPos.getY(), blockPos.getZ())));
        List<Block> blocks = bedBlocks.get(index);
        RenderUtil.drawRoundedRect(Math.max(17.5, blocks.size() * 17.5) / -2, -2, Math.max(17.5, blocks.size() * 17.5) - 2.5 + 2, 26.5, 3, new Color(0, 0, 0, 90).getRGB());
        String dist = Math.round(mc.thePlayer.getDistance(blockPos.getX(), blockPos.getY(), blockPos.getZ())) + "m";
        
        if (showDistance.get())
        	mc.fontRendererObj.drawString(dist, -mc.fontRendererObj.getStringWidth(dist) / 2, 0, new Color(255, 255, 255, 255).getRGB());
        
        double offset = (blocks.size() * -17.5) / 2;
        for (Block block : blocks) {
        	String blockName = formatBlockName(block);
        	mc.getTextureManager().bindTexture(new ResourceLocation("haru/images/bedplates/" + blockName + ".png"));
        	Gui.drawModalRectWithCustomSizedTexture((int) offset, 10, 0, 0, 15, 15, 15, 15);
            offset += 17.5;
        }
        GlStateManager.disableBlend();
        glEnable(GL_DEPTH_TEST);
        glPopMatrix();
    }

    private void findBed(double x, double y, double z, int index) {
        BlockPos bedPos = new BlockPos(x, y, z);
        Block bedBlock = mc.theWorld.getBlockState(bedPos).getBlock();

        while (bedBlocks.size() <= index) {
            bedBlocks.add(new ArrayList<>());
        }
        
        bedBlocks.get(index).clear();

        while (beds.size() <= index) {
            beds.add(null);
        }
        
        beds.set(index, null);

        if (beds.contains(bedPos) || !bedBlock.equals(Blocks.bed)) {
            return;
        }

        bedBlocks.get(index).add(Blocks.bed);
        beds.set(index, bedPos);

        int[][] directions = {
                {0, 1, 0},  // Arriba
                {1, 0, 0},  // Derecha
                {-1, 0, 0}, // Izquierda
                {0, 0, 1},  // Frente
                {0, 0, -1}  // Atrás
        };

        int layersCount = (int) layers.getValue();

        for (int[] dir : directions) {
            for (int layer = 1; layer <= layersCount; layer++) {
                BlockPos currentPos = bedPos.add(dir[0] * layer, dir[1] * layer, dir[2] * layer);
                Block currentBlock = mc.theWorld.getBlockState(currentPos).getBlock();

                if (currentBlock.equals(Blocks.air)) {
                    break;
                }

                if (isValidBedBlock(currentBlock) && !bedBlocks.get(index).contains(currentBlock)) {
                    bedBlocks.get(index).add(currentBlock);
                }
            }
        }
    }

    private boolean isValidBedBlock(Block block) {
        return block.equals(Blocks.wool) || block.equals(Blocks.stained_hardened_clay) ||
                block.equals(Blocks.stained_glass) || block.equals(Blocks.glass) || block.equals(Blocks.planks) ||
                block.equals(Blocks.log) || block.equals(Blocks.log2) ||
                block.equals(Blocks.end_stone) || block.equals(Blocks.obsidian) ||
                block.equals(Blocks.water) || block.equals(Blocks.ladder);
    }

    public static boolean isSamePos(BlockPos blockPos, BlockPos blockPos2) {
        return blockPos == blockPos2 || (blockPos.getX() == blockPos2.getX() && blockPos.getY() == blockPos2.getY() && blockPos.getZ() == blockPos2.getZ());
    }

    private String formatBlockName(Block block) {
        String blockName = Block.blockRegistry.getNameForObject(block).getResourcePath();
        blockName = blockName.replace("_", " ").toLowerCase();
        if (blockName.equals("glass") || blockName.contains("stained glass")) return "glass";
        if (blockName.contains("clay")) return "clay";
        if (blockName.contains("planks") || blockName.contains("log") || blockName.contains("log2")) return "planks";
        return blockName;
    }
}