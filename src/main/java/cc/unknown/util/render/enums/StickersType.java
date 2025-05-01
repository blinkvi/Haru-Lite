package cc.unknown.util.render.enums;

public enum StickersType {
    KIWI("Kiwi", "kiwi.png", 95, 160),
    KURUMI("Kurumi", "kurumi.png", 100, 180),
    MAI("Mai", "mai.png", 95, 160),
    UZAKI("UzakiChan", "uzaki.png", 95, 160),
    HIDERI("Hideri", "hideri.png", 95, 160),
    LOONA("Loona", "loona.png", 95, 160),
    NONE("None", "", 0, 0),
    TYPH("Typh", "typh.png", 95, 160);

    private final String name;
    private final String imagePath;
    private final float width;
    private final float height;

    StickersType(String name, String fileName, int width, int height) {
        this.name = name;
        this.imagePath = "haru/stickers/" + fileName;
        this.width = width;
        this.height = height;
    }

    @Override
    public String toString() {
        return name;
    }

	public String getDisplayName() {
		return name;
	}

	public String getImagePath() {
		return imagePath;
	}

	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}
}