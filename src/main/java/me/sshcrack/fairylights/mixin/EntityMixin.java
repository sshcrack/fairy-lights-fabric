package me.sshcrack.fairylights.mixin;

import me.sshcrack.fairylights.util.forge.capabilities.*;
import me.sshcrack.fairylights.util.forge.util.LazyOptional;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin implements CapabilityHelper<Entity> {
    private CapabilityProvider<Entity> provider;

    @Override
    public @NotNull CapabilityProvider<Entity> getProvider() {
        return provider;
    }

    @Inject(method="<init>", at=@At(value="RETURN"))
    public void onConstruct(EntityType<?> type, World world, CallbackInfo ci) {
        this.provider = new CapabilityProvider<>(Entity.class, (Entity) (Object) this);
        provider.gatherCapabilities();
    }

    @Inject(method = "readNbt", at = @At(value = "HEAD"))
    public void readNbt(NbtCompound nbt, CallbackInfo ci) {
        if (!nbt.contains(CapabilityManager.NBT_IDENTIFIER, NbtCompound.COMPOUND_TYPE))
            return;

        CapabilityDispatcher dispatcher = provider.getCapabilities();
        if(dispatcher != null) {
            dispatcher.deserializeNBT(nbt.getCompound(CapabilityManager.NBT_IDENTIFIER));
        }
    }

    @Inject(method = "writeNbt", at = @At("RETURN"))
    public void writeNbt(NbtCompound nbt, CallbackInfoReturnable<NbtCompound> cir) {
        CapabilityDispatcher dispatcher = provider.getCapabilities();
        if(dispatcher != null)
            nbt.put(CapabilityManager.NBT_IDENTIFIER, dispatcher.serializeNBT());
    }

    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap) {
        return provider.getCapability(cap);
    }

}
