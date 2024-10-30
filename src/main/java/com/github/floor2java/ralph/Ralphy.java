package com.github.floor2java.ralph;

import com.github.floor2java.ralph.features.AutoKab;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = "ralph", useMetadata = true)
public class Ralphy {
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        registerEvents(new AutoKab());
    }

    private static void registerEvents(Object... o) {
        for (Object toReg : o) {
            MinecraftForge.EVENT_BUS.register(toReg);
        }
    }
}
