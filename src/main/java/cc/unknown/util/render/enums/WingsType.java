package cc.unknown.util.render.enums;

public enum WingsType {
	CRYSTAL("Crystal", "crystal.png"),
	GALAXY("Galaxy", "galaxy.png"),
	NONE("None", "");
	
    private final String name;
    private final String imagePath;

    WingsType(String name, String fileName) {
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