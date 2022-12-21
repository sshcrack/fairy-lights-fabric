package me.sshcrack.fairylights.mixin;

import me.sshcrack.fairylights.FairyLightsMod;
import me.sshcrack.fairylights.util.forge.events.WorldEvent;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(ClientWorld.class)
public class ClientWorldMixin {
    @Inject(method = "<init>", at=@At("RETURN"))
    private void onWorldLoad(ClientPlayNetworkHandler networkHandler, ClientWorld.Properties properties, RegistryKey<?> registryRef, RegistryEntry<?> dimensionTypeEntry, int loadDistance, int simulationDistance, Supplier<?> profiler, WorldRenderer worldRenderer, boolean debugWorld, long seed, CallbackInfo ci) {
        FairyLightsMod.EVENT_BUS.fireEvent(new WorldEvent.Load((ClientWorld) (Object) this));
    }
}
