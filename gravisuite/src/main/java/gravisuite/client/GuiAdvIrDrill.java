package gravisuite.client;

import gravisuite.network.PacketManagePoints;
import gravisuite.ItemAdvIrDrill;
import gravisuite.network.PacketTuneIrDrill;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.GuiButton;
import cpw.mods.fml.client.config.GuiSlider;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiAdvIrDrill extends GuiContainer {
   private static ResourceLocation tex = new ResourceLocation("gravisuite", "textures/gui/advirdrill.png");
   private int mouseX;
   private int mouseY;
   private GuiButton buttonModeNormal;
   private GuiButton buttonMode3x3;
   private GuiButton buttonMode5x5;
   private GuiButton buttonMode7x7;
   private GuiButton buttonCentralModeOn;
   private GuiButton buttonCentralModeOff;
   private GuiButton buttonAutoSuckOn;
   private GuiButton buttonAutoSuckOff;
   private GuiSlider buttonPower;
   private GuiButton buttonExit;
   private String formatName = I18n.format("item.advIrDrill.gui.name", new Object[0]);
   private String formatMode = I18n.format("message.text.mode", new Object[0]);
   private String formatModeNormal = I18n.format("message.advDDrill.mode.normal", new Object[0]);
   private String formatMode3x3 = "3x3";
   private String formatMode5x5 = "5x5";
   private String formatMode7x7 = "7x7";
   private String formatCentralMode = I18n.format("message.text.centermode", new Object[0]);
   private String formatOn = I18n.format("message.text.on", new Object[0]);
   private String formatOff = I18n.format("message.text.off", new Object[0]);
   private String formatAutoSuck = I18n.format("item.advIrDrill.gui.autosuck", new Object[0]);
   private String formatPower = I18n.format("item.advIrDrill.gui.power", new Object[0]);
   private String formatExit = I18n.format("item.advIrDrill.gui.exit", new Object[0]);
   private PacketTuneIrDrill currSettings;

   public GuiAdvIrDrill() {
      super(new GuiAdvIrDrill.ContainerIrDrill());
      super.xSize = 180;
      super.ySize = 190;
   }

   public void initGui() {
      int xs = (super.width - super.xSize) / 2;
      int ys = (super.height - super.ySize) / 2;
      buttonModeNormal = new GuiButton(
         0, 
         xs + super.xSize - 10 - (super.fontRendererObj.getCharWidth('A')*formatMode7x7.length()+6) - 3 - (super.fontRendererObj.getCharWidth('A')*formatMode5x5.length()+6) - 3 - (super.fontRendererObj.getCharWidth('A')*formatMode3x3.length()+6) - 3 - (super.fontRendererObj.getCharWidth('A')*formatModeNormal.length()+6), 
         ys + 16, 
         super.fontRendererObj.getCharWidth('A')*formatModeNormal.length()+6, 
         super.fontRendererObj.FONT_HEIGHT+11, 
         formatModeNormal
      );
      super.buttonList.add(buttonModeNormal);
      buttonMode3x3 = new GuiButton(
         1, 
         xs + super.xSize - 10 - (super.fontRendererObj.getCharWidth('A')*formatMode7x7.length()+6) - 3 - (super.fontRendererObj.getCharWidth('A')*formatMode5x5.length()+6) - 3 - (super.fontRendererObj.getCharWidth('A')*formatMode3x3.length()+6), 
         ys + 16, 
         super.fontRendererObj.getCharWidth('A')*formatMode3x3.length()+6, 
         super.fontRendererObj.FONT_HEIGHT+11, 
         formatMode3x3
      );
      super.buttonList.add(buttonMode3x3);
      buttonMode5x5 = new GuiButton(
         2, 
         xs + super.xSize - 10 - (super.fontRendererObj.getCharWidth('A')*formatMode7x7.length()+6) - 3 - (super.fontRendererObj.getCharWidth('A')*formatMode5x5.length()+6), 
         ys + 16, 
         super.fontRendererObj.getCharWidth('A')*formatMode5x5.length()+6, 
         super.fontRendererObj.FONT_HEIGHT+11, 
         formatMode5x5
      );
      super.buttonList.add(buttonMode5x5);
      buttonMode7x7 = new GuiButton(
         3, 
         xs + super.xSize - 10 - (super.fontRendererObj.getCharWidth('A')*formatMode7x7.length()+6), 
         ys + 16, 
         super.fontRendererObj.getCharWidth('A')*formatMode7x7.length()+6, 
         super.fontRendererObj.FONT_HEIGHT+11, 
         formatMode7x7
      );
      super.buttonList.add(buttonMode7x7);

      buttonCentralModeOn = new GuiButton(
         4, 
         xs + super.xSize - 10 - (super.fontRendererObj.getCharWidth('A')*formatOn.length()+6), 
         ys + 16 + ((super.fontRendererObj.FONT_HEIGHT+11) + 1)*1, 
         super.fontRendererObj.getCharWidth('A')*formatOn.length()+6, 
         super.fontRendererObj.FONT_HEIGHT+11, 
         formatOn
      );
      super.buttonList.add(buttonCentralModeOn);
      buttonCentralModeOff = new GuiButton(
         5, 
         xs + super.xSize - 10 - (super.fontRendererObj.getCharWidth('A')*formatOn.length()+6) - 3 - (super.fontRendererObj.getCharWidth('A')*formatOff.length()+6), 
         ys + 16 + ((super.fontRendererObj.FONT_HEIGHT+11) + 1)*1, 
         super.fontRendererObj.getCharWidth('A')*formatOff.length()+6, 
         super.fontRendererObj.FONT_HEIGHT+11, 
         formatOff
      );
      super.buttonList.add(buttonCentralModeOff);

      buttonAutoSuckOn = new GuiButton(
         6, 
         xs + super.xSize - 10 - (super.fontRendererObj.getCharWidth('A')*formatOn.length()+6), 
          ys + 16 + ((super.fontRendererObj.FONT_HEIGHT+11) + 1)*2, 
         super.fontRendererObj.getCharWidth('A')*formatOn.length()+6, 
         super.fontRendererObj.FONT_HEIGHT+11, 
         formatOn
      );
      super.buttonList.add(buttonAutoSuckOn);
      buttonAutoSuckOff = new GuiButton(
         7, 
         xs + super.xSize - 10 - (super.fontRendererObj.getCharWidth('A')*formatOn.length()+6) - 3 - (super.fontRendererObj.getCharWidth('A')*formatOff.length()+6), 
         ys + 16 + ((super.fontRendererObj.FONT_HEIGHT+11) + 1)*2, 
         super.fontRendererObj.getCharWidth('A')*formatOff.length()+6, 
         super.fontRendererObj.FONT_HEIGHT+11, 
         formatOff
      );
      super.buttonList.add(buttonAutoSuckOff);

      buttonPower = new GuiSlider(
         8, 
         xs + 10 + (super.fontRendererObj.getCharWidth('A')*formatPower.length()) + 5, 
         ys + 16 + ((super.fontRendererObj.FONT_HEIGHT+11) + 1)*3, 
         super.xSize - (10 + (super.fontRendererObj.getCharWidth('A')*formatPower.length()) + 5) - 10, 
         super.fontRendererObj.FONT_HEIGHT + 11, 
         "", 
         " %", 
         1.0, 
         100.0, 
         50.0, 
         false, 
         true
      ); 
      super.buttonList.add(buttonPower);

      buttonExit = new GuiButton(
         9, 
         xs + 10, 
         ys + super.ySize - super.fontRendererObj.FONT_HEIGHT - 11 - 8, 
         super.xSize - 20, 
         super.fontRendererObj.FONT_HEIGHT+11, 
         formatExit
      );
      super.buttonList.add(buttonExit);

      currSettings = ItemAdvIrDrill.readDrillConfig(getCurrentItem());

      loadSettingsFromObj();

      buttonPower.enabled = true;

      buttonExit.enabled = true;

      super.initGui();
   }

   protected void saveSettingsAndExit() {
      storeSettingsToObj();
      PacketTuneIrDrill.issue(currSettings);
      ItemAdvIrDrill.saveDrillConfig(getCurrentItem(), currSettings);
      super.mc.thePlayer.closeScreen();
   }

   private void storeSettingsToObj() {
      if(!buttonModeNormal.enabled) {
         currSettings.currMode = 0;
         currSettings.modeNormalPower = (float)buttonPower.getValue();
      } else if(!buttonMode3x3.enabled) {
         currSettings.currMode = 1;
         currSettings.mode3x3Autopickup = !buttonAutoSuckOn.enabled;
         currSettings.mode3x3Power = (float)buttonPower.getValue();
      } else if(!buttonMode5x5.enabled) {
         currSettings.currMode = 2;
         currSettings.mode5x5Autopickup = !buttonAutoSuckOn.enabled;
         currSettings.mode5x5Center = !buttonCentralModeOn.enabled;
         currSettings.mode5x5Power = (float)buttonPower.getValue();
      } else {
         currSettings.currMode = 3;
         currSettings.mode7x7Autopickup = !buttonAutoSuckOn.enabled;
         currSettings.mode7x7Center = !buttonCentralModeOn.enabled;
         currSettings.mode7x7Power = (float)buttonPower.getValue();
      }
   }
   
   private void loadSettingsFromObj() {
      switch(currSettings.currMode) {
         case 0:
            buttonModeNormal.enabled = false;
            buttonMode3x3.enabled = true;
            buttonMode5x5.enabled = true;
            buttonMode7x7.enabled = true;
            buttonCentralModeOn.enabled = false;
            buttonCentralModeOff.enabled = false;
            buttonAutoSuckOn.enabled = false;
            buttonAutoSuckOff.enabled = false;
            buttonPower.setValue(currSettings.modeNormalPower);
            break;
         case 1:
            buttonModeNormal.enabled = true;
            buttonMode3x3.enabled = false;
            buttonMode5x5.enabled = true;
            buttonMode7x7.enabled = true;
            buttonCentralModeOn.enabled = false;
            buttonCentralModeOff.enabled = false;
            buttonAutoSuckOn.enabled = !currSettings.mode3x3Autopickup;
            buttonAutoSuckOff.enabled = currSettings.mode3x3Autopickup;
            buttonPower.setValue(currSettings.mode3x3Power);
            break;
         case 2:
            buttonModeNormal.enabled = true;
            buttonMode3x3.enabled = true;
            buttonMode5x5.enabled = false;
            buttonMode7x7.enabled = true;
            buttonCentralModeOn.enabled = !currSettings.mode5x5Center;
            buttonCentralModeOff.enabled = currSettings.mode5x5Center;
            buttonAutoSuckOn.enabled = !currSettings.mode5x5Autopickup;
            buttonAutoSuckOff.enabled = currSettings.mode5x5Autopickup;
            buttonPower.setValue(currSettings.mode5x5Power);
            break;
         case 3:
            buttonModeNormal.enabled = true;
            buttonMode3x3.enabled = true;
            buttonMode5x5.enabled = true;
            buttonMode7x7.enabled = false;
            buttonCentralModeOn.enabled = !currSettings.mode7x7Center;
            buttonCentralModeOff.enabled = currSettings.mode7x7Center;
            buttonAutoSuckOn.enabled = !currSettings.mode7x7Autopickup;
            buttonAutoSuckOff.enabled = currSettings.mode7x7Autopickup;
            buttonPower.setValue(currSettings.mode7x7Power);
            break;
      }
      buttonPower.updateSlider();
   }

   protected void actionPerformed(GuiButton button) {
      switch(button.id) {
         case 0:
               storeSettingsToObj();
               currSettings.currMode = 0;
               loadSettingsFromObj();
            break;
         case 1:
               storeSettingsToObj();
               currSettings.currMode = 1;
               loadSettingsFromObj();
            break;
         case 2:
               storeSettingsToObj();
               currSettings.currMode = 2;
               loadSettingsFromObj();
            break;
         case 3:
               storeSettingsToObj();
               currSettings.currMode = 3;
               loadSettingsFromObj();
            break;
         case 4:
               buttonCentralModeOn.enabled = false;
               buttonCentralModeOff.enabled = true;
               storeSettingsToObj();
            break;
         case 5:
               buttonCentralModeOn.enabled = true;
               buttonCentralModeOff.enabled = false;
               storeSettingsToObj();
            break;
         case 6:
               buttonAutoSuckOn.enabled = false;
               buttonAutoSuckOff.enabled = true;
               storeSettingsToObj();
            break;
         case 7:
               buttonAutoSuckOn.enabled = true;
               buttonAutoSuckOff.enabled = false;
               storeSettingsToObj();
            break;
         case 8:
               storeSettingsToObj();
            break;
         case 9:
            saveSettingsAndExit();
            break;
         default:
            System.err.println("Wrong button id: " + button.id);
      }
   }

   public ItemStack getCurrentItem() {
      return Minecraft.getMinecraft().thePlayer.inventory.getCurrentItem();
   }

   private int getMode() {
      if(!buttonModeNormal.enabled) return 0;
      else if(!buttonMode3x3.enabled) return 1;
      else if(!buttonMode5x5.enabled) return 2;
      else return 3;
   }

   private int getEuPerMine() {
      float euPerBlock = ItemAdvIrDrill.energyPerOperation * ((float)buttonPower.getValue()/100.0F);
      int blocks = 1;
      switch(getMode()) {
         case 0:
            break;
         case 1:
            blocks = 3*3;
            break;
         case 2:
            blocks = 5*5;
            break;
         case 3:
            blocks = 7*7;
            break;
      }
      return (int)(euPerBlock * blocks);
   }

   private float getSpeed() {
      switch(getMode()) {
         case 0:
            return (ItemAdvIrDrill.normalPower * ((float)buttonPower.getValue()/100.0F) * ((float)buttonPower.getValue()/100.0F))/(16.0F);
         case 1:
            return (ItemAdvIrDrill.Hole3x3Power * ((float)buttonPower.getValue()/100.0F) * ((float)buttonPower.getValue()/100.0F))/(16.0F);
         case 2:
            return (ItemAdvIrDrill.Hole5x5Power * ((float)buttonPower.getValue()/100.0F) * ((float)buttonPower.getValue()/100.0F))/(16.0F);
         case 3:
            return (ItemAdvIrDrill.Hole7x7Power * ((float)buttonPower.getValue()/100.0F) * ((float)buttonPower.getValue()/100.0F))/(16.0F);
      }
      return 0;
   }

   public void drawScreen(int x, int y, float par3) {
      super.drawScreen(x, y, par3);
      // buttonModeNormal.drawButton(super.mc, x, y);
      this.mouseX = x;
      this.mouseY = y;
   }

   protected void keyTyped(char par1, int keyCode) {
      if(keyCode == 1) {
         // super.mc.thePlayer.closeScreen();
         saveSettingsAndExit();
      }

      if(keyCode == 28) {
         saveSettingsAndExit();
      }

   }
   
   protected void drawGuiContainerForegroundLayer(int x, int y) {
      buttonModeNormal.func_146111_b(x, y);
      buttonMode3x3.func_146111_b(x, y);
      buttonMode5x5.func_146111_b(x, y);
      buttonMode7x7.func_146111_b(x, y);

      buttonCentralModeOn.func_146111_b(x, y);
      buttonCentralModeOff.func_146111_b(x, y);

      buttonAutoSuckOn.func_146111_b(x, y);
      buttonAutoSuckOff.func_146111_b(x, y);

      buttonPower.func_146111_b(x, y);

      buttonExit.func_146111_b(x, y);

      super.fontRendererObj.drawString(formatName, xSize/2 - (super.fontRendererObj.getCharWidth('A')*formatName.length()/2), 7, 16777215);
      super.fontRendererObj.drawString(formatMode, 10, 16 + (super.fontRendererObj.FONT_HEIGHT+11)/2 - super.fontRendererObj.FONT_HEIGHT/2, 16777215);
      super.fontRendererObj.drawString(formatCentralMode, 10, 16 + (super.fontRendererObj.FONT_HEIGHT+11) + 1 + (super.fontRendererObj.FONT_HEIGHT+11)/2 - super.fontRendererObj.FONT_HEIGHT/2, 16777215);
      super.fontRendererObj.drawString(formatAutoSuck, 10, 16 + ((super.fontRendererObj.FONT_HEIGHT+11) + 1)*2 + (super.fontRendererObj.FONT_HEIGHT+11)/2 - super.fontRendererObj.FONT_HEIGHT/2, 16777215);
      super.fontRendererObj.drawString(formatPower, 10, 16 + ((super.fontRendererObj.FONT_HEIGHT+11) + 1)*3 + (super.fontRendererObj.FONT_HEIGHT+11)/2 - super.fontRendererObj.FONT_HEIGHT/2, 16777215);

      float spd = getSpeed();
      String speedStr = String.format("%.2f", spd);

      super.fontRendererObj.drawString(speedStr, 156, 117, 16777215);

      int euPM = getEuPerMine();
      String energyStr = String.format("%d EU/mine", euPM);

      super.fontRendererObj.drawString(energyStr, 51, 130, 16777215);
   }

   protected void drawGuiContainerBackgroundLayer(float opacity, int x, int y) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		super.mc.renderEngine.bindTexture(tex);
		int xs = (super.width - super.xSize) / 2;
		int ys = (super.height - super.ySize) / 2;
		this.drawTexturedModalRect(xs, ys, 0, 0, super.xSize, super.ySize);
		switch(getMode()) {
            case 0:
               this.drawTexturedModalRect(xs+16, ys+103, 181, 0, 33, 33);
               break;
            case 1:
               this.drawTexturedModalRect(xs+16, ys+103, 181, 34, 33, 33);
               break;
            case 2:
               if(!buttonCentralModeOff.enabled) {
                  this.drawTexturedModalRect(xs+16, ys+103, 215, 68, 33, 33);
               } else {
                  this.drawTexturedModalRect(xs+16, ys+103, 181, 68, 33, 33);
               }
               break;
            case 3:
               if(!buttonCentralModeOff.enabled) {
                  this.drawTexturedModalRect(xs+16, ys+103, 215, 102, 33, 33);
               } else {
                  this.drawTexturedModalRect(xs+16, ys+103, 181, 102, 33, 33);
               }
               break;
      }
      float spd = getSpeed();
      int euPM = getEuPerMine();
      if(spd >= (ItemAdvIrDrill.normalPower/16.0F) * (4.0F/5.0F)) {
         this.drawTexturedModalRect(xs+114, ys+104, 237, 136, 13, 30);
      } else if(spd >= (ItemAdvIrDrill.normalPower/16.0F) * (3.0F/5.0F)) {
         this.drawTexturedModalRect(xs+114, ys+104, 223, 136, 13, 30);
      } else if(spd >= (ItemAdvIrDrill.normalPower/16.0F) * (2.0F/5.0F)) {
         this.drawTexturedModalRect(xs+114, ys+104, 209, 136, 13, 30);
      } else if(spd >= (ItemAdvIrDrill.normalPower/16.0F) * (1.0F/5.0F)) {
         this.drawTexturedModalRect(xs+114, ys+104, 195, 136, 13, 30);
      } else {
         this.drawTexturedModalRect(xs+114, ys+104, 181, 136, 13, 30);
      }

      if(euPM >= (ItemAdvIrDrill.energyPerOperation * 7*7) * (4.0F/5.0F)) {
         this.drawTexturedModalRect(xs+74, ys+104, 237, 136, 13, 30);
      } else if(euPM >= (ItemAdvIrDrill.energyPerOperation * 7*7) * (3.0F/5.0F)) {
         this.drawTexturedModalRect(xs+74, ys+104, 223, 136, 13, 30);
      } else if(euPM >= (ItemAdvIrDrill.energyPerOperation * 7*7) * (2.0F/5.0F)) {
         this.drawTexturedModalRect(xs+74, ys+104, 209, 136, 13, 30);
      } else if(euPM >= (ItemAdvIrDrill.energyPerOperation * 7*7) * (1.0F/5.0F)) {
         this.drawTexturedModalRect(xs+74, ys+104, 195, 136, 13, 30);
      } else {
         this.drawTexturedModalRect(xs+74, ys+104, 181, 136, 13, 30);
      }
   }

   public static class ContainerIrDrill extends Container {
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
