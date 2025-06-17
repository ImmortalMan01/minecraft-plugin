package com.example.areaplayercontrol;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Objects;

/**
 * Listener that tracks players entering and leaving saved regions.
 */
public class RegionPlayerListener implements Listener {
    private final AreaPlayerControl plugin;

    public RegionPlayerListener(AreaPlayerControl plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        updatePlayer(event.getPlayer(), event.getPlayer().getLocation());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        String region = plugin.playerRegion.remove(event.getPlayer().getUniqueId());
        if (region != null) {
            plugin.regionCounts.put(region, plugin.regionCounts.get(region) - 1);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.getTo() == null) return;
        if (sameBlock(event.getFrom(), event.getTo())) return;
        updatePlayer(event.getPlayer(), event.getTo());
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        if (event.getTo() != null) {
            updatePlayer(event.getPlayer(), event.getTo());
        }
    }

    private void updatePlayer(Player player, Location to) {
        String oldRegion = plugin.playerRegion.get(player.getUniqueId());
        String newRegion = plugin.getRegionName(to);
        if (Objects.equals(oldRegion, newRegion)) {
            return;
        }
        if (oldRegion != null) {
            plugin.regionCounts.put(oldRegion, plugin.regionCounts.get(oldRegion) - 1);
            plugin.playerRegion.remove(player.getUniqueId());
        }
        if (newRegion != null) {
            plugin.playerRegion.put(player.getUniqueId(), newRegion);
            plugin.regionCounts.put(newRegion, plugin.regionCounts.getOrDefault(newRegion, 0) + 1);
        }
    }

    private boolean sameBlock(Location a, Location b) {
        return a.getBlockX() == b.getBlockX() && a.getBlockY() == b.getBlockY() && a.getBlockZ() == b.getBlockZ() && Objects.equals(a.getWorld(), b.getWorld());
    }
}
