package com.github.command17.neohammers.common.event;

import com.github.command17.neohammers.NeoHammers;
import com.github.command17.neohammers.common.enchantment.ModEnchantmentEffectComponents;
import com.github.command17.neohammers.common.enchantment.ModEnchantments;
import com.github.command17.neohammers.common.item.HammerItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;

import java.util.stream.Stream;

@EventBusSubscriber(modid = NeoHammers.MOD_ID)
public final class ModEvents {
    @SubscribeEvent
    public static void onPlayerBreakBlock(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        BlockPos hitPos = event.getPos();
        BlockState hitState = event.getState();
        LevelAccessor level = event.getLevel();
        ItemStack stack = player.getMainHandItem();
        if (EnchantmentHelper.has(stack, ModEnchantmentEffectComponents.EXTENDED_AREA_MINE.get()) && HammerItem.canPlayerUseExtendedAreaMine(player)) {
            Stream<BlockPos> blocks = HammerItem.getBlocksInRadiusBasedOnEnchantment(player, hitPos, level);
            blocks.forEach((pos) -> {
                if (pos.equals(hitPos)) {
                    return;
                }

                BlockState state = level.getBlockState(pos);
                if (!HammerItem.canMineOther(stack, hitState, state)) {
                    return;
                }

                state.getBlock().playerDestroy(player.level(), player, pos, state, level.getBlockEntity(pos), stack);
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                stack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
            });
        }
    }

    @SubscribeEvent
    private static void addVillagerTrades(VillagerTradesEvent event) {
        var registryAccess = event.getRegistryAccess();
        Holder<Enchantment> hammeringEnchantment = registryAccess.lookup(Registries.ENCHANTMENT)
                .flatMap((registryLookup) -> registryLookup.get(ModEnchantments.HAMMERING))
                .orElseThrow();

        if (event.getType().equals(VillagerProfession.LIBRARIAN)) {
            event.getTrades().get(2).add((entity, random) -> {
                ItemStack enchantmentStack = EnchantedBookItem.createForEnchantment(
                        new EnchantmentInstance(hammeringEnchantment, random.nextIntBetweenInclusive(1, 3)));
                return new MerchantOffer(
                        new ItemCost(Items.EMERALD, random.nextIntBetweenInclusive(15, 55)),
                        enchantmentStack,
                        6,
                        3,
                        0.05f
                );
            });
        }
    }
}
