package eu.revamp.hub.scoreboard;

import java.util.ArrayList;
import java.util.List;

import eu.revamp.hub.RevampHub;
import eu.revamp.hub.handlers.Handler;
import eu.revamp.hub.queue.Queue;
import eu.revamp.hub.utilities.assemble.AssembleAdapter;
import org.bukkit.entity.Player;

public class ScorebardAdapter extends Handler implements AssembleAdapter {
    public ScorebardAdapter(RevampHub plugin) {
        super(plugin);
    }

    @Override
    public String getTitle(Player player) {
        return this.plugin.getCoreHandler().translate(player, this.plugin.getScoreboard().getString("title"));
    }

    @Override
    public List<String> getLines(Player player) {
        ArrayList<String> lines = new ArrayList<>();
        Queue queue = this.plugin.getQueueHandler().getQueueByPlayer(player);
        try {
            if (queue == null) {
                this.plugin.getScoreboard().getStringList("normal").forEach(line -> lines.add(line.replace("<player>", player.getName()).replace("<rank>", this.plugin.getCoreHandler().getPermissionSystem().getName(player)).replace("<pos>", String.valueOf(0)).replace("<queue_server>", "Unknown").replace("<global_count>", String.valueOf(this.plugin.getCoreHandler().getPlayerCount().getGlobalCount())).replace("<in_queue>", String.valueOf(0))));
                return this.plugin.getCoreHandler().translate(player, lines);
            }
            this.plugin.getScoreboard().getStringList("queued").forEach(line -> lines.add(line.replace("<player>", player.getName()).replace("<rank>", this.plugin.getCoreHandler().getPermissionSystem().getName(player)).replace("<pos>", String.valueOf(queue.getPosition(player))).replace("<queue_server>", queue.getServer()).replace("<global_count>", String.valueOf(this.plugin.getCoreHandler().getPlayerCount().getGlobalCount())).replace("<in_queue>", String.valueOf(queue.getInQueue()))));
        } catch (Exception e) {
            // remove errrors
        }
        return this.plugin.getCoreHandler().translate(player, lines);
    }
}

