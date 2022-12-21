package me.sshcrack.fairylights.mixin;

import me.sshcrack.fairylights.client.ClientEventHandler;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
	@Inject(at = @At("HEAD"), method = "updateTargetedEntity")
	private void init(CallbackInfo info) {
		ClientEventHandler.updateHitConnection();
	}
}
