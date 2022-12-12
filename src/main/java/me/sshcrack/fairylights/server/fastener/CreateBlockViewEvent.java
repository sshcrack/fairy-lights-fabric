package me.sshcrack.fairylights.server.fastener;

import net.minecraftforge.eventbus.api.Event;

public class CreateBlockViewEvent extends Event {
    private me.paulf.fairylights.server.fastener.BlockView view;

    public CreateBlockViewEvent(final me.paulf.fairylights.server.fastener.BlockView view) {
        this.view = view;
    }

    public me.paulf.fairylights.server.fastener.BlockView getView() {
        return this.view;
    }

    public void setView(final me.paulf.fairylights.server.fastener.BlockView view) {
        this.view = view;
    }
}
