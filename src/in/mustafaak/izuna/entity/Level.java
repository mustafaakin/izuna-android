package in.mustafaak.izuna.entity;

import in.mustafaak.izuna.meta.LevelInfo;
import in.mustafaak.izuna.meta.WaveEnemy;
import in.mustafaak.izuna.meta.WaveInfo;

import org.andengine.entity.scene.Scene;

public class Level extends Scene {
	private LevelInfo info;
	private int currentWave = 0;
	
	public Level(LevelInfo info) {
		this.info = info;
	}

	private void initWave(int waveNo){
		WaveInfo wave = info.getWaves().get(waveNo);
		for(WaveEnemy enemy : wave.getEnemies()){
			String key = enemy.getKey();
			
		}
	}
	
	@Override
	protected void onManagedUpdate(float pSecondsElapsed) {
		super.onManagedUpdate(pSecondsElapsed);
	}
}
