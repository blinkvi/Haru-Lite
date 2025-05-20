package cc.unknown.util.player;

import cc.unknown.util.structure.list.SHashSet;
import net.minecraft.entity.player.EntityPlayer;

public class FriendUtil {
    private static final SHashSet<String> friends = new SHashSet<>();

    public static void addFriend(String friend) {
        friends.add(friend.toLowerCase());
    }

    public static void removeFriend(String friend) {
        friends.remove(friend.toLowerCase());
    }

    public static void addFriend(EntityPlayer entityPlayer) {
        friends.add(entityPlayer.getName().toLowerCase());
    }

    public static boolean removeFriend(EntityPlayer entityPlayer) {
        return friends.remove(entityPlayer.getName().toLowerCase());
    }

    public static boolean isFriend(String friend) {
        return friends.contains(friend.toLowerCase());
    }

    public static boolean isFriend(EntityPlayer entityPlayer) {
        return friends.contains(entityPlayer.getName().toLowerCase());
    }

    public static SHashSet<String> getFriends() {
        return friends;
    }

    public static void removeFriends() {
        friends.clear();
    }
}