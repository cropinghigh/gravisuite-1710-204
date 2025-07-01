package gravisuite.client;

import gravisuite.network.PacketManagePoints;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class GuiRelocatorAdd extends GuiContainer {
   private static ResourceLocation tex = new ResourceLocation("gravisuite", "textures/gui/relocator_add.png");
   private GuiTextField pointTextField;
   private int mouseX;
   private int mouseY;

   public GuiRelocatorAdd() {
      super(new GuiRelocatorAdd.ContainerRelocatorAdd());
      super.xSize = 135;
      super.ySize = 59;
   }

   public void initGui() {
      super.initGui();
      Keyboard.enableRepeatEvents(true);
      int i = (super.width - super.xSize) / 2;
      int j = (super.height - super.ySize) / 2;
      this.pointTextField = new GuiTextField(super.fontRendererObj, i + 7, j + 19, 121, 12);
      this.pointTextField.setTextColor(16777215);
      this.pointTextField.setDisabledTextColour(-1);
      this.pointTextField.setEnableBackgroundDrawing(false);
      this.pointTextField.setMaxStringLength(20);
      this.pointTextField.setFocused(true);
      this.pointTextField.setCanLoseFocus(false);
      this.pointTextField.setText("");
   }

   protected void mouseClicked(int x, int y, int mouseButton) {
      super.mouseClicked(x, y, mouseButton);
      if(this.isPointInRegion(37, 37, 26, 13, this.mouseX, this.mouseY)) {
         this.sendPoint();
      }

      if(this.isPointInRegion(70, 37, 26, 13, this.mouseX, this.mouseY)) {
         super.mc.thePlayer.closeScreen();
      }

   }

   private boolean isPointInRegion(int left, int top, int width, int height, int mouseX, int mouseY) {
      return this.func_146978_c(left, top, width, height, mouseX, mouseY);
   }

   public ItemStack getCurrentItem() {
      return Minecraft.getMinecraft().thePlayer.inventory.getCurrentItem();
   }

   public void sendPoint() {
      if(this.pointTextField.getText().trim() != "") {
         PacketManagePoints.issue(Minecraft.getMinecraft().thePlayer, this.pointTextField.getText(), (byte)1);
         super.mc.thePlayer.closeScreen();
      }

   }

   protected void drawGuiContainerForegroundLayer(int x, int y) {
      String formatName = I18n.format("item.relocator.gui.addPoint", new Object[0]);
      super.fontRendererObj.drawString(formatName, 13, 5, 16777215);
   }

   public void onGuiClosed() {
      super.onGuiClosed();
      Keyboard.enableRepeatEvents(false);
   }

   public void drawScreen(int x, int y, float par3) {
      super.drawScreen(x, y, par3);
      this.mouseX = x;
      this.mouseY = y;
      this.pointTextField.drawTextBox();
   }

   public void updateScreen() {
      this.pointTextField.updateCursorCounter();
   }

   protected void keyTyped(char par1, int keyCode) {
      this.pointTextField.textboxKeyTyped(par1, keyCode);
      if(keyCode == 1) {
         super.mc.thePlayer.closeScreen();
      }

      if(keyCode == 28) {
         this.sendPoint();
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
      if(this.isPointInRegion(37, 37, 26, 13, this.mouseX, this.mouseY)) {
         this.drawTexturedModalRect(xStart + 37, yStart + 37, 140, 19, 26, 13);
      } else {
         this.drawTexturedModalRect(xStart + 37, yStart + 37, 140, 3, 26, 13);
      }

      if(this.isPointInRegion(70, 37, 26, 13, this.mouseX, this.mouseY)) {
         this.drawTexturedModalRect(xStart + 70, yStart + 37, 173, 19, 26, 13);
      } else {
         this.drawTexturedModalRect(xStart + 70, yStart + 37, 173, 3, 26, 13);
      }

      GL11.glDisable(3042);
   }

   public static class ContainerRelocatorAdd extends Container {
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
}
