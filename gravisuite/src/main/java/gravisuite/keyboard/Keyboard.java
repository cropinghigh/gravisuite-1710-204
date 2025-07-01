package gravisuite.keyboard;

import gravisuite.GraviSuite;
import gravisuite.ItemAdvancedJetPack;
import gravisuite.ItemGraviChestPlate;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class Keyboard {
   private static Map boostKeyState = new HashMap();
   private static Map jumpKeyState = new HashMap();
   private static Map sneakKeyState = new HashMap();
   private static Map forwardKeyState = new HashMap();
   private static Map altKeyState = new HashMap();
   private static Map modeKeyState = new HashMap();

   public static boolean isBoostKeyDown(EntityPlayer player) {
      return boostKeyState.containsKey(player)?((Boolean)boostKeyState.get(player)).booleanValue():false;
   }

   public static boolean isAltKeyDown(EntityPlayer player) {
      return altKeyState.containsKey(player)?((Boolean)altKeyState.get(player)).booleanValue():false;
   }

   public static boolean isModeKeyDown(EntityPlayer player) {
      return modeKeyState.containsKey(player)?((Boolean)modeKeyState.get(player)).booleanValue():false;
   }

   public static boolean isForwardKeyDown(EntityPlayer player) {
      return jumpKeyState.containsKey(player)?((Boolean)forwardKeyState.get(player)).booleanValue():false;
   }

   public static boolean isJumpKeyDown(EntityPlayer player) {
      return jumpKeyState.containsKey(player)?((Boolean)jumpKeyState.get(player)).booleanValue():false;
   }

   public static boolean isSneakKeyDown(EntityPlayer player) {
      return sneakKeyState.containsKey(player)?((Boolean)sneakKeyState.get(player)).booleanValue():false;
   }

   public void sendKeyUpdate(EntityPlayer player) {
   }

   public void processKeyPressed(EntityPlayer player, int keyPressed) {
      if(keyPressed == 1) {
         ItemStack itemstack = player.inventory.armorInventory[2];
         if(itemstack != null && itemstack.getItem() == GraviSuite.graviChestPlate) {
            ItemGraviChestPlate.switchFlyState(player, itemstack);
         }

         if(itemstack != null && (itemstack.getItem() == GraviSuite.advJetpack || itemstack.getItem() == GraviSuite.advNanoChestPlate)) {
            ItemAdvancedJetPack.switchFlyState(player, itemstack);
         }
      }

      if(keyPressed == 2) {
         ItemStack itemstack = player.inventory.armorInventory[2];
         if(itemstack != null && (itemstack.getItem() == GraviSuite.advJetpack || itemstack.getItem() == GraviSuite.advNanoChestPlate) && isJumpKeyDown(player)) {
            ItemAdvancedJetPack.switchWorkMode(player, itemstack);
         }

         if(itemstack != null && itemstack.getItem() == GraviSuite.graviChestPlate && isJumpKeyDown(player)) {
            ItemGraviChestPlate.switchWorkMode(player, itemstack);
         }
      }

      if(keyPressed == 3) {
         player.capabilities.allowFlying = false;
         player.capabilities.isFlying = false;
         GraviSuite.proxy.SetFlyActiveByMod(player, Boolean.valueOf(false));
         GraviSuite.proxy.SetLastUndressed(player, Boolean.valueOf(true));
      }

   }

   public void processKeyUpdate(EntityPlayer player, int i) {
      boostKeyState.put(player, Boolean.valueOf((i & 1) != 0));
      altKeyState.put(player, Boolean.valueOf((i & 2) != 0));
      modeKeyState.put(player, Boolean.valueOf((i & 4) != 0));
      forwardKeyState.put(player, Boolean.valueOf((i & 8) != 0));
      jumpKeyState.put(player, Boolean.valueOf((i & 16) != 0));
      sneakKeyState.put(player, Boolean.valueOf((i & 32) != 0));
      if(isForwardKeyDown(player)) {
         ;
      }

      if(isJumpKeyDown(player)) {
         ;
      }

      if(isBoostKeyDown(player)) {
         ;
      }

      if(isSneakKeyDown(player)) {
         ;
      }

   }
}
