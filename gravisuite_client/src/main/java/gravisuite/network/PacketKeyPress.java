package gravisuite.network;

import gravisuite.GraviSuite;
import gravisuite.network.IPacket;
import gravisuite.network.PacketHandler;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.entity.player.EntityPlayer;

public class PacketKeyPress extends IPacket {
   private int keyPressed;
   public int packetID = 1;

   public void readData(DataInputStream data) throws IOException {
      this.keyPressed = data.readInt();
   }

   public void writeData(DataOutputStream data) throws IOException {
      data.writeInt(this.keyPressed);
   }

   public static void issue(int currentKeyPressed) {
      try {
         ByteArrayOutputStream buffer = new ByteArrayOutputStream();
         DataOutputStream os = new DataOutputStream(buffer);
         os.writeByte(1);
         os.writeInt(currentKeyPressed);
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
         GraviSuite.keyboard.processKeyPressed(player, this.keyPressed);
      }

   }
}
