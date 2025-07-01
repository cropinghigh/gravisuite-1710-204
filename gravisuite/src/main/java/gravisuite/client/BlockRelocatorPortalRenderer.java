package gravisuite.client;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import gravisuite.GraviSuite;
import gravisuite.TileEntityRelocatorPortal;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.world.IBlockAccess;

public class BlockRelocatorPortalRenderer implements ISimpleBlockRenderingHandler {
   public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
      TileEntityRendererDispatcher.instance.renderTileEntityAt(new TileEntityRelocatorPortal(), 0.0D, 0.0D, 0.0D, 0.0F);
   }

   public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
      return false;
   }

   public int getRenderId() {
      return GraviSuite.blockRelocatorPortalRenderID;
   }

   public boolean shouldRender3DInInventory(int modelId) {
      return true;
   }
}
