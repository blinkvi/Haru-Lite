package cc.unknown.managers;

import cc.unknown.ui.drag.Drag;
import cc.unknown.ui.drag.impl.ArrayListDraggable;
import cc.unknown.ui.drag.impl.CPSDraggable;
import cc.unknown.ui.drag.impl.FPSDraggable;
import cc.unknown.ui.drag.impl.IGNDraggable;
import cc.unknown.ui.drag.impl.InventoryDraggable;
import cc.unknown.ui.drag.impl.PingDraggable;
import cc.unknown.ui.drag.impl.PlayerPositionDraggable;
import cc.unknown.ui.drag.impl.PotionStatusDraggable;
import cc.unknown.ui.drag.impl.StickersDraggable;
import cc.unknown.ui.drag.impl.WatermarkDraggable;
import cc.unknown.util.Accessor;
import cc.unknown.util.structure.list.SList;

public class DragManager extends SList<Drag> implements Accessor {

    private static final long serialVersionUID = 1L;

	public DragManager() {
        registerWidgets();
    }

    private void registerWidgets() {
        this.addAll(
            new IGNDraggable(),
            new InventoryDraggable(),
            new PotionStatusDraggable(),
            new ArrayListDraggable(),
            new WatermarkDraggable(),
            new FPSDraggable(),
            new StickersDraggable(),
            new PingDraggable(),
            new CPSDraggable(),
            new PlayerPositionDraggable()
        );
    }

    public Drag get(String name) {
        return this.stream()
                .filter(widget -> widget.name.equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public <T extends Drag> T get(Class<T> moduleClass) {
        return this.stream()
                .filter(module -> moduleClass.isInstance(module))
                .map(moduleClass::cast)
                .findFirst()
                .orElse(null);
    }
    
    public SList<Drag> getDragList() {
        return this;
    }
}