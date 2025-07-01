package gravisuite;

import net.minecraft.entity.player.EntityPlayer;

public interface ITickListener {
   void onTickClient();

   void onTickPlayer(EntityPlayer var1);

   void onTickRedner();
}
