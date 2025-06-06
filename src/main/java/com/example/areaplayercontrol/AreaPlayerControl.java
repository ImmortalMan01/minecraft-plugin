package com.example.areaplayercontrol;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.IncompleteRegionException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AreaPlayerControl extends JavaPlugin {
    private Map<String, Region> regions = new HashMap<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadRegions();
    }

    @Override
    public void onDisable() {
        saveRegions();
    }

    private void loadRegions() {
        FileConfiguration config = getConfig();
        if (config.isConfigurationSection("regions")) {
            for (String key : config.getConfigurationSection("regions").getKeys(false)) {
                String path = "regions." + key + ".";
                Location pos1 = config.getLocation(path + "pos1");
                Location pos2 = config.getLocation(path + "pos2");
                if (pos1 != null && pos2 != null) {
                    regions.put(key, new Region(pos1, pos2));
                }
            }
        }
    }

    private void saveRegions() {
        FileConfiguration config = getConfig();
        config.set("regions", null); // clear
        for (Map.Entry<String, Region> entry : regions.entrySet()) {
            String path = "regions." + entry.getKey() + ".";
            config.set(path + "pos1", entry.getValue().pos1);
            config.set(path + "pos2", entry.getValue().pos2);
        }
        saveConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("/area <save|remove|info|list> [name]");
            return true;
        }
        String sub = args[0].toLowerCase();
        if (sub.equals("save")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Only players can use this command");
                return true;
            }
            if (args.length < 2) {
                sender.sendMessage("Usage: /area save <name>");
                return true;
            }
            Player player = (Player) sender;
            Region region = getSelection(player);
            if (region == null) {
                sender.sendMessage("You must make a WorldEdit selection first");
                return true;
            }
            regions.put(args[1], region);
            sender.sendMessage("Region " + args[1] + " saved.");
            return true;
        } else if (sub.equals("remove")) {
            if (args.length < 2) {
                sender.sendMessage("Usage: /area remove <name>");
                return true;
            }
            if (regions.remove(args[1]) != null) {
                sender.sendMessage("Region " + args[1] + " removed.");
            } else {
                sender.sendMessage("Region not found");
            }
            return true;
        } else if (sub.equals("info")) {
            if (args.length < 2) {
                sender.sendMessage("Usage: /area info <name>");
                return true;
            }
            Region region = regions.get(args[1]);
            if (region != null) {
                sender.sendMessage("Region " + args[1] + ":");
                sender.sendMessage("Pos1: " + formatLocation(region.pos1));
                sender.sendMessage("Pos2: " + formatLocation(region.pos2));
                int players = countPlayers(region);
                sender.sendMessage("Players inside: " + players);
            } else {
                sender.sendMessage("Region not found");
            }
            return true;
        } else if (sub.equals("list")) {
            sender.sendMessage("Regions:");
            for (String key : regions.keySet()) {
                sender.sendMessage("- " + key);
            }
            return true;
        }
        return false;
    }

    private Region getSelection(Player player) {
        WorldEditPlugin we = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
        if (we == null) {
            player.sendMessage("WorldEdit not found");
            return null;
        }
        LocalSession session = WorldEdit.getInstance().getSessionManager().get(BukkitAdapter.adapt(player));
        com.sk89q.worldedit.regions.Region sel;
        try {
            sel = session.getSelection(BukkitAdapter.adapt(player.getWorld()));
        } catch (IncompleteRegionException e) {
            return null;
        }
        BlockVector3 min = sel.getMinimumPoint();
        BlockVector3 max = sel.getMaximumPoint();
        Location pos1 = new Location(player.getWorld(), min.getX(), min.getY(), min.getZ());
        Location pos2 = new Location(player.getWorld(), max.getX(), max.getY(), max.getZ());
        return new Region(pos1, pos2);
    }

    private String formatLocation(Location loc) {
        return String.format("%s,%d,%d,%d", loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }

    private int countPlayers(Region region) {
        int count = 0;
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (region.contains(p.getLocation())) {
                count++;
            }
        }
        return count;
    }

    private static class Region {
        final Location pos1;
        final Location pos2;

        Region(Location pos1, Location pos2) {
            this.pos1 = pos1;
            this.pos2 = pos2;
        }

        boolean contains(Location loc) {
            if (!loc.getWorld().equals(pos1.getWorld())) return false;
            double x1 = Math.min(pos1.getX(), pos2.getX());
            double x2 = Math.max(pos1.getX(), pos2.getX());
            double y1 = Math.min(pos1.getY(), pos2.getY());
            double y2 = Math.max(pos1.getY(), pos2.getY());
            double z1 = Math.min(pos1.getZ(), pos2.getZ());
            double z2 = Math.max(pos1.getZ(), pos2.getZ());
            return loc.getX() >= x1 && loc.getX() <= x2 &&
                   loc.getY() >= y1 && loc.getY() <= y2 &&
                   loc.getZ() >= z1 && loc.getZ() <= z2;
        }
    }
}
