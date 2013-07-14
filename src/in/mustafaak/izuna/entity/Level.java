package in.mustafaak.izuna.entity;

import in.mustafaak.izuna.Loader;
import in.mustafaak.izuna.MainActivity;
import in.mustafaak.izuna.TextureProvider;
import in.mustafaak.izuna.meta.EnemyInfo;
import in.mustafaak.izuna.meta.LevelInfo;
import in.mustafaak.izuna.meta.WaveEnemy;
import in.mustafaak.izuna.meta.WaveInfo;
import in.mustafaak.izuna.meta.WeaponInfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.andengine.entity.modifier.PathModifier;
import org.andengine.entity.modifier.PathModifier.Path;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.AutoParallaxBackground;
import org.andengine.entity.scene.background.ParallaxBackground.ParallaxEntity;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;

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
	private ArrayList<Weapon> weapons = new ArrayList<Weapon>();

	public Level(LevelInfo levelInfo, MainActivity owner, Loader loader, TextureProvider texProvider) {
		ITextureRegion texReg = texProvider.getBackground(levelInfo.getNo());

		Sprite bg = new Sprite(0, 0, texReg, texProvider.getVertexBufferObjectManager());
		bg.setHeight(720);

		final AutoParallaxBackground autoParallaxBackground = new AutoParallaxBackground(0, 0, 0, 4) {
			@Override
			public void onUpdate(float pSecondsElapsed) {
				Log.d("this.mParallaxValue", this.mParallaxValue + "");
				if (this.mParallaxValue >= 250) { // No idea how this is calculated.
					this.setParallaxChangePerSecond(0);
				}
				super.onUpdate(pSecondsElapsed);
			}
		};
		autoParallaxBackground.attachParallaxEntity(new ParallaxEntity(-1.0f, bg));
		setBackground(autoParallaxBackground);

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

		for (Iterator<Weapon> itr = weapons.iterator(); itr.hasNext();) {
			Weapon w = itr.next();
			if (w.getX() < -100) {
				itr.remove();
				this.detachChild(w);
			}
		}

		// Check for the collisions
		long time = System.currentTimeMillis();
		for (Iterator<Enemy> itr = enemies.iterator(); itr.hasNext();) {
			Enemy e = itr.next();
			if (e.collidesWith(player)) {
				itr.remove();
				this.detachChild(e);
			} else {
				EnemyInfo eInfo = e.getEnemyInfo();
				WeaponInfo wInfo = loader.getWeaponInfo(eInfo.getWeapon());
				if (time - e.lastFire > wInfo.getRateOfFire()) {
					e.lastFire = time;
					Weapon w = new Weapon(e, wInfo, texProvider.getWeapon(wInfo.getKey()),
							texProvider.getVertexBufferObjectManager());
					this.attachChild(w);
					weapons.add(w);
				}
			}
		}

		super.onManagedUpdate(pSecondsElapsed);
	}
}
