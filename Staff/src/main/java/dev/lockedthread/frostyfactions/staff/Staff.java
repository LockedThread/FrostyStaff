package dev.lockedthread.frostyfactions.staff;

import com.gameservergroup.gsgcore.plugin.Module;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Staff extends Module {

    private static Staff instance;
    private Set<UUID> staffPlayers, frozenPlayers;

    @Override
    public void enable() {
        instance = this;
    }

    @Override
    public void disable() {

    }

    public static Staff getInstance() {
        return instance;
    }

    public Set<UUID> getStaffPlayers() {
        return staffPlayers != null ? staffPlayers : (this.staffPlayers = new HashSet<>());
    }

    public Set<UUID> getFrozenPlayers() {
        return frozenPlayers != null ? frozenPlayers : (this.frozenPlayers = new HashSet<>());
    }
}
