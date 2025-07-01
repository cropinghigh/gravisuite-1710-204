package gravisuite.network;

import gravisuite.GraviSuite;
import gravisuite.network.IPacket;
import gravisuite.network.PacketHandler;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.entity.player.EntityPlayer;

public class PacketKeyboardUpdate extends IPacket {
   private int keyState;
   public int packetID = 0;

   public void readData(DataInputStream data) throws IOException {
      this.keyState = data.readInt();
   }

   public void writeData(DataOutputStream data) throws IOException {
      data.writeInt(this.keyState);
   }

   public int getPacketID() {
      return this.packetID;
   }

   public static void issue(int currentKeyState) {
      try {
         ByteArrayOutputStream buffer = new ByteArrayOutputStream();
         DataOutputStream os = new DataOutputStream(buffer);
         os.writeByte(0);
         os.writeInt(currentKeyState);
         os.close();
         PacketHandler.sendPacket(buffer.toByteArray());
      } catch (IOException var3) {
         throw new RuntimeException(var3);
      }
   }

   public void execute(EntityPlayer player) {
      if(player != null) {
         GraviSuite.keyboard.processKeyUpdate(player, this.keyState);
      }

   }
}
