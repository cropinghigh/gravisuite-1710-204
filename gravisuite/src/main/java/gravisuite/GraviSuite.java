package gravisuite;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import gravisuite.BlockRelocatorPortal;
import gravisuite.EntityPlasmaBall;
import gravisuite.GuiHandler;
import gravisuite.ItemAdvChainsaw;
import gravisuite.ItemAdvDDrill;
import gravisuite.ItemAdvancedJetPack;
import gravisuite.ItemAdvancedLappack;
import gravisuite.ItemAdvancedNanoChestPlate;
import gravisuite.ItemGraviChestPlate;
import gravisuite.ItemGraviTool;
import gravisuite.ItemRelocator;
import gravisuite.ItemRelocatorPortal;
import gravisuite.ItemSimpleItems;
import gravisuite.ItemSonicLauncher;
import gravisuite.ItemUltimateLappack;
import gravisuite.ItemVajra;
import gravisuite.ServerProxy;
import gravisuite.ServerTickHandler;
import gravisuite.StackUtils;
import gravisuite.TickHandler;
import gravisuite.TileEntityRelocatorPortal;
import gravisuite.audio.AudioManager;
import gravisuite.client.ClientTickHandler;
import gravisuite.keyboard.Keyboard;
import gravisuite.network.PacketHandler;
import ic2.api.item.IC2Items;
import ic2.api.recipe.Recipes;
import java.io.File;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

import mekanism.api.ItemRetriever;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.WorldTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.AxisAlignedBB;

import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import java.util.List;
import java.util.ArrayList;

@Mod(
   modid = "GraviSuite",
   name = "Gravitation Suite",
   dependencies = "required-after:IC2; after:RedPowerCore",
   version = "1.7.10-2.0.4"
)
public class GraviSuite {
   @SidedProxy(
      clientSide = "gravisuite.client.ClientProxy",
      serverSide = "gravisuite.ServerProxy"
   )
   public static ServerProxy proxy;
   @SidedProxy(
      clientSide = "gravisuite.keyboard.KeyboardClient",
      serverSide = "gravisuite.keyboard.Keyboard"
   )
   public static Keyboard keyboard;
   @SidedProxy(
      clientSide = "gravisuite.audio.AudioManagerClient",
      serverSide = "gravisuite.audio.AudioManager"
   )
   public static AudioManager audioManager;
   public static TickHandler tickHandler;
   public static int uhGenDay;
   public static int uhGenNight;
   public static int relocatorEnergyPerStandartTp;
   public static int relocatorEnergyPerDimesionTp;
   public static int relocatorEnergyPerPortal;
   public static int relocatorEnergyPerTranslocator;
   public static Configuration config;
   public static File configFile;
   private boolean keyDown;
   public static int hudPos;
   public static int blockRelocatorPortalRenderID;
   public static boolean displayHud;
   public static Item graviChestPlate;
   public static Item ultimateLappack;
   public static Item ultimateSolarHelmet;
   public static Item vajra;
   public static Item graviTool;
   public static Item advDDrill;
   public static Item advIrDrill;
   public static Item advChainsaw;
   public static Item advLappack;
   public static Item advJetpack;
   public static Item advNanoChestPlate;
   public static Item relocator;
   public static Item sonicLauncher;
   public static Block blockRelocatorPortal;
   public Item itemSimpleItem;
   public static ItemStack superConductorCover;
   public static ItemStack superConductor;
   public static ItemStack coolingCore;
   public static ItemStack gravitationEngine;
   public static ItemStack ultimateSolarPanel;
   public static ItemStack magnetron;
   public static ItemStack vajraCore;
   public static ItemStack itemEngineBoost;
   public static ClientTickHandler clientTickHandler;
   public static ServerTickHandler serverTickHandler;
   public static boolean disableUltimateLappackRecipe;
   public static boolean disableGraviChestPlateRecipe;
   public static boolean disableUltimateSolarHelmetRecipe;
   public static boolean disableVajraRecipe;
   public static boolean disableAdvancedLappackRecipe;
   public static boolean disableAdvancedJetpackRecipe;
   public static boolean disableAdvDDrillRecipe;
   public static boolean disableAdvChainsawRecipe;
   public static boolean disableAdvJetpackRecipe;
   public static boolean disableAdvNanoChestPlateRecipe;
   public static boolean disableGraviToolRecipe;
   public static boolean disableRelocatorRecipe;
   public static boolean disableRelocatorPortal;
   public static boolean disableRelocatorTranslocator;
   public static boolean disableVajraAccurate;
   public static boolean disableSounds;
   public static boolean logWrench;
   public static CreativeTabs ic2Tab;
   private static Class ASP;
   public static final Side side = FMLCommonHandler.instance().getEffectiveSide();
   public static Random random = new Random();
   public static PacketHandler packetHandler;
   @Instance("GraviSuite")
   public static GraviSuite instance;

   public static final byte PICKUP_TICK_DELAY = 1;

   public class QueuedPickupEvent {
      EntityPlayer player;
      World world;
      AxisAlignedBB boundingBox;
      byte ticked;
   };
   private static List<QueuedPickupEvent> queuedPickupEvents;

   public class GSEventHandler {
      @SubscribeEvent
      public void onWorldTick(TickEvent.WorldTickEvent evt) {
         if(evt.side == Side.SERVER && evt.phase == TickEvent.Phase.START) {
            //Process queued pickup events
            if(queuedPickupEvents.size() > 0) {
               QueuedPickupEvent e = queuedPickupEvents.get(0);
               if(evt.world == e.world) {
                  if(e.ticked < PICKUP_TICK_DELAY) e.ticked++;
                  else {
                     List<EntityItem> items = e.world.getEntitiesWithinAABB(EntityItem.class, e.boundingBox);
                     // System.out.println("PLAYER POS: " + e.player.lastTickPosX + " " + e.player.lastTickPosY + " " + e.player.lastTickPosZ);
                     // System.out.println(items.size() + " ITEMS IN BOX:");
                     for (EntityItem item : items) {
                           // System.out.println("    " + item.getEntityItem().getDisplayName());
                           // System.out.println("       ITEM POS: " + item.lastTickPosX + " " + item.lastTickPosY + " " + item.lastTickPosZ);
                           item.setPosition(e.player.lastTickPosX, e.player.lastTickPosY, e.player.lastTickPosZ);
                           item.setLocationAndAngles(e.player.lastTickPosX, e.player.lastTickPosY, e.player.lastTickPosZ, 0, 0);
                           // System.out.println("       NEW ITEM POS: " + item.serverPosX + " " + item.serverPosY + " " + item.serverPosZ);
                           item.onCollideWithPlayer(e.player);
                           EntityItemPickupEvent pE = new EntityItemPickupEvent(e.player, item);
                           MinecraftForge.EVENT_BUS.post(pE);
                           // System.out.println("       RETURN: " + x);
                     }
                     queuedPickupEvents.remove(0);
                  }
               }
            }
         }
      }
   };

   public static GSEventHandler evtHdl;

   public static void queuePickupEvent(QueuedPickupEvent e) {
      queuedPickupEvents.add(e);
   }

   public static void getIC2Tab() {
      for(int i = 0; i < CreativeTabs.creativeTabArray.length; ++i) {
         if(CreativeTabs.creativeTabArray[i].getTabLabel() == "IC2") {
            ic2Tab = CreativeTabs.creativeTabArray[i];
         }
      }
   }

   public static void addLog(String logString) {
      System.out.println("[GraviSuite] " + logString);
   }

   public static void registerEntity(Class entityClass, String name) {
      int entityID = EntityRegistry.findGlobalUniqueEntityId();
      EntityRegistry.registerModEntity(entityClass, name, entityID, instance, 64, 1, true);
   }

   @EventHandler
   public void preInit(FMLPreInitializationEvent event) {
      configFile = event.getSuggestedConfigurationFile();
      Configuration config = new Configuration(event.getSuggestedConfigurationFile());

      try {
         config.load();
         hudPos = config.get("Hud settings", "hudPosition", 1).getInt(1);
         displayHud = config.get("Hud settings", "Display hud", true).getBoolean(true);
         disableUltimateLappackRecipe = config.get("Recipes settings", "Disable UltimateLappack recipe", false).getBoolean(false);
         disableGraviChestPlateRecipe = config.get("Recipes settings", "Disable GraviChestPlate recipe", false).getBoolean(false);
         disableUltimateSolarHelmetRecipe = config.get("Recipes settings", "Disable UltimateSolarHelmet recipe", false).getBoolean(false);
         disableAdvancedLappackRecipe = config.get("Recipes settings", "Disable AdvancedLappack recipe", false).getBoolean(false);
         disableAdvancedJetpackRecipe = config.get("Recipes settings", "Disable Advanced Jetpack recipe", false).getBoolean(false);
         disableVajraRecipe = config.get("Recipes settings", "Disable Vajra recipe", false).getBoolean(false);
         disableAdvDDrillRecipe = config.get("Recipes settings", "Disable Advanced Dimond Drill recipe", false).getBoolean(false);
         disableAdvChainsawRecipe = config.get("Recipes settings", "Disable Advanced Chainsaw recipe", false).getBoolean(false);
         disableAdvJetpackRecipe = config.get("Recipes settings", "Disable Advanced Jetpack recipe", false).getBoolean(false);
         disableAdvNanoChestPlateRecipe = config.get("recipes settings", "Disable Advanced NanoChestPlate recipe", false).getBoolean(false);
         disableGraviToolRecipe = config.get("Recipes settings", "Disable GraviTool recipe", false).getBoolean(false);
         disableRelocatorRecipe = config.get("Recipes settings", "Disable Relocator recipe", false).getBoolean(false);
         disableVajraAccurate = config.get("Vajra settings", "Disable Vajra accurate mode", false).getBoolean(false);
         relocatorEnergyPerStandartTp = config.get("Relocator settings", "relocatorEnergyPerStandartTp", 1000000).getInt(1000000);
         relocatorEnergyPerDimesionTp = config.get("Relocator settings", "relocatorEnergyPerDimesionTp", 1500000).getInt(1500000);
         relocatorEnergyPerPortal = config.get("Relocator settings", "relocatorEnergyPerPortal", 2500000).getInt(2500000);
         relocatorEnergyPerTranslocator = config.get("Relocator settings", "relocatorEnergyPerTranslocator", 2000000).getInt(2000000);
         disableRelocatorTranslocator = config.get("Relocator settings", "Disable translocator mode", false).getBoolean(false);
         disableRelocatorPortal = config.get("Relocator settings", "Disable portal mode", false).getBoolean(false);
         disableSounds = config.get("Sounds settings", "Disable all sounds", false).getBoolean(false);
         logWrench = true;
      } catch (Exception var7) {
         addLog("error while loading config file");
         throw new RuntimeException(var7);
      } finally {
         config.save();
      }

      if(side == Side.CLIENT) {
         getIC2Tab();
      }

      blockRelocatorPortal = new BlockRelocatorPortal(Material.portal);
      GameRegistry.registerBlock(blockRelocatorPortal, ItemRelocatorPortal.class, "BlockRelocatorPortal");
      GameRegistry.registerTileEntity(TileEntityRelocatorPortal.class, "Relocator Portal");
      graviChestPlate = (new ItemGraviChestPlate(ArmorMaterial.DIAMOND, proxy.addArmor("GraviSuite"), 1)).setUnlocalizedName("graviChestPlate");
      GameRegistry.registerItem(graviChestPlate, "graviChestPlate");
      advNanoChestPlate = (new ItemAdvancedNanoChestPlate(ArmorMaterial.DIAMOND, proxy.addArmor("GraviSuite"), 1)).setUnlocalizedName("advNanoChestPlate");
      GameRegistry.registerItem(advNanoChestPlate, "advNanoChestPlate");
      ultimateLappack = (new ItemUltimateLappack(ArmorMaterial.DIAMOND, proxy.addArmor("GraviSuite"), 1, 30000000, 4, '\uea60')).setUnlocalizedName("ultLappack");
      GameRegistry.registerItem(ultimateLappack, "ultimateLappack");
      advLappack = (new ItemAdvancedLappack(ArmorMaterial.DIAMOND, proxy.addArmor("GraviSuite"), 1, 3000000, 3, 3000)).setUnlocalizedName("advLappack");
      GameRegistry.registerItem(advLappack, "advLappack");
      advJetpack = (new ItemAdvancedJetPack(ArmorMaterial.DIAMOND, proxy.addArmor("GraviSuite"), 1)).setUnlocalizedName("advElJetpack");
      GameRegistry.registerItem(advJetpack, "advJetpack");
      vajra = (new ItemVajra(ToolMaterial.EMERALD)).setUnlocalizedName("vajra");
      GameRegistry.registerItem(vajra, "vajra");
      graviTool = (new ItemGraviTool(ToolMaterial.IRON)).setUnlocalizedName("graviTool");
      GameRegistry.registerItem(graviTool, "graviTool");
      advDDrill = (new ItemAdvDDrill(ToolMaterial.EMERALD)).setUnlocalizedName("advDDrill");
      GameRegistry.registerItem(advDDrill, "advDDrill");
      advIrDrill = (new ItemAdvIrDrill(ToolMaterial.EMERALD)).setUnlocalizedName("advIrDrill");
      GameRegistry.registerItem(advIrDrill, "advIrDrill");
      advChainsaw = (new ItemAdvChainsaw(ToolMaterial.EMERALD)).setUnlocalizedName("advChainsaw");
      GameRegistry.registerItem(advChainsaw, "advChainsaw");
      relocator = (new ItemRelocator(ToolMaterial.EMERALD)).setUnlocalizedName("relocator");
      GameRegistry.registerItem(relocator, "relocator");
      sonicLauncher = (new ItemSonicLauncher(ToolMaterial.EMERALD)).setUnlocalizedName("sonicLauncher");
      GameRegistry.registerItem(sonicLauncher, "sonicLauncher");
      registerEntity(EntityPlasmaBall.class, "PlasmaBall");
      this.itemSimpleItem = new ItemSimpleItems();
      GameRegistry.registerItem(this.itemSimpleItem, "itemSimpleItem");
      superConductorCover = new ItemStack(this.itemSimpleItem, 1, 0);
      superConductor = new ItemStack(this.itemSimpleItem, 1, 1);
      coolingCore = new ItemStack(this.itemSimpleItem, 1, 2);
      gravitationEngine = new ItemStack(this.itemSimpleItem, 1, 3);
      magnetron = new ItemStack(this.itemSimpleItem, 1, 4);
      vajraCore = new ItemStack(this.itemSimpleItem, 1, 5);
      itemEngineBoost = new ItemStack(this.itemSimpleItem, 1, 6);
      ((ItemAdvDDrill)advDDrill).init();
      ((ItemAdvIrDrill)advIrDrill).init();
      ((ItemAdvChainsaw)advChainsaw).init();
      OreDictionary.registerOre("itemSuperconductor", superConductor);
      proxy.initCore();
      proxy.registerSoundHandler();
      proxy.registerRenderers();
      packetHandler = new PacketHandler();
      NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());

      queuedPickupEvents = new ArrayList<QueuedPickupEvent>();
      evtHdl = new GSEventHandler();
   }

   @EventHandler
   public void init(FMLInitializationEvent event) {
        FMLCommonHandler.instance().bus().register(evtHdl);
    }

   @EventHandler
   public void load(FMLInitializationEvent event) {
      tickHandler = new TickHandler();
   }

   @EventHandler
   public void afterModsLoaded(FMLPostInitializationEvent event) {
      if(hudPos != 1 && hudPos != 2 && hudPos != 3 && hudPos != 4) {
         hudPos = 1;
      }

      GameRegistry.addRecipe(new ItemStack(this.itemSimpleItem, 3, 0), new Object[]{"RBR", "CCC", "RBR", Character.valueOf('R'), IC2Items.getItem("advancedAlloy"), Character.valueOf('B'), IC2Items.getItem("iridiumPlate"), Character.valueOf('C'), IC2Items.getItem("carbonPlate")});
      
      GameRegistry.addRecipe(new ItemStack(this.itemSimpleItem, 3, 1), new Object[]{"RRR", "CBC", "RRR", Character.valueOf('R'), new ItemStack(this.itemSimpleItem, 1, 0), Character.valueOf('B'), Items.gold_ingot, Character.valueOf('C'), IC2Items.getItem("glassFiberCableItem")});
      
      GameRegistry.addRecipe(new ItemStack(this.itemSimpleItem, 1, 2), new Object[]{"RBR", "CDC", "RBR", Character.valueOf('R'), IC2Items.getItem("reactorCoolantSix"), Character.valueOf('B'), IC2Items.getItem("reactorHeatSwitchDiamond"), Character.valueOf('C'), IC2Items.getItem("reactorPlatingHeat"), Character.valueOf('D'), IC2Items.getItem("iridiumPlate")});
      GameRegistry.addRecipe(new ItemStack(this.itemSimpleItem, 1, 2), new Object[]{"RBR", "CDC", "RBR", Character.valueOf('R'), StackUtils.copyWithWildCard(IC2Items.getItem("reactorCoolantSix")), Character.valueOf('B'), StackUtils.copyWithWildCard(IC2Items.getItem("reactorHeatSwitchDiamond")), Character.valueOf('C'), IC2Items.getItem("reactorPlatingHeat"), Character.valueOf('D'), IC2Items.getItem("iridiumPlate")});
      
      Recipes.advRecipes.addRecipe(new ItemStack(this.itemSimpleItem, 1, 3), new Object[]{"RBR", "CDC", "RBR", Character.valueOf('R'), IC2Items.getItem("teslaCoil"), Character.valueOf('B'), "itemSuperconductor", Character.valueOf('C'), new ItemStack(this.itemSimpleItem, 1, 2), Character.valueOf('D'), IC2Items.getItem("hvTransformer")});
      
      if(!disableUltimateLappackRecipe) {
         Recipes.advRecipes.addRecipe(new ItemStack(ultimateLappack, 1), new Object[]{"RBR", "RDR", "RAR", Character.valueOf('R'), StackUtils.copyWithWildCard(IC2Items.getItem("lapotronCrystal")), Character.valueOf('B'), IC2Items.getItem("iridiumPlate"), Character.valueOf('D'), StackUtils.copyWithWildCard(IC2Items.getItem("lapPack")), Character.valueOf('A'), "itemSuperconductor"});
         Recipes.advRecipes.addRecipe(new ItemStack(ultimateLappack, 1), new Object[]{"RBR", "RDR", "RAR", Character.valueOf('R'), StackUtils.copyWithWildCard(IC2Items.getItem("lapotronCrystal")), Character.valueOf('B'), IC2Items.getItem("iridiumPlate"), Character.valueOf('D'), StackUtils.copyWithWildCard(new ItemStack(advLappack, 1)), Character.valueOf('A'), "itemSuperconductor"});
      }

      if(!disableGraviChestPlateRecipe) {
         Recipes.advRecipes.addRecipe(new ItemStack(graviChestPlate, 1), new Object[]{"RAR", "DBD", "RCR", Character.valueOf('R'), "itemSuperconductor", Character.valueOf('A'), StackUtils.copyWithWildCard(IC2Items.getItem("quantumBodyarmor")), Character.valueOf('D'), new ItemStack(this.itemSimpleItem, 1, 3), Character.valueOf('B'), IC2Items.getItem("hvTransformer"), Character.valueOf('C'), StackUtils.copyWithWildCard(new ItemStack(ultimateLappack))});
      }

      if(!disableAdvancedLappackRecipe) {
         Recipes.advRecipes.addRecipe(new ItemStack(advLappack, 1), new Object[]{" A ", " B ", " C ", Character.valueOf('A'), StackUtils.copyWithWildCard(IC2Items.getItem("lapPack")), Character.valueOf('B'), IC2Items.getItem("advancedCircuit"), Character.valueOf('C'), StackUtils.copyWithWildCard(IC2Items.getItem("lapotronCrystal"))});
      }

      if(!disableAdvDDrillRecipe) {
         Recipes.advRecipes.addRecipe(new ItemStack(advDDrill, 1), new Object[]{"   ", "ABA", "CAC", Character.valueOf('A'), IC2Items.getItem("overclockerUpgrade"), Character.valueOf('B'), StackUtils.copyWithWildCard(IC2Items.getItem("diamondDrill")), Character.valueOf('C'), IC2Items.getItem("advancedCircuit")});

         Recipes.advRecipes.addRecipe(new ItemStack(advIrDrill, 1), new Object[]{"ABA", "CDE", "AAA", Character.valueOf('A'), IC2Items.getItem("iridiumPlate"), Character.valueOf('B'), StackUtils.copyWithWildCard(new ItemStack(advDDrill, 1)), Character.valueOf('C'), StackUtils.copyWithWildCard(new ItemStack(vajra, 1)), Character.valueOf('D'), StackUtils.copyWithWildCard(IC2Items.getItem("energyCrystal")), Character.valueOf('E'), StackUtils.copyWithWildCard(ItemRetriever.getItem("AtomicDisassembler"))});
      }

      if(!disableAdvChainsawRecipe) {
         Recipes.advRecipes.addRecipe(new ItemStack(advChainsaw, 1), new Object[]{" F ", "ABA", "CAC", Character.valueOf('F'), Items.diamond, Character.valueOf('A'), IC2Items.getItem("overclockerUpgrade"), Character.valueOf('B'), StackUtils.copyWithWildCard(IC2Items.getItem("chainsaw")), Character.valueOf('C'), IC2Items.getItem("advancedCircuit")});
      }

      if(!disableVajraRecipe) {
         GameRegistry.addRecipe(new ShapedOreRecipe(magnetron, new Object[]{"ABA", "BCB", "ABA", Character.valueOf('A'), "plateIron", Character.valueOf('B'), "plateCopper", Character.valueOf('C'), "itemSuperconductor"}));
         Recipes.advRecipes.addRecipe(vajraCore, new Object[]{" A ", "BCB", "FDF", Character.valueOf('A'), magnetron, Character.valueOf('B'), IC2Items.getItem("iridiumPlate"), Character.valueOf('C'), IC2Items.getItem("teslaCoil"), Character.valueOf('F'), "itemSuperconductor", Character.valueOf('D'), IC2Items.getItem("hvTransformer")});
         Recipes.advRecipes.addRecipe(new ItemStack(vajra, 1), new Object[]{"ABA", "CDC", "FGF", Character.valueOf('A'), "plateIron", Character.valueOf('B'), StackUtils.copyWithWildCard(IC2Items.getItem("energyCrystal")), Character.valueOf('C'), IC2Items.getItem("carbonPlate"), Character.valueOf('D'), vajraCore, Character.valueOf('F'), IC2Items.getItem("advancedAlloy"), Character.valueOf('G'), StackUtils.copyWithWildCard(IC2Items.getItem("lapotronCrystal"))});
      }

      if(!disableGraviToolRecipe) {
         Recipes.advRecipes.addRecipe(new ItemStack(graviTool, 1), new Object[]{"ABA", "CDC", "EFG", Character.valueOf('A'), IC2Items.getItem("carbonPlate"), Character.valueOf('B'), StackUtils.copyWithWildCard(IC2Items.getItem("electricHoe")), Character.valueOf('C'), IC2Items.getItem("advancedAlloy"), Character.valueOf('D'), StackUtils.copyWithWildCard(IC2Items.getItem("energyCrystal")), Character.valueOf('E'), StackUtils.copyWithWildCard(IC2Items.getItem("electricWrench")), Character.valueOf('F'), IC2Items.getItem("advancedCircuit"), Character.valueOf('G'), StackUtils.copyWithWildCard(IC2Items.getItem("electricTreetap"))});
      }

      if(!disableAdvJetpackRecipe) {
         Recipes.advRecipes.addRecipe(itemEngineBoost, new Object[]{"ABA", "CDC", "BFB", Character.valueOf('A'), Items.glowstone_dust, Character.valueOf('B'), IC2Items.getItem("advancedAlloy"), Character.valueOf('C'), IC2Items.getItem("advancedCircuit"), Character.valueOf('D'), IC2Items.getItem("overclockerUpgrade"), Character.valueOf('F'), IC2Items.getItem("reactorVentDiamond")});
         Recipes.advRecipes.addRecipe(itemEngineBoost, new Object[]{"ABA", "CDC", "BFB", Character.valueOf('A'), Items.glowstone_dust, Character.valueOf('B'), IC2Items.getItem("advancedAlloy"), Character.valueOf('C'), IC2Items.getItem("advancedCircuit"), Character.valueOf('D'), IC2Items.getItem("overclockerUpgrade"), Character.valueOf('F'), StackUtils.copyWithWildCard(IC2Items.getItem("reactorVentDiamond"))});
         
         Recipes.advRecipes.addRecipe(new ItemStack(advJetpack, 1), new Object[]{"ABA", "CDC", "EFE", Character.valueOf('A'), IC2Items.getItem("carbonPlate"), Character.valueOf('B'), StackUtils.copyWithWildCard(IC2Items.getItem("electricJetpack")), Character.valueOf('C'), itemEngineBoost, Character.valueOf('D'), StackUtils.copyWithWildCard(new ItemStack(advLappack, 1)), Character.valueOf('E'), IC2Items.getItem("glassFiberCableItem"), Character.valueOf('F'), IC2Items.getItem("advancedCircuit")});
      }

      if(!disableAdvNanoChestPlateRecipe) {
         Recipes.advRecipes.addRecipe(new ItemStack(advNanoChestPlate, 1), new Object[]{"ABA", "ACA", "DFD", Character.valueOf('A'), IC2Items.getItem("carbonPlate"), Character.valueOf('B'), StackUtils.copyWithWildCard(new ItemStack(advJetpack, 1)), Character.valueOf('C'), StackUtils.copyWithWildCard(IC2Items.getItem("nanoBodyarmor")), Character.valueOf('D'), IC2Items.getItem("glassFiberCableItem"), Character.valueOf('F'), IC2Items.getItem("advancedCircuit")});
      }

      if(!disableRelocatorRecipe) {
         Recipes.advRecipes.addRecipe(new ItemStack(relocator, 1), new Object[]{"ABA", "BCB", "ABA", Character.valueOf('A'), IC2Items.getItem("iridiumPlate"), Character.valueOf('B'), Items.ender_pearl, Character.valueOf('C'), IC2Items.getItem("teleporter")});
      }

   }

   public static boolean isSimulating() {
      return !FMLCommonHandler.instance().getEffectiveSide().isClient();
   }

   public static NBTTagCompound getOrCreateNbtData(ItemStack itemstack) {
      NBTTagCompound nbttagcompound = itemstack.getTagCompound();
      if(nbttagcompound == null) {
         nbttagcompound = new NBTTagCompound();
         itemstack.setTagCompound(nbttagcompound);
         nbttagcompound.setInteger("charge", 0);
      }

      return nbttagcompound;
   }
}
