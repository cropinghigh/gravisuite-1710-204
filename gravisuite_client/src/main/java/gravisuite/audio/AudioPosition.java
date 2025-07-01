package gravisuite.audio;

import gravisuite.audio.PositionSpec;
import java.lang.ref.WeakReference;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class AudioPosition {
   private final WeakReference<World> worldRef;
   public final float x;
   public final float y;
   public final float z;

   public static AudioPosition getFrom(Object obj, PositionSpec positionSpec) {
      if(obj instanceof AudioPosition) {
         return (AudioPosition)obj;
      } else if(obj instanceof Entity) {
         Entity e = (Entity)obj;
         return new AudioPosition(e.worldObj, (float)e.posX, (float)e.posY, (float)e.posZ);
      } else if(obj instanceof TileEntity) {
         TileEntity te = (TileEntity)obj;
         return new AudioPosition(te.getWorldObj(), (float)te.xCoord + 0.5F, (float)te.yCoord + 0.5F, (float)te.zCoord + 0.5F);
      } else {
         return null;
      }
   }

   public AudioPosition(World world, float x1, float y1, float z1) {
      this.worldRef = new WeakReference(world);
      this.x = x1;
      this.y = y1;
      this.z = z1;
   }

   public World getWorld() {
      return (World)this.worldRef.get();
   }
}
