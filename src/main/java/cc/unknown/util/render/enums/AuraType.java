package cc.unknown.util.render.enums;

public enum AuraType {
    BLAZE("Blaze", "blaze.png"),
    CREEPER("Creeper", "creeper_armor.png"),
    ENCHANTING("Enchanting", "enchantGlint.png"),
    ORBIT("Orbit", ""),
    NONE("None", "");

    private final String name;
    private final String imagePath;

    AuraType(String name, String fileName) {
        this.name = name;
        if (fileName.isEmpty()) {
            if (name.equals("Orbit")) {
                this.imagePath = "textures/blocks/obsidian.png";
            } else {
                this.imagePath = "";
            }
        } else {
            this.imagePath = "haru/cosmes/" + fileName;
        }
    }
	
    public String getName() {
		return name;
	}

	public String getImagePath() {
		return imagePath;
	}

	@Override
    public String toString() {
        return name;
    }
}