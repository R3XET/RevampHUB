package eu.revamp.hub.utilities.assemble;

import eu.revamp.spigot.utils.chat.color.CC;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.Collections;
import java.util.List;

public class AssembleThread extends Thread {
  private Assemble assemble;

  AssembleThread(Assemble paramAssemble) {
    this.assemble = paramAssemble;
    this.start();
  }

  public void run() {
    while (true) {
      try {
        tick();
      } catch (NullPointerException e) {
        e.printStackTrace();
      }
      try {
        sleep(this.assemble.getTicks() * 50L);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  private void tick() {
      for (Player player : this.assemble.getPlugin().getServer().getOnlinePlayers()) {
        AssembleBoard board = this.assemble.getBoards().get(player.getUniqueId());
        if (board == null)
          continue;
        Scoreboard scoreboard = board.getScoreboard();
        Objective objective = board.getObjective();
        String title = CC.translate(this.assemble.getAdapter().getTitle(player));
        if (!objective.getDisplayName().equals(title))
          objective.setDisplayName(title);
        List<String> newLines = this.assemble.getAdapter().getLines(player);
        if (newLines == null || newLines.isEmpty()) {
          board.getEntries().forEach(AssembleBoardEntry::remove);
          board.getEntries().clear();
        } else {
          if (!this.assemble.getAssembleStyle().isDecending())
            Collections.reverse(newLines);
          if (board.getEntries().size() > newLines.size())
            for (int j = newLines.size(); j < board.getEntries().size(); j++) {
              AssembleBoardEntry entry = board.getEntryAtPosition(j);
              if (entry != null)
                entry.remove();
            }
          int cache = this.assemble.getAssembleStyle().getStartNumber();
          for (int i = 0; i < newLines.size(); i++) {
            AssembleBoardEntry entry = board.getEntryAtPosition(i);
            String line = CC.translate(newLines.get(i));
            if (entry == null)
              entry = new AssembleBoardEntry(board, line);
            entry.setText(line);
            entry.setup();
            entry.send(
                    this.assemble.getAssembleStyle().isDecending() ? cache-- : cache++);
          }
        }
        if (player.getScoreboard() != scoreboard)
          player.setScoreboard(scoreboard);
      }
  }
}
