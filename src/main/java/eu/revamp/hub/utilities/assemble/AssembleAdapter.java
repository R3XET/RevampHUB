package eu.revamp.hub.utilities.assemble;

import org.bukkit.entity.Player;

import java.util.List;

public interface AssembleAdapter {
  String getTitle(Player paramPlayer);
  
  List<String> getLines(Player paramPlayer);
}
