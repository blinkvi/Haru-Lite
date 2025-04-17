package cc.unknown.util.render.enums;

public enum CosmeticType {
    DIMMADOME("Dimmadome"),
    TOP("Top"),
    WHITER("Whiter"),
    DOG("Dog"),
    BANDANA("Bandana"),
    HALO("Halo"),
    GALAXY("Galaxy"),
    CRYSTAL("Crystal"),
    WITCH("Witch"),
    BLAZE("Blaze"),
    CREEPER("Creeper"),
    ENCHANTING("Enchanting"),
    ORBIT("Orbit");

    private final String name;

	private CosmeticType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
