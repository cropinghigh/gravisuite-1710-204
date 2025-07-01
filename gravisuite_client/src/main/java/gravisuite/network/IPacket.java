package gravisuite.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.entity.player.EntityPlayer;

public abstract class IPacket {
   public int packetID;

   public void readData(DataInputStream data) throws IOException {
   }

   public void writeData(DataOutputStream data) throws IOException {
   }

   public int getPacketID() {
      return this.packetID;
   }

   public void execute(EntityPlayer player) {
   }
}
