package com.github.floor2java.ralph.mixin;

import net.minecraft.client.gui.GuiMainMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.struct.MemberRef;

import java.lang.reflect.Field;

@Mixin(GuiMainMenu.class)
public class MixinGuiMainMenu {

    @Inject(method = "initGui", at = @At("HEAD"))
    public void onInitGui(CallbackInfo ci) {
        System.out.println("Hello from Main Menu!");
    }

}
