package com.github.command17.neohammers.client;

import com.github.command17.neohammers.NeoHammers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ExtractLevelRenderStateEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = NeoHammers.MOD_ID, value = Dist.CLIENT)
public final class ModClientEvents {
    @SubscribeEvent
    public static void renderExtraOutlinesAndDecals(RenderLevelStageEvent.AfterTranslucentBlocks event) {

    }

    @SubscribeEvent
    public static void extractExtraRenderState(ExtractLevelRenderStateEvent event) {

    }
}
