package cc.unknown.util.render.font;

import java.awt.Font;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cc.unknown.Haru;

public class FontUtil {
    private static final Map<String, FontRenderer> fontRenderers = new ConcurrentHashMap<>();
    private static final Map<String, Font> customFonts = new ConcurrentHashMap<>();

    public static void initializeFonts() {
        try {
            Enumeration<URL> resources = FontUtil.class.getClassLoader().getResources("assets/minecraft/haru/font");

            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                File file = new File(resource.toURI());

                if (file.isDirectory()) {
                    File[] fontFiles = file.listFiles((dir, name) -> name.endsWith(".ttf") || name.endsWith(".otf"));

                    if (fontFiles != null) {
                        for (File fontFile : fontFiles) {
                            getFontRenderer(fontFile.getName(), 16);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Haru.instance.getLogger().error("Error loading fonts: " + e.getMessage());
            e.printStackTrace();
        }

        Haru.instance.getLogger().info("Fonts initialized successfully.");
    }

    public static FontRenderer getFontRenderer(String fontName, int size) {
        String key = fontName + size;

        return fontRenderers.computeIfAbsent(key, k -> {
            Font font = loadFont(fontName, size);
            return (font != null) ? new FontRenderer(font, true, true) : null;
        });
    }

    public static Font getCustomFont(String fontName, int size) {
        String key = fontName + size;

        return customFonts.computeIfAbsent(key, k -> {
            Font font = loadFont(fontName, size);
            return (font != null) ? font.deriveFont((float) size) : new Font("SansSerif", Font.PLAIN, size);
        });
    }

    private static Font loadFont(String fontName, int size) {
        try (InputStream fontStream = FontUtil.class.getClassLoader().getResourceAsStream("assets/minecraft/haru/font/" + fontName)) {
            if (fontStream == null) {
                Haru.instance.getLogger().error("Font file not found: " + fontName);
                return new Font("Arial", Font.PLAIN, size);
            }
            return Font.createFont(Font.TRUETYPE_FONT, fontStream).deriveFont((float) size);
        } catch (Exception e) {
            Haru.instance.getLogger().error("Error loading font: " + fontName + " - " + e.getMessage());
            return new Font("Arial", Font.PLAIN, size);
        }
    }
}
