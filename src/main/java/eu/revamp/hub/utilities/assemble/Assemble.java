package eu.revamp.hub.utilities.assemble;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter @Setter
public class Assemble {
  @Getter private static Assemble instance;
  
  private JavaPlugin plugin;
  
  private AssembleAdapter adapter;
  
  private Map<UUID, AssembleBoard> boards;
  
  private AssembleThread thread;
  
  private AssembleListener listeners;
  
  private long ticks = 2L;
  
  private boolean hook = false;
  
  private AssembleStyle assembleStyle = AssembleStyle.MODERN;
  
  public Assemble(JavaPlugin paramJavaPlugin, AssembleAdapter paramAssembleAdapter) {
    if (paramJavaPlugin == null)
      throw new RuntimeException("Assemble can not be instantiated without a plugin instance!");
    instance = this;
    this.plugin = paramJavaPlugin;
    this.adapter = paramAssembleAdapter;
    this.boards = new ConcurrentHashMap<>();
    setup();
  }

  @SuppressWarnings("deprecation")
  private void setup() {
    this.listeners = new AssembleListener(this);
    this.plugin.getServer().getPluginManager().registerEvents(this.listeners, this.plugin);
    if (this.thread != null) {
      this.thread.stop();
      this.thread = null;
    } 
    this.thread = new AssembleThread(this);
  }
  @SuppressWarnings("deprecation")
  public void cleanup() {
    if (this.thread != null) {
      this.thread.stop();
      this.thread = null;
    } 
    if (this.listeners != null) {
      HandlerList.unregisterAll(this.listeners);
      this.listeners = null;
    } 
  }
}
