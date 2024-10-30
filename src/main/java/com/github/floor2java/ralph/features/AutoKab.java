package com.github.floor2java.ralph.features;

import com.github.floor2java.ralph.utils.ChatUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;


public class AutoKab {


    private boolean autoKab = true;
    private boolean sell = false;
    Minecraft mc = null;
    EntityPlayerSP player = null;
    int tick = 0;
    int sellTick = 0;

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {

        mc = Minecraft.getMinecraft();
        player = mc.thePlayer;

        if (player == null) return;
        if (!isAutoKab()) return;
        if (mc.isSingleplayer()) return;
        if (!mc.getCurrentServerData().serverIP.contains("play.battle-adventure.eu")) return;
        tick++;
        sellTick++;
        //ChatUtils.clientMessage(String.valueOf(a));
        if (tick == 10) {
            tick = 0;
            if (isInventoryFull()) {
                sell = true;
            }
        }
        if (sellTick == 30) {
            sellTick = 0;
            if (sell) {
                sell = false;
                ChatUtils.serverMessage("/kab sell all");
            }
        }
    }

    private boolean isInventoryFull() {
        for (ItemStack itemStack : player.inventory.mainInventory) {
            if (itemStack == null) return false;
        }
        return true;
    }

    public boolean isAutoKab() {
        return autoKab;
    }

    public void setAutoKab(boolean autoKab) {
        this.autoKab = autoKab;
    }
}
