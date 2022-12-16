package me.sshcrack.fairylights.mixin;

import me.sshcrack.fairylights.FairyLightsMod;
import me.sshcrack.fairylights.util.forge.capabilities.Capability;
import me.sshcrack.fairylights.util.forge.capabilities.CapabilityManager;
import me.sshcrack.fairylights.util.forge.capabilities.ICapabilityProvider;
import me.sshcrack.fairylights.util.forge.capabilities.ICapabilitySerializable;
import me.sshcrack.fairylights.util.forge.util.LazyOptional;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.stream.Collectors;

@Mixin(BlockEntity.class)
public abstract class BlockEntityMixin implements ICapabilityProvider {
    private NbtCompound capNbt = null;

    @Inject(method = "readNbt", at = @At(value = "HEAD"))
    public void readNbt(NbtCompound nbt, CallbackInfo ci) {
        if (!nbt.contains(CapabilityManager.NBT_IDENTIFIER, NbtCompound.COMPOUND_TYPE))
            return;

        this.capNbt = nbt.getCompound(CapabilityManager.NBT_IDENTIFIER);
    }

    @Inject(method = "writeNbt", at = @At("HEAD"))
    public void writeNbt(NbtCompound nbt, CallbackInfo ci) {
        if(this.capNbt != null)
            nbt.put(CapabilityManager.NBT_IDENTIFIER, this.capNbt);
    }
}
