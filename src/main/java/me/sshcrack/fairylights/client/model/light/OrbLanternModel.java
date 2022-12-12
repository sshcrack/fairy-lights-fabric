package me.sshcrack.fairylights.client.model.light;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.TexturedModelData;

public class OrbLanternModel extends ColorLightModel {
    public OrbLanternModel(final ModelPart root) {
        super(root);
    }

    public static TexturedModelData createLayer() {
        final LightMeshHelper helper = LightModel.LightMeshHelper.create();
        helper.unlit().setTextureOffset(30, 6);
        helper.unlit().addBox(-1, -0.5F, -1, 2, 2, 2, -0.05F);
        final LightModel.BulbBuilder bulb = helper.createBulb();
        bulb.setUV(0, 27);
        bulb.addBox(-3.5F, -7.5F, -3.5F, 7, 7, 7);
        return helper.build();
    }
}
