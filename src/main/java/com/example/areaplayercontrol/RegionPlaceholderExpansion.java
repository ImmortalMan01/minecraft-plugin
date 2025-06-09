package com.example.areaplayercontrol;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

/**
 * PlaceholderAPI expansion that exposes the player count for each saved region.
 *
 * <p>Use placeholders of the form
 * <code>%areaplayercontrol_players_{region}%</code> where {@code region}
 * is the name of a saved region. The placeholder returns the number of players
 * currently inside that region.</p>
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
        if (identifier.toLowerCase().startsWith("players_")) {
            String name = identifier.substring("players_".length());
            return String.valueOf(plugin.getPlayersInRegion(name));
        }
        return null;
    }
}
