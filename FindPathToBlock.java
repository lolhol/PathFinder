package com.dillo.Pathfinding;

import com.dillo.utils.DistanceFromTo;
import com.dillo.utils.previous.random.ids;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class FindPathToBlock {
    public static List<BlockPos> findPathToBlock(BlockPos block, int depth, List<BlockPos> routeBlocks, HashSet<BlockPos> alrVisited) {
        depth++;

        // Checks if the amount of recursions is less than 50 AKA how long the route is.
        // 1/2 to prevent lag, and 1/2 to make sure the route is not like 100000 blocks to get to a block 5 blocks away.
        if (depth >= 300)
            return null;

        if (block == null) {
            return null;
        }

        // Get list with optimized positions
        List<BlockPos> newBlockPositions = new ArrayList<>();
        newBlockPositions.add(new BlockPos(routeBlocks.get(routeBlocks.size() - 1).getX() + 1, routeBlocks.get(routeBlocks.size() - 1).getY(), routeBlocks.get(routeBlocks.size() - 1).getZ()));
        newBlockPositions.add(new BlockPos(routeBlocks.get(routeBlocks.size() - 1).getX(), routeBlocks.get(routeBlocks.size() - 1).getY(), routeBlocks.get(routeBlocks.size() - 1).getZ() + 1));
        newBlockPositions.add(new BlockPos(routeBlocks.get(routeBlocks.size() - 1).getX() - 1, routeBlocks.get(routeBlocks.size() - 1).getY(), routeBlocks.get(routeBlocks.size() - 1).getZ()));
        newBlockPositions.add(new BlockPos(routeBlocks.get(routeBlocks.size() - 1).getX(), routeBlocks.get(routeBlocks.size() - 1).getY(), routeBlocks.get(routeBlocks.size() - 1).getZ() - 1));
        List<BlockPos> bestBlockPositions = getClosestBlocks(newBlockPositions, block);

        // Performs the recursions via for loop because the "getClosestBlocks()" returns a list.
        for (int i = 0; i < bestBlockPositions.size(); i++) {
            BlockPos blockInQuestion = bestBlockPositions.get(i);
            // Checks to see if the optimal path has been found.
            if (blockInQuestion.equals(block))
                return routeBlocks;

            if (ids.mc.theWorld.getBlockState(blockInQuestion).getBlock() == Blocks.air) {
                if (!alrVisited.contains(blockInQuestion)) {
                    routeBlocks.add(blockInQuestion);
                    alrVisited.add(blockInQuestion);

                    List<BlockPos> foundPath = findPathToBlock(block, depth, routeBlocks, alrVisited);

                    if (foundPath != null) {
                        return foundPath;
                    }
                } else {
                    return null;
                }
            } else if (bestBlockPositions.size() == i - 1) {
                // This is an else-if because if it wouldn't be, the for loop would only check one side and if it was invalid
                // would return a null without checking the other sides.
                return null;
            }
        }

        return routeBlocks;
    }

    public static List<BlockPos> pathfinderTest(BlockPos block) {
        // A pathfinding sample code.

        List<BlockPos> foundRoute = new ArrayList<>();
        foundRoute.add(new BlockPos(ids.mc.thePlayer.posX, ids.mc.thePlayer.posY, ids.mc.thePlayer.posZ));

        foundRoute = findPathToBlock(block, 0, foundRoute, new HashSet<BlockPos>());

        return foundRoute;
    }

    public static List<BlockPos> getClosestBlocks(List<BlockPos> blocks, BlockPos block) {
        List<BlockPos> closestBlocks = new ArrayList<>();

        // A while re-checks the size of the list every time while a for loop only does it once at the start
        while (blocks.size() > 0) {
            closestBlocks.add(getSmallestFromList(blocks, block));
            blocks.remove(0);
        }

        return closestBlocks;
    }

    public static BlockPos getSmallestFromList(List<BlockPos> blocks, BlockPos block) {
        // A classical "get smallest in list" algorithm

        // If is not equal to zero because nothing is usually smaller than zero.
        double currSmallest = DistanceFromTo.distanceFromTo(blocks.get(0), block);
        int currSmallestPos = 0;

        for (int i = 1; i < blocks.size(); i++) {
            if (DistanceFromTo.distanceFromTo(blocks.get(i), block) <= currSmallest) {
                currSmallestPos = i;
            }
        }

        return blocks.get(currSmallestPos);
    }
}
