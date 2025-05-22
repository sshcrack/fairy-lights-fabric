package me.sshcrack.fairylights.mixin;

import com.google.common.collect.Lists;
import me.sshcrack.fairylights.FairyLightsMod;
import me.sshcrack.fairylights.util.forge.events.AddReloadListenerEvent;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.server.DataPackContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.List;

@Mixin(DataPackContents.class)
public class ReloadListenerMixin {

    @Redirect(method="reload", at=@At(value="INVOKE", target = "Lnet/minecraft/server/DataPackContents;getContents()Ljava/util/List;"))
    private static List<ResourceReloader> onReload(DataPackContents instance) {

        AddReloadListenerEvent event = new AddReloadListenerEvent(instance);
        FairyLightsMod.EVENT_BUS.fireEvent(event);

        ArrayList<ResourceReloader> listeners = Lists.newArrayList(instance.getContents());
        listeners.addAll(event.getListeners());

        return listeners;
    }
}
