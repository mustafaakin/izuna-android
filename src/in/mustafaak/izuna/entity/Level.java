package in.mustafaak.izuna.entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import in.mustafaak.izuna.Loader;
import in.mustafaak.izuna.MainActivity;
import in.mustafaak.izuna.TextureProvider;
import in.mustafaak.izuna.meta.EnemyInfo;
import in.mustafaak.izuna.meta.LevelInfo;
import in.mustafaak.izuna.meta.WaveEnemy;
import in.mustafaak.izuna.meta.WaveInfo;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.TextureRegion;

import android.util.Log;

public class Level extends Scene {
	private LevelInfo levelInfo;
	private Loader loader;
	private TextureProvider texProvider;
	private MainActivity owner;
	
	private WaveInfo[] waves;

	// Current state holders
	private Sprite player;

	private int currentWave = 0;
	private ArrayList<Enemy> enemies = new ArrayList<Enemy>();

	public Level(LevelInfo levelInfo, MainActivity owner, Loader loader, TextureProvider texProvider) {
		this.levelInfo = levelInfo;
		this.loader = loader;
		this.texProvider = texProvider;
		this.owner = owner;

		player = new Player(texProvider.getShip("player"), texProvider.getVertexBufferObjectManager());
		attachChild(player);
		registerTouchArea(player);
		setTouchAreaBindingOnActionDownEnabled(true);
		setTouchAreaBindingOnActionMoveEnabled(true);

		List<WaveInfo> wavesInfo = levelInfo.getWaves();
		waves = wavesInfo.toArray(new WaveInfo[wavesInfo.size()]);
	}

	private void addEnemies() {
		WaveInfo waveCurr = waves[currentWave];
		for (WaveEnemy waveEnemy : waveCurr.getEnemies()) {
			String key = waveEnemy.getKey();
			Log.d("Enemy key", key);
			EnemyInfo meta = loader.getEnemyInfo(key);

			TextureRegion texReg = texProvider.getShip(key);
			Enemy e = new Enemy(waveEnemy, meta, texReg, texProvider.getVertexBufferObjectManager());
			enemies.add(e);
			this.attachChild(e);
		}
	}

	@Override
	protected void onManagedUpdate(float pSecondsElapsed) {
		if (enemies.isEmpty()) {
			if (currentWave >= waves.length) {
				// No more enemies, signal the load of the next level
				owner.levelFinished();
			} else {
				addEnemies();
				currentWave++;
			}
		}

		// Check for the collisions
		for (Iterator<Enemy> itr = enemies.iterator(); itr.hasNext();) {
			Enemy e = itr.next();
			if (e.collidesWith(player)) {
				itr.remove();
				this.detachChild(e);
			}
		}

		super.onManagedUpdate(pSecondsElapsed);
	}
}
