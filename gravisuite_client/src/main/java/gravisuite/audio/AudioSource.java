package gravisuite.audio;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gravisuite.GraviSuite;
import gravisuite.audio.AudioManagerClient;
import gravisuite.audio.AudioPosition;
import gravisuite.audio.PositionSpec;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.net.URL;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import paulscode.sound.SoundSystem;

@SideOnly(Side.CLIENT)
public final class AudioSource implements Comparable<AudioSource> {
   private SoundSystem soundSystem;
   private String sourceName;
   private boolean valid = false;
   private boolean culled = false;
   private Reference<Object> obj;
   private AudioPosition position;
   private PositionSpec positionSpec;
   private float configuredVolume;
   private float realVolume;
   private boolean isPlaying = false;

   public AudioSource(SoundSystem soundSystem1, String sourceName1, Object obj1, PositionSpec positionSpec1, String initialSoundFile, boolean loop, boolean priorized, float volume) {
      this.soundSystem = soundSystem1;
      this.sourceName = sourceName1;
      this.obj = new WeakReference(obj1);
      this.positionSpec = positionSpec1;
      URL url = AudioSource.class.getClassLoader().getResource("assets/gravisuite/sounds/" + initialSoundFile);
      if(url == null) {
         GraviSuite.addLog("Invalid sound file: " + initialSoundFile);
      } else {
         this.position = AudioPosition.getFrom(obj1, positionSpec1);
         soundSystem1.newSource(priorized, sourceName1, url, initialSoundFile, loop, this.position.x, this.position.y, this.position.z, 0, ((AudioManagerClient)GraviSuite.audioManager).fadingDistance * Math.max(volume, 1.0F));
         this.valid = true;
         this.setVolume(volume);
      }
   }

   public int compareTo(AudioSource x) {
      return this.culled?(int)((this.realVolume * 0.9F - x.realVolume) * 128.0F):(int)((this.realVolume - x.realVolume) * 128.0F);
   }

   public void remove() {
      if(this.check()) {
         if(this.sourceName != null) {
            this.stop();
            this.soundSystem.removeSource(this.sourceName);
            this.sourceName = null;
            this.valid = false;
         }
      }
   }

   public void play() {
      if(this.check()) {
         if(!this.isPlaying) {
            this.isPlaying = true;
            if(!this.culled) {
               this.soundSystem.play(this.sourceName);
            }
         }
      }
   }

   public void pause() {
      if(this.check()) {
         if(this.isPlaying && !this.culled) {
            this.isPlaying = false;
            this.soundSystem.pause(this.sourceName);
         }
      }
   }

   public void stop() {
      if(this.check() && this.isPlaying) {
         this.isPlaying = false;
         if(!this.culled) {
            this.soundSystem.stop(this.sourceName);
         }
      }
   }

   public void flush() {
      if(this.check()) {
         if(this.isPlaying && !this.culled) {
            this.soundSystem.flush(this.sourceName);
         }
      }
   }

   public void cull() {
      if(this.check() && !this.culled) {
         this.soundSystem.cull(this.sourceName);
         this.culled = true;
      }
   }

   public void activate() {
      if(this.check() && this.culled) {
         this.soundSystem.activate(this.sourceName);
         this.culled = false;
         if(this.isPlaying) {
            this.isPlaying = false;
            this.play();
         }

      }
   }

   public float getVolume() {
      return !this.check()?0.0F:this.soundSystem.getVolume(this.sourceName);
   }

   public float getRealVolume() {
      return this.realVolume;
   }

   public void setVolume(float volume) {
      if(this.check()) {
         this.configuredVolume = volume;
         this.soundSystem.setVolume(this.sourceName, 0.001F);
      }
   }

   public void setPitch(float pitch) {
      if(this.check()) {
         this.soundSystem.setPitch(this.sourceName, pitch);
      }
   }

   public void updatePosition() {
      if(this.check()) {
         this.position = AudioPosition.getFrom(this.obj.get(), this.positionSpec);
         if(this.position != null) {
            this.soundSystem.setPosition(this.sourceName, this.position.x, this.position.y, this.position.z);
         }
      }
   }

   public void updateVolume(EntityPlayer player) {
      if(this.check() && this.isPlaying) {
         float maxDistance = ((AudioManagerClient)GraviSuite.audioManager).fadingDistance * Math.max(this.configuredVolume, 1.0F);
         float rolloffFactor = 1.0F;
         float referenceDistance = 1.0F;
         float x = (float)player.posX;
         float y = (float)player.posY;
         float z = (float)player.posZ;
         float distance;
         if(this.position != null && this.position.getWorld() == player.worldObj) {
            float deltaX = this.position.x - x;
            float deltaY = this.position.y - y;
            float deltaZ = this.position.z - z;
            distance = (float)Math.sqrt((double)(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ));
         } else {
            distance = 1.0F;
         }

         if(distance > maxDistance) {
            this.realVolume = 0.0F;
            this.cull();
         } else {
            if(distance < referenceDistance) {
               distance = referenceDistance;
            }

            float gain = 1.0F - rolloffFactor * (distance - referenceDistance) / (maxDistance - referenceDistance);
            float newRealVolume = gain * this.configuredVolume * GraviSuite.audioManager.getMasterVolume();
            float dx = (this.position.x - x) / distance;
            float dy = (this.position.y - y) / distance;
            float dz = (this.position.z - z) / distance;
            if((double)newRealVolume > 0.1D) {
               for(int i = 0; (float)i < distance; ++i) {
                  int xi = roundToNegInf(x);
                  int yi = roundToNegInf(y);
                  int zi = roundToNegInf(z);
                  Block block = player.worldObj.getBlock(xi, yi, zi);
                  if(!block.isAir(player.worldObj, xi, yi, zi)) {
                     if(block.isNormalCube(player.worldObj, xi, yi, zi)) {
                        newRealVolume *= 0.6F;
                     } else {
                        newRealVolume *= 0.8F;
                     }
                  }

                  x += dx;
                  y += dy;
                  z += dz;
               }
            }

            if((double)Math.abs(this.realVolume / newRealVolume - 1.0F) > 0.06D) {
               this.soundSystem.setVolume(this.sourceName, GraviSuite.audioManager.getMasterVolume() * Math.min(newRealVolume, 1.0F));
            }

            this.realVolume = newRealVolume;
         }
      } else {
         this.realVolume = 0.0F;
      }
   }

   private boolean check() {
      if(this.valid && GraviSuite.audioManager.valid()) {
         return true;
      } else {
         this.valid = false;
         return false;
      }
   }

   public static int roundToNegInf(float x) {
      int ret = (int)x;
      if((float)ret > x) {
         --ret;
      }

      return ret;
   }

   public static int roundToNegInf(double x) {
      int ret = (int)x;
      if((double)ret > x) {
         --ret;
      }

      return ret;
   }
}
