package cc.unknown.util.render.font;

import java.awt.Font;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import cc.unknown.Haru;

public class FontUtil {
    private static final Map<String, FontRenderer> fontRenderers = new ConcurrentHashMap<>();

    public static void initializeFonts() {
        try {
            Enumeration<URL> resources = FontUtil.class.getClassLoader().getResources("assets/minecraft/haru/fonts");
            List<URL> resourceList = Collections.list(resources);
            resourceList.stream().map(FontUtil::safeToURI).filter(Objects::nonNull).map(File::new).filter(File::isDirectory).flatMap(file -> Arrays.stream(file.listFiles((dir, name) -> name.endsWith(".ttf") || name.endsWith(".otf")))).forEach(fontFile -> getFontRenderer(fontFile.getName(), 16));
        } catch (Exception e) {
            Haru.instance.getLogger().error("Error loading fonts: " + e.getMessage());
            e.printStackTrace();
        }

        Haru.instance.getLogger().info("Fonts initialized successfully.");
    }
    
    private static URI safeToURI(URL url) {
        try {
            return url.toURI();
        } catch (URISyntaxException e) {
            Haru.instance.getLogger().error("Invalid URL syntax: " + url);
            return null;
        }
    }

    public static FontRenderer getFontRenderer(String fontName, int size) {
        String key = fontName + size;

        return fontRenderers.computeIfAbsent(key, k -> {
            return Stream.of(loadFont(fontName, size)).filter(font -> font != null).map(font -> new FontRenderer(font, true, true)).findFirst().orElse(null);
        });
    }

    private static Font loadFont(String fontName, int size) {
        return Optional.ofNullable(FontUtil.class.getClassLoader().getResourceAsStream("assets/minecraft/haru/fonts/" + fontName)).map(stream -> {
        	try {
        		return Font.createFont(Font.TRUETYPE_FONT, stream).deriveFont((float) size);
        	} catch (Exception e) {
        		return handleFontLoadingError(fontName, e, size);
        	}
        }).orElseGet(() -> handleFontLoadingError(fontName, null, size));
    }
    
    private static Font handleFontLoadingError(String fontName, Exception e, int size) {
        if (e != null) {
            Haru.instance.getLogger().error("Error loading font: " + fontName + " - " + e.getMessage());
        } else {
            Haru.instance.getLogger().error("Font file not found: " + fontName);
        }
        return new Font("Arial", Font.PLAIN, size);
    }
    
    public static FontRenderer getConsolas(int size) {
    	return FontUtil.getFontRenderer("consolas.ttf", size);
    }
}