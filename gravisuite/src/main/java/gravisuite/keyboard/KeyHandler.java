package gravisuite.keyboard;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import gravisuite.keyboard.KeyboardClient;
import net.minecraft.client.gui.GuiChat;

public class KeyHandler {
   @SubscribeEvent
   public void onKeyInput(KeyInputEvent event) {
      if(!FMLClientHandler.instance().isGUIOpen(GuiChat.class)) {
         if(KeyboardClient.flyKey.isPressed()) {
            KeyboardClient.keyFlyPressed();
         }

         if(KeyboardClient.displayHUDKey.isPressed()) {
            KeyboardClient.keyHudDisplayPressed();
         }
      }

   }
}
