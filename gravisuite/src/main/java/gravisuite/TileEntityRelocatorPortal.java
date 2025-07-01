package gravisuite;

import com.google.common.collect.Lists;
import gravisuite.BlockRelocatorPortal;
import gravisuite.GraviSuite;
import gravisuite.Helpers;
import gravisuite.ItemRelocator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldServer;

public class TileEntityRelocatorPortal extends TileEntity {
   private int timerTicks = 0;
   private int maxTime = 500;
   private int maxCoolDownTime = 60;
   public ItemRelocator.TeleportPoint parentTeleportPoint = null;
   private List<TileEntityRelocatorPortal.EntityInfo> entityList = Lists.newArrayList();

   public void updateEntity() {
      super.updateEntity();
      ++this.timerTicks;
      if(GraviSuite.isSimulating()) {
         this.updateEntityCoolDown();
         this.checkParentPortal();
      }

      if(this.timerTicks >= this.maxTime && GraviSuite.isSimulating()) {
         MinecraftServer minecraftserver = MinecraftServer.getServer();
         super.worldObj.setBlockToAir(super.xCoord, super.yCoord, super.zCoord);
         super.worldObj.func_147479_m(super.xCoord, super.yCoord, super.zCoord);
         super.worldObj.removeTileEntity(super.xCoord, super.yCoord, super.zCoord);
         if(this.parentTeleportPoint != null) {
            WorldServer targetServer = minecraftserver.worldServerForDimension(this.parentTeleportPoint.dimID);
            targetServer.theChunkProviderServer.loadChunk((int)this.parentTeleportPoint.x >> 4, (int)this.parentTeleportPoint.z >> 4);
            Block block = targetServer.getBlock((int)this.parentTeleportPoint.x, (int)this.parentTeleportPoint.y, (int)this.parentTeleportPoint.z);
            if(block == null) {
               return;
            }

            if(block instanceof BlockRelocatorPortal) {
               targetServer.setBlockToAir((int)this.parentTeleportPoint.x, (int)this.parentTeleportPoint.y, (int)this.parentTeleportPoint.z);
               targetServer.func_147479_m((int)this.parentTeleportPoint.x, (int)this.parentTeleportPoint.y, (int)this.parentTeleportPoint.z);
               targetServer.removeTileEntity((int)this.parentTeleportPoint.x, (int)this.parentTeleportPoint.y, (int)this.parentTeleportPoint.z);
            }
         }
      }

   }

   public void checkParentPortal() {
      if(this.parentTeleportPoint != null) {
         MinecraftServer minecraftserver = MinecraftServer.getServer();
         WorldServer targetServer = minecraftserver.worldServerForDimension(this.parentTeleportPoint.dimID);
         targetServer.theChunkProviderServer.loadChunk((int)this.parentTeleportPoint.x >> 4, (int)this.parentTeleportPoint.z >> 4);
         Block block = targetServer.getBlock((int)this.parentTeleportPoint.x, (int)this.parentTeleportPoint.y, (int)this.parentTeleportPoint.z);
         if(block == null) {
            return;
         }

         if(!(block instanceof BlockRelocatorPortal)) {
            super.worldObj.setBlockToAir(super.xCoord, super.yCoord, super.zCoord);
            super.worldObj.func_147479_m(super.xCoord, super.yCoord, super.zCoord);
            super.worldObj.removeTileEntity(super.xCoord, super.yCoord, super.zCoord);
         }
      }

   }

   public void updateEntityCoolDown() {
      if(this.entityList.size() > 0) {
         for(TileEntityRelocatorPortal.EntityInfo point : this.entityList) {
            --point.portalCooldown;
            if(point.portalCooldown < 0) {
               point.portalCooldown = 0;
            }
         }
      }

   }

   public int getCoolDownTime(Entity entity) {
      if(this.entityList.size() > 0) {
         for(TileEntityRelocatorPortal.EntityInfo point : this.entityList) {
            if(point.entityID.equalsIgnoreCase(entity.getPersistentID().toString())) {
               return point.portalCooldown;
            }
         }
      }

      return 0;
   }

   public void addEntityToList(Entity entity) {
      Boolean entityFound = Boolean.valueOf(false);
      if(this.entityList.size() > 0) {
         for(TileEntityRelocatorPortal.EntityInfo point : this.entityList) {
            if(point.entityID.equalsIgnoreCase(entity.getPersistentID().toString())) {
               point.portalCooldown = this.maxCoolDownTime;
               point.yaw = (double)(entity.rotationYaw - 180.0F);
               entityFound = Boolean.valueOf(true);
            }
         }
      }

      if(!entityFound.booleanValue()) {
         TileEntityRelocatorPortal.EntityInfo newPoint = new TileEntityRelocatorPortal.EntityInfo();
         newPoint.entityID = entity.getPersistentID().toString();
         newPoint.yaw = (double)(entity.rotationYaw - 180.0F);
         newPoint.portalCooldown = this.maxCoolDownTime;
         this.entityList.add(newPoint);
      }

   }

   public void TeleportEntity(Entity entity) {
      if(this.parentTeleportPoint != null) {
         MinecraftServer minecraftserver = MinecraftServer.getServer();
         WorldServer targetServer = minecraftserver.worldServerForDimension(this.parentTeleportPoint.dimID);
         targetServer.theChunkProviderServer.loadChunk((int)this.parentTeleportPoint.x >> 4, (int)this.parentTeleportPoint.z >> 4);
         TileEntity tileEntity = targetServer.getTileEntity((int)this.parentTeleportPoint.x, (int)this.parentTeleportPoint.y, (int)this.parentTeleportPoint.z);
         if(tileEntity != null && tileEntity instanceof TileEntityRelocatorPortal) {
            ((TileEntityRelocatorPortal)tileEntity).addEntityToList(entity);
         }

         double userYaw = this.parentTeleportPoint.yaw;
         if(this.entityList.size() > 0) {
            for(TileEntityRelocatorPortal.EntityInfo point : this.entityList) {
               if(point.entityID.equalsIgnoreCase(entity.getPersistentID().toString())) {
                  userYaw = point.yaw;
               }
            }
         }

         ItemRelocator.TeleportPoint tmpPoint = new ItemRelocator.TeleportPoint();
         tmpPoint.dimID = this.parentTeleportPoint.dimID;
         tmpPoint.x = this.parentTeleportPoint.x;
         tmpPoint.y = this.parentTeleportPoint.y;
         tmpPoint.z = this.parentTeleportPoint.z;
         tmpPoint.yaw = userYaw;
         tmpPoint.pitch = (double)entity.rotationPitch;
         Helpers.teleportEntity(entity, tmpPoint);
      }

   }

   public void setParentPortal(ItemRelocator.TeleportPoint parentPoint) {
      if(this.parentTeleportPoint != null) {
         MinecraftServer minecraftserver = MinecraftServer.getServer();
         WorldServer targetServer = minecraftserver.worldServerForDimension(this.parentTeleportPoint.dimID);
         targetServer.theChunkProviderServer.loadChunk((int)this.parentTeleportPoint.x >> 4, (int)this.parentTeleportPoint.z >> 4);
         Block block = targetServer.getBlock((int)this.parentTeleportPoint.x, (int)this.parentTeleportPoint.y, (int)this.parentTeleportPoint.z);
         if(block == null) {
            return;
         }

         if(block instanceof BlockRelocatorPortal) {
            targetServer.setBlockToAir((int)this.parentTeleportPoint.x, (int)this.parentTeleportPoint.y, (int)this.parentTeleportPoint.z);
            targetServer.func_147479_m((int)this.parentTeleportPoint.x, (int)this.parentTeleportPoint.y, (int)this.parentTeleportPoint.z);
            targetServer.removeTileEntity((int)this.parentTeleportPoint.x, (int)this.parentTeleportPoint.y, (int)this.parentTeleportPoint.z);
         }
      }

      this.parentTeleportPoint = parentPoint;
      this.resetTimer();
   }

   public void resetTimer() {
      this.timerTicks = 0;
   }

   public void readFromNBT(NBTTagCompound nbttagcompound) {
      super.readFromNBT(nbttagcompound);
      Boolean haveParent = Boolean.valueOf(nbttagcompound.getBoolean("parentPortal"));
      if(haveParent.booleanValue()) {
         if(this.parentTeleportPoint == null) {
            this.parentTeleportPoint = new ItemRelocator.TeleportPoint();
         }

         this.parentTeleportPoint.dimID = nbttagcompound.getInteger("parentDimId");
         this.parentTeleportPoint.x = nbttagcompound.getDouble("parentPosX");
         this.parentTeleportPoint.y = nbttagcompound.getDouble("parentPosY");
         this.parentTeleportPoint.z = nbttagcompound.getDouble("parentPosZ");
      }

      this.timerTicks = nbttagcompound.getInteger("timerTicks");
      NBTTagList list = nbttagcompound.getTagList("entityList", 10);
      List<TileEntityRelocatorPortal.EntityInfo> tpList = Lists.newArrayList();

      for(int i = 0; i < list.tagCount(); ++i) {
         TileEntityRelocatorPortal.EntityInfo newPoint = new TileEntityRelocatorPortal.EntityInfo();
         NBTTagCompound loadedPoint = list.getCompoundTagAt(i);
         newPoint.entityID = loadedPoint.getString("entityID");
         newPoint.yaw = loadedPoint.getDouble("entityYaw");
         newPoint.portalCooldown = loadedPoint.getInteger("entityCoolDown");
         tpList.add(newPoint);
      }

      if(tpList.size() > 0) {
         this.entityList.clear();
         this.entityList.addAll(tpList);
      }

   }

   public void writeToNBT(NBTTagCompound nbttagcompound) {
      super.writeToNBT(nbttagcompound);
      new NBTTagList();
      if(this.parentTeleportPoint != null) {
         nbttagcompound.setBoolean("parentPortal", true);
         nbttagcompound.setInteger("parentDimId", this.parentTeleportPoint.dimID);
         nbttagcompound.setDouble("parentPosX", this.parentTeleportPoint.x);
         nbttagcompound.setDouble("parentPosY", this.parentTeleportPoint.y);
         nbttagcompound.setDouble("parentPosZ", this.parentTeleportPoint.z);
      } else {
         nbttagcompound.setBoolean("parentPortal", false);
      }

      nbttagcompound.setInteger("timerTicks", this.timerTicks);
      NBTTagList nbtList = new NBTTagList();

      for(TileEntityRelocatorPortal.EntityInfo point : this.entityList) {
         NBTTagCompound nbt = new NBTTagCompound();
         nbt.setString("entityID", point.entityID);
         nbt.setDouble("entityYaw", point.yaw);
         nbt.setDouble("entityCoolDown", (double)point.portalCooldown);
         nbtList.appendTag(nbt);
      }

      nbttagcompound.setTag("entityList", nbtList);
   }

   public static class EntityInfo {
      public String entityID;
      public double yaw;
      public int portalCooldown;
   }
}
