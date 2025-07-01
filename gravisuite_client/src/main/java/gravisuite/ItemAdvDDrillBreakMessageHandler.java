package gravisuite;

// import com.gamerforea.eventhelper.util.EventUtils;
// import com.gamerforea.gravisuite.EventConfig;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gravisuite.keyboard.Keyboard;
import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import cpw.mods.fml.common.eventhandler.Event;

import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.ByteBufUtils;


public class ItemAdvDDrillBreakMessageHandler implements IMessageHandler<ItemAdvDDrillBreakMessageHandler.ItemAdvDDrillBreakMessage, IMessage> {

    @Override
    public IMessage onMessage(ItemAdvDDrillBreakMessage message, MessageContext ctx)
    {
            // System.out.println("INCOMING MESSAGE; COUNT = " + message.count);
            List<ItemAdvDDrillBreakMessage.Triple> blocks = message.getBlocks();
            EntityPlayer player = ctx.getServerHandler().playerEntity;
            ItemStack currentItemStack = player.getHeldItem();
            Item currentItem = currentItemStack.getItem();
            if(currentItem instanceof ItemTool) {
                // System.out.println("ItemTool");
                if(currentItem instanceof ItemAdvDDrill) {
                    // System.out.println("ItemAdvDDrill");
                    ItemAdvDDrill drill = (ItemAdvDDrill) currentItem;
                    drill.onBlockStartBreakAdditionalServerPart(currentItemStack, blocks, player);
                }
                if(currentItem instanceof ItemAdvIrDrill) {
                    // System.out.println("ItemAdvDDrill");
                    ItemAdvIrDrill drill = (ItemAdvIrDrill) currentItem;
                    drill.onBlockStartBreakAdditionalServerPart(currentItemStack, blocks, player);
                }
            }
            return null;
    }
	  

    public static class ItemAdvDDrillBreakMessage implements IMessage {
        public class Triple {

            private final int x;
            private final int y;
            private final int z;

            public Triple(int x, int y, int z) {
                this.x = x;
                this.y = y;
                this.z = z;
            }

            public int getX() { return x; }
            public int getY() { return y; }
            public int getZ() { return z; }
        }
	    int count;
	    List<Triple> blockList;

	    public ItemAdvDDrillBreakMessage() {
            this.blockList = new ArrayList<Triple>();
            this.count = 0;
	    }

	    public ItemAdvDDrillBreakMessage(List<Triple> l)
	    {
	      this.blockList = l;
	      this.count = l.size();
	    }
	    
	    public void addBlock(int x, int y, int z) {
            this.blockList.add(new Triple(x, y, z));
            this.count++;
	    }
	    
	    public List<Triple> getBlocks() {
            return this.blockList;
	    }
	    
	    @Override
	    public void fromBytes(ByteBuf buf)
	    {
            count = buf.readUnsignedByte();
            if(count > 100) count = 100;
            this.blockList.clear();
            for(int i = 0; i < this.count; i++) {
                this.blockList.add(new Triple(buf.readInt(), buf.readInt(), buf.readInt()));
            }
	    }
	    
	    @Override
	    public void toBytes(ByteBuf buf)
	    {
            buf.writeByte(this.count);
            for(int i = 0; i < this.count; i++) {
                buf.writeInt(this.blockList.get(i).x);
                buf.writeInt(this.blockList.get(i).y);
                buf.writeInt(this.blockList.get(i).z);
	    	}
	    }
	}
};
