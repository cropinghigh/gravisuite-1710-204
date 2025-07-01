package gravisuite;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.RenderTickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gravisuite.ServerTickHandler;
import gravisuite.client.ClientTickHandler;

public class TickHandler {
   public TickHandler() {
      FMLCommonHandler.instance().bus().register(this);
   }

   @SubscribeEvent
   public void onPlayerTick(PlayerTickEvent event) {
      if(event.phase == Phase.START) {
         if(event.side == Side.SERVER) {
            ServerTickHandler.ontickPlayer(event.player);
         } else {
            ClientTickHandler.onTickPlayer(event.player);
         }
      }

   }

   @SideOnly(Side.CLIENT)
   @SubscribeEvent
   public void renderTick(RenderTickEvent event) {
      if(event.phase != Phase.START) {
         ClientTickHandler.onTickRedner();
      }

   }

   @SideOnly(Side.CLIENT)
   @SubscribeEvent
   public void onClientTick(ClientTickEvent event) {
      if(event.phase == Phase.START) {
         ClientTickHandler.onTickClient();
      }

   }
}
