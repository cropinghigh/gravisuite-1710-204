package gravisuite;

import cpw.mods.fml.common.network.IGuiHandler;
import gravisuite.client.GuiRelocatorAdd;
import gravisuite.client.GuiRelocatorDisplay;
import gravisuite.client.GuiAdvIrDrill;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class GuiHandler implements IGuiHandler {
   public Object getServerGuiElement(int ID, EntityPlayer player, World world, int X, int Y, int Z) {
      return null;
   }

   public Object getClientGuiElement(int ID, EntityPlayer player, World world, int X, int Y, int Z) {
      switch(ID){
         case 1:
            return new GuiRelocatorAdd();
         case 2:
            return new GuiRelocatorDisplay(0);
         case 3:
            return new GuiRelocatorDisplay(1);
         case 4:
            return new GuiAdvIrDrill();
         default:
            return null;
      }
   }
}
