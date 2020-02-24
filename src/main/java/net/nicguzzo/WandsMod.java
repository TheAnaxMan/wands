package net.nicguzzo;

import java.util.Random;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.UnbreakingEnchantment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import java.util.function.Consumer;

public class WandsMod implements ModInitializer {

	public static final Identifier WAND_PACKET_ID = new Identifier("wands", "wand");

	// public static final WandItem NETHERITE_WAND_ITEM = new WandItem(31,2031);
	public static final WandItem DIAMOND_WAND_ITEM = new WandItem(27, 1561);
	public static final WandItem IRON_WAND_ITEM = new WandItem(9, 250);
	public static final WandItem STONE_WAND_ITEM = new WandItem(5, 131);

	@Override
	public void onInitialize() {

		Registry.register(Registry.ITEM, new Identifier("wands", "diamond_wand"), DIAMOND_WAND_ITEM);
		Registry.register(Registry.ITEM, new Identifier("wands", "iron_wand"), IRON_WAND_ITEM);
		Registry.register(Registry.ITEM, new Identifier("wands", "stone_wand"), STONE_WAND_ITEM);

		ServerSidePacketRegistry.INSTANCE.register(WAND_PACKET_ID, (packetContext, attachedData) -> {
			// Get the BlockPos we put earlier in the IO thread
			BlockPos pos0 = attachedData.readBlockPos();
			BlockPos pos1 = attachedData.readBlockPos();
			// BlockState state = attachedData.read
			packetContext.getTaskQueue().execute(() -> {
				// Execute on the main thread
				// ALWAYS validate that the information received is valid in a C2S packet!
				if (World.isValid(pos0) && World.isValid(pos1)) {
					PlayerEntity player = packetContext.getPlayer();
					BlockState state = packetContext.getPlayer().world.getBlockState(pos0);
					player.world.setBlockState(pos1, state);

					if (!player.abilities.creativeMode) {
						ItemStack item_stack = new ItemStack(state.getBlock());
						ItemStack off_hand_stack=(ItemStack)player.inventory.offHand.get(0);
						if (!off_hand_stack.isEmpty() &&
								item_stack.getItem() == off_hand_stack.getItem() && ItemStack.areTagsEqual(item_stack, off_hand_stack)							 
						) {
							player.inventory.offHand.get(0).decrement(1);
						}else{
							int slot=-1;
							for(int i = 0; i < player.inventory.main.size(); ++i) {
								ItemStack stack2=(ItemStack)player.inventory.main.get(i);
								if (!((ItemStack)player.inventory.main.get(i)).isEmpty() &&
									item_stack.getItem() == stack2.getItem() && ItemStack.areTagsEqual(item_stack, stack2)							 
								) {
								slot=i;
								}
							}
							//int slot = player.inventory.getSlotWithStack(item_stack);					
							if(slot > -1)	
								player.inventory.getInvStack(slot).decrement(1);
						}
						ItemStack wand_item = player.getMainHandStack();
						wand_item.damage(1, player.getRandom(), player instanceof ServerPlayerEntity ? (ServerPlayerEntity)player : null);
						/*wand_item.damage(1, (LivingEntity)player, (Consumer)((e) -> {
							((LivingEntity) e).sendEquipmentBreakStatus(EquipmentSlot.MAINHAND);
							}));*/
					}
                }
 
            });
        });
    }
    
}