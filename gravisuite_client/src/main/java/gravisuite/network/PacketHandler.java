package gravisuite.network;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLEventChannel;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientCustomPacketEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ServerCustomPacketEvent;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import gravisuite.network.PacketKeyPress;
import gravisuite.network.PacketKeyboardUpdate;
import gravisuite.network.PacketManagePoints;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import java.io.DataInputStream;
import java.io.InputStream;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;

public class PacketHandler {
   public static String channelName = "GraviSuite";
   private static FMLEventChannel channel;

   public PacketHandler() {
      channel = NetworkRegistry.INSTANCE.newEventDrivenChannel(channelName);
      channel.register(this);
   }

   @SubscribeEvent
   public void onPacket(ServerCustomPacketEvent event) {
      this.onPacketData(new ByteBufInputStream(event.packet.payload()), ((NetHandlerPlayServer)event.handler).playerEntity);
   }

   @SubscribeEvent
   public void onPacket(ClientCustomPacketEvent event) {
      this.onPacketData(new ByteBufInputStream(event.packet.payload()), (EntityPlayer)null);
   }

   public void onPacketData(InputStream is, EntityPlayer player) {
      DataInputStream data = new DataInputStream(is);

      try {
         int packetId = data.readByte();
         switch(packetId) {
         case PacketsName.KEYS_UPDATE:
            PacketKeyboardUpdate packetKU = new PacketKeyboardUpdate();
            packetKU.readData(data);
            packetKU.execute(player);
            break;
         case PacketsName.KEY_PRESS:
            PacketKeyPress packetKP = new PacketKeyPress();
            packetKP.readData(data);
            packetKP.execute(player);
            break;
         case PacketsName.MANAGE_POINTS:
            PacketManagePoints packetMngPoint = new PacketManagePoints();
            packetMngPoint.readData(data);
            packetMngPoint.managePoint(player, packetMngPoint.pointName, packetMngPoint.action);
            break;
         case PacketsName.BREAK_BLOCKS:
            PacketBreakBlocks packetBrkBlocks = new PacketBreakBlocks();
            packetBrkBlocks.readData(data);
            packetBrkBlocks.execute(player);
            break;
         case PacketsName.TUNE_IR_DRILL:
            PacketTuneIrDrill packetTuneIrDrill = new PacketTuneIrDrill();
            packetTuneIrDrill.readData(data);
            packetTuneIrDrill.execute(player);
         }
      } catch (Exception var8) {
         var8.printStackTrace();
      }

   }

   private static FMLProxyPacket makePacket(byte[] data) {
      return new FMLProxyPacket(Unpooled.wrappedBuffer(data), channelName);
   }

   public static void sendPacket(byte[] data) {
      if(FMLCommonHandler.instance().getEffectiveSide().isClient()) {
         channel.sendToServer(makePacket(data));
      } else {
         channel.sendToAll(makePacket(data));
      }

   }

   public static void sendPacket(byte[] data, EntityPlayerMP player) {
      channel.sendTo(makePacket(data), player);
   }
}
