package gravisuite;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gravisuite.GraviSuite;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class ItemSimpleItems extends Item {
   public static List<IIcon> itemsIconsList;
   public static List<String> itemNames;
   private IIcon[] iconsList = new IIcon[7];

   public ItemSimpleItems() {
      this.setHasSubtypes(true);
      this.setCreativeTab(GraviSuite.ic2Tab);
      this.setMaxStackSize(64);
      itemsIconsList = new ArrayList();
      itemNames = new ArrayList();
      this.addItemsNames();
   }

   public void addItemsNames() {
      itemNames.add("itemSuperConductorCover");
      itemNames.add("itemSuperConductor");
      itemNames.add("itemCoolingCore");
      itemNames.add("itemGravitationEngine");
      itemNames.add("itemMagnetron");
      itemNames.add("itemVajraCore");
      itemNames.add("itemEngineBoost");
   }

   @SideOnly(Side.CLIENT)
   public void registerIcons(IIconRegister iconRegister) {
      this.iconsList[0] = iconRegister.registerIcon("gravisuite:itemSuperconductorCover");
      this.iconsList[1] = iconRegister.registerIcon("gravisuite:itemSuperconductor");
      this.iconsList[2] = iconRegister.registerIcon("gravisuite:itemCoolingCore");
      this.iconsList[3] = iconRegister.registerIcon("gravisuite:itemGraviEngine");
      this.iconsList[4] = iconRegister.registerIcon("gravisuite:itemMagnetron");
      this.iconsList[5] = iconRegister.registerIcon("gravisuite:itemVajraCore");
      this.iconsList[6] = iconRegister.registerIcon("gravisuite:itemEngineBoost");
   }

   @Override
   public String getUnlocalizedName(ItemStack stack) {
      return "item." + (String)itemNames.get(stack.getItemDamage());
   }

   @Override
   public IIcon getIconFromDamage(int par1) {
      return this.iconsList[par1];
   }

   @Override
   public void getSubItems(Item item, CreativeTabs tabs, List itemList) {
      for(int meta = 0; meta <= itemNames.size() - 1; ++meta) {
         ItemStack stack = new ItemStack(this, 1, meta);
         itemList.add(stack);
      }

   }
}
