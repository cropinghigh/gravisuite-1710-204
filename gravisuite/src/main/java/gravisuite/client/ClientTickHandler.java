package gravisuite.client;

import cpw.mods.fml.client.FMLClientHandler;
import gravisuite.GraviSuite;
import gravisuite.ItemAdvancedJetPack;
import gravisuite.ItemAdvancedLappack;
import gravisuite.ItemGraviChestPlate;
import gravisuite.ItemUltimateLappack;
import gravisuite.keyboard.KeyboardClient;
import gravisuite.network.PacketKeyPress;
import ic2.api.item.IElectricItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ClientTickHandler {
   private boolean keyDown;
   public static Minecraft mc = FMLClientHandler.instance().getClient();
   public static boolean isFirstLoad = false;

   public static void onTickClient() {
      if(mc.theWorld != null) {
         if(!isFirstLoad) {
            isFirstLoad = true;

            for(int i = 0; i < mc.gameSettings.keyBindings.length; ++i) {
               if(mc.gameSettings.keyBindings[i].getKeyDescription() == "Boost Key") {
                  KeyboardClient.icBoostKeyID = i;
                  GraviSuite.addLog("Keyregistry: IC2 Boost key found");
               }

               if(mc.gameSettings.keyBindings[i].getKeyDescription() == "ALT Key") {
                  KeyboardClient.icAltKeyID = i;
                  GraviSuite.addLog("Keyregistry: IC2 ALT key found");
               }

               if(mc.gameSettings.keyBindings[i].getKeyDescription() == "Mode Switch Key") {
                  KeyboardClient.icModeKeyID = i;
                  GraviSuite.addLog("Keyregistry: IC2 MODE SWITCH key found");
               }
            }
         }

         EntityPlayer player = mc.thePlayer;
         GraviSuite.keyboard.sendKeyUpdate(player);
      }

   }

   public static void onTickPlayer(EntityPlayer player) {
      ItemStack itemstack = player.inventory.armorInventory[2];
      if(itemstack != null && itemstack.getItem() == GraviSuite.graviChestPlate) {
         if(!ItemGraviChestPlate.readFlyStatus(itemstack) && !player.capabilities.isCreativeMode && GraviSuite.proxy.checkFlyActiveByMod(player)) {
            player.capabilities.allowFlying = false;
            player.capabilities.isFlying = false;
            GraviSuite.proxy.SetFlyActiveByMod(player, Boolean.valueOf(false));
         }

         if(player.posY > 262.0D && !player.capabilities.isCreativeMode) {
            player.setPosition(player.posX, 262.0D, player.posZ);
         }
      } else if(GraviSuite.proxy.checkFlyActiveByMod(player) && !GraviSuite.proxy.checkLastUndressed(player)) {
         if(!player.capabilities.isCreativeMode) {
            player.capabilities.allowFlying = false;
            player.capabilities.isFlying = false;
            PacketKeyPress.issue(3);
         }

         GraviSuite.proxy.SetFlyActiveByMod(player, Boolean.valueOf(false));
         GraviSuite.proxy.SetLastUndressed(player, Boolean.valueOf(true));
      }

   }

   public static void onTickRedner() {
      EntityPlayer player = mc.thePlayer;
      if(GraviSuite.displayHud && mc.theWorld != null && mc.inGameHasFocus && !mc.gameSettings.showDebugInfo) {
         ItemStack itemstack = player.inventory.armorItemInSlot(2);
         int xPos = 0;
         int yPos = 0;
         int xPos2 = 0;
         int yPos2 = 0;
         int statusFontWidth = 0;
         int elevelFontWidth = 0;
         String elevelString = "";
         String statusString = "";
         int yOffset = 3;
         if(itemstack != null && itemstack.getItem() == GraviSuite.graviChestPlate) {
            int currCharge = ItemGraviChestPlate.getCharge(itemstack);
            float energyStatus = (float)currCharge / (float)((IElectricItem)itemstack.getItem()).getMaxCharge(itemstack) * 100.0F;
            String elevelname = I18n.format("message.text.energyLevel", new Object[0]) + ": ";
            elevelString = elevelname + GetTextEnergyStatus(energyStatus);
            elevelFontWidth = mc.fontRenderer.getStringWidth(elevelname + Integer.toString(Math.round(energyStatus)) + "%");
            if(ItemGraviChestPlate.readFlyStatus(itemstack)) {
               boolean levitationMode = ItemGraviChestPlate.readWorkMode(itemstack);
               String levitationModeStatus = "";
               String levitationColorStatus = "";
               if(levitationMode) {
                  levitationModeStatus = " (" + I18n.format("message.graviChestPlate.levitationMode.short", new Object[0]) + ")";
                  levitationColorStatus = " §e(" + I18n.format("message.graviChestPlate.levitationMode.short", new Object[0]) + ")";
               }

               statusString = "§a" + I18n.format("message.graviChestPlate.gravitationEngine", new Object[0]) + " " + I18n.format("message.text.on", new Object[0]) + levitationColorStatus;
               statusFontWidth = mc.fontRenderer.getStringWidth(I18n.format("message.graviChestPlate.gravitationEngine", new Object[0]) + " " + I18n.format("message.text.on", new Object[0]) + levitationColorStatus);
            } else {
               statusString = "";
            }
         } else if(itemstack == null || itemstack.getItem() != GraviSuite.ultimateLappack && itemstack.getItem() != GraviSuite.advLappack) {
            if(itemstack != null && (itemstack.getItem() == GraviSuite.advJetpack || itemstack.getItem() == GraviSuite.advNanoChestPlate)) {
               int currCharge = ItemAdvancedJetPack.getCharge(itemstack);
               float energyStatus = (float)currCharge / (float)((IElectricItem)itemstack.getItem()).getMaxCharge(itemstack) * 100.0F;
               boolean hoverMode = ItemAdvancedJetPack.readWorkMode(itemstack);
               String hoverModeStatus = "";
               String hoverModeColorStatus = "";
               if(hoverMode) {
                  hoverModeStatus = " (" + I18n.format("message.advElJetpack.hoverMode", new Object[0]) + ")";
                  hoverModeColorStatus = " §e(" + I18n.format("message.advElJetpack.hoverMode", new Object[0]) + ")";
               }

               String elevelname = I18n.format("message.text.energyLevel", new Object[0]) + ": ";
               elevelString = elevelname + GetTextEnergyStatus(energyStatus);
               elevelFontWidth = mc.fontRenderer.getStringWidth(elevelname + Integer.toString(Math.round(energyStatus)) + "%");
               if(ItemAdvancedJetPack.readFlyStatus(itemstack)) {
                  statusString = "§a" + I18n.format("message.advElJetpack.jetpackEngine", new Object[0]) + " " + I18n.format("message.text.on", new Object[0]) + hoverModeColorStatus;
                  statusFontWidth = mc.fontRenderer.getStringWidth(I18n.format("message.advElJetpack.jetpackEngine", new Object[0]) + " " + I18n.format("message.text.on", new Object[0]) + hoverModeStatus);
               } else {
                  statusString = "";
               }
            }
         } else {
            float energyStatus;
            if(itemstack.getItem() == GraviSuite.ultimateLappack) {
               ItemUltimateLappack var10000 = (ItemUltimateLappack)itemstack.getItem();
               int currCharge = ItemUltimateLappack.getCharge(itemstack);
               energyStatus = (float)currCharge / (float)((IElectricItem)itemstack.getItem()).getMaxCharge(itemstack) * 100.0F;
            } else if(itemstack.getItem() == GraviSuite.advLappack) {
               ItemAdvancedLappack var34 = (ItemAdvancedLappack)itemstack.getItem();
               int currCharge = ItemAdvancedLappack.getCharge(itemstack);
               energyStatus = (float)currCharge / (float)((IElectricItem)itemstack.getItem()).getMaxCharge(itemstack) * 100.0F;
            } else {
               int currCharge = 0;
               energyStatus = 0.0F;
            }

            String elevelname = I18n.format("message.text.energyLevel", new Object[0]) + ": ";
            elevelString = elevelname + GetTextEnergyStatus(energyStatus);
            elevelFontWidth = mc.fontRenderer.getStringWidth(elevelname + Integer.toString(Math.round(energyStatus)) + "%");
         }

         if(elevelString != "") {
            ScaledResolution scaledresolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
            int disp_width = scaledresolution.getScaledWidth();
            int disp_height = scaledresolution.getScaledHeight();
            if(GraviSuite.hudPos == 1) {
               xPos = 2;
               xPos2 = 2;
               yPos = 2;
               yPos2 = 2 + yOffset + mc.fontRenderer.FONT_HEIGHT;
            }

            if(GraviSuite.hudPos == 2) {
               if(statusString != "") {
                  xPos = disp_width - statusFontWidth - 2;
               }

               xPos2 = disp_width - elevelFontWidth - 2;
               yPos = 2;
               yPos2 = 2 + yOffset + mc.fontRenderer.FONT_HEIGHT;
            }

            if(GraviSuite.hudPos == 3) {
               xPos = 2;
               xPos2 = 2;
               yPos = disp_height - 2 - mc.fontRenderer.FONT_HEIGHT;
               yPos2 = yPos - yOffset - mc.fontRenderer.FONT_HEIGHT;
            }

            if(GraviSuite.hudPos == 4) {
               if(statusString != "") {
                  xPos = disp_width - statusFontWidth - 2;
               }

               xPos2 = disp_width - elevelFontWidth - 2;
               yPos = disp_height - 2 - mc.fontRenderer.FONT_HEIGHT;
               yPos2 = yPos - yOffset - mc.fontRenderer.FONT_HEIGHT;
            }

            if(statusString != "") {
               mc.ingameGUI.drawString(mc.fontRenderer, statusString, xPos, yPos, 16777215);
               mc.ingameGUI.drawString(mc.fontRenderer, elevelString, xPos2, yPos2, 16777215);
            } else {
               mc.ingameGUI.drawString(mc.fontRenderer, elevelString, xPos2, yPos, 16777215);
            }
         }
      }

   }

   public static String GetTextEnergyStatus(float energyStatus) {
      return energyStatus <= 10.0F && energyStatus > 5.0F?"§6" + Integer.toString(Math.round(energyStatus)) + "%":(energyStatus <= 5.0F?"§c" + Integer.toString(Math.round(energyStatus)) + "%":Integer.toString(Math.round(energyStatus)) + "%");
   }
}
