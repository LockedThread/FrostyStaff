package dev.lockedthread.frostyfactions.staff.menus;

import com.gameservergroup.gsgcore.menus.Menu;
import dev.lockedthread.frostyfactions.staff.Staff;

public class MenuFreeze extends Menu {

    public MenuFreeze() {
        super(Staff.getInstance().getConfig().getString("freeze.menu.name"), Staff.getInstance().getConfig().getInt("freeze.menu.size"));
    }

    @Override
    public void initialize() {

    }
}
