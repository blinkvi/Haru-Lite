package cc.unknown.handlers;

import java.net.URI;
import java.util.Arrays;
import java.util.regex.Pattern;

import cc.unknown.event.netty.InboundEvent;
import cc.unknown.util.Accessor;
import cc.unknown.util.client.network.PacketUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C19PacketResourcePackStatus;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S13PacketDestroyEntities;
import net.minecraft.network.play.server.S19PacketEntityStatus;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.network.play.server.S2APacketParticles;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.network.play.server.S48PacketResourcePackSend;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PacketHandler implements Accessor {

    private final Pattern regex = Pattern.compile(".*\\$\\{[^}]*}.*");
    private int particles;

    @SubscribeEvent
    public void onInbound(InboundEvent event) {
        Packet<?> packet = event.packet;
        String name = packet.getClass().getSimpleName();

        switch (name) {
            case "S2BPacketChangeGameState":
                handleGameState((S2BPacketChangeGameState) packet, event);
                break;

            case "S13PacketDestroyEntities":
                cancelIf(Arrays.stream(((S13PacketDestroyEntities) packet).getEntityIDs()).anyMatch(id -> id == mc.thePlayer.getEntityId()), event);
                break;

            case "S27PacketExplosion":
                handleExplosion((S27PacketExplosion) packet, event);
                break;

            case "S19PacketEntityStatus":
                handleEntityStatus((S19PacketEntityStatus) packet, event);
                break;

            case "S2APacketParticles":
                handleParticles((S2APacketParticles) packet, event);
                break;

            case "S12PacketEntityVelocity":
                handleVelocity((S12PacketEntityVelocity) packet, event);
                break;

            case "S48PacketResourcePackSend":
                handleResourcePack((S48PacketResourcePackSend) packet, event);
                break;

            case "S08PacketPlayerPosLook":
                handlePositionLook((S08PacketPlayerPosLook) packet, event);
                break;

            case "S29PacketSoundEffect":
                cancelIf(regex.matcher(((S29PacketSoundEffect) packet).getSoundName()).matches(), event);
                break;

            case "S02PacketChat":
                IChatComponent component = ((S02PacketChat) packet).getChatComponent();
                cancelIf(regex.matcher(component.getUnformattedText()).matches() || regex.matcher(component.getFormattedText()).matches(), event);
                break;
        }
    }

    private void handleGameState(S2BPacketChangeGameState packet, InboundEvent event) {
        float value = packet.func_149137_d();
        int state = packet.getGameState();
        cancelIf((state == 5 && value == 0) || (state == 7 || state == 8) && value >= 100, event);
    }

    private void handleExplosion(S27PacketExplosion packet, InboundEvent event) {
        boolean high = packet.func_149149_c() >= Byte.MAX_VALUE || packet.func_149144_d() >= Byte.MAX_VALUE || packet.func_149147_e() >= Byte.MAX_VALUE;
        cancelIf(high || packet.getStrength() == 0, event);
    }

    private void handleEntityStatus(S19PacketEntityStatus packet, InboundEvent event) {
        cancelIf(packet.getOpCode() == 3 && packet.getEntity(mc.theWorld) == mc.thePlayer, event);
    }

    private void handleParticles(S2APacketParticles packet, InboundEvent event) {
        particles += packet.getParticleCount();
        particles -= 6;
        particles = Math.min(particles, 150);
        boolean invalid = particles > 100 || packet.getParticleCount() < 1 || Math.abs(packet.getParticleCount()) > 20 || packet.getParticleSpeed() < 0 || packet.getParticleSpeed() > 1000;
        cancelIf(invalid, event);
    }

    private void handleVelocity(S12PacketEntityVelocity packet, InboundEvent event) {
        boolean big = Math.abs(packet.getMotionX()) > 31200 || Math.abs(packet.getMotionY()) > 31200 || Math.abs(packet.getMotionZ()) > 31200;
        cancelIf(big, event);
    }

    private void handleResourcePack(S48PacketResourcePackSend packet, InboundEvent event) {
        String url = packet.getURL().toLowerCase();
        cancelIf(url.startsWith("level://") && check(packet.getURL(), packet.getHash()), event);
    }

    private void handlePositionLook(S08PacketPlayerPosLook packet, InboundEvent event) {
        boolean far = Math.abs(packet.getX()) > 1E+9 || Math.abs(packet.getY()) > 1E+9 || Math.abs(packet.getZ()) > 1E+9;
        cancelIf(far, event);
    }

    private void cancelIf(boolean condition, InboundEvent event) {
    	if (condition) {
    		event.setCanceled(true);
    	}
    }

    private boolean check(String url, final String hash) {
        try {
            final URI uri = new URI(url);
            final String scheme = uri.getScheme();
            final boolean isLevel = "level".equals(scheme);

            return (!("http".equals(scheme) || "https".equals(scheme) || isLevel)) || (isLevel && (url.contains("..") || !url.endsWith("/resources.zip"))) ? throwFailed(hash) : false;

        } catch (Exception e) {
            PacketUtil.sendNoEvent(new C19PacketResourcePackStatus(hash, C19PacketResourcePackStatus.Action.FAILED_DOWNLOAD));
            return true;
        }
    }

    private boolean throwFailed(String hash) {
        PacketUtil.sendNoEvent(new C19PacketResourcePackStatus(hash, C19PacketResourcePackStatus.Action.FAILED_DOWNLOAD));
        return true;
    }
}