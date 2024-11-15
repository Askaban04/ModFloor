package com.github.floor2java.ralph;

import com.github.floor2java.ralph.features.AutoKab;
import com.github.floor2java.ralph.features.FlowerMacro;
import com.github.floor2java.ralph.features.Mining;
import com.github.floor2java.ralph.features.WartMacro;
import com.github.floor2java.ralph.utils.RotationUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

@Mod(modid = "ralph", useMetadata = true)
public class Ralphy {

    private KeyBinding flowerKey = new KeyBinding("Flower Macro (WIP)", Keyboard.KEY_NONE, "! Ralph");
    private KeyBinding wartKey = new KeyBinding("Wart Macro (WIP)", Keyboard.KEY_NONE, "! Ralph");

    private KeyBinding[] kList = {wartKey, flowerKey};

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        registerEvents(new AutoKab(), new FlowerMacro(), new RotationUtils(), new WartMacro());
        for (KeyBinding k : kList) {
            ClientRegistry.registerKeyBinding(k);
        }
    }

    @SubscribeEvent
    public void onKey(InputEvent.KeyInputEvent event) {
        if (Minecraft.getMinecraft().thePlayer == null) return;
        if (flowerKey.isPressed()) {
            FlowerMacro.setFlowerMacro(!FlowerMacro.isFlowerMacro());
            if (FlowerMacro.isFlowerMacro()) FlowerMacro.onEnable();
            else FlowerMacro.onDisable();
        } else if (wartKey.isPressed()) {
            WartMacro.setEnabled(!WartMacro.isEnabled());
            if (WartMacro.isEnabled()) WartMacro.onEnable();
            else WartMacro.onDisable();
        }
    }

    public void postInit(FMLPostInitializationEvent event) {

    }

    private static void registerEvents(Object... o) {
        for (Object toReg : o) {
            MinecraftForge.EVENT_BUS.register(toReg);
        }
    }

}
