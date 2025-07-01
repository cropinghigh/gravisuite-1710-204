package gravisuite.network;

import gravisuite.GraviSuite;
import gravisuite.ItemRelocator;
import gravisuite.network.IPacket;
import gravisuite.network.PacketHandler;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.entity.player.EntityPlayer;

public class PacketManagePoints extends IPacket {
   private int x;
   private int y;
   private int z;
   private int dimID;
   private int eventID;
   public String pointName;
   public int packetID = 2;
   public byte action;
   public static final byte REMOVE_ACTION = 0;
   public static final byte ADD_ACTION = 1;
   public static final byte TELEPORT_ACTION = 2;
   public static final byte SELECT_DEFAULT = 3;

   public void readData(DataInputStream data) throws IOException {
      this.action = data.readByte();
      this.pointName = data.readUTF();
   }

   public void writeData(DataOutputStream data) throws IOException {
   }

   public static void issue(EntityPlayer player, String ptName, byte action) {
      try {
         ByteArrayOutputStream buffer = new ByteArrayOutputStream();
         DataOutputStream os = new DataOutputStream(buffer);
         os.writeByte(2);
         os.writeByte(action);
         os.writeUTF(ptName);
         os.close();
         PacketHandler.sendPacket(buffer.toByteArray());
      } catch (IOException var5) {
         throw new RuntimeException(var5);
      }
   }

   public int getPacketID() {
      return this.packetID;
   }

   public void managePoint(EntityPlayer player, String ptName, byte action) {
      if(player != null) {
         ItemRelocator iRelocator = (ItemRelocator)player.getCurrentEquippedItem().getItem();
         if(player.getCurrentEquippedItem().getItem() != GraviSuite.relocator) {
            return;
         }

         if(action == 1) {
            ItemRelocator.TeleportPoint newPoint = new ItemRelocator.TeleportPoint();
            newPoint.dimID = player.worldObj.provider.dimensionId;
            newPoint.pointName = ptName;
            newPoint.x = player.posX;
            newPoint.y = player.posY;
            newPoint.z = player.posZ;
            newPoint.pitch = (double)player.rotationPitch;
            newPoint.yaw = (double)player.rotationYaw;
            ItemRelocator.addNewTeleportPoint(player, player.getCurrentEquippedItem(), newPoint);
         }

         if(action == 0) {
            ItemRelocator.removeTeleportPoint(player.getCurrentEquippedItem(), ptName);
         }

         if(action == 2) {
            iRelocator.teleportPlayer(player, player.getCurrentEquippedItem(), ptName);
         }

         if(action == 3) {
            ItemRelocator.setDefaultPoint(player, player.getCurrentEquippedItem(), ptName);
         }
      }

   }

   public void execute(EntityPlayer player) {
      if(player != null) {
         ;
      }

   }
}
