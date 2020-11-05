package me.allen.ziggurat.thread;

import lombok.Getter;
import me.allen.ziggurat.Ziggurat;
import me.allen.ziggurat.ZigguratTablist;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ZigguratThread
{
    @Getter
    private ScheduledExecutorService executorService;
    
    public ZigguratThread(Ziggurat ziggurat) {
        (this.executorService = Executors.newSingleThreadScheduledExecutor()).scheduleAtFixedRate(() -> {
            try {
                ziggurat.getTablists().values().forEach(ZigguratTablist::update);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }, 0L, ziggurat.getTicks() * 50L, TimeUnit.MILLISECONDS);
    }
}
