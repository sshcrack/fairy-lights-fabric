package me.sshcrack.fairylights.server.net_fabric;

import me.sshcrack.fairylights.server.net.Message;
import oshi.util.tuples.Triplet;

import java.util.function.Supplier;

public class ClientTriplet<K, T extends Message> extends Triplet<String, Supplier<T>, Supplier<GeneralClientHandler<K>>> {
    /**
     * Create a triplet and store three objects.
     *
     * @param prefix         Prefix of the message to registewr
     * @param msgSupplier Supplies the message to decode
     * @param generalSupplier General Supplier to Handler
     */
    public ClientTriplet(String prefix, Supplier<T> msgSupplier, Supplier<GeneralClientHandler<K>> generalSupplier) {
        super(prefix, msgSupplier, generalSupplier);
    }
}
