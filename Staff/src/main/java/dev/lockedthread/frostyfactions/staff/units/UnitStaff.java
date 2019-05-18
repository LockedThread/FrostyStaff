package dev.lockedthread.frostyfactions.staff.units;

import com.gameservergroup.gsgcore.commands.post.CommandPost;
import com.gameservergroup.gsgcore.events.EventFilters;
import com.gameservergroup.gsgcore.events.EventPost;
import com.gameservergroup.gsgcore.units.Unit;
import dev.lockedthread.frostyfactions.staff.Staff;
import dev.lockedthread.frostyfactions.staff.enums.Messages;
import dev.lockedthread.frostyfactions.staff.objs.StaffMember;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.*;

import java.util.UUID;
import java.util.function.Predicate;

public class UnitStaff extends Unit {

    @Override
    public void setup() {
        //noinspection unchecked
        cancelEvents(event -> {
            if (event.getPlayer().hasPermission("frostyfactions.staff")) {
                StaffMember staffMember = Staff.getInstance().getStaffPlayers().get(event.getPlayer().getUniqueId());
                return staffMember != null && !staffMember.isAuthenticated();
            }
            return false;
        }, new Class[]{PlayerMoveEvent.class, PlayerPickupItemEvent.class, PlayerDropItemEvent.class, PlayerFishEvent.class, PlayerInteractEntityEvent.class, PlayerItemConsumeEvent.class, PlayerBucketEmptyEvent.class, PlayerBucketFillEvent.class, PlayerInteractAtEntityEvent.class, PlayerArmorStandManipulateEvent.class, PlayerShearEntityEvent.class, PlayerEditBookEvent.class, PlayerEggThrowEvent.class});
        CommandPost.create()
                .builder()
                .assertPermission("frostyfactions.staff")
                .handler(commandHandler -> {
                    commandHandler.reply("&b&l[!] &fStaff Commands &b&l[!]");
                    commandHandler.reply("&b/staffchat &f- toggles staffchat");
                    commandHandler.reply("&b/freeze &f- toggles freeze for a player");
                    commandHandler.reply("&b");
                }).post(Staff.getInstance(), "staff");
        CommandPost.create()
                .builder()
                .assertPermission("frostyfactions.staff")
                .assertPlayer()
                .handler(commandHandler -> {
                    Player player = commandHandler.getSender();
                    StaffMember staffMember = Staff.getInstance().getStaffPlayers().get(player.getUniqueId());
                    if (staffMember != null) {
                        final boolean staffChat = staffMember.isStaffChat();
                        staffMember.setStaffChat(!staffChat);
                        commandHandler.reply(staffChat ? Messages.STAFF_CHAT_DISABLED : Messages.STAFF_CHAT_ENABLED);
                    } else {
                        player.sendMessage(ChatColor.RED + "Relog to use this.");
                    }
                }).post(Staff.getInstance(), "staffchat");

        EventPost.of(PlayerJoinEvent.class, EventPriority.HIGHEST)
                .filter(event -> event.getPlayer().hasPermission("frostyfactions.staff"))
                .handle(event -> {
                    Player player = event.getPlayer();
                    StaffMember staffMember = Staff.getInstance().getStaffPlayers().get(player.getUniqueId());
                    if (staffMember == null) {
                        Staff.getInstance().getStaffPlayers().put(player.getUniqueId(), staffMember = new StaffMember(player));
                        staffMember.setAuthenticated(false);
                        Staff.getInstance().getServer().getScheduler().runTaskLaterAsynchronously(Staff.getInstance(), () -> {
                            player.sendMessage(Messages.STAFF_AUTHENTICATOR_ADDED_DEFAULT_IP.format("{ip}", player.getAddress().getAddress().getHostAddress()));
                            player.sendMessage(Messages.STAFF_AUTHENTICATOR_CHOSE_A_CODE.toString());
                        }, 20L);
                    } else {
                        staffMember.setAuthenticated(false);
                        Staff.getInstance().getServer().getScheduler().runTaskLaterAsynchronously(Staff.getInstance(), () -> player.sendMessage(Messages.STAFF_AUTHENTICATOR_CODE_ENTER.toString()), 20L);
                    }
                }).post(Staff.getInstance());

        EventPost.of(AsyncPlayerChatEvent.class, EventPriority.LOWEST)
                .filter(EventFilters.getIgnoreCancelled())
                .filter(event -> event.getPlayer().hasPermission("frostyfactions.staff"))
                .handle(event -> {
                    Player player = event.getPlayer();
                    StaffMember staffMember = Staff.getInstance().getStaffPlayers().get(player.getUniqueId());
                    if (staffMember != null) {
                        String trim = event.getMessage().trim();
                        if (staffMember.hasChosenCode()) {
                            if (staffMember.isAuthenticated()) {
                                if (staffMember.isStaffChat()) {
                                    for (UUID uuid : Staff.getInstance().getStaffPlayers().keySet()) {
                                        Bukkit.getPlayer(uuid).sendMessage(Messages.STAFF_CHAT_FORMAT.toString().replace("{displayname}", player.getDisplayName()).replace("{message}", event.getMessage()));
                                    }
                                }
                            } else {
                                String code = staffMember.getCode();
                                if (trim.equals(code)) {
                                    staffMember.setAuthenticated(true);
                                    player.sendMessage(Messages.STAFF_AUTHENTICATOR_CODE_CORRECT.toString());
                                    event.setCancelled(true);
                                } else {
                                    player.sendMessage(Messages.STAFF_AUTHENTICATOR_CODE_INCORRECT.toString());
                                    event.setCancelled(true);
                                }
                            }
                        } else {
                            int length = trim.length();
                            if (length > Staff.getInstance().getConfig().getInt("staff.authenticator.max-length")) {
                                player.sendMessage(Messages.STAFF_AUTHENTICATOR_CODE_TOO_LONG.toString());
                            } else if (length < Staff.getInstance().getConfig().getInt("staff.authenticator.min-length")) {
                                player.sendMessage(Messages.STAFF_AUTHENTICATOR_CODE_TOO_SHORT.toString());
                            } else {
                                staffMember.setCode(trim);
                                player.sendMessage(Messages.STAFF_AUTHENTICATOR_CODE_SET.format("{code}", event.getMessage()));
                                staffMember.setAuthenticated(true);
                            }
                            event.setCancelled(true);
                        }
                    } else {
                        throw new RuntimeException("Somehow found " + player.toString() + " as a null staff member");
                    }
                }).post(Staff.getInstance());

        EventPost.of(BlockBreakEvent.class)
                .filter(event -> (event.getPlayer().hasPermission("frostyfactions.staff")))
                .filter(event -> {
                    StaffMember staffMember = Staff.getInstance().getStaffPlayers().get(event.getPlayer().getUniqueId());
                    return staffMember != null && !staffMember.isAuthenticated();
                })
                .handle(event -> event.setCancelled(true))
                .post(Staff.getInstance());

        EventPost.of(BlockPlaceEvent.class)
                .filter(event -> (event.getPlayer().hasPermission("frostyfactions.staff")))
                .filter(event -> {
                    StaffMember staffMember = Staff.getInstance().getStaffPlayers().get(event.getPlayer().getUniqueId());
                    return staffMember != null && !staffMember.isAuthenticated();
                })
                .handle(event -> event.setCancelled(true))
                .post(Staff.getInstance());
    }

    private <T extends PlayerEvent> void cancelEvents(Predicate<T> predicate, Class<T>[] playerEvents) {
        for (Class<T> playerEvent : playerEvents) {
            EventPost.of(playerEvent)
                    .filter(predicate)
                    .handle(event -> {
                        if (event instanceof Cancellable) {
                            ((Cancellable) event).setCancelled(true);
                        }
                    }).post(Staff.getInstance());
        }
    }
}