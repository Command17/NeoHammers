package com.github.command17.neohammers.common.item;

import com.github.command17.neohammers.NeoHammers;
import com.github.command17.neohammers.common.util.ModTags;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class ModItems {
    private static final DeferredRegister.Items REGISTER = DeferredRegister.createItems(NeoHammers.MOD_ID);

    public static final Supplier<Item> WOODEN_HAMMER = createHammer("wooden_hammer", ToolMaterial.WOOD, 5, -3.1f, of());
    public static final Supplier<Item> STONE_HAMMER = createHammer("stone_hammer", ToolMaterial.STONE, 6, -3.1f, of());
    public static final Supplier<Item> COPPER_HAMMER = createHammer("copper_hammer", ToolMaterial.COPPER, 7, -3.2f, of());
    public static final Supplier<Item> IRON_HAMMER = createHammer("iron_hammer", ToolMaterial.IRON, 5, -3.1f, of());
    public static final Supplier<Item> GOLDEN_HAMMER = createHammer("golden_hammer", ToolMaterial.GOLD, 5, -3.1f, of());
    public static final Supplier<Item> DIAMOND_HAMMER = createHammer("diamond_hammer", ToolMaterial.DIAMOND, 4, -3.1f, of());
    public static final Supplier<Item> NETHERITE_HAMMER = createHammer("netherite_hammer", ToolMaterial.NETHERITE, 4, -3, of().fireResistant());

    private static Supplier<Item> createHammer(String id, ToolMaterial material, float attackDamage, float attackSpeed, Item.Properties properties) {
        return REGISTER.registerItem(id,
                (props) -> new HammerItem(material, ModTags.BlockTags.MINEABLE_WITH_HAMMER, attackDamage, attackSpeed, 2, props)
        );
    }

    private static Item.Properties of() {
        return new Item.Properties();
    }

    public static void register(IEventBus eventBus) {
        REGISTER.register(eventBus);
        NeoHammers.LOGGER.info("Registered Items.");
    }
}
