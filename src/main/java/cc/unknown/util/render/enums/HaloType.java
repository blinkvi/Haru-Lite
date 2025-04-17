package cc.unknown.util.render.enums;

import java.util.function.Consumer;

import cc.unknown.util.render.HaloRenderer;
import net.minecraftforge.client.event.RenderWorldLastEvent;

public enum HaloType {
    SHIROKO("Shiroko", HaloRenderer::drawShirokoHalo),
    HOSHINO("Hoshino", HaloRenderer::drawHoshinoHalo),
    ARIS("Aris", HaloRenderer::drawArisHalo),
    NATSU("Natsu", HaloRenderer::drawNatsuHalo),
    REISA("Reisa", HaloRenderer::drawReisaHalo),
    NONE("None", null);

    private final String name;
    private final Consumer<RenderWorldLastEvent> renderAction;

    HaloType(String name, Consumer<RenderWorldLastEvent> renderAction) {
        this.name = name;
        this.renderAction = renderAction;
    }

    public String getName() {
        return name;
    }

    public void render(RenderWorldLastEvent event) {
        if (renderAction != null) {
            renderAction.accept(event);
        }
    }
    
	@Override
    public String toString() {
        return name;
    }
}