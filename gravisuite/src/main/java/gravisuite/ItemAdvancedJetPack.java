package gravisuite;

import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gravisuite.GraviSuite;
import gravisuite.Helpers;
import gravisuite.ServerProxy;
import gravisuite.keyboard.Keyboard;
import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.api.item.IMetalArmor;
import ic2.core.IC2;
import ic2.core.audio.AudioSource;
import ic2.core.audio.PositionSpec;
import java.util.List;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.common.ISpecialArmor.ArmorProperties;

public class ItemAdvancedJetPack extends ItemArmor implements IElectricItem, IMetalArmor, ISpecialArmor {
   public static int maxCharge;
   private int transferLimit;
   private int tier;
   public static int energyPerTick;
   public static int boostMultiplier;
   private static byte toggleTimer;
   private static double hoverModeFallSpeed;
   private static boolean lastJetpackUsed = false;
   public AudioSource audioSource;

   public ItemAdvancedJetPack(ArmorMaterial armorMaterial, int par3, int par4) {
      super(armorMaterial, par3, par4);
      maxCharge = 3000000;
      energyPerTick = 12;
      boostMultiplier = 5;
      this.transferLimit = 3000;
      hoverModeFallSpeed = 0.03D;
      this.setCreativeTab(GraviSuite.ic2Tab);
      this.tier = 3;
      toggleTimer = 20;
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

   public static int switchWorkMode(EntityPlayer player, ItemStack itemstack) {
      if(readWorkMode(itemstack)) {
         saveWorkMode(itemstack, false);
         ServerProxy.sendPlayerMessage(player, "§e" + Helpers.formatMessage("message.advElJetpack.hoverMode") + " " + "§c" + Helpers.formatMessage("message.text.disabled"));
      } else {
         saveWorkMode(itemstack, true);
         ServerProxy.sendPlayerMessage(player, "§e" + Helpers.formatMessage("message.advElJetpack.hoverMode") + " " + "§a" + Helpers.formatMessage("message.text.enabled"));
      }

      return 0;
   }

   public static int switchFlyState(EntityPlayer player, ItemStack itemstack) {
      if(readFlyStatus(itemstack)) {
         saveFlyStatus(itemstack, false);
         ServerProxy.sendPlayerMessage(player, "§e" + Helpers.formatMessage("message.advElJetpack.jetpackEngine") + " " + "§c" + Helpers.formatMessage("message.text.off"));
      } else {
         saveFlyStatus(itemstack, true);
         ServerProxy.sendPlayerMessage(player, "§e" + Helpers.formatMessage("message.advElJetpack.jetpackEngine") + " " + "§a" + Helpers.formatMessage("message.text.on"));
      }

      return 0;
   }

   public void onArmorTick(World worldObj, EntityPlayer player, ItemStack itemStack) {
      NBTTagCompound var3 = GraviSuite.getOrCreateNbtData(itemStack);
      boolean hoverMode = readWorkMode(itemStack);
      byte var5 = var3.getByte("toggleTimer");
      boolean var6 = false;
      Keyboard var10000 = GraviSuite.keyboard;
      if((Keyboard.isJumpKeyDown(player) || hoverMode && player.motionY < -hoverModeFallSpeed && !player.onGround) && readFlyStatus(itemStack)) {
         var6 = useJetpack(player, hoverMode);
      }

      if(ServerProxy.isSimulating() && var5 > 0) {
         --var5;
         var3.setByte("toggleTimer", var5);
      }

      if(!GraviSuite.isSimulating()) {
         if(lastJetpackUsed != var6) {
            if(var6) {
               if(this.audioSource == null) {
                  this.audioSource = IC2.audioManager.createSource(player, PositionSpec.Backpack, "Tools/Jetpack/JetpackLoop.ogg", true, false, IC2.audioManager.getDefaultVolume());
               }

               if(this.audioSource != null) {
                  this.audioSource.play();
               }
            } else if(this.audioSource != null) {
               this.audioSource.remove();
               this.audioSource = null;
            }

            lastJetpackUsed = var6;
         }

         if(this.audioSource != null) {
            this.audioSource.updatePosition();
         }
      }

   }

   public static boolean useJetpack(EntityPlayer player, boolean hoverMode) {
      ItemStack itemstack = player.inventory.armorInventory[2];
      int currCharge = getCharge(itemstack);
      if(currCharge < energyPerTick && !player.capabilities.isCreativeMode) {
         return false;
      } else {
         float power = 1.0F;
         float dropPercentage = 0.001F;
         float bcoff = (float)maxCharge / 20.0F;
         if((float)getCharge(itemstack) / (float)maxCharge <= dropPercentage) {
            power *= (float)getCharge(itemstack) / bcoff;
         }

         if(player.capabilities.isCreativeMode) {
            power = 1.0F;
         }

         Keyboard var10000 = GraviSuite.keyboard;
         if(Keyboard.isForwardKeyDown(player)) {
            float retruster = 0.3F;
            if(hoverMode) {
               retruster = 0.65F;
            }

            float forwardpower = power * retruster * 2.0F;
            float boostSpeed = 0.0F;
            var10000 = GraviSuite.keyboard;
            if(Keyboard.isBoostKeyDown(player) && (currCharge > energyPerTick * boostMultiplier || player.capabilities.isCreativeMode)) {
               boostSpeed = 0.09F;
               if(hoverMode) {
                  boostSpeed = 0.07F;
               }
            }

            if(forwardpower > 0.0F) {
               player.moveFlying(0.0F, 0.4F * forwardpower + boostSpeed, 0.02F + boostSpeed);
               if(boostSpeed > 0.0F && !player.capabilities.isCreativeMode && GraviSuite.isSimulating()) {
                  use(itemstack, energyPerTick * boostMultiplier);
               }
            }
         }

         int worldHeight = player.worldObj.getHeight();
         double currYPos = player.posY;
         if(currYPos > (double)(worldHeight - 25)) {
            if(currYPos > (double)worldHeight) {
               currYPos = (double)worldHeight;
            }

            power = (float)((double)power * (((double)worldHeight - currYPos) / 25.0D));
         }

         double prevmotion = player.motionY;
         player.motionY = Math.min(player.motionY + (double)(power * 0.2F), 0.6000000238418579D);
         if(hoverMode) {
            double maxHoverY = -hoverModeFallSpeed;
            var10000 = GraviSuite.keyboard;
            if(Keyboard.isJumpKeyDown(player)) {
               maxHoverY = 0.2D;
            }

            var10000 = GraviSuite.keyboard;
            if(Keyboard.isSneakKeyDown(player)) {
               maxHoverY = -0.2D;
            }

            if(currCharge > energyPerTick * boostMultiplier || player.capabilities.isCreativeMode) {
               var10000 = GraviSuite.keyboard;
               if(Keyboard.isBoostKeyDown(player)) {
                  label402: {
                     var10000 = GraviSuite.keyboard;
                     if(!Keyboard.isSneakKeyDown(player)) {
                        var10000 = GraviSuite.keyboard;
                        if(!Keyboard.isJumpKeyDown(player)) {
                           break label402;
                        }
                     }

                     maxHoverY *= 2.0D;
                     use(itemstack, energyPerTick * boostMultiplier);
                  }
               }
            }

            if(player.motionY > maxHoverY) {
               player.motionY = maxHoverY;
               if(prevmotion > player.motionY) {
                  player.motionY = prevmotion;
               }
            }
         }

         if(!player.capabilities.isCreativeMode && !player.onGround) {
            use(itemstack, energyPerTick);
         }

         player.fallDistance = 0.0F;
         player.distanceWalkedModified = 0.0F;
         if(player instanceof EntityPlayerMP) {
            ObfuscationReflectionHelper.setPrivateValue(NetHandlerPlayServer.class, ((EntityPlayerMP)player).playerNetServerHandler, Integer.valueOf(0), new String[]{"field_147365_f", "floatingTickCount"});
         }

         return true;
      }
   }

   public static void use(ItemStack item, int value) {
      if(GraviSuite.isSimulating()) {
         ElectricItem.manager.discharge(item, (double)value, Integer.MAX_VALUE, true, false, false);
      }

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
      super.itemIcon = par1IconRegister.registerIcon("gravisuite:itemAdvancedJetPack");
   }

   @SideOnly(Side.CLIENT)
   public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
      return "gravisuite:textures/armor/armor_advanced_jetpack.png";
   }

   public boolean isMetalArmor(ItemStack itemstack, EntityPlayer player) {
      return true;
   }

   public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) {
      return (int)Math.round(20.0D * this.getBaseAbsorptionRatio() * this.getDamageAbsorptionRatio());
   }

   public boolean isRepairable() {
      return false;
   }

   public int getItemEnchantability() {
      return 0;
   }

   public static boolean readWorkMode(ItemStack itemstack) {
      NBTTagCompound nbttagcompound = GraviSuite.getOrCreateNbtData(itemstack);
      return nbttagcompound.getBoolean("isHoverActive");
   }

   public static boolean saveWorkMode(ItemStack itemstack, boolean workMode) {
      NBTTagCompound nbttagcompound = GraviSuite.getOrCreateNbtData(itemstack);
      nbttagcompound.setBoolean("isHoverActive", workMode);
      nbttagcompound.setByte("toggleTimer", toggleTimer);
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

   @SideOnly(Side.CLIENT)
   public void getSubItems(Item item, CreativeTabs var2, List var3) {
      ItemStack var4 = new ItemStack(this, 1);
      ElectricItem.manager.charge(var4, 2.147483647E9D, Integer.MAX_VALUE, true, false);
      var3.add(var4);
      var3.add(new ItemStack(this, 1, this.getMaxDamage()));
   }

   @SideOnly(Side.CLIENT)
   public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean par4) {
      String jetPackEngine = readFlyStatus(itemStack)?EnumChatFormatting.GREEN + Helpers.formatMessage("message.text.on"):EnumChatFormatting.RED + Helpers.formatMessage("message.text.off");
      String hoverModeStatus = readWorkMode(itemStack)?EnumChatFormatting.GREEN + Helpers.formatMessage("message.text.on"):EnumChatFormatting.RED + Helpers.formatMessage("message.text.off");
      list.add(EnumChatFormatting.GOLD + Helpers.formatMessage("message.advElJetpack.jetpackEngine") + ": " + jetPackEngine);
      list.add(EnumChatFormatting.GOLD + Helpers.formatMessage("message.advElJetpack.hoverMode") + ": " + hoverModeStatus);
   }

   public void resetPlayerInAirTime(EntityPlayer player) {
      if(player instanceof EntityPlayerMP) {
         ObfuscationReflectionHelper.setPrivateValue(NetHandlerPlayServer.class, ((EntityPlayerMP)player).playerNetServerHandler, Integer.valueOf(0), new String[]{"field_147365_f", "floatingTickCount"});
      }
   }

   @SideOnly(Side.CLIENT)
   public EnumRarity getRarity(ItemStack var1) {
      return EnumRarity.uncommon;
   }

   public boolean canProvideEnergy(ItemStack itemStack) {
      return true;
   }

   public Item getChargedItem(ItemStack itemStack) {
      return this;
   }

   public Item getEmptyItem(ItemStack itemStack) {
      return this;
   }

   public double getMaxCharge(ItemStack itemStack) {
      return (double)maxCharge;
   }

   public int getTier(ItemStack itemStack) {
      return this.tier;
   }

   public double getTransferLimit(ItemStack itemStack) {
      return (double)this.transferLimit;
   }

   public void damageArmor(EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot) {
      ElectricItem.manager.discharge(stack, (double)(damage * this.getEnergyPerDamage()), Integer.MAX_VALUE, true, false, false);
   }
}
