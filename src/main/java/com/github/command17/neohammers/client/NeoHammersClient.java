package com.github.command17.neohammers.client;

import com.github.command17.neohammers.NeoHammers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = NeoHammers.MOD_ID, dist = Dist.CLIENT)
public final class NeoHammersClient {
    public NeoHammersClient(ModContainer modContainer) {
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }
}
