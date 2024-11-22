package com.github.floor2java.ralph.features;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import static com.github.floor2java.ralph.utils.ChatUtils.debug;

public class AutoClicker {


    private static boolean enabled = false;
    Minecraft mc = Minecraft.getMinecraft();
    static int tick = 0;

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent e) {
        mc = Minecraft.getMinecraft();
        if (!isEnabled()) return;
        if (mc.thePlayer == null) return;
        tick++;
        if (tick == 2) {
            simulateLeftClickAttack();
            tick = 0;
        }

    }

    @SubscribeEvent
    public void onLoad(WorldEvent.Load e) {

    }

    private void simulateLeftClickAttack() {
        mc = Minecraft.getMinecraft();
        EntityPlayerSP player = mc.thePlayer;
        MovingObjectPosition objectMouseOver = mc.objectMouseOver;
        if (objectMouseOver != null && objectMouseOver.entityHit != null) {
            mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(objectMouseOver.entityHit, C02PacketUseEntity.Action.ATTACK));
            player.swingItem();
        }
    }

    public static void onEnable() {
        debug("Auto Clicker : activé");
    }

    public static void onDisable() {
        debug("Auto Clicker : désactivé");
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static void setEnabled(boolean enabled) {
        AutoClicker.enabled = enabled;
    }
}
