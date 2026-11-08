package br.com.sodiummobile;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Reduces client rendering work by skipping distant living-entity models.
 * This only cancels drawing; entity simulation, AI, spawning and world ticks
 * continue normally.
 */
public final class EntityRenderCulling {
    private static final double MAX_RENDER_DISTANCE_SQ = 24.0D * 24.0D;

    public void register() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onRenderLiving(RenderLivingEvent.Pre event) {
        Minecraft minecraft = Minecraft.getMinecraft();

        if (minecraft.thePlayer == null || event.entity == null) {
            return;
        }

        // Never hide players.
        if (event.entity instanceof EntityPlayer) {
            return;
        }

        if (event.entity.getDistanceSqToEntity(minecraft.thePlayer)
                > MAX_RENDER_DISTANCE_SQ) {
            event.setCanceled(true);
        }
    }
}
