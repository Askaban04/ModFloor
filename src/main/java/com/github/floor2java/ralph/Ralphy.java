package com.github.floor2java.ralph;

import com.github.floor2java.ralph.commands.RalphyCommand;
import com.github.floor2java.ralph.features.*;
import com.github.floor2java.ralph.utils.ChatUtils;
import com.github.floor2java.ralph.utils.RotationUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.command.CommandHandler;
import net.minecraftforge.client.ClientCommandHandler;
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
    private KeyBinding wartKey = new KeyBinding("Wart Macro", Keyboard.KEY_NONE, "! Ralph");
    private KeyBinding autoClickerKey = new KeyBinding("Auto Click", Keyboard.KEY_NONE, "! Ralph");
    private KeyBinding testKey = new KeyBinding("test (Ne surtout pas utiliser)", Keyboard.KEY_NONE, "! Ralph");

    private KeyBinding[] kList = {wartKey, autoClickerKey, testKey};

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        registerEvents(new AutoKab(), new FlowerMacro(), new RotationUtils(), new WartMacro(), new AutoClicker());
        for (KeyBinding k : kList) {
            ClientRegistry.registerKeyBinding(k);
        }
        ClientCommandHandler.instance.registerCommand(new RalphyCommand());
    }

    @SubscribeEvent
    public void onKey(InputEvent.KeyInputEvent event) {
        if (Minecraft.getMinecraft().thePlayer == null) return;
        if (flowerKey.isPressed()) {

        } else if (wartKey.isPressed()) {
            WartMacro.setEnabled(!WartMacro.isEnabled());
            if (WartMacro.isEnabled()) WartMacro.onEnable();
            else WartMacro.onDisable();
        } else if (autoClickerKey.isPressed()) {
            AutoClicker.setEnabled(!AutoClicker.isEnabled());
            if (AutoClicker.isEnabled()) AutoClicker.onEnable();
            else AutoClicker.onDisable();
        } else if (testKey.isPressed()) {
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
