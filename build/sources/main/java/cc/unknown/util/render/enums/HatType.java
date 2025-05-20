package cc.unknown.util.render.enums;

public enum HatType {
	TOPHAT("Top", "hat.png"),
	WITCH("Witch", "witch.png"),
	DOUG("Dimmadome", "doug.png"),
	NONE("None", "");
	
    private final String name;
    private final String imagePath;

    HatType(String name, String fileName) {
        this.name = name;
        this.imagePath = "haru/cosmes/" + fileName;
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