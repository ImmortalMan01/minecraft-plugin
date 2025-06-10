package com.example.areaplayercontrol;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class AreaMenu implements InventoryHolder {
    private final AreaPlayerControl plugin;
    private final Inventory inventory;

    public AreaMenu(AreaPlayerControl plugin) {
        this.plugin = plugin;
        String title = ChatColor.translateAlternateColorCodes('&', plugin.getMenuTitle());
        this.inventory = Bukkit.createInventory(this, 9, title);
        createButtons();
    }

    private void createButtons() {
        inventory.setItem(2, createItem(Material.PAPER, plugin.getMenuButton("save")));
        inventory.setItem(4, createItem(Material.BARRIER, plugin.getMenuButton("remove")));
        inventory.setItem(6, createItem(Material.BOOK, plugin.getMenuButton("info")));
    }

    private ItemStack createItem(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
            item.setItemMeta(meta);
        }
        return item;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public void open(Player player) {
        player.openInventory(inventory);
        plugin.registerOpenMenu(player, this);
    }

    public void handleClick(Player player, InventoryClickEvent e) {
        e.setCancelled(true);
        int slot = e.getSlot();
        if (slot == 2) {
            player.performCommand(plugin.getBaseCommand() + " " + plugin.getCmdSave());
        } else if (slot == 4) {
            player.performCommand(plugin.getBaseCommand() + " " + plugin.getCmdRemove());
        } else if (slot == 6) {
            player.performCommand(plugin.getBaseCommand() + " " + plugin.getCmdInfo());
        }
    }
}
