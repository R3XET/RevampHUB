package eu.revamp.hub.queue;

import org.bukkit.entity.Player;

public interface Queue {
    int getInQueue();

    int getPosition(Player var1);

    void addEntry(Player var1);

    void removeEntry(Player var1);

    boolean isPaused();

    void setPaused(boolean var1);

    boolean isQueued(Player var1);

    void check();

    String getServer();

    boolean isDefaultQueueSystem();
}

