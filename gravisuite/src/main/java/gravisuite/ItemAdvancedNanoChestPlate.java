package gravisuite;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gravisuite.GraviSuite;
import gravisuite.ItemAdvancedJetPack;
import gravisuite.audio.AudioSource;
import ic2.api.item.ElectricItem;
import ic2.api.item.IC2Items;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor.ArmorProperties;

public class ItemAdvancedNanoChestPlate extends ItemAdvancedJetPack {
   public static int maxCharge;
   private int transferLimit;
   private int tier;
   public static int energyPerTick;
   public static int boostMultiplier;
   private static byte tickRate = 20;
   private static byte ticker;
   private int energyForExtinguish = '썐';
   private static boolean lastJetpackUsed = false;
   public AudioSource audioSource;

   public ItemAdvancedNanoChestPlate(ArmorMaterial armorMaterial, int par3, int par4) {
      super(armorMaterial, par3, par4);
      this.setCreativeTab(GraviSuite.ic2Tab);
      this.setMaxDamage(27);
   }

   public ArmorProperties getProperties(EntityLivingBase player, ItemStack armor, DamageSource source, double damage, int slot) {
      double absorptionRatio = this.getBaseAbsorptionRatio() * this.getDamageAbsorptionRatio();
      int energyPerDamage = this.getEnergyPerDamage();
      int damageLimit = (int)(energyPerDamage > 0?25.0D * ElectricItem.manager.getCharge(armor) / (double)energyPerDamage:0.0D);
      return new ArmorProperties(0, absorptionRatio, damageLimit);
   }

   public int getArmorDisplay(EntityPlayer var1, ItemStack var2, int var3) {
      return ElectricItem.manager.discharge(var2, 2.147483647E9D, Integer.MAX_VALUE, true, false, true) >= (double)this.getEnergyPerDamage()?(int)Math.round(20.0D * this.getBaseAbsorptionRatio() * this.getDamageAbsorptionRatio()):0;
   }

   public void onArmorTick(World worldObj, EntityPlayer player, ItemStack itemStack) {
      super.onArmorTick(worldObj, player, itemStack);
      if(ticker++ % tickRate == 0 && player.isBurning() && ElectricItem.manager.canUse(itemStack, (double)this.energyForExtinguish)) {
         for(int i = 0; i < player.inventory.mainInventory.length; ++i) {
            if(player.inventory.mainInventory[i] != null) {
               ItemStack mainItem = player.inventory.mainInventory[i].copy();
               if(mainItem.getItem() == IC2Items.getItem("waterCell").getItem()) {
                  if(player.inventory.mainInventory[i].stackSize > 1) {
                     --player.inventory.mainInventory[i].stackSize;
                  } else {
                     player.inventory.mainInventory[i] = null;
                  }

                  use(itemStack, this.energyForExtinguish);
                  player.extinguish();
                  break;
               }
            }
         }
      }

   }

   public int getEnergyPerDamage() {
      return 800;
   }

   public double getDamageAbsorptionRatio() {
      return 0.9D;
   }

   private double getBaseAbsorptionRatio() {
      return 0.4D;
   }

   @SideOnly(Side.CLIENT)
   public void registerIcons(IIconRegister par1IconRegister) {
      super.itemIcon = par1IconRegister.registerIcon("gravisuite:itemAdvancedNanoChestPlate");
   }

   @SideOnly(Side.CLIENT)
   public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
      return "gravisuite:textures/armor/armor_advNanoChestPlate.png";
   }
}
