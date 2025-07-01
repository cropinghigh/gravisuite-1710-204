package gravisuite.audio;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gravisuite.GraviSuite;
import gravisuite.audio.AudioManager;
import gravisuite.audio.AudioPosition;
import gravisuite.audio.AudioSource;
import gravisuite.audio.PositionSpec;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Vector;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.common.MinecraftForge;
import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;

@SideOnly(Side.CLIENT)
public class AudioManagerClient extends AudioManager {
   public float fadingDistance = 16.0F;
   private boolean enabled = true;
   private int maxSourceCount = 32;
   private final int streamingSourceCount = 4;
   private SoundManager soundManager;
   private Field soundManagerLoaded;
   private volatile Thread initThread;
   private SoundSystem soundSystem = null;
   private float masterVolume = 0.5F;
   public float defaultVolume = 1.2F;
   private int nextId = 0;
   private final Map<AudioManagerClient.WeakObject, List<AudioSource>> objectToAudioSourceMap = new HashMap();

   public void initialize() {
      this.enabled = !GraviSuite.disableSounds;
      this.maxSourceCount = 32;
      if(this.maxSourceCount <= 6) {
         GraviSuite.addLog("Audio source limit too low to enable sounds.");
         this.enabled = false;
      }

      if(!this.enabled) {
         GraviSuite.addLog("Sounds disabled.");
      } else if(this.maxSourceCount < 6) {
         this.enabled = false;
      } else {
         GraviSuite.addLog("Using " + this.maxSourceCount + " audio sources.");
         SoundSystemConfig.setNumberStreamingChannels(4);
         SoundSystemConfig.setNumberNormalChannels(this.maxSourceCount - 4);
         int found = 0;

         for(Field field : SoundManager.class.getDeclaredFields()) {
            if(field.getType().isAssignableFrom(Boolean.TYPE)) {
               this.soundManagerLoaded = field;
               ++found;
            }
         }

         if(found != 1) {
            GraviSuite.addLog("Can\'t find SoundManager.loaded, audio disabled.");
            this.enabled = false;
         } else {
            this.soundManagerLoaded.setAccessible(true);
            MinecraftForge.EVENT_BUS.register(this);
         }
      }
   }

   @SubscribeEvent
   public void onSoundSetup(SoundLoadEvent event) {
      if(this.enabled) {
         this.objectToAudioSourceMap.clear();
         GraviSuite.addLog("Audio starting.");
         this.soundSystem = null;
         this.soundManager = getSoundManager();
         this.initThread = new Thread(new Runnable() {
            public void run() {
               while(true) {
                  try {
                     if(!Thread.currentThread().isInterrupted()) {
                        System.out.println("Thread");

                        boolean loaded;
                        try {
                           loaded = AudioManagerClient.this.soundManagerLoaded.getBoolean(AudioManagerClient.this.soundManager);
                        } catch (Exception var3) {
                           throw new RuntimeException(var3);
                        }

                        if(!loaded) {
                           Thread.sleep(100L);
                           continue;
                        }

                        AudioManagerClient var10001 = AudioManagerClient.this;
                        AudioManagerClient.this.soundSystem = AudioManagerClient.getSoundSystem(AudioManagerClient.this.soundManager);
                        if(AudioManagerClient.this.soundSystem == null) {
                           GraviSuite.addLog("Audio unavailable.");
                        }

                        GraviSuite.addLog("Audio ready.");
                     }
                  } catch (InterruptedException var4) {
                     ;
                  }

                  AudioManagerClient.this.soundSystem = null;
                  return;
               }
            }
         }, "GraviSuite audio init thread");
         this.initThread.setDaemon(true);
         this.initThread.start();
      }
   }

   private static SoundManager getSoundManager() {
      SoundHandler handler = Minecraft.getMinecraft().getSoundHandler();

      for(Field field : SoundHandler.class.getDeclaredFields()) {
         if(SoundManager.class.isAssignableFrom(field.getType())) {
            field.setAccessible(true);

            try {
               System.out.println("Found soundhandler");
               return (SoundManager)field.get(handler);
            } catch (Exception var6) {
               throw new RuntimeException(var6);
            }
         }
      }

      return null;
   }

   private static SoundSystem getSoundSystem(SoundManager soundManager) {
      for(Field field : SoundManager.class.getDeclaredFields()) {
         if(SoundSystem.class.isAssignableFrom(field.getType())) {
            field.setAccessible(true);

            try {
               System.out.println("Found soundsystem");
               return (SoundSystem)field.get(soundManager);
            } catch (Exception var6) {
               throw new RuntimeException(var6);
            }
         }
      }

      return null;
   }

   public void onTick() {
      if(this.enabled && this.valid()) {
         float configSoundVolume = Minecraft.getMinecraft().gameSettings.getSoundLevel(SoundCategory.MASTER);
         if(configSoundVolume != this.masterVolume) {
            this.masterVolume = configSoundVolume;
         }

         EntityPlayer player = Minecraft.getMinecraft().thePlayer;
         List audioSourceObjectsToRemove = new Vector();
         if(player == null) {
            audioSourceObjectsToRemove.addAll(this.objectToAudioSourceMap.keySet());
         } else {
            Queue validAudioSources = new PriorityQueue();

            for(Entry entry : this.objectToAudioSourceMap.entrySet()) {
               if(((AudioManagerClient.WeakObject)entry.getKey()).isEnqueued()) {
                  audioSourceObjectsToRemove.add(entry.getKey());
               } else {
                  for(Object audioSourceO : (List)entry.getValue()) {
                     AudioSource audioSource = (AudioSource)audioSourceO;
                     audioSource.updateVolume(player);
                     if(audioSource.getRealVolume() > 0.0F) {
                        validAudioSources.add(audioSource);
                     }
                  }
               }
            }

            for(int i = 0; !((Queue)validAudioSources).isEmpty(); ++i) {
               if(i < this.maxSourceCount) {
                  ((AudioSource)validAudioSources.poll()).activate();
               } else {
                  ((AudioSource)validAudioSources.poll()).cull();
               }
            }
         }

         for(Object asToRemoveO : audioSourceObjectsToRemove) {
            AudioManagerClient.WeakObject asToRemove = (AudioManagerClient.WeakObject)asToRemoveO;
            this.removeSources(asToRemove);
         }

      }
   }

   public AudioSource createSource(Object obj, String initialSoundFile) {
      return this.createSource(obj, PositionSpec.Center, initialSoundFile, false, false, this.defaultVolume);
   }

   public AudioSource createSource(Object obj, PositionSpec positionSpec, String initialSoundFile, boolean loop, boolean priorized, float volume) {
      if(!this.enabled) {
         return null;
      } else if(!this.valid()) {
         return null;
      } else {
         String sourceName = getSourceName(this.nextId);
         ++this.nextId;
         AudioSource audioSource = new AudioSource(this.soundSystem, sourceName, obj, positionSpec, initialSoundFile, loop, priorized, volume);
         AudioManagerClient.WeakObject key = new AudioManagerClient.WeakObject(obj);
         if(!this.objectToAudioSourceMap.containsKey(key)) {
            this.objectToAudioSourceMap.put(key, new LinkedList());
         }

         ((List)this.objectToAudioSourceMap.get(key)).add(audioSource);
         return audioSource;
      }
   }

   public void removeSources(Object obj) {
      if(this.valid()) {
         AudioManagerClient.WeakObject key;
         if(obj instanceof AudioManagerClient.WeakObject) {
            key = (AudioManagerClient.WeakObject)obj;
         } else {
            key = new AudioManagerClient.WeakObject(obj);
         }

         if(this.objectToAudioSourceMap.containsKey(key)) {
            for(Object audioSourceO : (List)this.objectToAudioSourceMap.get(key)) {
               AudioSource audioSource = (AudioSource)audioSourceO;
               audioSource.remove();
            }

            this.objectToAudioSourceMap.remove(key);
         }
      }
   }

   public void playOnce(Object obj, String soundFile) {
      this.playOnce(obj, PositionSpec.Center, soundFile, false, this.defaultVolume);
   }

   public void playOnce(Object obj, PositionSpec positionSpec, String soundFile, boolean priorized, float volume) {
      if(this.enabled) {
         if(this.valid()) {
            System.out.println("Play once sound");
            AudioPosition position = AudioPosition.getFrom(obj, positionSpec);
            if(position != null) {
               URL url = AudioSource.class.getClassLoader().getResource("assets/gravisuite/sounds/" + soundFile);
               if(url == null) {
                  GraviSuite.addLog("Invalid sound file: " + soundFile);
               } else {
                  String sourceName = this.soundSystem.quickPlay(priorized, url, soundFile, false, position.x, position.y, position.z, 2, this.fadingDistance * Math.max(volume, 1.0F));
                  this.soundSystem.setVolume(sourceName, this.masterVolume * Math.min(volume, 1.0F));
               }
            }
         }
      }
   }

   public static void playSound(Object obj, PositionSpec positionSpec, String soundName) {
      ResourceLocation soundFile = new ResourceLocation("gravisuite", soundName);
      AudioPosition position = AudioPosition.getFrom(obj, positionSpec);
      FMLClientHandler.instance().getClient().getSoundHandler().playSound(new PositionedSoundRecord(soundFile, 1.0F, 1.0F, position.x, position.y, position.z));
   }

   public float getMasterVolume() {
      return this.masterVolume;
   }

   protected boolean valid() {
      try {
         return this.soundSystem != null && this.soundManager != null && this.soundManagerLoaded.getBoolean(this.soundManager);
      } catch (Exception var2) {
         throw new RuntimeException();
      }
   }

   private static String getSourceName(int id) {
      return "asm_snd" + id;
   }

   public static class WeakObject extends WeakReference<Object> {
      public WeakObject(Object obj) {
         super(obj);
      }

      public boolean equals(Object object) {
         return object instanceof AudioManagerClient.WeakObject?((AudioManagerClient.WeakObject)object).get() == this.get():this.get() == object;
      }

      public int hashCode() {
         Object object = this.get();
         return object == null?0:object.hashCode();
      }
   }
}
