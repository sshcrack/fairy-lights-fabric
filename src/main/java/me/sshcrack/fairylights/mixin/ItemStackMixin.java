package me.sshcrack.fairylights.mixin;

import me.sshcrack.fairylights.FairyLightsMod;
import me.sshcrack.fairylights.util.forge.capabilities.*;
import me.sshcrack.fairylights.util.forge.util.LazyOptional;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements CapabilityHelper<ItemStack> {
    @Shadow @Nullable public abstract NbtCompound getNbt();

    private CapabilityProvider<ItemStack> provider;

    @Override
    public @NotNull CapabilityProvider<ItemStack> getProvider() {
        return provider;
    }

    @Inject(method = "<init>(Lnet/minecraft/nbt/NbtCompound;)V", at = @At(value = "RETURN"))
    private void onItemstackInit(NbtCompound nbt, CallbackInfo ci) {
        this.initializeProvider();
    }

    private void initializeProvider() {
        this.initializeProvider(this.getNbt());
    }

    private void initializeProvider(@Nullable NbtCompound nbt) {
        provider = new CapabilityProvider<>(ItemStack.class, (ItemStack) (Object) this);
        provider.gatherCapabilities();

        if(nbt == null)
            return;

        if (!nbt.contains(CapabilityManager.NBT_IDENTIFIER, NbtCompound.COMPOUND_TYPE))
            return;

        CapabilityDispatcher dispatcher = provider.getCapabilities();
        if(dispatcher != null) {
            dispatcher.deserializeNBT(nbt.getCompound(CapabilityManager.NBT_IDENTIFIER));
        }
    }

    @Inject(method = "writeNbt", at = @At("RETURN"))
    public void writeNbt(NbtCompound nbt, CallbackInfoReturnable<NbtCompound> cir) {
        if(provider == null)
            initializeProvider();

        CapabilityDispatcher dispatcher = provider.getCapabilities();
        if(dispatcher != null) {
            nbt.put(CapabilityManager.NBT_IDENTIFIER, dispatcher.serializeNBT());
        }
    }

    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap) {
        return provider.getCapability(cap);
    }
}
