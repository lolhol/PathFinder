package com.dillo.Pathfinding;

import com.dillo.ArmadilloMain.ArmadilloStates;
import com.dillo.dilloUtils.LookAt;
import com.dillo.dilloUtils.TpUtils.WaitThenCall;
import com.dillo.utils.previous.SendChat;
import com.dillo.utils.previous.random.ids;
import com.dillo.utils.previous.random.prefix;
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

    public static void walkOnPath(List<BlockPos> route) {
        blockRoute = route;

        if (blockRoute.size() > 0) {
            nextBlock = blockRoute.get(0);
            blockRoute.remove(0);
            LookAt.smoothLook(LookAt.getRotation(new Vec3(nextBlock.getX(), nextBlock.getY() + 1, nextBlock.getZ())), 30);
            WaitThenCall.waitThenCall(40, "startWalkingPath");
        } else {
            SendChat.chat(prefix.prefix + "Path end!");
            ArmadilloStates.offlineState = "offline";
        }
    }

    public static void startWalkingPath() {
        ArmadilloStates.currentState = null;
        SendChat.chat("WALKING");

        if (nextBlock != null) {
            startWalking = true;
        } else {
            SendChat.chat("Stopped walking on path!");
        }
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if (startWalking) {
            if (!isOnBlock(nextBlock)) {
                if (!FORWARD.isKeyDown()) {
                    KeyBinding.setKeyBindState(FORWARD.getKeyCode(), true);
                }
            } else {
                KeyBinding.setKeyBindState(FORWARD.getKeyCode(), false);
                startWalking = false;
                walkOnPath(blockRoute);
            }
        }
    }

    public static boolean isOnBlock(BlockPos pos) {
        if (Math.abs(ids.mc.thePlayer.posX - pos.getX()) < 0.5 && Math.abs(ids.mc.thePlayer.posZ - pos.getZ()) < 0.5) {
            return true;
        }

        return false;
    }
}
