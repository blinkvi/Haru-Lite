package cc.unknown.ui.menu;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import cc.unknown.ui.menu.impl.TextField;
import cc.unknown.util.Accessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiResourcePackAvailable;
import net.minecraft.client.gui.GuiResourcePackSelected;
import net.minecraft.client.gui.GuiScreenResourcePacks;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.ResourcePackListEntry;

public class SearchResourceMenu implements Accessor {

    private GuiScreenResourcePacks parent;
    
    public SearchResourceMenu(GuiScreenResourcePacks parent) {
        this.parent = parent;
    }

    public void initGui(List<GuiButton> buttonList) {
        buttonList.forEach(b -> {
            b.setWidth(200);
            if (b.id == 2) {
                b.xPosition = parent.width / 2 - 204;
            }
        });
    }

    public void drawScreen(GuiResourcePackAvailable availableList, GuiResourcePackSelected selectedList, int mouseX, int mouseY, float partialTicks, FontRenderer fontRendererObj, int width) {
        parent.drawBackground(0);
        availableList.drawScreen(mouseX, mouseY, partialTicks);
        selectedList.drawScreen(mouseX, mouseY, partialTicks);
        parent.drawCenteredString(fontRendererObj, I18n.format("resourcePack.title"), width / 2, 16, 16777215);
    }
    
    public GuiResourcePackAvailable updateList(TextField search, GuiResourcePackAvailable clone, List<ResourcePackListEntry> available, Minecraft mc, int width, int height) {
        List<ResourcePackListEntry> entries;

        if (search == null || search.getText().isEmpty()) {
            entries = available;
        } else {
            String text = search.getText().toLowerCase();
            entries = clone.getList().stream().filter(entry -> {
                try {
                    Method method = ResourcePackListEntry.class.getDeclaredMethod("func_148312_b");
                    method.setAccessible(true);

                    String name = ChatColor.stripColor(((String) method.invoke(entry))).replaceAll("[^A-Za-z0-9 ]", "").trim().toLowerCase();

                    if (name.endsWith("zip")) {
                        name = name.substring(0, name.length() - 3);
                    }

                    return Arrays.stream(text.split(" ")).allMatch(name::contains) || name.startsWith(text) || name.equalsIgnoreCase(text);

                } catch (Exception e) {
                    e.printStackTrace();
                    return true;
                }
            }).collect(Collectors.toList());
        }

        GuiResourcePackAvailable availableList = new GuiResourcePackAvailable(mc, 200, height, entries);
        availableList.setSlotXBoundsFromLeft(width / 2 - 4 - 200);
        availableList.registerScrollButtons(7, 8);
        return availableList;
    }
    
    enum ChatColor {
        BLACK('0'),
        DARK_BLUE('1'),
        DARK_GREEN('2'),
        DARK_AQUA('3'),
        DARK_RED('4'),
        DARK_PURPLE('5'),
        GOLD('6'),
        GRAY('7'),
        DARK_GRAY('8'),
        BLUE('9'),
        GREEN('a'),
        AQUA('b'),
        RED('c'),
        LIGHT_PURPLE('d'),
        YELLOW('e'),
        WHITE('f'),
        MAGIC('k', true),
        BOLD('l', true),
        STRIKETHROUGH('m', true),
        UNDERLINE('n', true),
        ITALIC('o', true),
        RESET('r');

        public static final char COLOR_CHAR = '\u00A7';
        private final boolean isFormat;
        private final String toString;

        ChatColor(char code) {
            this(code, false);
        }

        ChatColor(char code, boolean isFormat) {
            this.isFormat = isFormat;
            toString = new String(new char[]{COLOR_CHAR, code});
        }

        public static String stripColor(final String input) {
            return input == null ? null : Pattern.compile("(?i)" + COLOR_CHAR + "[0-9A-FK-OR]").matcher(input).replaceAll("");
        }

        public static String translateAlternateColorCodes(char altColorChar, String textToTranslate) {
            return IntStream.range(0, textToTranslate.length())
                    .mapToObj(i -> {
                        char c = textToTranslate.charAt(i);
                        if (c == altColorChar && i + 1 < textToTranslate.length()) {
                            char nextChar = textToTranslate.charAt(i + 1);
                            if ("0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(nextChar) > -1) {
                                return String.valueOf(ChatColor.COLOR_CHAR) + Character.toLowerCase(nextChar);
                            }
                        }
                        return String.valueOf(c);
                    })
                    .collect(Collectors.joining());
        }

        @Override
        public String toString() {
            return toString;
        }
        
        public boolean isColor() {
            return !isFormat && this != RESET;
        }
    }
}