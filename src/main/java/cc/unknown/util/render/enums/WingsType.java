package cc.unknown.util.render.enums;

public enum WingsType {
	GALAXY("Galaxy", "galaxy.png"),
	SHANA("Shana", "shana.png"),
	ANGELIC("Angelic", "angelic.png"),
	DEMON("Demon", "demon.png"),
	METAL("Metal", "metal.png"),
	KUROYUKIHIME("Kuroyukihime", "kuroyukihime.png"),
	MECH("Mech", ""),
	SMALLMECH("SmallMech", ""),
	NONE("None", "");
	
    private final String name;
    private final String imagePath;

    WingsType(String name, String fileName) {
        this.name = name;
        this.imagePath = "haru/cosmes/wings/" + fileName;
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