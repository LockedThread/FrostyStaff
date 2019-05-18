package dev.lockedthread.frostyfactions.staff.enums;

import com.gameservergroup.gsgcore.utils.Text;
import org.bukkit.configuration.file.FileConfiguration;

public enum Messages {

    STAFF_AUTHENTICATOR_CHOSE_A_CODE("\n\n&cChose a staff authenticator code by saying it in chat!\n\n"),
    STAFF_AUTHENTICATOR_ADDED_DEFAULT_IP("&aThe IP {ip} has been added to your IP whitelist. Contact higher staff to add more ips to your IP whitelist."),
    STAFF_AUTHENTICATOR_CODE_SET("&aYou have successfully set your code to {code}!"),
    STAFF_AUTHENTICATOR_CODE_TOO_LONG("&cYour code is too long, pick another one!"),
    STAFF_AUTHENTICATOR_CODE_TOO_SHORT("&cYour code is too short, pick another one!"),
    STAFF_AUTHENTICATOR_CODE_ENTER("&cEnter your code:"),
    STAFF_AUTHENTICATOR_CODE_CORRECT("&aYour code was correct!"),
    STAFF_AUTHENTICATOR_CODE_INCORRECT("&cThat code was incorrect, please enter your code again!"),

    STAFF_CHAT_FORMAT("&8[&b&lStaffChat&8] {displayname} &8» &f{message}"),
    STAFF_CHAT_ENABLED("&aYou have enabled staffchat!"),
    STAFF_CHAT_DISABLED("&aYou have disabled staffchat!"),
    ;

    private String rawMessage;

    Messages(String rawMessage) {
        this.rawMessage = rawMessage;
    }

    public String getKey() {
        return name().toLowerCase().replace("_", "-");
    }

    public String getRawMessage() {
        return rawMessage;
    }

    public void setRawMessage(String rawMessage) {
        this.rawMessage = rawMessage;
    }

    public String format(String s, String replacement) {
        return toString().replace(s, replacement);
    }

    @Override
    public String toString() {
        return Text.toColor(rawMessage);
    }

    public static void load(FileConfiguration configuration) {
        for (Messages message : values()) {
            if (configuration.isSet("messages." + message.getKey())) {
                message.setRawMessage(configuration.getString("messages." + message.getKey()));
            } else {
                configuration.set("messages." + message.getKey(), message.getRawMessage());
            }
        }
    }
}
