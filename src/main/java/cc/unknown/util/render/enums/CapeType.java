package cc.unknown.util.render.enums;

public enum CapeType {
    CANADA("Canada", "flag", "canada.png"),
    FRANCE("France", "flag", "france.png"),
    GERMANY("Germany", "flag", "germany.png"),
    INDIA("India", "flag", "india.png"),
    INDONESIA("Indonesia", "flag", "indonesia.png"),
    ITALY("Italy", "flag", "italy.png"),
    JAPAN("Japan", "flag", "japan.png"),
    KOREAN("Korean", "flag", "korean.png"),
    UK("UnitedKingdom", "flag", "uk.png"),
    US("UnitedStates", "flag", "us.png"),

    ARCADE("Arcade", "custom", "arcade.png"),
    BOOST("Boost", "custom", "boost.png"),
    DARK("Dark", "custom", "dark.png"),
    EYES("Eyes", "custom", "eyes.png"),
    FLAME("Flame", "custom", "flame.png"),
    KOCHO("Kocho", "custom", "kocho.png"),
    ZERO("ZeroTwo", "custom", "zero.png"),

    MINECON2011("Minecon2011", "minecon", "2011.png"),
    MINECON2012("Minecon2012", "minecon", "2012.png"),
    MINECON2013("Minecon2013", "minecon", "2013.png"),
    MINECON2015("Minecon2015", "minecon", "2015.png"),
    MINECON2016("Minecon2016", "minecon", "2016.png"),

    NONE("None", "", "");

    private final String name;
    private final String path;

    CapeType(String name, String path, String fileName) {
        this.name = name;
        this.path = path.isEmpty() ? "" : "haru/cape/" + path + "/" + fileName;
    }

    public String getName() {
		return name;
	}

	public String getPath() {
		return path;
	}

	@Override
    public String toString() {
        return name;
    }
}