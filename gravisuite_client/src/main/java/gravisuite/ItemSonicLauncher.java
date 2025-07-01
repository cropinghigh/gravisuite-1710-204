package gravisuite;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gravisuite.EntityPlasmaBall;
import gravisuite.ItemRelocator;
import java.util.HashSet;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.world.World;

public class ItemSonicLauncher extends ItemTool {
   protected ItemSonicLauncher(ToolMaterial toolMaterial) {
      super(0.0F, toolMaterial, new HashSet());
      this.setMaxDamage(27);
   }

   public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
      ItemStack tpItem = null;

      for(int i = 0; i < player.inventory.mainInventory.length; ++i) {
         if(player.inventory.mainInventory[i] != null && player.inventory.mainInventory[i].getItem() instanceof ItemRelocator) {
            tpItem = player.inventory.mainInventory[i];
         }
      }

      if(tpItem != null) {
         ItemRelocator.TeleportPoint tpPoint = ItemRelocator.getTeleportPointByName(tpItem, "Main");
         EntityPlasmaBall plasmaBall = new EntityPlasmaBall(world, player, tpPoint, (byte)0);
         if(!world.isRemote) {
            world.spawnEntityInWorld(plasmaBall);
         }

         player.swingItem();
      } else {
         System.out.println("Item not found");
      }

      return itemStack;
   }

   @SideOnly(Side.CLIENT)
   public void registerIcons(IIconRegister par1IconRegister) {
      super.itemIcon = par1IconRegister.registerIcon("gravisuite:plazma_launcher");
   }

   @SideOnly(Side.CLIENT)
   public EnumRarity getRarity(ItemStack var1) {
      return EnumRarity.epic;
   }
}
