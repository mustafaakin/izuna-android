package in.mustafaak.izuna;

import java.io.IOException;
import java.util.Random;

import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.audio.sound.SoundManager;

import android.content.Context;
import android.content.res.AssetManager;


public class SoundPlayer {
	private AssetManager assets;
	private Random rnd = new Random();
	
	private Sound[] explosion = new Sound[5];
	private Sound[] laser = new Sound[3];
	
	
	public SoundPlayer(SoundManager pSoundManager, Context context){
		SoundFactory.setAssetBasePath("sfx/");
		try {
			for(int i = 0; i < 5; i++){
				explosion[i] = SoundFactory.createSoundFromAsset(pSoundManager, context, "explosion_" + i + ".mp3");
			}			
			for(int i = 0; i < 3; i++){
				laser[i] = SoundFactory.createSoundFromAsset(pSoundManager, context, "laser_" + i + ".mp3");
			}			

		} catch (final IOException e) {
			e.printStackTrace();
		}
	}
	
	public void playLaser(){
		laser[rnd.nextInt(laser.length)].play();
	}
	
	public void playExplosion(){
		explosion[rnd.nextInt(explosion.length)].play();
	}
}
