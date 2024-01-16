package io.github.scopedknife;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.block.data.Ageable;

import java.util.Set;
import java.util.HashSet;

public class SporeGrowth extends JavaPlugin implements Listener {
    private int horizontalRadius = 4;
    private int verticalRadius = 5;
    private Set<Location> boostedCrops = new HashSet<>(); // Stores locations of boosted crops

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block placedBlock = event.getBlockPlaced();
        if (placedBlock.getType() == Material.SPORE_BLOSSOM) {
            addBoostedCrops(placedBlock.getLocation());
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block brokenBlock = event.getBlock();
        if (brokenBlock.getType() == Material.SPORE_BLOSSOM) {
            removeBoostedCrops(brokenBlock.getLocation());
        }
    }

    @EventHandler
    public void onBlockGrow(BlockGrowEvent event) {
        Block block = event.getBlock();
        if (boostedCrops.contains(block.getLocation())) {
            if (block.getBlockData() instanceof Ageable ageable) {
                if (ageable.getAge() < ageable.getMaximumAge()) {
                    int newAge = Math.min(ageable.getAge() + 2, ageable.getMaximumAge());
                    ageable.setAge(newAge);
                    block.setBlockData(ageable);
                    event.setCancelled(true);
                }
            }
        }
    }




    private void addBoostedCrops(Location sporeBlossomLocation) {
        for (int xOffset = -horizontalRadius; xOffset <= horizontalRadius; xOffset++) {
            for (int zOffset = -horizontalRadius; zOffset <= horizontalRadius; zOffset++) {
                for (int yOffset = -verticalRadius; yOffset <= -1; yOffset++) {
                    Location cropLocation = sporeBlossomLocation.clone().add(xOffset, yOffset, zOffset);
                    boostedCrops.add(cropLocation);
                }
            }
        }
    }

    private void removeBoostedCrops(Location sporeBlossomLocation) {
        for (int xOffset = -horizontalRadius; xOffset <= horizontalRadius; xOffset++) {
            for (int zOffset = -horizontalRadius; zOffset <= horizontalRadius; zOffset++) {
                for (int yOffset = -verticalRadius; yOffset <= -1; yOffset++) {
                    Location cropLocation = sporeBlossomLocation.clone().add(xOffset, yOffset, zOffset);
                    boostedCrops.remove(cropLocation);
                }
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("setradius")) {
            if (!sender.hasPermission("sporegrowth.setradius")) {
                sender.sendMessage("You don't have permission to use this command.");
                return true;
            }

            if (args.length != 2) {
                return false;
            }

            try {
                horizontalRadius = Integer.parseInt(args[0]);
                verticalRadius = Integer.parseInt(args[1]);

                sender.sendMessage("Radii set successfully.");
            } catch (NumberFormatException e) {
                sender.sendMessage("Invalid radius. Please enter a number.");
            }

            return true;
        }

        return false; // Unknown command, let Bukkit handle it
    }


}
