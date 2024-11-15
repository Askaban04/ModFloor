package com.github.floor2java.ralph.features;

import com.github.floor2java.ralph.utils.ChatUtils;

public class Mining {

    private static boolean enabled = false;


    public static void onEnable() {
        ChatUtils.clientMessage("Auto Mining is enabled");
    }

    public static void onDisable() {
        ChatUtils.clientMessage("Auto Mining is disabled");
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static void setEnabled(boolean enabled1) {
        enabled = enabled1;
    }


}
