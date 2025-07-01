package gravisuite.client;

import com.google.common.collect.Lists;
import gravisuite.GraviSuite;
import gravisuite.Helpers;
import gravisuite.ItemRelocator;
import gravisuite.network.PacketManagePoints;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.DimensionManager;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class GuiRelocatorDisplay extends GuiContainer {
   private static ResourceLocation tex = new ResourceLocation("gravisuite", "textures/gui/relocator_display.png");
   private int mouseX;
   private int mouseY;
   private int itemInterval;
   private int firstItemX;
   private int firstItemY;
   private int cancelBtnX1;
   private int cancelBtnSize;
   private int firstSelX;
   private int firstSelY;
   private int selWidth;
   private int selHeight;
   private int firstItemBGX;
   private int firstItemBGY;
   private int itemBGinterval;
   private int cancelBtnWidth;
   private int cancelBtnHeight;
   private int openType;
   private int itemBGX;
   private int itemBGY;
   private int itemBGWidth;
   private int itemBGHeight;
   private int itemBGdefX;
   private int itemBGdefY;
   private int itemBGselX;
   private int itemBGselY;
   private int itemBGdelX;
   private int itemBGdelY;
   public static final int GUI_POINT_DISPLAY_LIST = 0;
   public static final int GUI_POINT_DISPLAY_DEFSELECT = 1;

   public GuiRelocatorDisplay(int guiType) {
      super(new GuiRelocatorDisplay.ContainerRelocatorDisplay());
      super.xSize = 162;
      super.ySize = 129;
      this.firstItemX = 17;
      this.firstItemY = 16;
      this.firstItemBGX = 14;
      this.firstItemBGY = 14;
      this.firstSelX = 15;
      this.firstSelY = 15;
      this.selWidth = 132;
      this.selHeight = 9;
      this.itemBGinterval = 10;
      this.itemInterval = 10;
      this.cancelBtnX1 = 138;
      this.cancelBtnWidth = 9;
      this.cancelBtnHeight = 9;
      this.itemBGX = 0;
      this.itemBGY = 131;
      this.itemBGWidth = 134;
      this.itemBGHeight = 11;
      this.itemBGselX = 0;
      this.itemBGselY = 144;
      this.itemBGdelX = 0;
      this.itemBGdelY = 157;
      this.itemBGdefX = 0;
      this.itemBGdefY = 170;
      this.openType = guiType;
   }

   public void initGui() {
      super.initGui();
   }

   public GuiRelocatorDisplay.SelectedItem getSelected(List<ItemRelocator.TeleportPoint> tpList) {
      GuiRelocatorDisplay.SelectedItem tmpSel = new GuiRelocatorDisplay.SelectedItem();
      int xStart = (super.width - super.xSize) / 2;
      int yStart = (super.height - super.ySize) / 2;
      int var10000 = this.mouseX - xStart;
      int realMouseY = this.mouseY - yStart;
      int itemsCount = tpList.size();
      if(this.isPointInRegion(this.firstSelX, this.firstSelY + 1, this.selWidth, this.itemInterval * itemsCount - 2, this.mouseX, this.mouseY)) {
         double newCalc = (double)(realMouseY - this.firstSelY + 1) / (double)this.itemBGinterval;
         tmpSel.selItem = (int)Math.ceil(newCalc);
         if(this.isPointInRegion(this.cancelBtnX1, this.firstSelY + 1, this.cancelBtnWidth, this.itemInterval * itemsCount - 1, this.mouseX, this.mouseY)) {
            tmpSel.delFlag = true;
         }

         return tmpSel;
      } else {
         return null;
      }
   }

   protected void mouseClicked(int x, int y, int mouseButton) {
      super.mouseClicked(x, y, mouseButton);
      int xStart = (super.width - super.xSize) / 2;
      int yStart = (super.height - super.ySize) / 2;
      int var10000 = this.mouseX - xStart;
      var10000 = this.mouseY - yStart;
      List<ItemRelocator.TeleportPoint> tpList = Lists.newArrayList();
      tpList.addAll(ItemRelocator.loadTeleportPoints(this.getCurrentItem()));
      GuiRelocatorDisplay.SelectedItem selectedItem = this.getSelected(tpList);
      if(selectedItem != null) {
         if(selectedItem.delFlag) {
            PacketManagePoints.issue(Minecraft.getMinecraft().thePlayer, ((ItemRelocator.TeleportPoint)tpList.get(selectedItem.selItem - 1)).pointName, (byte)0);
         } else {
            if(this.openType == 0) {
               PacketManagePoints.issue(Minecraft.getMinecraft().thePlayer, ((ItemRelocator.TeleportPoint)tpList.get(selectedItem.selItem - 1)).pointName, (byte)2);
            }

            if(this.openType == 1) {
               PacketManagePoints.issue(Minecraft.getMinecraft().thePlayer, ((ItemRelocator.TeleportPoint)tpList.get(selectedItem.selItem - 1)).pointName, (byte)3);
            }

            super.mc.thePlayer.closeScreen();
         }
      }

   }

   private boolean isPointInRegion(int left, int top, int width, int height, int mouseX, int mouseY) {
      return this.func_146978_c(left, top, width, height, mouseX, mouseY);
   }

   public ItemStack getCurrentItem() {
      return Minecraft.getMinecraft().thePlayer.inventory.getCurrentItem();
   }

   public void sendPoint() {
      super.mc.thePlayer.closeScreen();
   }

   protected void drawGuiContainerForegroundLayer(int x, int y) {
      int xStart = (super.width - super.xSize) / 2;
      int yStart = (super.height - super.ySize) / 2;

      try {
         List<ItemRelocator.TeleportPoint> tpList = Lists.newArrayList();
         tpList.addAll(ItemRelocator.loadTeleportPoints(this.getCurrentItem()));
         if(tpList.size() > 0) {
            for(int i = 0; i < ((List)tpList).size(); ++i) {
               ItemRelocator.TeleportPoint tmpPoint = (ItemRelocator.TeleportPoint)tpList.get(i);
               super.fontRendererObj.drawString(tmpPoint.pointName, this.firstItemX, this.firstItemY + i * this.itemInterval, 16777215);
            }

            GameSettings var10000 = super.mc.gameSettings;
            if(GameSettings.isKeyDown(super.mc.gameSettings.keyBindSneak)) {
               GuiRelocatorDisplay.SelectedItem selectedItem = this.getSelected(tpList);
               if(selectedItem != null) {
                  List<String> toolTipData = new ArrayList();
                  ItemRelocator.TeleportPoint tmpPoint = (ItemRelocator.TeleportPoint)tpList.get(selectedItem.selItem - 1);
                  int hotKey = selectedItem.selItem;
                  if(hotKey == 10) {
                     hotKey = 0;
                  }

                  toolTipData.add(EnumChatFormatting.GOLD + "Hotkey: " + hotKey);
                  toolTipData.add("Dimension: " + DimensionManager.getProvider(tmpPoint.dimID).getDimensionName());
                  toolTipData.add("Height: " + (int)tmpPoint.y);
                  toolTipData.add("X: " + (int)tmpPoint.x);
                  toolTipData.add("Y: " + (int)tmpPoint.z);
                  int realMouseX = this.mouseX - xStart;
                  int realMouseY = this.mouseY - yStart;
                  Helpers.renderTooltip(realMouseX - 2, realMouseY, toolTipData);
               }
            }
         }
      } catch (Exception var12) {
         GraviSuite.addLog("Error in draw relocatorGui foregroundLayer:" + var12.toString());
      }

   }

   public void onGuiClosed() {
      super.onGuiClosed();
      Keyboard.enableRepeatEvents(false);
   }

   public void drawScreen(int x, int y, float par3) {
      super.drawScreen(x, y, par3);
      this.mouseX = x;
      this.mouseY = y;
   }

   public void updateScreen() {
   }

   protected void keyTyped(char par1, int keyCode) {
      if(keyCode == 1) {
         super.mc.thePlayer.closeScreen();
      }

      if(keyCode > 1 && keyCode < 12) {
         List<ItemRelocator.TeleportPoint> tpList = Lists.newArrayList();
         tpList.addAll(ItemRelocator.loadTeleportPoints(this.getCurrentItem()));
         if(keyCode - 2 < tpList.size()) {
            ItemRelocator.TeleportPoint tmpPoint = (ItemRelocator.TeleportPoint)tpList.get(keyCode - 2);
            PacketManagePoints.issue(Minecraft.getMinecraft().thePlayer, tmpPoint.pointName, (byte)2);
            super.mc.thePlayer.closeScreen();
         }
      }

   }

   protected void drawGuiContainerBackgroundLayer(float opacity, int x, int y) {
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      super.mc.getTextureManager().bindTexture(tex);
      int xStart = (super.width - super.xSize) / 2;
      int yStart = (super.height - super.ySize) / 2;
      GL11.glEnable(3042);
      GL11.glBlendFunc(770, 771);
      this.drawTexturedModalRect(xStart, yStart, 0, 0, super.xSize, super.ySize);
      GL11.glDisable(3042);
      List<ItemRelocator.TeleportPoint> tpList = Lists.newArrayList();
      tpList.addAll(ItemRelocator.loadTeleportPoints(this.getCurrentItem()));
      if(tpList.size() > 0) {
         for(int i = 0; i < ((List)tpList).size(); ++i) {
            this.drawTexturedModalRect(xStart + this.firstItemBGX, yStart + this.firstItemBGY + i * this.itemInterval, this.itemBGX, this.itemBGY, this.itemBGWidth, this.itemBGHeight);
            ItemRelocator.TeleportPoint tmpPoint = (ItemRelocator.TeleportPoint)tpList.get(i);
            if(tmpPoint.defPoint) {
               this.drawTexturedModalRect(xStart + this.firstItemBGX, yStart + this.firstItemBGY + i * this.itemInterval, this.itemBGdefX, this.itemBGdefY, this.itemBGWidth, this.itemBGHeight);
            }
         }

         GuiRelocatorDisplay.SelectedItem selectedItem = this.getSelected(tpList);
         if(selectedItem != null) {
            if(!selectedItem.delFlag) {
               this.drawTexturedModalRect(xStart + this.firstItemBGX, yStart + this.firstItemBGY + (selectedItem.selItem - 1) * this.itemInterval, this.itemBGselX, this.itemBGselY, this.itemBGWidth, this.itemBGHeight);
            } else {
               this.drawTexturedModalRect(xStart + this.firstItemBGX, yStart + this.firstItemBGY + (selectedItem.selItem - 1) * this.itemInterval, this.itemBGdelX, this.itemBGdelY, this.itemBGWidth, this.itemBGHeight);
            }
         }
      }

   }

   public static class ContainerRelocatorDisplay extends Container {
      public boolean canInteractWith(EntityPlayer var1) {
         return true;
      }

      public Slot getSlot(int p_75139_1_) {
         return new Slot(new IInventory() {
            public int getSizeInventory() {
               return 0;
            }

            public ItemStack getStackInSlot(int p_70301_1_) {
               return null;
            }

            public ItemStack decrStackSize(int p_70298_1_, int p_70298_2_) {
               return null;
            }

            public ItemStack getStackInSlotOnClosing(int p_70304_1_) {
               return null;
            }

            public void setInventorySlotContents(int p_70299_1_, ItemStack p_70299_2_) {
            }

            public String getInventoryName() {
               return null;
            }

            public boolean hasCustomInventoryName() {
               return false;
            }

            public int getInventoryStackLimit() {
               return 0;
            }

            public void markDirty() {
            }

            public boolean isUseableByPlayer(EntityPlayer p_70300_1_) {
               return false;
            }

            public void openInventory() {
            }

            public void closeInventory() {
            }

            public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_) {
               return false;
            }
         }, 0, 0, 0);
      }
   }

   public static class SelectedItem {
      public int selItem = -1;
      public boolean delFlag = false;
   }
}
