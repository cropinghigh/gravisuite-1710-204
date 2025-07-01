package gravisuite.client;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gravisuite.EntityPlasmaBall;
import gravisuite.Helpers;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderPlasmaBall extends Render {
   private float field_77002_a;
   private static final ResourceLocation plazmaTextloc = new ResourceLocation("gravisuite", "textures/models/plazma.png");
   private static final ResourceLocation particlesTextloc = new ResourceLocation("gravisuite", "textures/models/particles.png");
   private static Map textureSizeCache = new HashMap();
   public int ticker;

   public RenderPlasmaBall(float p_i1254_1_) {
      this.field_77002_a = p_i1254_1_;
   }

   public static int getTextureSize(String s, int dv) {
      if(textureSizeCache.get(Arrays.asList(new Serializable[]{s, Integer.valueOf(dv)})) != null) {
         return ((Integer)textureSizeCache.get(Arrays.asList(new Serializable[]{s, Integer.valueOf(dv)}))).intValue();
      } else {
         try {
            InputStream inputstream = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("advancedsolarpanel", s)).getInputStream();
            if(inputstream == null) {
               throw new Exception("Image not found: " + s);
            } else {
               BufferedImage bi = ImageIO.read(inputstream);
               int size = bi.getWidth() / dv;
               textureSizeCache.put(Arrays.asList(new Serializable[]{s, Integer.valueOf(dv)}), Integer.valueOf(size));
               return size;
            }
         } catch (Exception var5) {
            var5.printStackTrace();
            return 16;
         }
      }
   }

   public void renderCore(EntityPlasmaBall entity, double x, double y, double z, float fq, float pticks) {
      int size1 = 0;
      int size2 = 0;
      size1 = getTextureSize("textures/models/plazma.png", 64);
      size2 = getTextureSize("textures/models/particles.png", 32);
      float f1 = ActiveRenderInfo.rotationX;
      float f2 = ActiveRenderInfo.rotationXZ;
      float f3 = ActiveRenderInfo.rotationZ;
      float f4 = ActiveRenderInfo.rotationYZ;
      float f5 = ActiveRenderInfo.rotationXY;
      float scaleCore = 1.0F;
      double posY = (double)((float)y);
      double posZ = (double)((float)z);
      Tessellator tessellator = Tessellator.instance;
      Color color = Helpers.convertRGBtoColor(226, 88, 255);
      if(entity.getActionType() == 0) {
         color = Helpers.convertRGBtoColor(254, 255, 131);
      }

      GL11.glPushMatrix();
      GL11.glDepthMask(false);
      GL11.glEnable(3042);
      GL11.glBlendFunc(770, 1);
      FMLClientHandler.instance().getClient().renderEngine.bindTexture(plazmaTextloc);
      int i = entity.ticksExisted % 16;
      float size4 = (float)(size1 * 4);
      float float_sizeMinus0_01 = (float)size1 - 0.01F;
      float float_texNudge = 1.0F / ((float)(size1 * size1) * 2.0F);
      float float_reciprocal = 1.0F / (float)size1;
      float x0 = ((float)(i % 4 * size1) + 0.0F) / size4;
      float x1 = ((float)(i % 4 * size1) + float_sizeMinus0_01) / size4;
      float x2 = ((float)(i / 4 * size1) + 0.0F) / size4;
      float x3 = ((float)(i / 4 * size1) + float_sizeMinus0_01) / size4;
      tessellator.startDrawingQuads();
      tessellator.setBrightness(240);
      tessellator.setColorRGBA_F((float)color.getRed() / 255.0F, (float)color.getGreen() / 255.0F, (float)color.getBlue() / 255.0F, 1.0F);
      tessellator.addVertexWithUV(x - (double)(f1 * scaleCore) - (double)(f4 * scaleCore), posY - (double)(f2 * scaleCore), posZ - (double)(f3 * scaleCore) - (double)(f5 * scaleCore), (double)x1, (double)x3);
      tessellator.addVertexWithUV(x - (double)(f1 * scaleCore) + (double)(f4 * scaleCore), posY + (double)(f2 * scaleCore), posZ - (double)(f3 * scaleCore) + (double)(f5 * scaleCore), (double)x1, (double)x2);
      tessellator.addVertexWithUV(x + (double)(f1 * scaleCore) + (double)(f4 * scaleCore), posY + (double)(f2 * scaleCore), posZ + (double)(f3 * scaleCore) + (double)(f5 * scaleCore), (double)x0, (double)x2);
      tessellator.addVertexWithUV(x + (double)(f1 * scaleCore) - (double)(f4 * scaleCore), posY - (double)(f2 * scaleCore), posZ + (double)(f3 * scaleCore) - (double)(f5 * scaleCore), (double)x0, (double)x3);
      tessellator.draw();
      GL11.glDisable(3042);
      GL11.glDepthMask(true);
      GL11.glPopMatrix();
      GL11.glPushMatrix();
      GL11.glDepthMask(true);
      GL11.glEnable(3042);
      GL11.glBlendFunc(770, 1);
      FMLClientHandler.instance().getClient().renderEngine.bindTexture(particlesTextloc);
      int qq = entity.ticksExisted % 16;
      i = 24 + qq;
      float size8 = (float)(size2 * 8);
      float_sizeMinus0_01 = (float)size2 - 0.01F;
      float_texNudge = 1.0F / ((float)(size2 * size2) * 2.0F);
      float_reciprocal = 1.0F / (float)size2;
      x0 = ((float)(i % 8 * size2) + 0.0F) / size8;
      x1 = ((float)(i % 8 * size2) + float_sizeMinus0_01) / size8;
      x2 = ((float)(i / 8 * size2) + 0.0F) / size8;
      x3 = ((float)(i / 8 * size2) + float_sizeMinus0_01) / size8;
      float var11 = MathHelper.sin(((float)entity.ticksExisted + pticks) / 10.0F) * 0.1F;
      scaleCore = 0.4F + var11;
      tessellator.startDrawingQuads();
      tessellator.setBrightness(240);
      tessellator.setColorRGBA_F(1.0F, 1.0F, 1.0F, 1.0F);
      tessellator.addVertexWithUV(x - (double)(f1 * scaleCore) - (double)(f4 * scaleCore), posY - (double)(f2 * scaleCore), posZ - (double)(f3 * scaleCore) - (double)(f5 * scaleCore), (double)x1, (double)x3);
      tessellator.addVertexWithUV(x - (double)(f1 * scaleCore) + (double)(f4 * scaleCore), posY + (double)(f2 * scaleCore), posZ - (double)(f3 * scaleCore) + (double)(f5 * scaleCore), (double)x1, (double)x2);
      tessellator.addVertexWithUV(x + (double)(f1 * scaleCore) + (double)(f4 * scaleCore), posY + (double)(f2 * scaleCore), posZ + (double)(f3 * scaleCore) + (double)(f5 * scaleCore), (double)x0, (double)x2);
      tessellator.addVertexWithUV(x + (double)(f1 * scaleCore) - (double)(f4 * scaleCore), posY - (double)(f2 * scaleCore), posZ + (double)(f3 * scaleCore) - (double)(f5 * scaleCore), (double)x0, (double)x3);
      tessellator.draw();
      GL11.glDisable(3042);
      GL11.glDepthMask(true);
      GL11.glPopMatrix();
   }

   public void doRender(Entity entity, double x, double y, double z, float fq, float ticks) {
      this.renderCore((EntityPlasmaBall)entity, x, y, z, fq, ticks);
   }

   protected ResourceLocation getEntityTexture(Entity p_110775_1_) {
      return plazmaTextloc;
   }
}
