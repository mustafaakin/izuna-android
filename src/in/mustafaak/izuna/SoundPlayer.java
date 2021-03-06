package in.mustafaak.izuna;

import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.audio.sound.SoundManager;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;


public class SoundPlayer {
	private AssetManager assets;
	private Random rnd = new Random();
		
	private HashMap<String,Sound> sounds = new HashMap<String, Sound>();
	
	public SoundPlayer(SoundManager pSoundManager, Context context){
		SoundFactory.setAssetBasePath("sfx/");
		try {
			String soundNames[] = context.getAssets().list("sfx");
			for(String soundName : soundNames){
				Sound sound = SoundFactory.createSoundFromAsset(pSoundManager, context, soundName);
				soundName = soundName.substring(0, soundName.length() - 4);
				sounds.put(soundName, sound);
			}
		} catch (IOException e) {
			Log.d("Soundloaderror", e.getMessage());
		}
	}
	
	public void playBonus(){
		sounds.get("bonus").play();
	}
	
	public void playClick(){
		sounds.get("click").play();
	}
	
	public void playEndingHappy(){
		sounds.get("ending_happy").play();
	}
	
	public void playEndingSad(){
		sounds.get("ending_sad").play();
	}

	public void playHitExplosion(){
		Sound hit = sounds.get("explosion_hit");
		hit.setVolume(0.5f);
		hit.play();
	}
	
	public void playBigExplosion(){
		sounds.get("explosion_big" + rnd.nextInt(2)).play();
	}
	
	public void playFlyBy(){
		sounds.get("flyby_" + rnd.nextInt(3)).play();		
	}
	
	public void playLaser(String fireSound) {
		sounds.get(fireSound).play();
	}
	
	public void playSound(String name){
		Sound s = sounds.get(name);
		if ( s != null){
			s.play();
		} else {
			Log.d("Sound not found", name);
		}	
	}
}
