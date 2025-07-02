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

public class PacketBreakBlocks extends IPacket {
   private int count;
   private List<Coords> blockList;

   public int packetID = PacketsName.BREAK_BLOCKS;

   public PacketBreakBlocks() {
      this.blockList = new ArrayList<Coords>();
      this.count = 0;
   }

   public void readData(DataInputStream data) throws IOException {
      // this.keyPressed = data.readInt();
      this.count = data.readUnsignedByte();
      if(this.count > 100) this.count = 100;
      this.blockList.clear();
      for(int i = 0; i < this.count; i++) {
            this.blockList.add(new Coords(data.readInt(), data.readInt(), data.readInt()));
      }
   }

   public void writeData(DataOutputStream data) throws IOException {
   }

   public static void issue(List<Coords> blList) {
      try {
         ByteArrayOutputStream buffer = new ByteArrayOutputStream();
         DataOutputStream os = new DataOutputStream(buffer);
         os.writeByte(PacketsName.BREAK_BLOCKS);
         os.writeByte(blList.size());
         for(int i = 0; i < blList.size(); i++) {
               os.writeInt(blList.get(i).getX());
               os.writeInt(blList.get(i).getY());
               os.writeInt(blList.get(i).getZ());
         }
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
                if(currentItem instanceof ItemAdvDDrill) {
                    ItemAdvDDrill drill = (ItemAdvDDrill) currentItem;
                    drill.onBlockStartBreakAdditionalServerPart(currentItemStack, this.blockList, player);
                }
                if(currentItem instanceof ItemAdvIrDrill) {
                    ItemAdvIrDrill drill = (ItemAdvIrDrill) currentItem;
                    drill.onBlockStartBreakAdditionalServerPart(currentItemStack, this.blockList, player);
                }
            }
      }

   }
}
