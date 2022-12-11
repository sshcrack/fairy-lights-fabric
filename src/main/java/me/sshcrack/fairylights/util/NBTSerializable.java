package me.sshcrack.fairylights.util;

import net.minecraft.nbt.NbtCompound;

public interface NBTSerializable {
    NbtCompound serialize();

    void deserialize(NbtCompound compound);
}
