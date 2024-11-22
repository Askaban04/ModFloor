package com.github.floor2java.ralph.features;

import com.github.floor2java.ralph.utils.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.github.floor2java.ralph.utils.ChatUtils.debug;

public class WartMacro {

    static Minecraft mc = null;
    private static boolean enabled = false;
    public static WartState state = WartState.walking;

    static int hours, minutes, seconds;
    static int timer = 0;
    static int captchaAmount = 0;
    static int jdcAmount = 0;
    static int moneyAmount = 0;

    static int tick = 0;
    static boolean ready = false;

    GuiScreen currentScreen = null;
    GuiChest currentChest = null;
    ContainerChest container = null;

    static boolean cd = false;
    static boolean halfCd = false;
    static boolean rotaCd = false;

    private static boolean stopOnFull = true;

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent e) {
        mc = Minecraft.getMinecraft();
        if (!isEnabled()) return;

        if (e.phase == TickEvent.Phase.END) timer++;

        if (mc.currentScreen != null) {
            currentScreen = mc.currentScreen;
            if (!(currentScreen instanceof GuiChest)) {
                onDisable();
            } else {
                currentChest = (GuiChest) currentScreen;
                container = (ContainerChest) currentChest.inventorySlots;
                if (container.getLowerChestInventory().getDisplayName().getFormattedText().contains("Cliquez sur le")) {
                    state = WartState.captcha;
                    currentScreen = null;
                    currentChest = null;
                    container = null;
                    tick = 0;
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), false);
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.getKeyCode(), false);
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), false);
                }
            }
        }
        if (isInventoryFull()) {
            if (isStopOnFull()) onDisable();
        }

        if (state == WartState.walking) {
            tick++;
            if (tick == 20) {
                ready = true;
                tick = 0;
            }
            if (!ready) return;
            if (mc.thePlayer.inventory.currentItem != InventoryUtils.findItemInHotbar("Hoe de Récolte")) {
                InventoryUtils.selectSlot(InventoryUtils.findItemInHotbar("Hoe de Récolte"));
            }
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), true);

            KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.getKeyCode(), true);
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), true);
            if (facedBlock() != null) {
                if (mc.theWorld.getBlockState(facedBlock()).getBlock() == Blocks.planks) {
                    state = WartState.rotating;
                    tick = 0;
                } else if (mc.theWorld.getBlockState(facedBlock()).getBlock() == Blocks.cobblestone) {

                }
            }
        } else if (state == WartState.rotating) {
            if (playerFacing() == EnumFacing.NORTH || playerFacing() == EnumFacing.EAST) {
                tick++;
                if (tick == 2) {
                    RotationUtils.smoothLook(new RotationUtils.Rotation(90, 90), 2, null);
                } else if (tick == 30) {
                    RotationUtils.smoothLook(new RotationUtils.Rotation(90, 180), 2, null);
                    state = WartState.walking;
                }
            } else if (playerFacing() == EnumFacing.SOUTH || playerFacing() == EnumFacing.WEST) {
                tick++;
                if (tick == 2) {
                    RotationUtils.smoothLook(new RotationUtils.Rotation(90, -90), 2, null);
                } else if (tick == 30) {
                    RotationUtils.smoothLook(new RotationUtils.Rotation(90, 0), 2, null);
                    state = WartState.walking;
                }
            }
        } else if (state == WartState.captcha) {
            if (!cd) return;
            if (e.phase != TickEvent.Phase.START) return;
            tick++;
            if (tick == 60 * 20 + 1) {
                onDisable();
                onEnable();
                state = WartState.walking;
                cd = false;
            }
        }
    }

    @SubscribeEvent
    public void onOverlayRender(RenderGameOverlayEvent.Post e) {
        mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) return;
        if (!isEnabled()) return;

        FontUtils.drawCenteredString("Session time : " + str(formatSeconds(timer / 20)), 200, 100);
        FontUtils.drawCenteredString("Captchas : " + str(captchaAmount), 200, 110);
        FontUtils.drawCenteredString(!isInventoryFull() ? "JDC : " + str(jdcAmount) : "JDC : " + str(jdcAmount) + " (inventory full)", 200, 120);
        FontUtils.drawCenteredString("RinaCoins : "+formatNumber(moneyAmount) + "\u24C7", 200, 130);
    }

    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent e) {
        if (!isEnabled()) return;
        String message = StringUtils.stripControlCodes(e.message.getUnformattedText());
        if (message.contains("Le temps est écoulé, vous devez attendre 60 secondes pour réessayer !")) {
            captchaAmount++;
            cd = true;
        } else if (message.contains("Vous trouvez un Jeton de Casino.")) {
            if (!isInventoryFull())
                jdcAmount++;
        } else if (message.contains("Vous recevez une prime d'activité de ")) {

            String moneyString = message.replaceAll("Vous recevez une prime d'activité de ", "").substring(2);
            moneyString = moneyString.replaceAll("[^\\d]", "");
            try {
                int mA = Integer.parseInt(moneyString);
                moneyAmount += mA;
            } catch (NumberFormatException ex) {
                debug("WartMacro.java LINE 170");
            }

        }
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent e) {
        //if (!isEnabled()) return;
        if (state != WartState.walking) return;
        mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) return;
        if (facedBlock() != null) {
            //  DrawUtils.renderEspBox(facedBlock(), e.partialTicks, 0xFFFFAAFF);
        }
    }

    private static BlockPos facedBlock() {
        mc = Minecraft.getMinecraft();
        if (playerFacing() == EnumFacing.SOUTH) {
            return new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ - 1);
        } else if (playerFacing() == EnumFacing.NORTH) {
            return new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ + 1);
        } else if (playerFacing() == EnumFacing.EAST) {
            return new BlockPos(mc.thePlayer.posX - 1, mc.thePlayer.posY, mc.thePlayer.posZ);
        } else if (playerFacing() == EnumFacing.WEST) {
            return new BlockPos(mc.thePlayer.posX + 1, mc.thePlayer.posY, mc.thePlayer.posZ);
        }


        return null;
    }

    @SubscribeEvent
    public void onLoad(WorldEvent.Load e) {
        if (isEnabled()) onDisable();
    }

    private static RotationUtils.Rotation starterRotation() {
        mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) return null;

        if (playerFacing() == EnumFacing.NORTH) {
            return new RotationUtils.Rotation(90, 0);
        } else if (playerFacing() == EnumFacing.SOUTH) {
            return new RotationUtils.Rotation(90, 180);
        }

        return null;
    }

    public static String formatNumber(int number) {
        float formattedValue;
        String suffix;

        if (number >= 1_000_000_000) {
            formattedValue = number / 1_000_000_000f;
            suffix = "b";
        } else if (number >= 1_000_000) {
            formattedValue = number / 1_000_000f;
            suffix = "m";
        } else if (number >= 1_000) {
            formattedValue = number / 1_000f;
            suffix = "k";
        } else {
            return String.valueOf(number);
        }

        return String.format("%.1f%s", formattedValue, suffix);
    }

    public static EnumFacing playerFacing() {

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) return null;
        float yaw = mc.thePlayer.rotationYaw;

        yaw = yaw % 360;
        if (yaw < 0) {
            yaw += 360;
        }

        if (yaw >= 45 && yaw < 135) {
            return EnumFacing.EAST;
        } else if (yaw >= 135 && yaw < 225) {
            return EnumFacing.SOUTH;
        } else if (yaw >= 225 && yaw < 315) {
            return EnumFacing.WEST;
        } else {
            return EnumFacing.NORTH;
        }
    }

    public static void onEnable() {
        if (starterRotation() == null) {
            ChatUtils.debug("Vous devez regarder au Nord ou au Sud !");
            setEnabled(false);
            onDisable();
            state = null;
            return;
        }
        MouseUtils.ungrabMouse();
        InventoryUtils.selectSlot(InventoryUtils.findItemInHotbar("Hoe de Récolte"));
        RotationUtils.smoothLook(starterRotation(), 4, null);
        tick = 0;
        ready = false;
        cd = false;
        halfCd = false;
        rotaCd = false;
        state = WartState.walking;
        mc.gameSettings.pauseOnLostFocus = false;
        setEnabled(true);
        ChatUtils.clientMessage("Wart Macro : Enabled");
    }

    private static boolean isInventoryFull() {
        mc = Minecraft.getMinecraft();
        if (mc.thePlayer != null) {
            InventoryPlayer inventoryPlayer = mc.thePlayer.inventory;
            return mc.thePlayer.inventory.getFirstEmptyStack() == -1;
        }
        return false;
    }

    public static void onDisable() {
        setEnabled(false);
        ChatUtils.clientMessage("Wart Macro : Disabled");
        state = null;
        MouseUtils.regrabMouse();
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), false);
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.getKeyCode(), false);
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), false);
    }

    public static String formatSeconds(int totalSeconds) {
        hours = totalSeconds / 3600;
        minutes = (totalSeconds % 3600) / 60;
        seconds = totalSeconds % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static void setEnabled(boolean enabled) {
        WartMacro.enabled = enabled;
    }

    public static boolean isStopOnFull() {
        return stopOnFull;
    }

    public static void setStopOnFull(boolean stopOnFull) {
        WartMacro.stopOnFull = stopOnFull;
    }

    private static String str(Object o) {
        return String.valueOf(o);
    }
}
