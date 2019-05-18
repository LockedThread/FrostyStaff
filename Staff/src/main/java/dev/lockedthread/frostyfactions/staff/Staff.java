package dev.lockedthread.frostyfactions.staff;

import com.gameservergroup.gsgcore.plugin.Module;
import com.gameservergroup.gsgcore.storage.JsonFile;
import com.google.gson.reflect.TypeToken;
import dev.lockedthread.frostyfactions.staff.enums.Messages;
import dev.lockedthread.frostyfactions.staff.objs.StaffMember;
import dev.lockedthread.frostyfactions.staff.units.UnitFreeze;
import dev.lockedthread.frostyfactions.staff.units.UnitStaff;

import java.util.*;

public class Staff extends Module {

    private static Staff instance;
    private Set<UUID> frozenPlayers;
    private HashMap<UUID, StaffMember> staffMembers;
    private JsonFile<HashMap<UUID, StaffMember>> staffMemberJsonFile;

    @Override
    public void enable() {
        instance = this;
        saveDefaultConfig();

        Messages.load(getConfig());

        this.frozenPlayers = new HashSet<>();

        this.staffMemberJsonFile = new JsonFile<>(getDataFolder(), "staffmembers.json", new TypeToken<HashMap<UUID, StaffMember>>() {
        });
        this.staffMembers = this.staffMemberJsonFile.load().orElseGet(HashMap::new);

        registerUnits(new UnitStaff(), new UnitFreeze());
    }

    @Override
    public void disable() {
        staffMemberJsonFile.save(staffMembers);
    }

    public static Staff getInstance() {
        return instance;
    }

    public Map<UUID, StaffMember> getStaffPlayers() {
        return staffMembers != null ? staffMembers : (this.staffMembers = new HashMap<>());
    }

    public Set<UUID> getFrozenPlayers() {
        return frozenPlayers != null ? frozenPlayers : (this.frozenPlayers = new HashSet<>());
    }
}
