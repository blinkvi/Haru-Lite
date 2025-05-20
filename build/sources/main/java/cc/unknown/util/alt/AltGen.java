package cc.unknown.util.alt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import cc.unknown.util.Accessor;
import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.util.ResourceLocation;

@SuppressWarnings("unchecked")
public class AltGen implements Accessor {

	public static String[] retrieve() {
	    try (InputStream stream = mc.getResourceManager().getResource(new ResourceLocation("haru/alt/usernames.txt")).getInputStream();
	         BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {

	        return reader.lines().toArray(String[]::new);

	    } catch (IOException ex) {
	        ex.printStackTrace();
	        return null;
	    }
	}
    
    public static String generate() {
        return generate(1)[0];
    }

    public static String[] generate(int amount) {
        String[] usernames = retrieve();
        if (usernames == null) {
            return null;
        }

        List<String> acceptableUsernames = Arrays.stream(usernames)
                .filter(username -> username.length() >= 3 && username.length() <= 6)
                .collect(Collectors.toList());

        if (acceptableUsernames.isEmpty()) {
            return null;
        }

        int size = acceptableUsernames.size();
        return IntStream.range(0, amount)
                .mapToObj(i -> {
                    String prefix = acceptableUsernames.get(ThreadLocalRandom.current().nextInt(size));
                    String suffix = acceptableUsernames.get(ThreadLocalRandom.current().nextInt(size));
                    return applyPattern(applyPattern(prefix, suffix));
                })
                .toArray(String[]::new);
    }

    private static String applyPattern(String prefix, String suffix) {
		Supplier<String>[] patterns = new Supplier[] {
            () -> prefix + "_" + suffix,
            () -> prefix + suffix.substring(0, 2) + (int) (Math.random() * 100),
            () -> {
                int index = (int) (Math.random() * Math.min(prefix.length(), suffix.length()));
                return prefix.substring(0, index) + "_" + suffix.substring(index);
            },
            () -> {
                StringBuilder merge = new StringBuilder(prefix).append(suffix);
                int uIndex = (int) (Math.random() * merge.length());
                int nIndex = (int) (Math.random() * merge.length());
                merge.insert(uIndex, "_");
                merge.insert(nIndex, (int) (Math.random() * 100));
                return merge.toString();
            }
        };

        int pattern = (int) (Math.random() * patterns.length);
        return patterns[pattern].get();
    }


    private static String applyPattern(String username) {
        final double[] numberChance = {0.125};
        final double upperChance = 0.25;
        char[] chars = username.toCharArray();

        String result = IntStream.range(0, chars.length)
            .mapToObj(i -> {
                char c = chars[i];
                boolean isStartOrAfterSpecial = i == 0 || chars[i - 1] == '_' || Character.isDigit(chars[i - 1]);

                if (isStartOrAfterSpecial && Character.isLetter(c) && Math.random() < upperChance) {
                    return Character.toUpperCase(c);
                }

                char lower = Character.toLowerCase(c);
                char replacement = getReplacement(lower);
                if (replacement != lower && Math.random() < numberChance[0]) {
                    numberChance[0] *= 0.5;
                    return replacement;
                }

                return c;
            })
            .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
            .toString();

        return result;
    }

    private static char getReplacement(char c) {
        if (c == 'a') {
            return '4';
        } else if (c == 'e') {
            return '3';
        } else if (c == 'i') {
            return '1';
        } else if (c == 'o') {
            return '0';
        } else if (c == 't') {
            return '7';
        } else {
            return c;
        }
    }
    
    public static boolean validate(String name) {
        return validate(name, 3, 16);
    }

    public static boolean validate(String name, int min, int max) {
        return name.length() >= min && name.length() <= max && name.chars().allMatch(c -> Character.isLetterOrDigit(c) || c == '_');
    }
}