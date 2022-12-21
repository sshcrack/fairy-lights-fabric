package me.sshcrack.fairylights.mixin;

import me.sshcrack.fairylights.FairyLightsMod;
import me.sshcrack.fairylights.server.event.ModelEvent;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(BakedModelManager.class)
public class BakedModelManagerMixin {
    @Shadow private Map<Identifier, BakedModel> models;

    @Inject(method="apply(Lnet/minecraft/client/render/model/ModelLoader;Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;)V", at=@At(value = "INVOKE", target = "Lnet/minecraft/client/render/block/BlockModels;reload()V", shift = At.Shift.BEFORE))
    public void applyMixin(ModelLoader modelLoader, ResourceManager resourceManager, Profiler profiler, CallbackInfo ci) {
        ModelEvent.BakingCompleted event = new ModelEvent.BakingCompleted((BakedModelManager)(Object) this, models, modelLoader);
        FairyLightsMod.EVENT_BUS.fireEvent(event);
    }

}
