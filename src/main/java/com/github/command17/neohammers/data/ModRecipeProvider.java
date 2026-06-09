package com.github.command17.neohammers.data;

import com.github.command17.neohammers.NeoHammers;
import com.github.command17.neohammers.common.item.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.concurrent.CompletableFuture;

@ParametersAreNonnullByDefault
public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput output) {
        createHammer(output, ItemTags.PLANKS, ModItems.WOODEN_HAMMER.get());
        createHammer(output, Tags.Items.COBBLESTONES, ModItems.STONE_HAMMER.get());
        createMetalHammer(output, Items.IRON_BLOCK, ModItems.IRON_HAMMER.get(), Items.IRON_INGOT);
        createMetalHammer(output, Items.GOLD_BLOCK, ModItems.GOLDEN_HAMMER.get(), Items.GOLD_INGOT);
        createHammer(output, Items.DIAMOND_BLOCK, ModItems.DIAMOND_HAMMER.get());
        modNetheriteSmithing(output, ModItems.DIAMOND_HAMMER.get(), RecipeCategory.TOOLS, ModItems.NETHERITE_HAMMER.get());
    }

    private static void createMetalHammer(RecipeOutput output, ItemLike material, ItemLike result, ItemLike smeltedResult) {
        createHammer(output, material, result);
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(result), RecipeCategory.TOOLS, smeltedResult, 0.8f, 13 * 20).unlockedBy(getHasName(result), has(result))
                .save(output, NeoHammers.resource("smelt_" + getItemName(result) + "_back_to_resource"));
        SimpleCookingRecipeBuilder.blasting(Ingredient.of(result), RecipeCategory.TOOLS, smeltedResult, 0.8f, 13 * 20).unlockedBy(getHasName(result), has(result))
                .save(output, NeoHammers.resource("blast_" + getItemName(result) + "_back_to_resource"));
    }

    private static void createHammer(RecipeOutput output, ItemLike material, ItemLike result) {
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, result)
                .pattern(" x ")
                .pattern(" #x")
                .pattern("#  ")
                .define('x', material)
                .define('#', Tags.Items.RODS_WOODEN)
                .unlockedBy(getHasName(material), has(material))
                .save(output);
    }

    private static void createHammer(RecipeOutput output, TagKey<Item> material, ItemLike result) {
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, result)
                .pattern(" x ")
                .pattern(" #x")
                .pattern("#  ")
                .define('x', material)
                .define('#', Tags.Items.RODS_WOODEN)
                .unlockedBy("has_materials", has(material))
                .save(output);
    }

    private static void modNetheriteSmithing(RecipeOutput recipeOutput, Item ingredientItem, RecipeCategory category, Item resultItem) {
        SmithingTransformRecipeBuilder.smithing(
                Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
                Ingredient.of(ingredientItem),
                Ingredient.of(Items.NETHERITE_INGOT),
                category,
                resultItem
        ).unlocks("has_netherite_ingot", has(Items.NETHERITE_INGOT))
                .save(recipeOutput, NeoHammers.resource(getItemName(resultItem) + "_smithing"));
    }
}
