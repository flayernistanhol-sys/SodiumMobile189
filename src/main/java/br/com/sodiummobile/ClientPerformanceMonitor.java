package br.com.sodiummobile;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.entity.living.LivingEvent;

/**
 * Diagnostic-only monitor for client and integrated-server tick timing,
 * living-entity updates and client chunk loads.
 * This class does not change vanilla gameplay behavior.
 */
public final class ClientPerformanceMonitor {
    private static final int REPORT_EVERY_TICKS = 200;
    private static final long SLOW_TICK_NANOS = 50000000L;

    private long clientTickStartNanos;
    private long clientTotalTickNanos;
    private long clientMaxTickNanos;
    private int clientMeasuredTicks;
    private int clientSlowTicks;
    private int clientChunksLoaded;
    private int clientLivingEntityUpdates;

    private long serverTickStartNanos;
    private long serverTotalTickNanos;
    private long serverMaxTickNanos;
    private int serverMeasuredTicks;
    private int serverSlowTicks;
    private int serverLivingEntityUpdates;

    public void register() {
        FMLCommonHandler.instance().bus().register(this);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            clientTickStartNanos = System.nanoTime();
            return;
        }

        if (event.phase != TickEvent.Phase.END || clientTickStartNanos == 0L) {
            return;
        }

        long elapsed = System.nanoTime() - clientTickStartNanos;
        clientTotalTickNanos += elapsed;
        clientMeasuredTicks++;

        if (elapsed > clientMaxTickNanos) {
            clientMaxTickNanos = elapsed;
        }
        if (elapsed >= SLOW_TICK_NANOS) {
            clientSlowTicks++;
        }

        if (clientMeasuredTicks >= REPORT_EVERY_TICKS) {
            long averageMillis = clientTotalTickNanos / clientMeasuredTicks / 1000000L;
            long maximumMillis = clientMaxTickNanos / 1000000L;

            System.out.println(
                "[Sodium Mobile] Cliente: tick medio=" + averageMillis
                + " ms, maximo=" + maximumMillis
                + " ms, ticks acima de 50 ms=" + clientSlowTicks
                + ", atualizacoes de entidades=" + clientLivingEntityUpdates
                + ", chunks carregados=" + clientChunksLoaded
                + " (ultimos " + clientMeasuredTicks + " ticks)."
            );

            clientTotalTickNanos = 0L;
            clientMaxTickNanos = 0L;
            clientMeasuredTicks = 0;
            clientSlowTicks = 0;
            clientChunksLoaded = 0;
            clientLivingEntityUpdates = 0;
        }
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            serverTickStartNanos = System.nanoTime();
            return;
        }

        if (event.phase != TickEvent.Phase.END || serverTickStartNanos == 0L) {
            return;
        }

        long elapsed = System.nanoTime() - serverTickStartNanos;
        serverTotalTickNanos += elapsed;
        serverMeasuredTicks++;

        if (elapsed > serverMaxTickNanos) {
            serverMaxTickNanos = elapsed;
        }
        if (elapsed >= SLOW_TICK_NANOS) {
            serverSlowTicks++;
        }

        if (serverMeasuredTicks >= REPORT_EVERY_TICKS) {
            long averageMillis = serverTotalTickNanos / serverMeasuredTicks / 1000000L;
            long maximumMillis = serverMaxTickNanos / 1000000L;

            System.out.println(
                "[Sodium Mobile] Servidor integrado: tick medio=" + averageMillis
                + " ms, maximo=" + maximumMillis
                + " ms, ticks acima de 50 ms=" + serverSlowTicks
                + ", atualizacoes de entidades=" + serverLivingEntityUpdates
                + " (ultimos " + serverMeasuredTicks + " ticks)."
            );

            serverTotalTickNanos = 0L;
            serverMaxTickNanos = 0L;
            serverMeasuredTicks = 0;
            serverSlowTicks = 0;
            serverLivingEntityUpdates = 0;
        }
    }

    @SubscribeEvent
    public void onLivingEntityUpdate(LivingEvent.LivingUpdateEvent event) {
        if (event.entity == null || event.entity.worldObj == null) {
            return;
        }

        if (event.entity.worldObj.isRemote) {
            clientLivingEntityUpdates++;
        } else {
            serverLivingEntityUpdates++;
        }
    }

    @SubscribeEvent
    public void onChunkLoad(ChunkEvent.Load event) {
        if (event.world != null && event.world.isRemote) {
            clientChunksLoaded++;
        }
    }
}
