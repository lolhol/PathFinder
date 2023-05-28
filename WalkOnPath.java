package com.dillo.Pathfinding;

import com.dillo.ArmadilloMain.ArmadilloStates;
import com.dillo.dilloUtils.LookAt;
import com.dillo.dilloUtils.TpUtils.WaitThenCall;
import com.dillo.utils.DistanceFromTo;
import com.dillo.utils.previous.SendChat;
import com.dillo.utils.previous.random.ids;
import com.dillo.utils.previous.random.prefix;
import com.dillo.utils.renderUtils.renderModules.RenderMultipleBlocksMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.List;

public class WalkOnPath {
    private static BlockPos nextBlock = null;
    private static boolean startWalking = false;
    public static List<BlockPos> blockRoute = null;
    private static final KeyBinding FORWARD = Minecraft.getMinecraft().gameSettings.keyBindForward;
    private static final KeyBinding JUMP = Minecraft.getMinecraft().gameSettings.keyBindJump;
    private static int failSafeCounter1 = 0;
    private static int failSafeCounter2 = 0;
    private static int timesTriggered = 0;
    private static BlockPos currPlayerPos = null;

    public static void walkOnPath(List<BlockPos> route) {
        blockRoute = route;

        if (blockRoute.size() > 0) {
            nextBlock = blockRoute.get(0);
            blockRoute.remove(0);

            if (nextBlock.getY() > ids.mc.thePlayer.posY) {
                LookAt.smoothLook(LookAt.getRotation(new Vec3(nextBlock.getX() + 0.5, nextBlock.getY() + 0.5, nextBlock.getZ() + 0.5)), 150);
            } else {
                LookAt.smoothLook(LookAt.getRotation(new Vec3(nextBlock.getX() + 0.5, nextBlock.getY() + 1, nextBlock.getZ() + 0.5)), 150);
            }

            WaitThenCall.waitThenCall(200, "startWalkingPath");
        } else {
            SendChat.chat(prefix.prefix + "Path end!");
            startWalking = false;
            ArmadilloStates.offlineState = "offline";
        }
    }

    public static void startWalkingPath() {
        ArmadilloStates.currentState = null;

        if (nextBlock != null) {
            startWalking = true;
        } else {
            SendChat.chat("Stopped walking on path!");
        }
    }
    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        if (startWalking) {
            if (!isOnBlock(nextBlock, 1.2)) {
                LookAt.smoothLook(LookAt.getRotation(new Vec3(nextBlock.getX() + 0.5, nextBlock.getY() + 1, nextBlock.getZ() + 0.5)), 20);
            }

            if (failSafeCounter1 >= 20) {
                currPlayerPos = new BlockPos(ids.mc.thePlayer.posX, ids.mc.thePlayer.posY, ids.mc.thePlayer.posZ);
                failSafeCounter1 = 0;

                if (Math.abs(currPlayerPos.getX() - ids.mc.thePlayer.posX) < 0.5 &&  Math.abs(currPlayerPos.getZ() - ids.mc.thePlayer.posZ) < 0.5) {
                    SendChat.chat("GGGGG");
                    if (timesTriggered >= 5) {
                        SendChat.chat(prefix.prefix + "Detected stuck! Restarting!");
                        stopWalking();
                        KeyBinding.setKeyBindState(JUMP.getKeyCode(), false);
                        KeyBinding.setKeyBindState(FORWARD.getKeyCode(), false);
                        timesTriggered = 0;

                        WaitThenCall.waitThenCall(1000, "restartPathfinder");
                    }

                    timesTriggered++;
                } else {
                    timesTriggered = 0;
                }
            }

            failSafeCounter1++;
        }
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if (startWalking) {
            if (!isOnBlock(nextBlock, 1)) {
                if (ids.mc.thePlayer.posY == nextBlock.getY()) {
                    if (!FORWARD.isKeyDown()) {
                        KeyBinding.setKeyBindState(FORWARD.getKeyCode(), true);
                    }
                } else if (ids.mc.thePlayer.posY < nextBlock.getY()) {
                    KeyBinding.setKeyBindState(JUMP.getKeyCode(), true);
                    KeyBinding.setKeyBindState(FORWARD.getKeyCode(), true);
                } else if (ids.mc.thePlayer.posY > nextBlock.getY()) {
                    KeyBinding.setKeyBindState(FORWARD.getKeyCode(), true);
                }
            } else {
                RenderMultipleBlocksMod.stopRenderBlock(new Vec3(nextBlock.getX(), nextBlock.getY(), nextBlock.getZ()));
                KeyBinding.setKeyBindState(FORWARD.getKeyCode(), false);
                KeyBinding.setKeyBindState(JUMP.getKeyCode(), false);
                walkOnPath(blockRoute);
            }
        }
    }

    public static boolean isOnBlock(BlockPos pos, double minDist) {
        return DistanceFromTo.distanceFromTo(new BlockPos(ids.mc.thePlayer.posX, ids.mc.thePlayer.posY, ids.mc.thePlayer.posZ), pos) < minDist;
    }

    public static boolean canJump(BlockPos block) {
        return ids.mc.thePlayer.posY < block.getY();
    }

    public static boolean canFall(BlockPos block) {
        return ids.mc.thePlayer.posY > block.getY();
    }

    public static void stopWalking() {
        nextBlock = null;
        startWalking = false;
        blockRoute = null;
    }
}
