package gravisuite.client;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gravisuite.EntityPlasmaBall;
import gravisuite.GraviSuite;
import gravisuite.ServerProxy;
import gravisuite.TileEntityRelocatorPortal;
import gravisuite.client.BlockRelocatorPortalRenderer;
import gravisuite.client.RenderPlasmaBall;
import gravisuite.client.TileRelocatorPortalRenderer;
import gravisuite.keyboard.KeyHandler;
import gravisuite.keyboard.KeyboardClient;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;

@SideOnly(Side.CLIENT)
public class ClientProxy extends ServerProxy {
   private boolean isFlyActiveByMod;
   private boolean lastUndressed;
   private boolean isLastCreativeState;
   public static Minecraft mc = FMLClientHandler.instance().getClient();

   public void initCore() {
      FMLCommonHandler.instance().bus().register(new KeyHandler());
      KeyboardClient.registerKeys();
   }

   public EntityPlayer getPlayerInstance() {
      return Minecraft.getMinecraft().thePlayer;
   }

   public void SetLastUndressed(EntityPlayer player, Boolean value) {
      if(!GraviSuite.isSimulating()) {
         this.lastUndressed = value.booleanValue();
      }

   }

   public void SetFlyActiveByMod(EntityPlayer player, Boolean value) {
      if(!GraviSuite.isSimulating()) {
         this.isFlyActiveByMod = value.booleanValue();
      }

   }

   public void SetLastCreativeState(EntityPlayer player, Boolean value) {
      this.isLastCreativeState = value.booleanValue();
   }

   public boolean checkLastUndressed(EntityPlayer player) {
      return this.lastUndressed;
   }

   public boolean checkFlyActiveByMod(EntityPlayer player) {
      return this.isFlyActiveByMod;
   }

   public boolean checkLastCreativeState(EntityPlayer player) {
      return this.isLastCreativeState;
   }

   public void registerSoundHandler() {
   }

   public void registerRenderers() {
      RenderingRegistry.registerEntityRenderingHandler(EntityPlasmaBall.class, new RenderPlasmaBall(1.0F));
      GraviSuite.blockRelocatorPortalRenderID = RenderingRegistry.getNextAvailableRenderId();
      RenderingRegistry.registerBlockHandler(new BlockRelocatorPortalRenderer());
      ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRelocatorPortal.class, new TileRelocatorPortalRenderer());
   }

   public int addArmor(String armorName) {
      return RenderingRegistry.addNewArmourRendererPrefix(armorName);
   }

   public static void sendPlayerMessage(EntityPlayer player, String message) {
      if(!mc.theWorld.isRemote) {
         player.addChatMessage(new ChatComponentText(message));
      }

   }
}
