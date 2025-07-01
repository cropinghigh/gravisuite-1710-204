package gravisuite;

import com.google.common.collect.ImmutableSet;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gravisuite.GraviSuite;
import gravisuite.Helpers;
import gravisuite.ServerProxy;
import gravisuite.keyboard.Keyboard;
import ic2.api.item.ElectricItem;
import ic2.api.item.IC2Items;
import ic2.api.item.IElectricItem;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.EntityInteractEvent;

public class ItemAdvChainsaw extends ItemTool implements IElectricItem {
   private int maxCharge;
   private int tier;
   private float effPower;
   private int energyPerOperation;
   private int transferLimit;
   public Set mineableBlocks = new HashSet();
   public int soundTicker;
   public int field_77865_bY;

   protected ItemAdvChainsaw(ToolMaterial toolMaterial) {
      super(0.0F, toolMaterial, new HashSet());
      this.setMaxDamage(27);
      this.maxCharge = '꿈';
      this.transferLimit = 500;
      this.tier = 2;
      this.effPower = 30.0F;
      super.efficiencyOnProperMaterial = this.effPower;
      this.energyPerOperation = 100;
      this.field_77865_bY = 1;
      this.setCreativeTab(GraviSuite.ic2Tab);
      MinecraftForge.EVENT_BUS.register(this);
   }

   public void init() {
      this.mineableBlocks.add(Blocks.planks);
      this.mineableBlocks.add(Blocks.bookshelf);
      this.mineableBlocks.add(Blocks.wooden_slab);
      this.mineableBlocks.add(Blocks.chest);
      this.mineableBlocks.add(Blocks.leaves);
      this.mineableBlocks.add(Blocks.web);
      this.mineableBlocks.add(Blocks.wool);
      this.mineableBlocks.add(Blocks.pumpkin);
      this.mineableBlocks.add(Blocks.melon_block);
      this.mineableBlocks.add(Blocks.cactus);
      this.mineableBlocks.add(Blocks.snow);
      ItemStack tmpItem = IC2Items.getItem("rubberLeaves");
      if(tmpItem != null) {
         this.mineableBlocks.add(tmpItem);
      }

   }

   public boolean canHarvestBlock(Block block, ItemStack stack) {
      return Items.diamond_axe.canHarvestBlock(block, stack) || Items.diamond_axe.func_150893_a(stack, block) > 1.0F || Items.diamond_sword.canHarvestBlock(block, stack) || Items.diamond_sword.func_150893_a(stack, block) > 1.0F || this.mineableBlocks.contains(block);
   }

   public int getHarvestLevel(ItemStack stack, String toolClass) {
      return toolClass.equals("axe")?super.toolMaterial.getHarvestLevel():super.getHarvestLevel(stack, toolClass);
   }

   public float getDigSpeed(ItemStack tool, Block block, int meta) {
      return !ElectricItem.manager.canUse(tool, (double)this.energyPerOperation)?1.0F:(this.canHarvestBlock(block, tool)?super.efficiencyOnProperMaterial:1.0F);
   }

   public Set<String> getToolClasses(ItemStack stack) {
      return ImmutableSet.of("axe");
   }

   public boolean hitEntity(ItemStack itemstack, EntityLivingBase entityliving, EntityLivingBase attacker) {
      if(ElectricItem.manager.use(itemstack, (double)this.energyPerOperation, attacker)) {
         entityliving.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer)attacker), 13.0F);
      } else {
         entityliving.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer)attacker), 1.0F);
      }

      return false;
   }

   @SubscribeEvent
   public void onEntityInteract(EntityInteractEvent event) {
      if(GraviSuite.isSimulating()) {
         Entity entity = event.target;
         EntityPlayer player = event.entityPlayer;
         ItemStack itemstack = player.inventory.getStackInSlot(player.inventory.currentItem);
         if(itemstack != null && itemstack.getItem() == this && entity instanceof IShearable && readToolMode(itemstack).intValue() == 0 && ElectricItem.manager.use(itemstack, (double)this.energyPerOperation, player)) {
            IShearable target = (IShearable)entity;
            if(target.isShearable(itemstack, entity.worldObj, (int)entity.posX, (int)entity.posY, (int)entity.posZ)) {
               ArrayList drops = target.onSheared(itemstack, entity.worldObj, (int)entity.posX, (int)entity.posY, (int)entity.posZ, EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, itemstack));

               for(int i = 0; i < drops.size(); ++i) {
                  ItemStack stack = (ItemStack)drops.get(i);
                  EntityItem ent = entity.entityDropItem(stack, 1.0F);
                  ent.motionY += (double)(Item.itemRand.nextFloat() * 0.05F);
                  ent.motionX += (double)((Item.itemRand.nextFloat() - Item.itemRand.nextFloat()) * 0.1F);
                  ent.motionZ += (double)((Item.itemRand.nextFloat() - Item.itemRand.nextFloat()) * 0.1F);
               }
            }
         }
      }

   }

   public boolean onBlockStartBreak(ItemStack itemstack, int x, int y, int z, EntityPlayer player) {
      if(!GraviSuite.isSimulating()) {
         return false;
      } else if(readToolMode(itemstack).intValue() != 0) {
         return false;
      } else {
         Block block = player.worldObj.getBlock(x, y, z);
         if(block instanceof IShearable) {
            IShearable target = (IShearable)block;
            if(target.isShearable(itemstack, player.worldObj, x, y, z) && ElectricItem.manager.use(itemstack, (double)this.energyPerOperation, player)) {
               ArrayList drops = target.onSheared(itemstack, player.worldObj, x, y, z, EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, itemstack));

               for(int i = 0; i < drops.size(); ++i) {
                  ItemStack stack = (ItemStack)drops.get(i);
                  float f = 0.7F;
                  double d = (double)(Item.itemRand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
                  double d1 = (double)(Item.itemRand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
                  double d2 = (double)(Item.itemRand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
                  EntityItem entityitem = new EntityItem(player.worldObj, (double)x + d, (double)y + d1, (double)z + d2, stack);
                  entityitem.delayBeforeCanPickup = 10;
                  player.worldObj.spawnEntityInWorld(entityitem);
               }

               player.addStat(StatList.mineBlockStatArray[Block.getIdFromBlock(block)], 1);
            }
         }

         return false;
      }
   }

   public boolean isRepairable() {
      return false;
   }

   @SideOnly(Side.CLIENT)
   public void registerIcons(IIconRegister par1IconRegister) {
      super.itemIcon = par1IconRegister.registerIcon("gravisuite:itemAdvancedChainsaw");
   }

   public boolean onBlockDestroyed(ItemStack par1ItemStack, World par2World, Block block, int xPos, int yPos, int zPos, EntityLivingBase par7EntityLiving) {
      if((double)block.getBlockHardness(par2World, xPos, yPos, zPos) != 0.0D) {
         if(par7EntityLiving != null) {
            ElectricItem.manager.use(par1ItemStack, (double)this.energyPerOperation, par7EntityLiving);
         } else {
            ElectricItem.manager.discharge(par1ItemStack, (double)this.energyPerOperation, this.tier, true, false, false);
         }
      }

      return true;
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
      if(Keyboard.isModeKeyDown(player)) {
         Integer toolMode = readToolMode(itemStack);
         toolMode = Integer.valueOf(toolMode.intValue() + 1);
         if(toolMode.intValue() > 1) {
            toolMode = Integer.valueOf(0);
         }

         this.saveToolMode(itemStack, toolMode);
         if(toolMode.intValue() == 0) {
            ServerProxy.sendPlayerMessage(player, EnumChatFormatting.GREEN + Helpers.formatMessage("message.advChainsaw.shear") + " " + Helpers.formatMessage("message.text.on"));
         }

         if(toolMode.intValue() == 1) {
            ServerProxy.sendPlayerMessage(player, EnumChatFormatting.RED + Helpers.formatMessage("message.advChainsaw.shear") + " " + Helpers.formatMessage("message.text.off"));
         }
      }

      return itemStack;
   }

   @SideOnly(Side.CLIENT)
   public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
      super.addInformation(par1ItemStack, par2EntityPlayer, par3List, par4);
      Integer toolMode = readToolMode(par1ItemStack);
      if(toolMode.intValue() == 0) {
         par3List.add(EnumChatFormatting.GREEN + Helpers.formatMessage("message.advChainsaw.shear") + " " + Helpers.formatMessage("message.text.on"));
      }

      if(toolMode.intValue() == 1) {
         par3List.add(EnumChatFormatting.RED + Helpers.formatMessage("message.advChainsaw.shear") + " " + Helpers.formatMessage("message.text.off"));
      }

   }

   @SideOnly(Side.CLIENT)
   public void getSubItems(Item item, CreativeTabs var2, List var3) {
      ItemStack var4 = new ItemStack(this, 1);
      ElectricItem.manager.charge(var4, 2.147483647E9D, Integer.MAX_VALUE, true, false);
      var3.add(var4);
      var3.add(new ItemStack(this, 1, this.getMaxDamage()));
   }

   public boolean canProvideEnergy(ItemStack itemStack) {
      return false;
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

   @SideOnly(Side.CLIENT)
   public EnumRarity getRarity(ItemStack var1) {
      return EnumRarity.uncommon;
   }

   public Item getChargedItem(ItemStack itemStack) {
      return this;
   }

   public Item getEmptyItem(ItemStack itemStack) {
      return this;
   }
}
