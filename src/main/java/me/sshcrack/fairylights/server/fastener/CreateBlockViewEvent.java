package me.sshcrack.fairylights.server.fastener;

import me.sshcrack.fairylights.util.forge.events.Event;

public class CreateBlockViewEvent extends Event {
    private BlockView view;

    public CreateBlockViewEvent(final BlockView view) {
        this.view = view;
    }

    public BlockView getView() {
        return this.view;
    }

    public void setView(final BlockView view) {
        this.view = view;
    }
}
