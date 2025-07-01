package gravisuite;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IArmorTickListener {
   boolean onTick(EntityPlayer var1, ItemStack var2);
}
