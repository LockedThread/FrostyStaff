package dev.lockedthread.frostyfactions.staff.menus;

import com.gameservergroup.gsgcore.menus.Menu;
import com.gameservergroup.gsgcore.menus.MenuItem;
import dev.lockedthread.frostyfactions.staff.Staff;
import dev.lockedthread.items.ItemStackBuilder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.function.Consumer;

public class MenuFreeze extends Menu {

    public MenuFreeze() {
        super(Staff.getInstance().getConfig().getString("freeze.menu.name"), Staff.getInstance().getConfig().getInt("freeze.menu.size"));
        setInventoryOpenEventConsumer(event -> Staff.getInstance().getFrozenPlayers().add(event.getPlayer().getUniqueId()));
        setInventoryCloseEventConsumer(event -> event.getPlayer().openInventory(getInventory()));
    }

    @Override
    public void initialize() {
        ConfigurationSection section = Staff.getInstance().getConfig().getConfigurationSection("freeze.menu.items");
        for (String key : section.getKeys(false)) {
            Consumer<InventoryClickEvent> consumer = key.equalsIgnoreCase("admit") ? (event -> {
                event.getWhoClicked().closeInventory();
                for (String command : Staff.getInstance().getConfig().getStringList("freeze.menu.admit-command")) {
                    Staff.getInstance().getServer().dispatchCommand(Staff.getInstance().getServer().getConsoleSender(), command.replace("{player}", event.getWhoClicked().getName()));
                }
            }) : (event -> event.setCancelled(true));
            ConfigurationSection itemSection = section.getConfigurationSection(key);
            MenuItem menuItem = MenuItem.of(ItemStackBuilder.of(itemSection).build()).setInventoryClickEventConsumer(consumer);
            setItem(itemSection.getInt("slot"), menuItem);
        }
    }
}
