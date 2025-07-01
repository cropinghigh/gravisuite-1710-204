package gravisuite;

import cpw.mods.fml.common.network.IGuiHandler;
import gravisuite.client.GuiRelocatorAdd;
import gravisuite.client.GuiRelocatorDisplay;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class GuiHandler implements IGuiHandler {
   public Object getServerGuiElement(int ID, EntityPlayer player, World world, int X, int Y, int Z) {
      return null;
   }

   public Object getClientGuiElement(int ID, EntityPlayer player, World world, int X, int Y, int Z) {
      return ID == 1?new GuiRelocatorAdd():(ID == 2?new GuiRelocatorDisplay(0):(ID == 3?new GuiRelocatorDisplay(1):null));
   }
}
