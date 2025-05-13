package cc.unknown.module.impl.visual;

import java.util.Arrays;

import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.util.render.font.FontUtil;
import cc.unknown.value.impl.Bool;
import cc.unknown.value.impl.Mode;
import cc.unknown.value.impl.MultiBool;
import cc.unknown.value.impl.Slider;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.client.event.RenderLivingEvent.Specials.Pre;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleInfo(name = "NameTags", description = "Displays customizable nametags above players.", category = Category.VISUAL)
public final class NameTags extends Module {
	
	private final Mode mode = new Mode("Font", this, "Default", "Smooth", "Normal", "Default");
	private final Slider distance = new Slider("Distance", this, 2.4f, 1, 7, 0.1f);
	private final Slider scale = new Slider("Scale", this, 2.4f, 0.1f, 10, 0.1f);
	private final Bool shadow = new Bool("Shadow", this, true, () -> mode.is("Default"));
	
	public final MultiBool armor = new MultiBool("Armor", this, Arrays.asList(
			new Bool("Enchants", true),
			new Bool("Durability", true),
			new Bool("StackSize", true)));
	
	public final MultiBool conditionals = new MultiBool("Conditionals", this, Arrays.asList(
			new Bool("ShowHealth", false),
			new Bool("OnlyRenderName", false),
			new Bool("ShowInvisibles", true)));
	    
    @SubscribeEvent
    public void onRenderLiving(Pre<? extends EntityLivingBase> event) {
        if (event.entity instanceof EntityPlayer && event.entity.deathTime == 0) {        
            EntityPlayer player = (EntityPlayer) event.entity;
            event.setCanceled(true);
            String name;

            if (!conditionals.isEnabled("ShowInvisibles") && player.isInvisible()) {
                return;
            }
            
            if (conditionals.isEnabled("OnlyRenderName")) {
            	name = player.getName();
            } else {
            	name = player.getDisplayName().getFormattedText();
            }
            
            if (conditionals.isEnabled("ShowHealth")) {
                name = name + " " + PlayerUtil.getHealthStr(player) + " HP";
            }
            
            renderNewTag(event, player, name);
        }
    }
	
	private void renderNewTag(Pre<? extends EntityLivingBase> event, EntityPlayer player, String name) {
	    if (PlayerUtil.unusedNames(player) && player.getName().contains("AstralMC-CTM")) {
	        return;
	    }
	    
	    double scaleRatio;
	    float scale = 0.02666667F;

	    if (player == mc.thePlayer) {
	        scaleRatio = 1.0D;
	    } else {
	        scaleRatio = (double) (getSize(player) / 10.0F * this.scale.getValue()) * 1.5D;
	    }

	    GlStateManager.pushMatrix();
	    GlStateManager.translate(event.x, event.y + player.height + 0.5F, event.z);
	    GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
	    GlStateManager.rotate(mc.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
	    GlStateManager.scale(-scale * scaleRatio, -scale * scaleRatio, scale * scaleRatio);

	    if (armor.canDisplay()) {
	        renderArmor(player);
	    }

	    if (player.isSneaking()) {
	        GlStateManager.translate(0.0F, 9.374999F, 0.0F);
	    }

	    GlStateManager.disableLighting();
	    GlStateManager.depthMask(false);
	    GlStateManager.disableDepth();
	    GlStateManager.enableBlend();
	    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);

	    int fontSize = 20;
	    String fontName = "consolas.ttf";
	    double nameWidth = FontUtil.getFontRenderer(fontName, fontSize).getStringWidth(name);

	    switch (mode.getMode()) {
	    case "Default":
	    	mc.fontRendererObj.drawString(name, (float) (-mc.fontRendererObj.getStringWidth(name) / 2), 0, -1, shadow.get());
	    	break;
	    case "Smooth":
	    	FontUtil.getFontRenderer(fontName, fontSize).drawSmoothString(name, (float) (-nameWidth / 2), 0, -1);
	    	break;
	    case "Normal":
	    	FontUtil.getFontRenderer(fontName, fontSize).drawString(name, (float) (-nameWidth / 2), 0, -1);
	    	break;
	    }

	    //GlStateManager.disableBlend();
	    GlStateManager.enableDepth();
	    GlStateManager.depthMask(true);
	    GlStateManager.enableLighting();
	    GlStateManager.popMatrix();
	}
	
	private void renderItemStack(final ItemStack stack, final int x, final int y) {
	    GlStateManager.pushMatrix();
	    GlStateManager.enableBlend();
	    GlStateManager.enableAlpha();
	    //fixGlintShit();
	    mc.getRenderItem().zLevel = -150.0F;

	    GlStateManager.disableDepth();
	    mc.getRenderItem().renderItemAndEffectIntoGUI(stack, x, y);
	    if (armor.isEnabled("StackSize") && !(stack.getItem() instanceof ItemSword) && !(stack.getItem() instanceof ItemBow) && !(stack.getItem() instanceof ItemTool) && !(stack.getItem() instanceof ItemArmor)) {
	    	mc.getRenderItem().renderItemOverlays(mc.fontRendererObj, stack, x, y);
	    }
	    mc.getRenderItem().zLevel = 0.0F;

	    renderEnchantText(stack, x, y);

	    GlStateManager.popMatrix();
	}
	
	private void renderArmor(EntityPlayer e) {
		int pos = 0;
		for (ItemStack is : e.inventory.armorInventory) {
			if (is != null) {
				pos -= 8;
			}
		}
		if (e.getHeldItem() != null) {
			pos -= 8;
			ItemStack item = e.getHeldItem().copy();
			if (item.hasEffect() && (item.getItem() instanceof ItemTool || item.getItem() instanceof ItemArmor)) {
				item.stackSize = 1;
			}
			renderItemStack(item, pos, -20);
			pos += 16;
		}
		for (int i = 3; i >= 0; --i) {
			ItemStack stack = e.inventory.armorInventory[i];
			if (stack != null) {
				renderItemStack(stack, pos, -20);
				pos += 16;
			}
		}
	}
	
	private void renderEnchantText(ItemStack stack, int x, int y) {
        GlStateManager.pushMatrix();
        GlStateManager.disableDepth();
		int enchantY = y - 24;
		
	    if (armor.isEnabled("Durability") && stack.getItem() instanceof ItemArmor) {
	        int remainingDurability = stack.getMaxDamage() - stack.getItemDamage();
	        String durabilityText = String.valueOf(remainingDurability);

	        int textWidth = (int) FontUtil.getFontRenderer("consolas.ttf", 20).getStringWidth(durabilityText);
	        float drawX = (float) (x * 2 - textWidth / 2) + 20;
	        float drawY = (float) (y - 12);

	        FontUtil.getFontRenderer("consolas.ttf", 20).drawStringWithShadow(durabilityText, drawX, drawY, 0xFFFFFF);
	    }
		
	    if (armor.isEnabled("Enchants") && stack.getEnchantmentTagList() != null && stack.getEnchantmentTagList().tagCount() < 6) {
	        if (stack.getItem() instanceof ItemTool || stack.getItem() instanceof ItemSword || stack.getItem() instanceof ItemBow || stack.getItem() instanceof ItemArmor) {
	            NBTTagList nbttaglist = stack.getEnchantmentTagList();
	            for (int i = 0; i < nbttaglist.tagCount(); ++i) {
	                int id = nbttaglist.getCompoundTagAt(i).getShort("id");
	                int lvl = nbttaglist.getCompoundTagAt(i).getShort("lvl");
	                if (lvl > 0) {
	                    String abbreviated = getEnchantmentAbbreviated(id);
	                    String text = abbreviated + lvl;

	                    int textWidth = (int) FontUtil.getFontRenderer("consolas.ttf", 20).getStringWidth(text);
	                    float drawX = (float) (x * 2 - textWidth / 2) + 20;
	                    FontUtil.getFontRenderer("consolas.ttf", 20).drawStringWithShadow(text, drawX, (float) enchantY, -1);
	                    enchantY += 8;
	                }
	            }
	        }
	    }
	    
        GlStateManager.enableDepth();
        GlStateManager.popMatrix();
	}
	
	private float getSize(EntityPlayer player) {
		return Math.max(mc.thePlayer.getDistanceToEntity(player) / 4.0F, distance.getValue());
	}

	private String getEnchantmentAbbreviated(int id) {
		switch (id) {
		case 0:
			return "pt";   // Protection
		case 1:
			return "frp";   // Fire Protection
		case 2:
			return "ff";    // Feather Falling
		case 3:
			return "blp";   // Blast Protection
		case 4:
			return "prp";   // Projectile Protection
		case 5:
			return "thr";   // Thorns
		case 6:
			return "res";   // Respiration
		case 7:
			return "aa";    // Aqua Affinity
		case 16:
			return "sh";   // Sharpness
		case 17:
			return "smt";   // Smite
		case 18:
			return "ban";   // Bane of Arthropods
		case 19:
			return "kb";    // Knockback
		case 20:
			return "fa";    // Fire Aspect
		case 21:
			return "lot";  // Looting
		case 32:
			return "eff";   // Efficiency
		case 33:
			return "sil";   // Silk Touch
		case 34:
			return "ub";   // Unbreaking
		case 35:
			return "for";   // Fortune
		case 48:
			return "pow";   // Power
		case 49:
			return "pun";   // Punch
		case 50:
			return "flm";   // Flame
		case 51:
			return "inf";   // Infinity
		default:
			return null;
		}
	}
	
	/*private static void fixGlintShit() {
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.disableTexture2D();
        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
    }*/
}