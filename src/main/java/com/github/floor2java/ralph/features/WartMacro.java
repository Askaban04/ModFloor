package com.github.floor2java.ralph.features;

import com.github.floor2java.ralph.utils.ChatUtils;
import com.github.floor2java.ralph.utils.DrawUtils;
import com.github.floor2java.ralph.utils.InventoryUtils;
import com.github.floor2java.ralph.utils.RotationUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import static com.github.floor2java.ralph.utils.ChatUtils.debug;

public class WartMacro {

    static Minecraft mc = null;
    private static boolean enabled = false;
    static WartState state = WartState.walking;
    static int tick = 0;
    static boolean ready = false;
    private static int rot = 0;
    private static int northIndex = -1;

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent e) {
        mc = Minecraft.getMinecraft();
        if (!isEnabled()) return;
        if (mc.currentScreen != null) {
            onDisable();
        }
        if (isInventoryFull()) {
            onDisable();
            debug("Inventaire plein !");
        }
        if (state == WartState.walking) {
            tick++;
            if (tick == 20) {
                ready = true;
                tick = 0;
            }
            if (!ready) return;
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), true);
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.getKeyCode(), true);
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), true);
            if (facedBlock() != null) {
                if (mc.theWorld.getBlockState(facedBlock()).getBlock() == Blocks.planks) {
                    state = WartState.rotating;
                    tick = 0;
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
        }
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent e) {
        //if (!isEnabled()) return;
        if (state != WartState.walking) return;
        mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) return;
        if (facedBlock() != null) {
        }
    }

    private static BlockPos facedBlock() {
        mc = Minecraft.getMinecraft();
        if (playerFacing() == EnumFacing.SOUTH) {
            return new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ - 1);
        } else if (playerFacing() == EnumFacing.NORTH) {
            return new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ + 1);
        }
        return null;
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
        InventoryUtils.selectSlot(InventoryUtils.findItemInHotbar("Hoe de Récolte"));
        RotationUtils.smoothLook(starterRotation(), 4, null);
        tick = 0;
        rot = 0;
        northIndex = -1;
        ready = false;
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
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), false);
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.getKeyCode(), false);
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), false);
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static void setEnabled(boolean enabled) {
        WartMacro.enabled = enabled;
    }

}
