package gravisuite;

import cpw.mods.fml.common.FMLCommonHandler;
import gravisuite.GraviSuite;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentTranslation;




public class ServerProxy {
   private Map isFlyActiveByMod = new HashMap();
   private Map lastUndressed = new HashMap();
   private Map isLastCreativeState = new HashMap();

   public void initCore() {
      
   }

   public static void sendPlayerMessage(EntityPlayer player, String message) {
      if(GraviSuite.isSimulating()) {
         player.addChatMessage(new ChatComponentTranslation(message, new Object[0]));
      }

   }

   public void SetLastUndressed(EntityPlayer player, Boolean value) {
      this.lastUndressed.put(player, value);
   }

   public void SetFlyActiveByMod(EntityPlayer player, Boolean value) {
      this.isFlyActiveByMod.put(player, value);
   }

   public void SetLastCreativeState(EntityPlayer player, Boolean value) {
      this.isLastCreativeState.put(player, value);
   }

   public boolean checkLastUndressed(EntityPlayer player) {
      return this.lastUndressed.containsKey(player)?((Boolean)this.lastUndressed.get(player)).booleanValue():false;
   }

   public boolean checkFlyActiveByMod(EntityPlayer player) {
      return this.isFlyActiveByMod.containsKey(player)?((Boolean)this.isFlyActiveByMod.get(player)).booleanValue():false;
   }

   public boolean checkLastCreativeState(EntityPlayer player) {
      return this.isLastCreativeState.containsKey(player)?((Boolean)this.isLastCreativeState.get(player)).booleanValue():false;
   }

   public static boolean isSimulating() {
      return !FMLCommonHandler.instance().getEffectiveSide().isClient();
   }

   public void playSoundSp(String sound, float var2, float var3) {
   }

   public void registerSoundHandler() {
   }

   public void registerRenderers() {
   }

   public EntityPlayer getPlayerInstance() {
      return null;
   }

   public int addArmor(String armorName) {
      return 0;
   }
}
