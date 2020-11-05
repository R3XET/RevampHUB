package eu.revamp.hub.messages;

import java.util.Arrays;

import eu.revamp.hub.RevampHub;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public enum Message {
    PREFIX("prefix", "&7(&3RevampHub&7) &8»"),
    SPAWN_SET("spawn-set", "<prefix> &aYou have updated spawn location to your location."),
    GADGET_NON_APPLIED("GADGETS.NON-APPLIED", "<prefix> &cYou don't have any gadgets applied."),
    GADGET_REMOVED("GADGETS.REMOVED", "<prefix> &aYou've removed your active gadget."),
    GADGET_APPLIED("GADGETS.APPLIED", "<prefix> &aYou have applied &b<gadget> &agadget."),
    GADGET_INVALID("GADGETS.INVALID", "<prefix> &cGadget with name '&c&l<name>&c' doesn't exists."),
    GADGET_NO_PERMISSION("GADGETS.NO-PERM", "<prefix> &cYou don't have access to &b<gadget> &cgadget."),
    QUEUE_PAUSED("QUEUE.PAUSED", "<prefix> &eQueue for &6<server> &eis currently paused. Please wait!"),
    QUEUE_JOINED("QUEUE.JOINED", "<prefix> &eYou've joined the queue for &a<server>&e."),
    QUEUE_LEAVE("QUEUE.LEFT", "<prefix> &eYou've left the queue for &a<server>&e."),
    QUEUE_NOT_QUEUED("QUEUE.NOT-QUEUED", "<prefix> &cYou're not currently queued."),
    QUEUE_ALREADY_QUEUED("QUEUE.ALREADY-QUEUED", "<prefix> &cYou're already in a queue."),
    QUEUE_INVALID_SERVER("QUEUE.INVALID-SERVER", "<prefix> &cQueue for that server doesn't exists"),
    QUEUE_PAUSED_BY_PLAYER("QUEUE.PAUSED-BY-PLAYER", "<prefix> &eYou've &a<option> &equeue for &b<server>"),
    QUEUE_HIGHER_PRIORITY("QUEUE.HIGHER-PRIORITY-JOINED", "<prefix> &eSomeone with higher priority just joined your queue."),
    PLAYER_VISIBILITY_COOLDOWN("PLAYER-VISIBILITY-COOLDOWN", "<prefix> &cYou can't use this action for another &c&l<time>&c."),
    SENDING_TO_SERVER("SENDING-TO-SERVER", "<prefix> &aSending you to <server>.."),
    CANT_USE_CHAT("CANT-USE-CHAT", "<prefix> &cYou don't have permission to chat on this server.");
    
    private RevampHub plugin = RevampHub.INSTANCE;
    private String path;
    private String message;

    Message(String path, String message) {
        this.path = path;
        this.message = message;
    }

    public static void load(RevampHub plugin) {
        Arrays.stream(Message.values()).forEach(message -> {
            if (plugin.getMessages().getString(message.getPath(), true) != null) return;
            plugin.getMessages().set(message.getPath(), message.getMessage());
        });
        plugin.getMessages().save();
    }

    public String get() {
        return this.plugin.getMessages().getString(this.path).replace("<prefix>", this.plugin.getMessages().getString("prefix"));
    }

    public String get(Player player) {
        return this.plugin.getCoreHandler().translate(player, this.get());
    }
}

