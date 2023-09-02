package fr.synchroneyes.mineral.Core.Game;

import fr.synchroneyes.mineral.mineralcontest;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.Stack;

import static org.bukkit.Material.*;

public class BlockManager {

    // Singleton
    private static BlockManager instance;


    // Using a stack, because it is most likely that an recently added block gets removed first
    private Stack<Block> placedBlocks;
    private Material[] blockedMaterials = {AIR, OBSIDIAN, WATER, LAVA, BUCKET, LAVA_BUCKET, WATER_BUCKET, BEDROCK};



    private BlockManager() {
        instance = this;
        placedBlocks = new Stack<>();
    }

    public Stack<Block> getPlacedBlocks() {
        return placedBlocks;
    }

    public void addBlock(Block b) {
        // If block was added, we dont add it
        if (wasBlockAdded(b)) return;


        if (b.getType().equals(CHEST)) mineralcontest.getPlayerGame(b.getWorld().getPlayers().get(0)).addAChest(b);

        this.placedBlocks.add(b);
    }

    public boolean wasBlockAdded(Block b) {
        return this.placedBlocks.contains(b);
    }


    public static BlockManager getInstance() {
        if (instance == null) return new BlockManager();
        return instance;
    }


    public boolean isBlockAllowedToBeAdded(Block b) {
        for (Material blockedType : blockedMaterials)
            if (blockedType.toString().toLowerCase().contains(b.getType().toString().toLowerCase()))
                return false;
        return true;
    }

    public boolean isBlockAllowedToBeAdded(Material b) {
        for (Material blockedType : blockedMaterials) {
            if (blockedType.toString().toLowerCase().contains(b.toString().toLowerCase()))
                return false;
        }

        return true;
    }
}
