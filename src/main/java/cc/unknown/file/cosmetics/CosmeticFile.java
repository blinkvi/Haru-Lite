package cc.unknown.file.cosmetics;

import java.io.File;
import java.time.LocalDateTime;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import cc.unknown.Haru;
import cc.unknown.file.Directory;
import cc.unknown.module.impl.visual.Cosmetics;
import cc.unknown.util.client.system.LocalDateTimeStructuredAdapter;

public class CosmeticFile extends Directory {
	private final Gson gson = new GsonBuilder()
	        .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeStructuredAdapter())
	        .create();

    public CosmeticFile(String name) {
        super(name, new File(Haru.MAIN_DIR + "/cosmetics", name + ".json"));
    }

    @Override
    public void load(JsonObject object) {
        SuperCosmetic sc = gson.fromJson(object, SuperCosmetic.class);
        Cosmetics cosmetics = Haru.instance.getModuleManager().getModule(Cosmetics.class);
        if (cosmetics == null || sc == null) return;
        
        if (sc.getHalo() != null) cosmetics.haloType.setMode(sc.getHalo());
        if (sc.getHat() != null) cosmetics.hatType.setMode(sc.getHat());
        if (sc.getPet() != null) cosmetics.petType.setMode(sc.getPet());
        if (sc.getAura() != null) cosmetics.auraType.setMode(sc.getAura());
        if (sc.getWings() != null) cosmetics.wingsType.setMode(sc.getWings());
        if (sc.getAccesories() != null) cosmetics.accesoriesType.setMode(sc.getAccesories());
        if (sc.getCape() != null) cosmetics.capeType.setMode(sc.getCape());
    }

    @Override
    public JsonObject save() {
        Cosmetics cosmetics = Haru.instance.getModuleManager().getModule(Cosmetics.class);
        if (cosmetics == null) return null;

        SuperCosmetic cosmetic = new SuperCosmetic(
            Haru.getUser(),
            cosmetics.haloType.getMode(),
            cosmetics.hatType.getMode(),
            cosmetics.petType.getMode(),
            cosmetics.auraType.getMode(),
            cosmetics.wingsType.getMode(),
            cosmetics.accesoriesType.getMode(),
            cosmetics.capeType.getMode()
        );

        return gson.toJsonTree(cosmetic).getAsJsonObject();
    }
}
