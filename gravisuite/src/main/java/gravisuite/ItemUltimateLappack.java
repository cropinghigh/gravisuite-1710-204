package gravisuite;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gravisuite.GraviSuite;
import gravisuite.ItemAdvancedLappack;
import ic2.api.item.IElectricItem;
import ic2.api.item.IMetalArmor;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraftforge.common.ISpecialArmor;

public class ItemUltimateLappack extends ItemAdvancedLappack implements IElectricItem, IMetalArmor, ISpecialArmor {
   private int maxCharge;
   private int transferLimit;
   private int tier;

   public ItemUltimateLappack(ArmorMaterial armorMaterial, int par3, int par4, int MaxCharge, int Tier, int TransferLimit) {
      super(armorMaterial, par3, par4, MaxCharge, Tier, TransferLimit);
      this.setCreativeTab(GraviSuite.ic2Tab);
   }

   @SideOnly(Side.CLIENT)
   public void registerIcons(IIconRegister iconRegister) {
      super.itemIcon = iconRegister.registerIcon("gravisuite:itemUltimateLappack");
   }

   @SideOnly(Side.CLIENT)
   public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
      return "gravisuite:textures/armor/armor_ultimate_lappack.png";
   }

   @SideOnly(Side.CLIENT)
   public EnumRarity getRarity(ItemStack var1) {
      return EnumRarity.rare;
   }
}
