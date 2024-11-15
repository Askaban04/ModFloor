package com.github.floor2java.ralph.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

public class InventoryUtils {

    private static Minecraft mc = Minecraft.getMinecraft();
    private static EntityPlayerSP player = mc.thePlayer;

    public static void selectSlot(int slot) {

        if (Minecraft.getMinecraft().thePlayer != null) {
            Minecraft.getMinecraft().thePlayer.inventory.currentItem = slot;
        }
    }

    public static int findItemInHotbar(String name) {
        InventoryPlayer inv = mc.thePlayer.inventory;
        for (int i = 0; i < 9; i++) {
            ItemStack curStack = inv.getStackInSlot(i);
            if (curStack != null) {
                if (curStack.getDisplayName().contains(name)) {
                    // ChatUtils.clientMessage(""); WORKING
                    return i;
                }
            }
        }
        ChatUtils.debug("Invalid hotbar slot, set to 0");
        return 0;
    }

    public static boolean isInventoryFull() {
        mc = Minecraft.getMinecraft();
        if (mc.thePlayer != null) {
            InventoryPlayer inventoryPlayer = mc.thePlayer.inventory;
            return mc.thePlayer.inventory.getFirstEmptyStack() == -1;
        }
        return false;

    }

    private static boolean inGame() {
        Minecraft mc = Minecraft.getMinecraft();
        return (!(mc.isSingleplayer()) && (mc.thePlayer != null) && (mc.theWorld != null));
    }

    public static void clickSlot(int slotId, int mouseButton, int mode) {
        if (inGame() && Minecraft.getMinecraft().currentScreen != null) {
            Minecraft.getMinecraft().playerController.windowClick(
                    Minecraft.getMinecraft().thePlayer.openContainer.windowId, slotId, mouseButton, mode,
                    Minecraft.getMinecraft().thePlayer);
        }
    }

}
