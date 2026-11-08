package br.com.sodiummobile;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.entity.living.LivingEvent;

/**
 * Lightweight client-side diagnostics for identifying tick stalls,
 * client chunk loads and living-entity update frequency.
 * This class measures performance; it does not change vanilla behavior.
 */
public final class ClientPerformanceMonitor {
    private static final int REPORT_EVERY_TICKS = 200;
    private static final long SLOW_TICK_NANOS = 50000000L;

    private long tickStartNanos;
    private long totalTickNanos;
    private long maxTickNanos;
    private int measuredTicks;
    private int slowTicks;
    private int clientChunksLoaded;
    private int livingEntityUpdates;

    public void register() {
        FMLCommonHandler.instance().bus().register(this);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            tickStartNanos = System.nanoTime();
            return;
        }

        if (event.phase == TickEvent.Phase.END && tickStartNanos != 0L) {
            long elapsed = System.nanoTime() - tickStartNanos;
            totalTickNanos += elapsed;
            measuredTicks++;

            if (elapsed > maxTickNanos) {
                maxTickNanos = elapsed;
            }
            if (elapsed >= SLOW_TICK_NANOS) {
                slowTicks++;
            }

            if (measuredTicks >= REPORT_EVERY_TICKS) {
                long averageMillis = totalTickNanos / measuredTicks / 1000000L;
                long maximumMillis = maxTickNanos / 1000000L;

                System.out.println(
                    "[Sodium Mobile] Diagnostico: tick medio=" + averageMillis
                    + " ms, maximo=" + maximumMillis
                    + " ms, ticks acima de 50 ms=" + slowTicks
                    + ", atualizacoes de entidades vivas=" + livingEntityUpdates
                    + ", chunks carregados=" + clientChunksLoaded
                    + " (ultimos " + measuredTicks + " ticks)."
                );

                totalTickNanos = 0L;
                maxTickNanos = 0L;
                measuredTicks = 0;
                slowTicks = 0;
                clientChunksLoaded = 0;
                livingEntityUpdates = 0;
            }
        }
    }

    @SubscribeEvent
    public void onLivingEntityUpdate(LivingEvent.LivingUpdateEvent event) {
        if (event.entity != null && event.entity.worldObj != null
                && event.entity.worldObj.isRemote) {
            livingEntityUpdates++;
        }
    }

    @SubscribeEvent
    public void onChunkLoad(ChunkEvent.Load event) {
        if (event.world != null && event.world.isRemote) {
            clientChunksLoaded++;
        }
    }
}
