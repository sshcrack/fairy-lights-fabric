package me.sshcrack.fairylights.util.forge.capabilities;

import me.sshcrack.fairylights.FairyLightsMod;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class CapabilityManager {
    public static final String NBT_IDENTIFIER = FairyLightsMod.ModID + "_cap";
    public static final Map<String, Class<? extends ICapabilitySerializable<NbtCompound>>> listenerMap = new HashMap<>();

    public static Class<? extends ICapabilitySerializable<NbtCompound>> getClassFromCapId(String id) {
        return listenerMap.get(id);
    }

    public static <T extends ICapabilitySerializable<NbtCompound>> Capability<T> of(Identifier id, Class<? extends ICapabilitySerializable<NbtCompound>> capClass) {
            listenerMap.put(id.getPath(), capClass);
            return new Capability<>(id.getPath());
    }
}
