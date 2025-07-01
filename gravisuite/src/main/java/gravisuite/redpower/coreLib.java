package gravisuite.redpower;

import gravisuite.redpower.WorldCoord;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class coreLib {
   public static Object getTileEntity(IBlockAccess var0, int var1, int var2, int var3, Class var4) {
      TileEntity var5 = var0.getTileEntity(var1, var2, var3);
      System.out.println("getTile_1");
      return !var4.isInstance(var5)?null:var5;
   }

   public static Object getTileEntity(IBlockAccess var0, WorldCoord var1, Class var2) {
      System.out.println("getTile_2");
      TileEntity var3 = var0.getTileEntity(var1.x, var1.y, var1.z);
      return !var2.isInstance(var3)?null:var3;
   }

   public static MovingObjectPosition retraceBlock(World var0, EntityPlayer var1, int var2, int var3, int var4) {
      Vec3 var5 = Vec3.createVectorHelper(var1.posX, var1.posY + 1.62D - (double)var1.yOffset, var1.posZ);
      Vec3 var6 = var1.getLook(1.0F);
      Vec3 var7 = var5.addVector(var6.xCoord * 5.0D, var6.yCoord * 5.0D, var6.zCoord * 5.0D);
      Block var8 = var0.getBlock(var2, var3, var4);
      return var8 == null?null:var8.collisionRayTrace(var0, var2, var3, var4, var5, var7);
   }
}
