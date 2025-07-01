package gravisuite.keyboard;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gravisuite.GraviSuite;
import gravisuite.Helpers;
import gravisuite.ItemAdvancedJetPack;
import gravisuite.ItemAdvancedLappack;
import gravisuite.keyboard.Keyboard;
import gravisuite.network.PacketKeyPress;
import gravisuite.network.PacketKeyboardUpdate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.config.Configuration;

@SideOnly(Side.CLIENT)
public class KeyboardClient extends Keyboard {
   public static Minecraft mc = FMLClientHandler.instance().getClient();
   public static KeyBinding flyKey = new KeyBinding("Gravi Fly Key", 33, "GraviSuite");
   public static KeyBinding displayHUDKey = new KeyBinding("Gravi Display Hud", 35, "GraviSuite");
   private static int lastKeyState = 0;
   private static boolean lastKeyModeState = false;
   public static int icBoostKeyID;
   public static int icAltKeyID;
   public static int icModeKeyID;
   public static float moveStrafe;
   public static float moveForward;

   public static void registerKeys() {
      ClientRegistry.registerKeyBinding(flyKey);
      ClientRegistry.registerKeyBinding(displayHUDKey);
   }

   public static void keyFlyPressed() {
      if(mc.inGameHasFocus) {
         ItemStack itemstack = mc.thePlayer.inventory.armorItemInSlot(2);
         if(itemstack != null && itemstack.getItem() == GraviSuite.graviChestPlate) {
            PacketKeyPress.issue(1);
         }

         if(itemstack != null && (itemstack.getItem() == GraviSuite.advJetpack || itemstack.getItem() == GraviSuite.advNanoChestPlate)) {
            PacketKeyPress.issue(1);
         }
      }

   }

   public static void keyHudDisplayPressed() {
      if(mc.inGameHasFocus) {
         ItemStack itemstack = mc.thePlayer.inventory.armorItemInSlot(2);
         if(itemstack != null && (itemstack.getItem() == GraviSuite.graviChestPlate || itemstack.getItem() instanceof ItemAdvancedJetPack || itemstack.getItem() instanceof ItemAdvancedLappack)) {
            if(isAltKeyDown(mc.thePlayer)) {
               SwitchDisplayHud(Boolean.valueOf(false));
            } else {
               SwitchDisplayHud(Boolean.valueOf(true));
            }
         }
      }

   }

   public static boolean isBoostKeyDown(EntityPlayer player) {
      return mc.gameSettings.keyBindings[icBoostKeyID].getIsKeyPressed();
   }

   public static boolean isAltKeyDown(EntityPlayer player) {
      return mc.gameSettings.keyBindings[icAltKeyID].getIsKeyPressed();
   }

   public static boolean isModeKeyPress(EntityPlayer player) {
      if(mc.gameSettings.keyBindings[icModeKeyID].getIsKeyPressed()) {
         if(!lastKeyModeState) {
            lastKeyModeState = true;
            sendModeKey(player);
         }

         return true;
      } else {
         lastKeyModeState = false;
         return false;
      }
   }

   public static boolean isJumpKeyDown(EntityPlayer player) {
      return mc.gameSettings.keyBindJump.getIsKeyPressed();
   }

   public static boolean isForwardKeyDown(EntityPlayer player) {
      return mc.gameSettings.keyBindForward.getIsKeyPressed();
   }

   public static boolean isSneakKeyDown(EntityPlayer player) {
      return mc.gameSettings.keyBindSneak.getIsKeyPressed();
   }

   public static void sendModeKey(EntityPlayer player) {
      PacketKeyPress.issue(2);
   }

   public void sendKeyUpdate(EntityPlayer player) {
      int currentKeyState = (isBoostKeyDown(player)?1:0) << 0 | (isAltKeyDown(player)?1:0) << 1 | (isModeKeyPress(player)?1:0) << 2 | (isForwardKeyDown(player)?1:0) << 3 | (isJumpKeyDown(player)?1:0) << 4 | (isSneakKeyDown(player)?1:0) << 5;
      if(currentKeyState != lastKeyState) {
         PacketKeyboardUpdate.issue(currentKeyState);
         lastKeyState = currentKeyState;
         super.processKeyUpdate(player, currentKeyState);
      }

   }

   public static void updatePlayerMove() {
      moveStrafe = 0.0F;
      moveForward = 0.0F;
      if(mc.gameSettings.keyBindForward.getIsKeyPressed()) {
         ++moveForward;
      }

      if(mc.gameSettings.keyBindBack.getIsKeyPressed()) {
         --moveForward;
      }

      if(mc.gameSettings.keyBindLeft.getIsKeyPressed()) {
         ++moveStrafe;
      }

      if(mc.gameSettings.keyBindRight.getIsKeyPressed()) {
         --moveStrafe;
      }

      if(mc.gameSettings.keyBindSneak.getIsKeyPressed()) {
         moveStrafe = (float)((double)moveStrafe * 0.3D);
         moveForward = (float)((double)moveForward * 0.3D);
      }

   }

   public static void SwitchDisplayHud(Boolean onOffHud) {
      Configuration tmpConfig = new Configuration(GraviSuite.configFile);

      try {
         tmpConfig.load();
         if(onOffHud.booleanValue()) {
            if(GraviSuite.displayHud) {
               GraviSuite.displayHud = false;
               tmpConfig.get("Hud settings", "Display hud", false).set(false);
               mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "[GraviSuite] " + Helpers.formatMessage("message.hud.displayHud") + ": " + EnumChatFormatting.RED + Helpers.formatMessage("message.text.disabled")));
            } else {
               GraviSuite.displayHud = true;
               tmpConfig.get("Hud settings", "Display hud", true).set(true);
               mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "[GraviSuite] " + Helpers.formatMessage("message.hud.displayHud") + ": " + ": " + EnumChatFormatting.GREEN + Helpers.formatMessage("message.text.enabled")));
            }
         } else if(GraviSuite.displayHud) {
            int tmpHudPos = GraviSuite.hudPos;
            ++tmpHudPos;
            if(tmpHudPos > 4) {
               tmpHudPos = 1;
            }

            String HudPosString = "";
            switch(tmpHudPos) {
            case 1:
               HudPosString = Helpers.formatMessage("message.hud.location.topLeft");
               break;
            case 2:
               HudPosString = Helpers.formatMessage("message.hud.location.topRight");
               break;
            case 3:
               HudPosString = Helpers.formatMessage("message.hud.location.bottomLeft");
               break;
            case 4:
               HudPosString = Helpers.formatMessage("message.hud.location.bottomRight");
            }

            GraviSuite.hudPos = tmpHudPos;
            tmpConfig.get("Hud settings", "hudPosition", tmpHudPos).set(tmpHudPos);
            mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "[GraviSuite] " + Helpers.formatMessage("message.hud.location") + ": " + EnumChatFormatting.AQUA + HudPosString));
         }
      } catch (Exception var7) {
         GraviSuite.addLog("Error while loading config file: " + var7);
         throw new RuntimeException(var7);
      } finally {
         tmpConfig.save();
      }

   }
}
