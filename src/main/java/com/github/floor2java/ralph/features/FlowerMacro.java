package com.github.floor2java.ralph.features;

import com.github.floor2java.ralph.utils.ChatUtils;
import com.github.floor2java.ralph.utils.DrawUtils;
import com.github.floor2java.ralph.utils.RotationUtils;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.StringUtils;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vec3i;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.commons.lang3.ArrayUtils;

import java.awt.*;
import java.util.ArrayList;

public class FlowerMacro {


    private static boolean flowerMacro = false;
    static FlowerState state;
    private static int tick = 0;
    private static int index = 0;
    private static boolean canRotate = true;
    private static boolean loaded = false;
    Minecraft mc = null;
    static int rotationSpeed = 8;
    static BlockPos[] bps = {new BlockPos(-9.5, 69, -68.5), new BlockPos(-11.5, 69, -30.5)};
    static BlockPos target = bps[index];

    BlockPos flower = null;

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent ev) {
        if (!isFlowerMacro()) return;
        mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) return;
        if (state == FlowerState.walking) {
            tick++;
            if (!loaded) {
                if (tick == 100) {
                    loaded = true;
                    tick = 0;
                }
                return;
            }
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), true);

            if (tick == rotationSpeed) {
                tick = 0;
                if (canRotate)
                    RotationUtils.smoothLook(RotationUtils.getRotationToBlock(target), rotationSpeed, null);
                canRotate = false;

            }
            if (doBpMatch(target)) {
                canRotate = true;
                index++;
                if (index != bps.length)
                    target = bps[index];
                else {
                    canRotate = false;
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), false);
                    state = FlowerState.breaking;
                    flower = closestFlower(playerLoc());
                }
            }
        } else if (state == FlowerState.breaking) {
            if (flower == null) flower = closestFlower(playerLoc());
            if (flower == null) {
                ChatUtils.clientMessage("flower null");
            }
            if (flower != null)
                RotationUtils.smoothLook(RotationUtils.getRotationToBlock(flower), rotationSpeed, null);
        }
    }


    @SubscribeEvent
    public void onRender(RenderWorldLastEvent ev){

    }
    @SubscribeEvent
    public void onBreak(BlockEvent.BreakEvent ev) {
        if (!isFlowerMacro()) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) return;
        ChatUtils.debug(ev.pos.getX());
    }

    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent ev) {
        String mess = StringUtils.stripControlCodes(ev.message.getUnformattedText());
        if (mess.contains("Couldn't warp you! Try again later. (PLAYER_TRANSFER_COOLDOWN)")) {

        }
    }



    private static BlockPos closestBp(BlockPos[] route) {
        Minecraft mc = Minecraft.getMinecraft();
        double closestDistanceSquared = Double.MAX_VALUE;
        BlockPos closestBp = null;
        for (BlockPos bp : route) {
            if (mc.thePlayer.getDistanceSq(bp) < closestDistanceSquared) {
                closestBp = bp;
                closestDistanceSquared = mc.thePlayer.getDistanceSq(bp);
            }
        }
        return closestBp;
    }


    private static boolean isFlower(IBlockState ibs) {
        if (ibs.getBlock() instanceof BlockFlower) return true;
        return false;
    }

    public static BlockPos playerLoc() {
        EntityPlayerSP p = Minecraft.getMinecraft().thePlayer;
        return new
                BlockPos(p.posX, p.posY - 0.4d, p.posZ);
    }

    public static boolean doBpMatch(BlockPos b2) {
        Minecraft mc = Minecraft.getMinecraft();

        if (mc.thePlayer.getDistanceSq(b2) < 2.6d)// 1.5d au carré
            return true;
        return false;
    }

    private static BlockPos closestFlower(BlockPos from) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP player = mc.thePlayer;

        int r = 6;
        if (player == null || mc.theWorld == null)
            return null;
        BlockPos playerPos = player.getPosition().add(0, 1, 0);
        Vec3 playerVec = player.getPositionVector();
        Vec3 bpVec = new Vec3(from.getX(), from.getY(), from.getZ());
        Vec3i vec3i = new Vec3i(r, r, r);
        ArrayList<Vec3> blocks = new ArrayList<Vec3>();
        if (playerPos != null) {

            for (BlockPos blockPos : BlockPos.getAllInBox(from.add(vec3i), from.subtract(vec3i))) {
                IBlockState blockState = mc.theWorld.getBlockState(blockPos);
                if (isFlower(blockState)) {

                    blocks.add(new Vec3(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5));

                }
            }
        }
        //   blk = blocks;
        return RotationUtils.closestRotation(blocks);

    }

    public static void onEnable() {
        ChatUtils.clientMessage("Flower Shit enabled");
        target = bps[0];
        state = FlowerState.breaking;
        index = 0;
        tick = 0;
        loaded = false;
        canRotate = true;
        // ChatUtils.serverMessage("/hub");

    }

    public static void onDisable() {
        KeyBinding.setKeyBindState(Minecraft.getMinecraft().gameSettings.keyBindForward.getKeyCode(), false);
        KeyBinding.setKeyBindState(Minecraft.getMinecraft().gameSettings.keyBindAttack.getKeyCode(), false);
        ChatUtils.clientMessage("Flower Shit disabled (comme sean)");
    }

    public static boolean isFlowerMacro() {
        return flowerMacro;
    }

    public static void setFlowerMacro(boolean flowerMacro) {
        FlowerMacro.flowerMacro = flowerMacro;
    }
}
