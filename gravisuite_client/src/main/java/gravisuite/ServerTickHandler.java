package gravisuite;

import gravisuite.GraviSuite;
import gravisuite.IItemTickListener;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ServerTickHandler {
   public static Map isFlyActiveByMod = new HashMap();
   public static Map lastUndressed = new HashMap();
   public static Map isLastCreativeState = new HashMap();

   public static void ontickPlayer(EntityPlayer player) {
      ItemStack ultimateSH = player.inventory.armorInventory[3];

      for(int i = 0; i < player.inventory.mainInventory.length; ++i) {
         ItemStack mainItem = player.inventory.mainInventory[i];
         if(mainItem != null && mainItem.getItem() instanceof IItemTickListener) {
            ((IItemTickListener)mainItem.getItem()).onTick(player, mainItem);
         }
      }

      ItemStack itemstack = player.inventory.armorInventory[2];
      if(itemstack != null && itemstack.getItem() == GraviSuite.graviChestPlate) {
         if(player.posY > 262.0D && !player.capabilities.isCreativeMode) {
            player.setPosition(player.posX, 262.0D, player.posZ);
         }
      } else if(GraviSuite.proxy.checkFlyActiveByMod(player) && !GraviSuite.proxy.checkLastUndressed(player)) {
         if(!player.capabilities.isCreativeMode) {
            player.capabilities.allowFlying = false;
            player.capabilities.isFlying = false;
         }

         GraviSuite.proxy.SetFlyActiveByMod(player, Boolean.valueOf(false));
         GraviSuite.proxy.SetLastUndressed(player, Boolean.valueOf(true));
      }

   }

   public static boolean checkLastUndressed(EntityPlayer player) {
      return lastUndressed.containsKey(player)?((Boolean)lastUndressed.get(player)).booleanValue():false;
   }

   public static boolean checkFlyActiveByMode(EntityPlayer player) {
      return isFlyActiveByMod.containsKey(player)?((Boolean)isFlyActiveByMod.get(player)).booleanValue():false;
   }

   public static boolean checkLastCreativeState(EntityPlayer player) {
      return isLastCreativeState.containsKey(player)?((Boolean)isLastCreativeState.get(player)).booleanValue():false;
   }
}
