package com.github.floor2java.ralph.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class ChatUtils {

    private static final String RALPH = EnumChatFormatting.AQUA + "RALPH : "+ EnumChatFormatting.RESET;

    public static void clientMessage(String str) {
        if (Minecraft.getMinecraft().thePlayer == null) return;
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(RALPH + str));
    }

    public static void serverMessage(String str) {
        if (Minecraft.getMinecraft().thePlayer == null) return;
        Minecraft.getMinecraft().thePlayer.sendChatMessage(str);
    }

    public static void debug(Object o) {
        if (Minecraft.getMinecraft().thePlayer == null) return;
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(RALPH + String.valueOf(o)));
    }
}
