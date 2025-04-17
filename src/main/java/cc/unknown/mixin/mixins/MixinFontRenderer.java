package cc.unknown.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import cc.unknown.event.render.RenderTextEvent;
import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.common.MinecraftForge;

@Mixin(FontRenderer.class)
public class MixinFontRenderer {
	
    @ModifyVariable(method = "renderString", at = @At("HEAD"), ordinal = 0)
    private String renderString(String text) {
        if (text == null) {
            return text;
        }
        
        RenderTextEvent renderTextEvent = new RenderTextEvent(text);
        MinecraftForge.EVENT_BUS.post(renderTextEvent);
        text = renderTextEvent.getString();
        return text;
    }

    @ModifyVariable(method = "getStringWidth", at = @At("HEAD"), ordinal = 0)
    private String getStringWidth(String text) {
        if (text == null) {
            return text;
        }
        
        RenderTextEvent renderTextEvent = new RenderTextEvent(text);
        MinecraftForge.EVENT_BUS.post(renderTextEvent);
        text = renderTextEvent.getString();
        return text;
    }
}
