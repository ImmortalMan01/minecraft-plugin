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
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.ChatColor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Collections;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import com.example.areaplayercontrol.RegionPlaceholderExpansion;

public class AreaPlayerControl extends JavaPlugin {
    private Map<String, Region> regions = new HashMap<>();
    /* Tracks how many players are currently inside each region */
    final Map<String, Integer> regionCounts = new HashMap<>();
    /* Tracks which region a player is currently inside */
    final Map<UUID, String> playerRegion = new HashMap<>();
    private final String baseCommand = "area";
    private String cmdSave;
    private String cmdRemove;
    private String cmdInfo;
    private String cmdList;
    private String cmdMenu;
    private String cmdReload;
    private String language = "en";
    private Map<String, String> descriptions = new HashMap<>();
    private Map<String, String> messages = new HashMap<>();
    private PlaceholderExpansion expansion;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        FileConfiguration config = getConfig();

        language = config.getString("language", "en").toLowerCase();

        cmdSave = config.getString("commands.save", "save").toLowerCase();
        cmdRemove = config.getString("commands.remove", "remove").toLowerCase();
        cmdInfo = config.getString("commands.info", "info").toLowerCase();
        cmdList = config.getString("commands.list", "list").toLowerCase();
        cmdMenu = config.getString("commands.menu", "menu").toLowerCase();
        cmdReload = config.getString("commands.reload", "reload").toLowerCase();

        loadLocalizedSection(config, "descriptions", descriptions, false);
        loadLocalizedSection(config, "messages", messages, true);

        registerBaseCommand();
        loadRegions();

        initializeTracking();
        Bukkit.getPluginManager().registerEvents(new RegionPlayerListener(this), this);

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            expansion = new RegionPlaceholderExpansion(this);
            expansion.register();
        } else {
            getLogger().info("PlaceholderAPI not found, placeholders disabled");
        }
    }

    @Override
    public void onDisable() {
        saveRegions();
        if (expansion != null) {
            expansion.unregister();
        }
        regionCounts.clear();
        playerRegion.clear();
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

    /**
     * Initialise regionCounts and playerRegion based on current regions and
     * online players.
     */
    private void initializeTracking() {
        regionCounts.clear();
        playerRegion.clear();
        for (String name : regions.keySet()) {
            regionCounts.put(name, 0);
        }
        for (Player p : Bukkit.getOnlinePlayers()) {
            String region = getRegionName(p.getLocation());
            if (region != null) {
                playerRegion.put(p.getUniqueId(), region);
                regionCounts.put(region, regionCounts.get(region) + 1);
            }
        }
    }

    /**
     * Find the name of the region containing the given location, or null if none.
     */
    String getRegionName(Location loc) {
        for (Map.Entry<String, Region> e : regions.entrySet()) {
            if (e.getValue().contains(loc)) {
                return e.getKey();
            }
        }
        return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(msg("onlyPlayers"));
            return true;
        }
        if (!sender.isOp()) {
            sender.sendMessage(msg("noPermission"));
            return true;
        }
        if (args.length == 0) {
            // Show the command menu when no sub command is provided
            sender.sendMessage(msg("menuHeader"));
            sendMenuEntry(sender, cmdSave, " <name>", descriptions.get(cmdSave));
            sendMenuEntry(sender, cmdRemove, " <name>", descriptions.get(cmdRemove));
            sendMenuEntry(sender, cmdInfo, " <name>", descriptions.get(cmdInfo));
            sendMenuEntry(sender, cmdList, "", descriptions.get(cmdList));
            sendMenuEntry(sender, cmdMenu, "", descriptions.get(cmdMenu));
            sendMenuEntry(sender, cmdReload, "", descriptions.get(cmdReload));
            return true;
        }
        String sub = args[0].toLowerCase();
        if (sub.equals(cmdSave)) {
            if (args.length < 2) {
                sender.sendMessage(msg("usageSave").replace("%cmd%", baseCommand).replace("%save%", cmdSave));
                return true;
            }
            Player player = (Player) sender;
            Region region = getSelection(player);
            if (region == null) {
                sender.sendMessage(msg("needSelection"));
                return true;
            }
            regions.put(args[1], region);
            regionCounts.put(args[1], 0);
            sender.sendMessage(msg("regionSaved", Map.of("name", args[1])));
            return true;
        } else if (sub.equals(cmdRemove)) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.YELLOW + "Usage: /" + baseCommand + " " + cmdRemove + " <name>");
                return true;
            }
            if (regions.remove(args[1]) != null) {
                regionCounts.remove(args[1]);
                playerRegion.entrySet().removeIf(e -> e.getValue().equals(args[1]));
                sender.sendMessage(msg("regionRemoved", Map.of("name", args[1])));
            } else {
                sender.sendMessage(msg("regionNotFound"));
            }
            return true;
        } else if (sub.equals(cmdInfo)) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.YELLOW + "Usage: /" + baseCommand + " " + cmdInfo + " <name>");
                return true;
            }
            Region region = regions.get(args[1]);
            if (region != null) {
                sender.sendMessage(msg("regionHeader", Map.of("name", args[1])));
                sender.sendMessage(msg("pos1", Map.of("pos1", formatLocation(region.pos1))));
                sender.sendMessage(msg("pos2", Map.of("pos2", formatLocation(region.pos2))));
                int players = countPlayers(region);
                sender.sendMessage(msg("playersInside", Map.of("count", String.valueOf(players))));
            } else {
                sender.sendMessage(msg("regionNotFound"));
            }
            return true;
        } else if (sub.equals(cmdList)) {
            if (args.length >= 2) {
                String name = args[1];
                if (regions.containsKey(name)) {
                    String placeholder = "%areaplayercontrol_players_" + name + "%";
                    sender.sendMessage(msg("placeholderInfo", Map.of(
                            "name", name,
                            "placeholder", placeholder
                    )));
                } else {
                    sender.sendMessage(msg("regionNotFound"));
                }
            } else {
                sender.sendMessage(msg("regionsHeader"));
                for (String key : regions.keySet()) {
                    sender.sendMessage(msg("regionEntry", Map.of("name", key)));
                }
            }
            return true;
        } else if (sub.equals(cmdMenu)) {
            sender.sendMessage(msg("menuHeader"));
            sendMenuEntry(sender, cmdSave, " <name>", descriptions.get(cmdSave));
            sendMenuEntry(sender, cmdRemove, " <name>", descriptions.get(cmdRemove));
            sendMenuEntry(sender, cmdInfo, " <name>", descriptions.get(cmdInfo));
            sendMenuEntry(sender, cmdList, "", descriptions.get(cmdList));
            sendMenuEntry(sender, cmdMenu, "", descriptions.get(cmdMenu));
            sendMenuEntry(sender, cmdReload, "", descriptions.get(cmdReload));
            return true;
        } else if (sub.equals(cmdReload)) {
            reloadPlugin();
            sender.sendMessage(msg("reloaded"));
            return true;
        }
        return false;
    }

    @Override
    public java.util.List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            java.util.List<String> subs = java.util.Arrays.asList(cmdSave, cmdRemove, cmdInfo, cmdList, cmdMenu, cmdReload);
            String prefix = args[0].toLowerCase();
            java.util.List<String> result = new java.util.ArrayList<>();
            for (String s : subs) {
                if (s.startsWith(prefix)) {
                    result.add(s);
                }
            }
            return result;
        }
        return java.util.Collections.emptyList();
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


    public int getPlayersInRegion(String name) {
        return regionCounts.getOrDefault(name, 0);
    }

    private void registerBaseCommand() {
        PluginCommand cmd = getCommand("areaplayercontrol");
        if (cmd == null) {
            getLogger().warning("Base command registration failed");
            return;
        }

        cmd.setExecutor(this);
        cmd.setTabCompleter(this);
        cmd.setAliases(Collections.singletonList(baseCommand));
        cmd.setDescription("Manage areas");
        cmd.setUsage("/" + baseCommand + " <" + String.join("|",
                cmdSave, cmdRemove, cmdInfo, cmdList, cmdMenu, cmdReload) + "> [name]");
    }

    private CommandMap getCommandMap() throws Exception {
        Field f = Bukkit.getServer().getClass().getDeclaredField("commandMap");
        f.setAccessible(true);
        return (CommandMap) f.get(Bukkit.getServer());
    }

    private String msg(String key) {
        return messages.getOrDefault(key, key);
    }

    private String msg(String key, Map<String, String> params) {
        String m = msg(key);
        if (params != null) {
            for (Map.Entry<String, String> e : params.entrySet()) {
                m = m.replace("%" + e.getKey() + "%", e.getValue());
            }
        }
        return m;
    }

    private void reloadPlugin() {
        reloadConfig();
        saveRegions();
        regions.clear();
        messages.clear();
        descriptions.clear();
        regionCounts.clear();
        playerRegion.clear();

        FileConfiguration config = getConfig();

        language = config.getString("language", "en").toLowerCase();

        cmdSave = config.getString("commands.save", "save").toLowerCase();
        cmdRemove = config.getString("commands.remove", "remove").toLowerCase();
        cmdInfo = config.getString("commands.info", "info").toLowerCase();
        cmdList = config.getString("commands.list", "list").toLowerCase();
        cmdMenu = config.getString("commands.menu", "menu").toLowerCase();
        cmdReload = config.getString("commands.reload", "reload").toLowerCase();

        loadLocalizedSection(config, "descriptions", descriptions, false);
        loadLocalizedSection(config, "messages", messages, true);

        registerBaseCommand();
        loadRegions();
        initializeTracking();
    }

    private void loadLocalizedSection(FileConfiguration config, String base, Map<String, String> target, boolean colorize) {
        String langPath = base + "." + language;
        if (config.isConfigurationSection(langPath)) {
            for (String key : config.getConfigurationSection(langPath).getKeys(false)) {
                String val = config.getString(langPath + "." + key);
                if (val != null) {
                    if (colorize) {
                        val = ChatColor.translateAlternateColorCodes('&', val);
                    }
                    target.put(key, val);
                }
            }
        }
        if (!language.equals("en")) {
            String enPath = base + ".en";
            if (config.isConfigurationSection(enPath)) {
                for (String key : config.getConfigurationSection(enPath).getKeys(false)) {
                    if (!target.containsKey(key)) {
                        String val = config.getString(enPath + "." + key);
                        if (val != null) {
                            if (colorize) {
                                val = ChatColor.translateAlternateColorCodes('&', val);
                            }
                            target.put(key, val);
                        }
                    }
                }
            }
        }
    }

    private void sendMenuEntry(CommandSender sender, String sub, String usage, String desc) {
        sender.sendMessage(msg("menuEntry", Map.of(
                "cmd", baseCommand,
                "sub", sub,
                "usage", usage,
                "desc", desc
        )));
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
