package gravisuite;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gravisuite.GraviSuite;
import gravisuite.Helpers;
import gravisuite.IItemTickListener;
import gravisuite.ServerProxy;
import gravisuite.keyboard.Keyboard;
import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.api.item.IMetalArmor;
import java.util.List;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.common.ISpecialArmor.ArmorProperties;

public class ItemAdvancedLappack extends ItemArmor implements IElectricItem, IMetalArmor, ISpecialArmor, IItemTickListener {
   private int maxCharge;
   private int transferLimit;
   private int tier;
   public String itemNm;
   private Boolean activated = Boolean.valueOf(false);

   public ItemAdvancedLappack(ArmorMaterial armorMaterial, int par3, int par4, int MaxCharge, int Tier, int TransferLimit) {
      super(armorMaterial, par3, par4);
      this.maxCharge = MaxCharge;
      this.transferLimit = TransferLimit;
      this.setCreativeTab(GraviSuite.ic2Tab);
      this.tier = Tier;
      this.setMaxDamage(27);
   }

   public ArmorProperties getProperties(EntityLivingBase player, ItemStack armor, DamageSource source, double damage, int slot) {
      double absorptionRatio = this.getBaseAbsorptionRatio() * this.getDamageAbsorptionRatio();
      int energyPerDamage = this.getEnergyPerDamage();
      int damageLimit = (int)(energyPerDamage > 0?25.0D * ElectricItem.manager.getCharge(armor) / (double)energyPerDamage:0.0D);
      return new ArmorProperties(0, absorptionRatio, damageLimit);
   }

   public static int getCharge(ItemStack itemstack) {
      NBTTagCompound nbttagcompound = GraviSuite.getOrCreateNbtData(itemstack);
      int k = nbttagcompound.getInteger("charge");
      return k;
   }

   public int getEnergyPerDamage() {
      return 0;
   }

   public double getDamageAbsorptionRatio() {
      return 0.0D;
   }

   private double getBaseAbsorptionRatio() {
      return 0.0D;
   }

   @SideOnly(Side.CLIENT)
   public void registerIcons(IIconRegister par1IconRegister) {
      super.itemIcon = par1IconRegister.registerIcon("gravisuite:itemAdvancedLappack");
   }

   @SideOnly(Side.CLIENT)
   public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
      return "gravisuite:textures/armor/armor_advanced_lappack.png";
   }

   public boolean isMetalArmor(ItemStack itemstack, EntityPlayer player) {
      return true;
   }

   public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) {
      return (int)Math.round(20.0D * this.getBaseAbsorptionRatio() * this.getDamageAbsorptionRatio());
   }

   public void damageArmor(EntityLiving entity, ItemStack itemstack, DamageSource source, int damage, int slot) {
      ElectricItem.manager.discharge(itemstack, (double)(damage * this.getEnergyPerDamage()), Integer.MAX_VALUE, true, false, false);
   }

   public boolean isRepairable() {
      return false;
   }

   public int getItemEnchantability() {
      return 0;
   }

   @SideOnly(Side.CLIENT)
   public void getSubItems(Item item, CreativeTabs var2, List var3) {
      ItemStack var4 = new ItemStack(this, 1);
      ElectricItem.manager.charge(var4, 2.147483647E9D, Integer.MAX_VALUE, true, false);
      var3.add(var4);
      var3.add(new ItemStack(this, 1, this.getMaxDamage()));
   }

   @SideOnly(Side.CLIENT)
   public EnumRarity getRarity(ItemStack var1) {
      return EnumRarity.uncommon;
   }

   public boolean canProvideEnergy(ItemStack itemStack) {
      return true;
   }

   public double getMaxCharge(ItemStack itemStack) {
      return (double)this.maxCharge;
   }

   public int getTier(ItemStack itemStack) {
      return this.tier;
   }

   public double getTransferLimit(ItemStack itemStack) {
      return (double)this.transferLimit;
   }

   public void damageArmor(EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot) {
   }

   public static Integer readToolMode(ItemStack itemstack) {
      NBTTagCompound nbttagcompound = GraviSuite.getOrCreateNbtData(itemstack);
      Integer toolMode = Integer.valueOf(nbttagcompound.getInteger("toolMode"));
      if(toolMode.intValue() < 0 || toolMode.intValue() > 1) {
         toolMode = Integer.valueOf(0);
      }

      return toolMode;
   }

   public void saveToolMode(ItemStack itemstack, Integer toolMode) {
      NBTTagCompound nbttagcompound = GraviSuite.getOrCreateNbtData(itemstack);
      nbttagcompound.setInteger("toolMode", toolMode.intValue());
   }

   public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
      if(Keyboard.isModeKeyDown(player) && GraviSuite.isSimulating()) {
         Integer toolMode = readToolMode(itemStack);
         toolMode = Integer.valueOf(toolMode.intValue() + 1);
         if(toolMode.intValue() > 1) {
            toolMode = Integer.valueOf(0);
         }

         this.saveToolMode(itemStack, toolMode);
         if(toolMode.intValue() == 0) {
            ServerProxy.sendPlayerMessage(player, EnumChatFormatting.GOLD + Helpers.formatMessage("message.text.powerSupply") + " " + EnumChatFormatting.RED + Helpers.formatMessage("message.text.disabled"));
         }

         if(toolMode.intValue() == 1) {
            ServerProxy.sendPlayerMessage(player, EnumChatFormatting.GOLD + Helpers.formatMessage("message.text.powerSupply") + " " + EnumChatFormatting.GREEN + Helpers.formatMessage("message.text.enabled"));
         }
      }

      return itemStack;
   }

   @SideOnly(Side.CLIENT)
   public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
      super.addInformation(par1ItemStack, par2EntityPlayer, par3List, par4);
      Integer toolMode = readToolMode(par1ItemStack);
      if(toolMode.intValue() == 0) {
         par3List.add(EnumChatFormatting.GOLD + Helpers.formatMessage("message.text.powerSupply") + ": " + EnumChatFormatting.RED + Helpers.formatMessage("message.text.disabled"));
      }

      if(toolMode.intValue() == 1) {
         par3List.add(EnumChatFormatting.GOLD + Helpers.formatMessage("message.text.powerSupply") + ": " + EnumChatFormatting.GREEN + Helpers.formatMessage("message.text.enabled"));
      }

   }

   public boolean onTick(EntityPlayer player, ItemStack itemstack) {
      Integer toolMode = readToolMode(itemstack);
      if(toolMode.intValue() == 1 && GraviSuite.isSimulating()) {
         ItemStack armorItemStack = player.inventory.armorInventory[2];
         if(armorItemStack != null && armorItemStack.getItem() instanceof IElectricItem) {
            int energyPacket = this.transferLimit;
            int mainCharge = getCharge(itemstack);
            if(mainCharge <= this.transferLimit) {
               energyPacket = mainCharge;
            }

            double sentPacket = ElectricItem.manager.charge(armorItemStack, (double)energyPacket, 4, false, false);
            if(sentPacket > 0.0D) {
               ElectricItem.manager.discharge(itemstack, sentPacket, this.tier, false, false, false);
            }
         }
      }

      return true;
   }

   public Item getChargedItem(ItemStack itemStack) {
      return this;
   }

   public Item getEmptyItem(ItemStack itemStack) {
      return this;
   }
}
