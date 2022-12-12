package me.sshcrack.fairylights.server.item;

import me.sshcrack.fairylights.server.connection.ConnectionTypes;
import net.minecraft.item.Item;

public final class GarlandConnectionItem extends ConnectionItem {
    public GarlandConnectionItem(final Item.Settings properties) {
        super(properties, ConnectionTypes.VINE_GARLAND);
    }
}
