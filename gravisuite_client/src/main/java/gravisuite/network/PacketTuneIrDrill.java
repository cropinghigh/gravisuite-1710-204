package gravisuite.network;

import gravisuite.GraviSuite;
import gravisuite.network.IPacket;
import gravisuite.network.PacketHandler;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.entity.player.EntityPlayer;

import java.util.List;
import java.util.ArrayList;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;

import gravisuite.ItemAdvDDrill;
import gravisuite.ItemAdvIrDrill;

public class PacketTuneIrDrill extends IPacket {
   public int currMode = 0;
   public float modeNormalPower = 100;
   public boolean mode3x3Autopickup = true;
   public float mode3x3Power = 100;
   public boolean mode5x5Center = true;
   public boolean mode5x5Autopickup = true;
   public float mode5x5Power = 100;
   public boolean mode7x7Center = true;
   public boolean mode7x7Autopickup = true;
   public float mode7x7Power = 100;

   public int packetID = PacketsName.TUNE_IR_DRILL;

   public PacketTuneIrDrill() {
      
   }

   public void readData(DataInputStream data) throws IOException {
      this.currMode = data.readByte();
      this.modeNormalPower = data.readFloat();
      this.mode3x3Power = data.readFloat();
      this.mode5x5Power = data.readFloat();
      this.mode7x7Power = data.readFloat();
      this.mode3x3Autopickup = data.readBoolean();
      this.mode5x5Autopickup = data.readBoolean();
      this.mode7x7Autopickup = data.readBoolean();
      this.mode5x5Center = data.readBoolean();
      this.mode7x7Center = data.readBoolean();
   }

   public void writeData(DataOutputStream data) throws IOException {
   }

   public static void issue(PacketTuneIrDrill s) {
      try {
         ByteArrayOutputStream buffer = new ByteArrayOutputStream();
         DataOutputStream os = new DataOutputStream(buffer);
         os.writeByte(PacketsName.TUNE_IR_DRILL);
         os.writeByte(s.currMode);
         os.writeFloat(s.modeNormalPower);
         os.writeFloat(s.mode3x3Power);
         os.writeFloat(s.mode5x5Power);
         os.writeFloat(s.mode7x7Power);
         os.writeBoolean(s.mode3x3Autopickup);
         os.writeBoolean(s.mode5x5Autopickup);
         os.writeBoolean(s.mode7x7Autopickup);
         os.writeBoolean(s.mode5x5Center);
         os.writeBoolean(s.mode7x7Center);
         os.close();
         PacketHandler.sendPacket(buffer.toByteArray());
      } catch (IOException var3) {
         throw new RuntimeException(var3);
      }
   }

   public int getPacketID() {
      return this.packetID;
   }

   public void execute(EntityPlayer player) {
      if(player != null) {
            ItemStack currentItemStack = player.getHeldItem();
            Item currentItem = currentItemStack.getItem();
            if(currentItem instanceof ItemTool) {
                if(currentItem instanceof ItemAdvIrDrill) {
                    ItemAdvIrDrill drill = (ItemAdvIrDrill) currentItem;
                    drill.saveDrillConfig(currentItemStack, this);
                }
            }
      }

   }
}
