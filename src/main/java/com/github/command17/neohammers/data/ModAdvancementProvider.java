package com.github.command17.neohammers.data;

import com.github.command17.neohammers.NeoHammers;
import com.github.command17.neohammers.common.advancement.UseExtendedAreaMineTrigger;
import com.github.command17.neohammers.common.item.ModItems;
import com.github.command17.neohammers.common.util.ModTags;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ModAdvancementProvider extends AdvancementProvider {
    public ModAdvancementProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, ExistingFileHelper existingFileHelper) {
        super(output, registries, existingFileHelper, List.of(new ModAdvancementGenerator()));
    }

    @ParametersAreNonnullByDefault
    @MethodsReturnNonnullByDefault
    private static class ModAdvancementGenerator implements AdvancementGenerator {
        @Override
        public void generate(HolderLookup.Provider provider, Consumer<AdvancementHolder> saver, ExistingFileHelper existingFileHelper) {
            AdvancementHolder hammerTime = Advancement.Builder.advancement()
                    .display(
                            ModItems.IRON_HAMMER.get(),
                            NeoHammers.translate("advancement.", ".hammerTime.title"),
                            NeoHammers.translate("advancement.", ".hammerTime.desc"),
                            null,
                            AdvancementType.TASK,
                            true,
                            true,
                            false
                    )
                    .parent(AdvancementSubProvider.createPlaceholder("minecraft:adventure/root"))
                    .addCriterion(
                            "has_any_hammer",
                            InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(ModTags.ItemTags.HAMMERS))
                    )
                    .save(saver, NeoHammers.resource("adventure/hammer_time"), existingFileHelper);

            AdvancementHolder seriousDestructionNeeds = Advancement.Builder.advancement()
                    .display(
                            ModItems.NETHERITE_HAMMER.get(),
                            NeoHammers.translate("advancement.", ".seriousDestructionNeeds.title"),
                            NeoHammers.translate("advancement.", ".seriousDestructionNeeds.desc"),
                            null,
                            AdvancementType.CHALLENGE,
                            true,
                            true,
                            false
                    )
                    .parent(hammerTime)
                    .rewards(AdvancementRewards.Builder.experience(115))
                    .addCriterion(
                            "has_netherite_hammer",
                            InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.NETHERITE_HAMMER.get())
                    )
                    .save(saver, NeoHammers.resource("adventure/serious_destruction_needs"), existingFileHelper);

            AdvancementHolder seriousDestruction = Advancement.Builder.advancement()
                    .display(
                            Items.ENCHANTED_BOOK,
                            NeoHammers.translate("advancement.", ".seriousDestruction.title"),
                            NeoHammers.translate("advancement.", ".seriousDestruction.desc"),
                            null,
                            AdvancementType.CHALLENGE,
                            true,
                            true,
                            false
                    )
                    .parent(hammerTime)
                    .rewards(AdvancementRewards.Builder.experience(95))
                    .addCriterion(
                            "destroy_everything",
                            UseExtendedAreaMineTrigger.TriggerInstance.instance(6)
                    )
                    .save(saver, NeoHammers.resource("adventure/serious_destruction"), existingFileHelper);
        }
    }
}
