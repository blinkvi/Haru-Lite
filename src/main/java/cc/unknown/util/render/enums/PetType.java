package cc.unknown.util.render.enums;

public enum PetType {
    DOG("Dog", "dog.png"),
    WHITER("Wither", ""),
    NONE("None", "");

    private final String name;
    private final String imagePath;

    PetType(String name, String fileName) {
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