package cc.unknown.util.render.enums;

public enum AccesoriesType {
	BANDANA("Bandana", "bandana.png"),
	NONE("None", "");
	
    private final String name;
    private final String imagePath;

    AccesoriesType(String name, String fileName) {
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