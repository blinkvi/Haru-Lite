package cc.unknown.util.render;

import cc.unknown.util.Accessor;
import cc.unknown.util.structure.vectors.Vector3d;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelRenderer;

public class WingsUtil implements Accessor {
	public static float centreOffset = 0f;
	public static float SCALE = 0.0625F;
	public static float wingScale = 0.75F;
	public static int layerId = 0;
    
    public static float getWingAngle(AbstractClientPlayer player, float maxAngle, int totalTime, int flyingTime) {
        float angle = 0F;
        
        int flapTime = totalTime;
        if (player.capabilities.isFlying & player.isAirBorne) {
            flapTime = flyingTime;
        }
        
        float deltaTime = getAnimationTime(flapTime, player.getEntityId());
        
        if (deltaTime <= 0.5F) {
            angle = Sigmoid(-4 + ((deltaTime * 2) * 8));
        } else {
            angle = 1 - Sigmoid(-4 + (((deltaTime * 2) - (1)) * 8));
        }
        angle *= maxAngle;
        
        return angle;
    }
    
    public static void setRotation(ModelRenderer model, Vector3d rotation) {
        model.rotateAngleX = (float) rotation.x;
        model.rotateAngleY = (float) rotation.y;
        model.rotateAngleZ = (float) rotation.z;
    }
    
    private static float getAnimationTime(int totalTime, int offset) {
        float time = (System.currentTimeMillis() + offset) % totalTime;
        return time / totalTime;
    }
    
    private static float Sigmoid(double value) {
        return 1.0f / (1.0f + (float) Math.exp(-value));
    }
}
