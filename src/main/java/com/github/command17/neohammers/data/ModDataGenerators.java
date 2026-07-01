package com.github.command17.neohammers.data;

import com.github.command17.neohammers.NeoHammers;
import com.github.command17.neohammers.common.enchantment.ModEnchantmentEffectComponents;
import com.github.command17.neohammers.common.enchantment.ModEnchantments;
import com.github.command17.neohammers.common.util.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = NeoHammers.MOD_ID)
public final class ModDataGenerators {
    public static final RegistrySetBuilder ENCHANTMENT_BUILDER = new RegistrySetBuilder()
            .add(Registries.ENCHANTMENT, (context) -> {
                var hammersHolderSet = HolderSet.emptyNamed(BuiltInRegistries.ITEM.holderOwner(), ModTags.ItemTags.HAMMERS);
                context.register(ModEnchantments.HAMMERING, Enchantment.enchantment(Enchantment.definition(
                        hammersHolderSet, // Supported Items
                        hammersHolderSet, // Primary Items
                        4, // Weight
                        6, // Max Level
                        Enchantment.dynamicCost(1, 10), // Min Cost
                        Enchantment.dynamicCost(51, 10), // Max Cost
                        2, // Anvil Cost
                        EquipmentSlotGroup.MAINHAND // Equipment Slots
                )).withEffect(ModEnchantmentEffectComponents.EXTENDED_AREA_MINE.get())
                        .build(ModEnchantments.HAMMERING.location()));
            });

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        // Generate enchantments first so that it's added to the registry lookup
        event.createDatapackRegistryObjects(ENCHANTMENT_BUILDER);

        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        generator.addProvider(event.includeClient(), new ModItemModelProvider(output, existingFileHelper));

        var blockTagsProvider = generator.addProvider(event.includeServer(), new ModBlockTagsProvider(output, lookupProvider, existingFileHelper));
        generator.addProvider(event.includeServer(), new ModItemTagsProvider(output, lookupProvider, blockTagsProvider.contentsGetter(), existingFileHelper));
        generator.addProvider(event.includeServer(), new ModEnchantmentTagsProvider(output, lookupProvider, existingFileHelper));
        generator.addProvider(event.includeServer(), new ModGlobalLootModifierProvider(output, lookupProvider));
        generator.addProvider(event.includeServer(), new ModRecipeProvider(output, lookupProvider));
        generator.addProvider(event.includeServer(), new ModAdvancementProvider(output, lookupProvider, existingFileHelper));
        generator.addProvider(event.includeServer(), new ModDataMapProvider(output, lookupProvider));
    }
}
