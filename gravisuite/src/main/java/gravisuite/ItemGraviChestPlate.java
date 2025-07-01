package gravisuite;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gravisuite.GraviSuite;
import gravisuite.Helpers;
import gravisuite.ServerProxy;
import gravisuite.keyboard.Keyboard;
import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.api.item.IMetalArmor;
import java.util.List;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
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

import java.util.Collection;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.Potion;


public class ItemGraviChestPlate extends ItemArmor implements IElectricItem, IMetalArmor, ISpecialArmor {
   public static int maxCharge;
   public static int minCharge;
   public static int transferLimit;
   public static int tier;
   public static int dischargeInFlight;
   public static int dischargeIdleMode;
   public static float boostSpeed;
   public static int boostMultiplier;
   private static double hoverModeFallSpeed;

   public ItemGraviChestPlate(ArmorMaterial armorMaterial, int par3, int par4) {
      super(armorMaterial, par3, par4);
      maxCharge = 30000000;
      transferLimit = '\uea60';
      tier = 4;
      minCharge = 10000;
      dischargeInFlight = 278;
      dischargeIdleMode = 1;
      boostSpeed = 0.2F;
      boostMultiplier = 3;
      this.setCreativeTab(GraviSuite.ic2Tab);
      this.setMaxDamage(27);
   }

   public static int switchWorkMode(EntityPlayer player, ItemStack itemstack) {
      if(readWorkMode(itemstack)) {
         saveWorkMode(itemstack, false);
         ServerProxy.sendPlayerMessage(player, "§e" + Helpers.formatMessage("message.graviChestPlate.levitationMode") + " " + "§c" + Helpers.formatMessage("message.text.disabled"));
      } else {
         saveWorkMode(itemstack, true);
         ServerProxy.sendPlayerMessage(player, "§e" + Helpers.formatMessage("message.graviChestPlate.levitationMode") + " " + "§a" + Helpers.formatMessage("message.text.enabled"));
      }

      return 0;
   }

   public static void applyMiningSpeedEffect(EntityPlayer player) {
      Collection<PotionEffect> currEffects = player.getActivePotionEffects();
      boolean currentlyOtherActive = false;
      for(PotionEffect eff : currEffects) {
         if(eff.getPotionID() == Potion.digSpeed.id && eff.getAmplifier() != 20) {
            currentlyOtherActive = true;
            break;
         }
      }
      if(currentlyOtherActive)
         return;
      player.addPotionEffect(new PotionEffect(Potion.digSpeed.id, 100, 20, true));
   }

   public static void clearMiningSpeedEffect(EntityPlayer player) {
      Collection<PotionEffect> currEffects = player.getActivePotionEffects();
      boolean currentlyOtherActive = false;
      for(PotionEffect eff : currEffects) {
         if(eff.getPotionID() == Potion.digSpeed.id && eff.getAmplifier() != 20) {
            currentlyOtherActive = true;
            break;
         }
      }
      if(currentlyOtherActive)
         return;
      player.removePotionEffect(Potion.digSpeed.id);
   }

   public static int switchFlyState(EntityPlayer player, ItemStack itemstack) {
      if(readFlyStatus(itemstack)) {
         if(!player.capabilities.isCreativeMode) {
            player.capabilities.allowFlying = false;
            player.capabilities.isFlying = false;
         }

         saveFlyStatus(itemstack, false);
         ServerProxy.sendPlayerMessage(player, "§c" + Helpers.formatMessage("message.graviChestPlate.gravitationEngine") + " " + Helpers.formatMessage("message.text.disabled"));
      } else {
         int currCharge = getCharge(itemstack);
         if(currCharge < minCharge && !player.capabilities.isCreativeMode) {
            ServerProxy.sendPlayerMessage(player, Helpers.formatMessage("message.graviChestPlate.lowEnergy"));
         } else {
            ServerProxy.sendPlayerMessage(player, "§a" + Helpers.formatMessage("message.graviChestPlate.gravitationEngine") + " " + Helpers.formatMessage("message.text.enabled"));
            player.capabilities.allowFlying = true;
            player.capabilities.isFlying = true;
            saveFlyStatus(itemstack, true);
         }
      }

      return 0;
   }

   public void onArmorTick(World worldObj, EntityPlayer player, ItemStack itemStack) {
      if(GraviSuite.proxy.checkLastUndressed(player)) {
         GraviSuite.proxy.SetLastUndressed(player, Boolean.valueOf(false));
      }

      if(readFlyStatus(itemStack)) {
         if(!GraviSuite.proxy.checkFlyActiveByMod(player)) {
            GraviSuite.proxy.SetFlyActiveByMod(player, Boolean.valueOf(true));
         }

         if(!player.capabilities.allowFlying) {
            player.capabilities.allowFlying = true;
            if(!player.onGround) {
               player.capabilities.isFlying = true;
            }
         }

         if(readWorkMode(itemStack)) {
            Keyboard var10000 = GraviSuite.keyboard;
            if((Keyboard.isJumpKeyDown(player) || player.motionY < -hoverModeFallSpeed && !player.onGround) && !player.capabilities.isFlying) {
               player.capabilities.isFlying = true;
            }
         }

         NBTTagCompound nbttagcompound = GraviSuite.getOrCreateNbtData(itemStack);
         int currCharge = getCharge(itemStack);
         if(!player.capabilities.isCreativeMode) {
            if(currCharge < dischargeInFlight) {
               ServerProxy.sendPlayerMessage(player, "§c" + Helpers.formatMessage("message.graviChestPlate.shutdown"));
               switchFlyState(player, itemStack);
            } else if(!player.onGround) {
               ElectricItem.manager.discharge(itemStack, (double)dischargeInFlight, 4, false, false, false);
            } else {
               ElectricItem.manager.discharge(itemStack, (double)dischargeIdleMode, 4, false, false, false);
            }
         }

         player.fallDistance = 0.0F;
         if(!player.onGround && player.capabilities.isFlying && Keyboard.isBoostKeyDown(player)) {
            this.boostMode(player, itemStack);
            if(currCharge <= dischargeInFlight * boostMultiplier && !player.capabilities.isCreativeMode) {
               ServerProxy.sendPlayerMessage(player, Helpers.formatMessage("message.graviChestPlate.noEnergyToBoost"));
            } else {
               if(Keyboard.isJumpKeyDown(player)) {
                  player.motionY += (double)(boostSpeed + 0.1F);
               }

               if(Keyboard.isSneakKeyDown(player)) {
                  player.motionY -= (double)(boostSpeed + 0.1F);
               }

               if(!player.capabilities.isCreativeMode) {
                  ElectricItem.manager.discharge(itemStack, (double)(dischargeInFlight * boostMultiplier), 3, true, false, false);
               }
            }
         }
         if(!player.onGround && player.capabilities.isFlying) {
            applyMiningSpeedEffect(player);
         } else {
            clearMiningSpeedEffect(player);
         }
      } else if(GraviSuite.proxy.checkFlyActiveByMod(player)) {
         if(!player.capabilities.isCreativeMode) {
            player.capabilities.allowFlying = false;
            player.capabilities.isFlying = false;
            clearMiningSpeedEffect(player);
         }

         if(GraviSuite.isSimulating()) {
            GraviSuite.proxy.SetFlyActiveByMod(player, Boolean.valueOf(false));
         }
      }

      if(player.isBurning()) {
         player.extinguish();
      }

   }

   public boolean boostMode(EntityPlayer player, ItemStack itemstack) {
      if(readFlyStatus(itemstack) && !player.onGround && player.capabilities.isFlying && !player.isInWater()) {
         int currCharge = getCharge(itemstack);
         if(currCharge > dischargeInFlight * boostMultiplier || player.capabilities.isCreativeMode) {
            player.moveFlying(player.moveStrafing, player.moveForward, boostSpeed);
            if(!player.capabilities.isCreativeMode) {
               ElectricItem.manager.discharge(itemstack, (double)(dischargeInFlight * boostMultiplier), 3, true, false, false);
            }
         }
      }

      return true;
   }

   public static boolean firstLoadServer(EntityPlayer player, ItemStack itemstack) {
      return true;
   }

   @SideOnly(Side.CLIENT)
   public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean par4) {
      String gravitationEngine = readFlyStatus(itemStack)?EnumChatFormatting.GREEN + Helpers.formatMessage("message.text.on"):EnumChatFormatting.RED + Helpers.formatMessage("message.text.off");
      String leviatitonModeStatus = readWorkMode(itemStack)?EnumChatFormatting.GREEN + Helpers.formatMessage("message.text.on"):EnumChatFormatting.RED + Helpers.formatMessage("message.text.off");
      list.add(EnumChatFormatting.AQUA + Helpers.formatMessage("message.graviChestPlate.gravitationEngine") + ": " + gravitationEngine);
      list.add(EnumChatFormatting.AQUA + Helpers.formatMessage("message.graviChestPlate.levitationMode") + ": " + leviatitonModeStatus);
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

   public static void setCharge(ItemStack itemstack, int newCharge) {
      NBTTagCompound nbttagcompound = GraviSuite.getOrCreateNbtData(itemstack);
      nbttagcompound.setInteger("charge", newCharge);
      System.out.println(newCharge);
   }

   public int getEnergyPerDamage() {
      return 2000;
   }

   public double getDamageAbsorptionRatio() {
      return 1.1D;
   }

   private double getBaseAbsorptionRatio() {
      return 0.4D;
   }

   @SideOnly(Side.CLIENT)
   public void registerIcons(IIconRegister par1IconRegister) {
      super.itemIcon = par1IconRegister.registerIcon("gravisuite:itemGraviChestPlate");
   }

   @SideOnly(Side.CLIENT)
   public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
      return "gravisuite:textures/armor/armor_graviChestPlate.png";
   }

   public boolean canProvideEnergy(ItemStack itemStack) {
      return true;
   }

   public double getMaxCharge(ItemStack itemStack) {
      return (double)maxCharge;
   }

   public int getTier(ItemStack itemStack) {
      return tier;
   }

   public double getTransferLimit(ItemStack itemStack) {
      return (double)transferLimit;
   }

   public boolean isMetalArmor(ItemStack itemstack, EntityPlayer player) {
      return true;
   }

   public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) {
      return (int)Math.round(20.0D * this.getBaseAbsorptionRatio() * this.getDamageAbsorptionRatio());
   }

   public void damageArmor(EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot) {
      ElectricItem.manager.discharge(stack, (double)(damage * this.getEnergyPerDamage()), Integer.MAX_VALUE, true, false, false);
   }

   public static boolean readWorkMode(ItemStack itemstack) {
      NBTTagCompound nbttagcompound = GraviSuite.getOrCreateNbtData(itemstack);
      return nbttagcompound.getBoolean("isLevitationActive");
   }

   public static boolean saveWorkMode(ItemStack itemstack, boolean workMode) {
      NBTTagCompound nbttagcompound = GraviSuite.getOrCreateNbtData(itemstack);
      nbttagcompound.setBoolean("isLevitationActive", workMode);
      return true;
   }

   public static boolean readFlyStatus(ItemStack itemstack) {
      NBTTagCompound nbttagcompound = GraviSuite.getOrCreateNbtData(itemstack);
      return nbttagcompound.getBoolean("isFlyActive");
   }

   public static boolean saveFlyStatus(ItemStack itemstack, boolean flyMode) {
      NBTTagCompound nbttagcompound = GraviSuite.getOrCreateNbtData(itemstack);
      nbttagcompound.setBoolean("isFlyActive", flyMode);
      return true;
   }

   public static boolean readActiveByModStatus(ItemStack itemstack) {
      NBTTagCompound nbttagcompound = GraviSuite.getOrCreateNbtData(itemstack);
      return nbttagcompound.getBoolean("isFlyActiveByMod");
   }

   public static boolean saveActiveByModStatus(ItemStack itemstack, boolean activeByMod) {
      NBTTagCompound nbttagcompound = GraviSuite.getOrCreateNbtData(itemstack);
      nbttagcompound.setBoolean("isFlyActiveByMod", activeByMod);
      return true;
   }

   @SideOnly(Side.CLIENT)
   public void getSubItems(Item item, CreativeTabs var2, List var3) {
      ItemStack var4 = new ItemStack(this, 1);
      ElectricItem.manager.charge(var4, 2.147483647E9D, Integer.MAX_VALUE, true, false);
      var3.add(var4);
      var3.add(new ItemStack(this, 1, this.getMaxDamage()));
   }

   public boolean isRepairable() {
      return false;
   }

   public int getItemEnchantability() {
      return 0;
   }

   @SideOnly(Side.CLIENT)
   public EnumRarity getRarity(ItemStack var1) {
      return EnumRarity.epic;
   }

   public Item getChargedItem(ItemStack itemStack) {
      return this;
   }

   public Item getEmptyItem(ItemStack itemStack) {
      return this;
   }
}
