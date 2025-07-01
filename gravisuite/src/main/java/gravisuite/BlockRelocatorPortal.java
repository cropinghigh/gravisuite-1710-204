package gravisuite;

import gravisuite.GraviSuite;
import gravisuite.TileEntityRelocatorPortal;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockRelocatorPortal extends BlockContainer {
   private IIcon blockIcon;

   protected BlockRelocatorPortal(Material material) {
      super(material);
      this.setLightLevel(1.0F);
   }

   public void registerBlockIcons(IIconRegister par1IconRegister) {
      this.blockIcon = par1IconRegister.registerIcon("gravisuite:block_side");
   }

   public IIcon getIcon(IBlockAccess world, int x, int y, int z, int blockSide) {
      return this.blockIcon;
   }

   public boolean isOpaqueCube() {
      return false;
   }

   public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
      return new TileEntityRelocatorPortal();
   }

   public int quantityDropped(Random random) {
      return 0;
   }

   public int damageDropped(int i) {
      return 0;
   }

   public AxisAlignedBB getCollisionBoundingBoxFromPool(World p_149668_1_, int p_149668_2_, int p_149668_3_, int p_149668_4_) {
      return null;
   }

   public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
      if(GraviSuite.isSimulating()) {
         TileEntity tileEntity = world.getTileEntity(x, y, z);
         if(tileEntity != null && tileEntity instanceof TileEntityRelocatorPortal) {
            int entitCoolDown = ((TileEntityRelocatorPortal)tileEntity).getCoolDownTime(entity);
            if(entitCoolDown == 0) {
               ((TileEntityRelocatorPortal)tileEntity).TeleportEntity(entity);
            }
         }
      }

   }

   public void breakBlock(World world, int i, int j, int k, Block par5, int par6) {
      world.removeTileEntity(i, j, k);
      super.breakBlock(world, i, j, k, par5, par6);
   }

   public boolean renderAsNormalBlock() {
      return false;
   }

   public int getRenderType() {
      return GraviSuite.blockRelocatorPortalRenderID;
   }

   public boolean isBlockSolid(IBlockAccess p_149747_1_, int p_149747_2_, int p_149747_3_, int p_149747_4_, int p_149747_5_) {
      return false;
   }
}
