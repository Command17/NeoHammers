package com.github.command17.neohammers.data;

import com.github.command17.neohammers.NeoHammers;
import com.github.command17.neohammers.common.enchantment.ModEnchantmentEffectComponents;
import com.github.command17.neohammers.common.enchantment.ModEnchantments;
import com.github.command17.neohammers.common.util.ModTags;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = NeoHammers.MOD_ID)
public final class ModDataGenerators {
    public static final RegistrySetBuilder ENCHANTMENT_BUILDER = new RegistrySetBuilder()
            .add(Registries.ENCHANTMENT, (context) -> {
                var hammersHolderSet = context.lookup(Registries.ITEM).get(ModTags.ItemTags.HAMMERS).orElseThrow();
                context.register(ModEnchantments.HAMMERING, Enchantment.enchantment(
                        Enchantment.definition(
                                hammersHolderSet, // Supported Items
                                hammersHolderSet, // Primary Items
                                4, // Weight
                                6, // Max Level
                                Enchantment.dynamicCost(1, 10), // Min Cost
                                Enchantment.dynamicCost(51, 10), // Max Cost
                                2, // Anvil Cost
                                EquipmentSlotGroup.MAINHAND // Equipment Slots
                        )
                ).withEffect(ModEnchantmentEffectComponents.EXTENDED_AREA_MINING.get())
                        .build(ModEnchantments.HAMMERING.identifier()));
            });

    @SubscribeEvent
    public static void onGatherClientData(GatherDataEvent.Client event) {
        event.createProvider(ModModelProvider::new);
    }

    @SubscribeEvent
    public static void onGatherServerData(GatherDataEvent.Server event) {
        event.createDatapackRegistryObjects(ENCHANTMENT_BUILDER); // Generate enchantments first so that it's added to the registry lookup
        event.createProvider(ModEnchantmentTagsProvider::new);
        event.createProvider(ModBlockTagsProvider::new);
        event.createProvider(ModItemTagsProvider::new);
        event.createProvider(ModGlobalLootModifierProvider::new);
        event.createProvider(ModRecipeProvider.Runner::new);
    }
}
