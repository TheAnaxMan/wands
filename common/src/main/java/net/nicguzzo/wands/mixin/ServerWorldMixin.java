package net.nicguzzo.wands.mixin;

import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.nicguzzo.wands.PlayerWand;
import net.nicguzzo.wands.WandsMod;


@Mixin(ServerLevel.class)
public class ServerWorldMixin {	

	@Inject(at = @At("HEAD"), method = "removePlayerImmediately")
	public void removePlayer(ServerPlayer player, Entity.RemovalReason removalReason, CallbackInfo ci)
	{
		WandsMod.LOGGER.info("player disconnected !!!");
		if (player != null) {
			WandsMod.LOGGER.info("removing wand from player: "+player.getName().getString());			
			PlayerWand.remove_player(player);
		}
	}
}