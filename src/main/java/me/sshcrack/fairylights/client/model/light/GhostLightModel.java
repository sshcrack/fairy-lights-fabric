package me.sshcrack.fairylights.client.model.light;

import me.sshcrack.fairylights.util.FLMth;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;

public class GhostLightModel extends ColorLightModel {
    public GhostLightModel(final ModelPart root) {
        super(root);
    }

    public static TexturedModelData createLayer() {
        final LightMeshHelper helper = LightMeshHelper.create();
        final EasyMeshBuilder littleFace = new EasyMeshBuilder("little_face", 40, 17);
        littleFace.setRotationPoint(0.0F, -1.0F, -2.25F);
        littleFace.addBox(-1.5F, -1.5F, 0, 3, 3, 0, 0);
        littleFace.xRot = FLMth.PI;
        littleFace.yRot = FLMth.PI;
        helper.lit().addChild(littleFace);
        final BulbBuilder bulb = helper.createBulb();
        final BulbBuilder bodyTop = bulb.createChild("body_top", 52, 48);
        bodyTop.setPosition(0.0F, 2.0F, 0.0F);
        bodyTop.addBox(-1.5F, 0.0F, -1.5F, 3.0F, 1.0F, 3.0F);
        bodyTop.setAngles(FLMth.PI, 0.0F, 0.0F);
        final BulbBuilder body = bulb.createChild("body", 46, 40);
        body.setPosition(0.0F, 1.0F, 0.0F);
        body.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 4.0F, 4.0F);
        body.setAngles(FLMth.PI, 0.0F, 0.0F);
        final Vec3f vec = new Vec3f(-1.0F, 0.0F, 1.0F);
        vec.normalize();
        final Quaternion droop = vec.getRadialQuaternion(-FLMth.PI / 3.0F);
        final int finCount = 8;
        for (int i = 0; i < finCount; i++) {
            final BulbBuilder fin = bulb.createChild("fin_" + i, 40, 21);
            final Quaternion q = Vec3f.POSITIVE_Y.getRadialQuaternion(i * FLMth.TAU / finCount);
            q.hamiltonProduct(droop);
            final float[] magicAngles = toEuler(q);
            final float theta = i * FLMth.TAU / finCount;
            fin.setPosition(MathHelper.cos(-theta + FLMth.PI / 4) * 1.1F, -2.75F, MathHelper.sin(-theta + FLMth.PI / 4.0F) * 1.1F);
            fin.addBox(0.0F, 0.0F, 0.0F, 2.0F, 1.0F, 2.0F, -0.1F);
            fin.setAngles(magicAngles[0], magicAngles[1], magicAngles[2]);
        }
        return helper.build();
    }
}
