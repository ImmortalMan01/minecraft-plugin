package com.example.areaplayercontrol;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

/**
 * PlaceholderAPI expansion providing the number of saved regions.
 */
public class RegionPlaceholderExpansion extends PlaceholderExpansion {

    private final AreaPlayerControl plugin;

    public RegionPlaceholderExpansion(AreaPlayerControl plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String getIdentifier() {
        return "areaplayercontrol";
    }

    @Override
    public String getAuthor() {
        return String.join(",", plugin.getDescription().getAuthors());
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (identifier.equalsIgnoreCase("regions") || identifier.equalsIgnoreCase("region_count")) {
            return String.valueOf(plugin.getRegionCount());
        }
        return null;
    }
}
