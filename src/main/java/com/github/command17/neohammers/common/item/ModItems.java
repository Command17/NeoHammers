package com.github.command17.neohammers.common.item;

import com.github.command17.neohammers.NeoHammers;
import com.github.command17.neohammers.common.util.ModTags;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {
    private static final DeferredRegister.Items REGISTER = DeferredRegister.createItems(NeoHammers.MOD_ID);

    public static final DeferredItem<HammerItem> WOODEN_HAMMER = createHammer("wooden_hammer", Tiers.WOOD, 5, -3.1f, of());
    public static final DeferredItem<HammerItem> STONE_HAMMER = createHammer("stone_hammer", Tiers.STONE, 6, -3.1f, of());
    public static final DeferredItem<HammerItem> IRON_HAMMER = createHammer("iron_hammer", Tiers.IRON, 5, -3.1f, of());
    public static final DeferredItem<HammerItem> GOLDEN_HAMMER = createHammer("golden_hammer", Tiers.GOLD, 5, -3.1f, of());
    public static final DeferredItem<HammerItem> DIAMOND_HAMMER = createHammer("diamond_hammer", Tiers.DIAMOND, 4, -3.1f, of());
    public static final DeferredItem<HammerItem> NETHERITE_HAMMER = createHammer("netherite_hammer", Tiers.NETHERITE, 4, -3, of().fireResistant());

    private static DeferredItem<HammerItem> createHammer(String id, Tier tier, float attackDamage, float attackSpeed, Item.Properties properties) {
        return REGISTER.registerItem(id,
                (props) -> new HammerItem(tier, ModTags.BlockTags.MINEABLE_WITH_HAMMER, 2, props),
                properties.attributes(DiggerItem.createAttributes(tier, attackDamage, attackSpeed)));
    }

    private static Item.Properties of() {
        return new Item.Properties();
    }

    public static void register(IEventBus eventBus) {
        REGISTER.register(eventBus);
        NeoHammers.LOGGER.info("Registered Items.");
    }
}
