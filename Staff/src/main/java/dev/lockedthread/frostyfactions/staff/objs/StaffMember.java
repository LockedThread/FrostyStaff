package dev.lockedthread.frostyfactions.staff.objs;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class StaffMember {

    private UUID uuid;
    private List<String> ipAddresses;
    private String code;

    private transient boolean staffMode = false;
    private transient boolean authenticated = false;
    private transient boolean staffChat = false;

    public StaffMember(Player player) {
        this.uuid = player.getUniqueId();
        this.ipAddresses = new ArrayList<>(Collections.singletonList(player.getAddress().getAddress().getHostAddress()));
    }

    public boolean hasChosenCode() {
        return code != null;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public List<String> getIpAddresses() {
        return ipAddresses;
    }

    public void setIpAddresses(List<String> ipAddresses) {
        this.ipAddresses = ipAddresses;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isStaffMode() {
        return staffMode;
    }

    public void setStaffMode(boolean staffMode) {
        this.staffMode = staffMode;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public boolean isStaffChat() {
        return staffChat;
    }

    public void setStaffChat(boolean staffChat) {
        this.staffChat = staffChat;
    }
}
