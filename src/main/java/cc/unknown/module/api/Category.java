package cc.unknown.module.api;

public enum Category {
	COMBAT("Combat"),
    MOVE("Move"),
    UTILITY("Utility"),
    VISUAL("Visual"),
    SETTINGS("Settings");

    private final String name;

	private Category(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}