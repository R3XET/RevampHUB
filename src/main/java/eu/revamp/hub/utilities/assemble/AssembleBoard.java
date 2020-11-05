package eu.revamp.hub.utilities.assemble;

import eu.revamp.hub.utilities.assemble.events.AssembleBoardCreatedEvent;
import eu.revamp.spigot.utils.chat.color.CC;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Getter
public class AssembleBoard {
  private List<AssembleBoardEntry> entries = new ArrayList<>();
  private List<String> identifiers = new ArrayList<>();
  private Scoreboard scoreboard;
  private Objective objective;
  private UUID uuid;
  private Assemble assemble;

  public AssembleBoard(Player player, Assemble assemble) {
    this.assemble = assemble;
    setup(player);
    this.uuid = player.getUniqueId();
  }

  private static String getRandomChatColor() {
    return ChatColor.values()[ThreadLocalRandom.current().nextInt((ChatColor.values()).length)].toString();
  }

  private void setup(Player player) {
    if (getAssemble().isHook() || player.getScoreboard() != Bukkit.getScoreboardManager().getMainScoreboard()) {
      this.scoreboard = player.getScoreboard();
    } else {
      this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
    }
    this.objective = this.scoreboard.registerNewObjective("Default", "dummy");
    this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    this.objective.setDisplayName(getAssemble().getAdapter().getTitle(player));
    player.setScoreboard(this.scoreboard);
    AssembleBoardCreatedEvent createdEvent = new AssembleBoardCreatedEvent(this);
    Bukkit.getPluginManager().callEvent(createdEvent);
  }

  public Objective getOrCreateObjective(Scoreboard scoreboard, String objective, String type) {
    Objective value = scoreboard.getObjective(objective);
    if (value == null)
      value = scoreboard.registerNewObjective(objective, type);
    value.setDisplayName(objective);
    return value;
  }

  public AssembleBoardEntry getEntryAtPosition(int pos) {
    if (pos >= this.entries.size())
      return null;
    return this.entries.get(pos);
  }

  public String getUniqueIdentifier(String text) {
    String identifier = getRandomChatColor() + ChatColor.WHITE;
    while (this.identifiers.contains(identifier))
      identifier = identifier + getRandomChatColor() + ChatColor.WHITE;
    if (identifier.length() > 16)
      return getUniqueIdentifier(text);
    this.identifiers.add(identifier);
    return identifier;
  }
}
