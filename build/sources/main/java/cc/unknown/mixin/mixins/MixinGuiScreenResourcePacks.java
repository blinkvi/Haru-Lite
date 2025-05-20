package cc.unknown.mixin.mixins;

import java.io.IOException;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.collect.Lists;

import cc.unknown.ui.menu.SearchResourceMenu;
import cc.unknown.ui.menu.impl.TextField;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiOptionButton;
import net.minecraft.client.gui.GuiResourcePackAvailable;
import net.minecraft.client.gui.GuiResourcePackSelected;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenResourcePacks;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.ResourcePackListEntry;
import net.minecraft.client.resources.ResourcePackListEntryDefault;
import net.minecraft.client.resources.ResourcePackListEntryFound;
import net.minecraft.client.resources.ResourcePackRepository;

@Mixin(GuiScreenResourcePacks.class)
public abstract class MixinGuiScreenResourcePacks extends GuiScreen {

	@Unique
    private GuiResourcePackAvailable availablePacksClone;
    
    @Unique
    private TextField searchField;
    
    @Unique
    private SearchResourceMenu searchResourcePack = new SearchResourceMenu((GuiScreenResourcePacks) (Object) this);

    @Shadow
    private List<ResourcePackListEntry> availableResourcePacks;
    
    @Shadow
    private List<ResourcePackListEntry> selectedResourcePacks;
    
    @Shadow
    private GuiResourcePackAvailable availableResourcePacksList;
    
    @Shadow
    private GuiResourcePackSelected selectedResourcePacksList;
    
    @Shadow
    private boolean changed = false;
    
    @Overwrite
    public void initGui() {
    	String s1 = searchField == null ? "" : searchField.getText();
    	
        this.buttonList.add(new GuiOptionButton(2, this.width / 2 - 180, this.height - 48, I18n.format("resourcePack.openFolder")));
        this.buttonList.add(new GuiOptionButton(1, this.width / 2 + 4, this.height - 48, I18n.format("gui.done")));

        if (!this.changed) {
            this.availableResourcePacks = Lists.newArrayList();
            this.selectedResourcePacks = Lists.newArrayList();
            final ResourcePackRepository resourcepackrepository = this.mc.getResourcePackRepository();
            resourcepackrepository.updateRepositoryEntriesAll();
            final List<ResourcePackRepository.Entry> list = Lists.newArrayList(resourcepackrepository.getRepositoryEntriesAll());
            list.removeAll(resourcepackrepository.getRepositoryEntries());

            for (final ResourcePackRepository.Entry resourcepackrepository$entry : list) {
                this.availableResourcePacks.add(new ResourcePackListEntryFound((GuiScreenResourcePacks) (Object) this, resourcepackrepository$entry));
            }

            for (final ResourcePackRepository.Entry resourcepackrepository$entry1 : Lists.reverse(resourcepackrepository.getRepositoryEntries())) {
                this.selectedResourcePacks.add(new ResourcePackListEntryFound((GuiScreenResourcePacks) (Object) this, resourcepackrepository$entry1));
            }

            this.selectedResourcePacks.add(new ResourcePackListEntryDefault((GuiScreenResourcePacks) (Object) this));
        }

        this.availableResourcePacksList = new GuiResourcePackAvailable(this.mc, 200, this.height, this.availableResourcePacks);
        this.availableResourcePacksList.setSlotXBoundsFromLeft(this.width / 2 - 4 - 200);
        this.availableResourcePacksList.registerScrollButtons(7, 8);
        this.selectedResourcePacksList = new GuiResourcePackSelected(this.mc, 200, this.height, this.selectedResourcePacks);
        this.selectedResourcePacksList.setSlotXBoundsFromLeft(this.width / 2 + 4);
        this.selectedResourcePacksList.registerScrollButtons(7, 8);
        
        this.availablePacksClone = this.availableResourcePacksList;
        searchField = new TextField(3, fontRendererObj, width / 2 - 4 - 200, height - 24, 200, 20);
        searchField.setText(s1);
        searchResourcePack.initGui(buttonList);
    }
    
    @Overwrite
    protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.availableResourcePacksList.mouseClicked(mouseX, mouseY, mouseButton);
        this.selectedResourcePacksList.mouseClicked(mouseX, mouseY, mouseButton);
        searchField.mouseClicked(mouseX, mouseY, mouseButton);
        if (searchField != null) searchField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Overwrite
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
    	searchResourcePack.drawScreen(availableResourcePacksList, selectedResourcePacksList, mouseX, mouseY, partialTicks, fontRendererObj, width);
        searchField.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
    
    @Unique
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);

        if (this.searchField != null) {
            this.searchField.textboxKeyTyped(typedChar, keyCode);
        }
        availableResourcePacksList = searchResourcePack.updateList(searchField, availablePacksClone, availableResourcePacks, mc, width, height);
    }
    
    @Inject(method = "actionPerformed", at = @At(value = "INVOKE", target = "Ljava/util/Collections;reverse(Ljava/util/List;)V", remap = false))
    private void clearHandles(CallbackInfo ci) {
        ResourcePackRepository repository = Minecraft.getMinecraft().getResourcePackRepository();
        for (ResourcePackRepository.Entry entry : repository.getRepositoryEntries()) {
            IResourcePack current = repository.getResourcePackInstance();
            if (current == null || !entry.getResourcePackName().equals(current.getPackName())) {
                entry.closeResourcePack();
            }
        }
    }
}
