package gravisuite;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemRelocatorPortal extends ItemBlock {
   public ItemRelocatorPortal(Block block) {
      super(block);
      this.setMaxDamage(0);
      this.setHasSubtypes(false);
      this.setUnlocalizedName("block.relocatorPortal.name");
   }

   public String getUnlocalizedName(ItemStack itemstack) {
      return "block.relocatorPortal.name";
   }
}
