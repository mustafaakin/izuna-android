package in.mustafaak.izuna.entity;

import in.mustafaak.izuna.Constants;
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

import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.AutoParallaxBackground;
import org.andengine.entity.scene.background.ParallaxBackground.ParallaxEntity;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.AnimatedSprite.IAnimationListener;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegion;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.util.Log;

public class Level extends Scene {
	private LevelInfo levelInfo;
	private Loader loader;
	private TextureProvider texProvider;
	private MainActivity owner;

	private WaveInfo[] waves;

	// Current state holders
	private Player player;

	private int currentWave = 0;
	private ArrayList<Enemy> enemies = new ArrayList<Enemy>();
	private ArrayList<Weapon> weaponsEnemy = new ArrayList<Weapon>();
	private ArrayList<Weapon> weaponsPlayer = new ArrayList<Weapon>();

	public Level(LevelInfo levelInfo, MainActivity owner, Loader loader, TextureProvider texProvider) {
		ITextureRegion texReg = texProvider.getBackground(levelInfo.getNo());
		this.setRotation(Constants.SCENE_ROTATION);

		Sprite bg = new Sprite(0, 0, texReg, texProvider.getVertexBufferObjectManager());
		bg.setHeight(720);

		final AutoParallaxBackground autoParallaxBackground = new AutoParallaxBackground(0, 0, 0, 4) {
			@Override
			public void onUpdate(float pSecondsElapsed) {
				if (this.mParallaxValue >= 250) { // No idea how this is
													// calculated.
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

		player = new Player();
		attachChild(player);
		registerTouchArea(player);
		setTouchAreaBindingOnActionDownEnabled(true);
		setTouchAreaBindingOnActionMoveEnabled(true);

		List<WaveInfo> wavesInfo = levelInfo.getWaves();
		waves = wavesInfo.toArray(new WaveInfo[wavesInfo.size()]);
	}

	public static long getUsedMemorySize() {
		long freeSize = 0L;
		long totalSize = 0L;
		long usedSize = -1L;
		try {
			Runtime info = Runtime.getRuntime();
			freeSize = info.freeMemory();
			totalSize = info.totalMemory();
			usedSize = totalSize - freeSize;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return usedSize;

	}

	private void addEnemies() {
		WaveInfo waveCurr = waves[currentWave];
		for (WaveEnemy waveEnemy : waveCurr.getEnemies()) {
			Enemy e = new Enemy(waveEnemy);
			enemies.add(e);
			this.attachChild(e);
		}
	}

	@Override
	protected void onManagedUpdate(float pSecondsElapsed) {
		long time = System.currentTimeMillis();
		// Add user fires to screen
		if (player.canFire && (time - player.lastFire) > 200) {
			player.lastFire = time;
			float x = player.getX(), y = player.getY();
			WeaponInfo wInfo = loader.getWeaponInfo("c3");
			Weapon w = new Weapon(x, y + player.getHeight() / 2, Constants.CAMERA_WIDTH + 200, y + player.getHeight()
					/ 2, wInfo, texProvider.getWeapon(wInfo.getKey()), texProvider.getVertexBufferObjectManager());

			weaponsPlayer.add(w);
			attachChild(w);
		}

		if (enemies.isEmpty()) {
			if (currentWave >= waves.length) {
				// No more enemies, signal the load of the next level
				owner.levelFinished();
			} else {
				addEnemies();
				currentWave++;
			}
		}

		for (Iterator<Weapon> itr = weaponsEnemy.iterator(); itr.hasNext();) {
			Weapon w = itr.next();
			if (w.getX() < -100) {
				itr.remove();
				this.detachChild(w);
			}
		}

		for (Iterator<Weapon> itrWeapon = weaponsPlayer.iterator(); itrWeapon.hasNext();) {
			Weapon w = itrWeapon.next();

			if (w.getX() > Constants.CAMERA_WIDTH + 100) {
				itrWeapon.remove();
				this.detachChild(w);
			} else {
				boolean isWeaponRemoved = false;
				for (Iterator<Enemy> itrEnemy = enemies.iterator(); itrEnemy.hasNext();) {
					Enemy e = itrEnemy.next();
					if (e.collidesWith(w)) {
						if (!isWeaponRemoved) {
							isWeaponRemoved = true;
							itrWeapon.remove();
							this.detachChild(w);
						}
						if (e.applyDamage(w.weaponInfo.getCausedDamage())) {
							itrEnemy.remove();
							// Spawn big explosion animation
							AnimatedSprite as = new AnimatedSprite(e.getX(), e.getY(), texProvider.getExplosionBig(),
									texProvider.getVertexBufferObjectManager());
							final Scene s = this;
							as.animate(1000 / 24, false, new IAnimationListener() {
								@Override
								public void onAnimationStarted(AnimatedSprite pAnimatedSprite, int pInitialLoopCount) {
								}

								@Override
								public void onAnimationLoopFinished(AnimatedSprite pAnimatedSprite,
										int pRemainingLoopCount, int pInitialLoopCount) {

								}

								@Override
								public void onAnimationFrameChanged(AnimatedSprite pAnimatedSprite, int pOldFrameIndex,
										int pNewFrameIndex) {

								}

								@Override
								public void onAnimationFinished(AnimatedSprite pAnimatedSprite) {
									pAnimatedSprite.setVisible(false);
								}
							});
							this.attachChild(as);
							this.detachChild(e);
						}
					}
				}
			}
		}

		// Check for the collisions
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

					float y = e.getY();
					float x = e.getX();
					Weapon w = new Weapon(x, y + e.getHeight() / 2, -200, y + e.getHeight() / 2, wInfo,
							texProvider.getWeapon(wInfo.getKey()), texProvider.getVertexBufferObjectManager());
					w.setRotation(180);
					this.attachChild(w);
					weaponsEnemy.add(w);
				}
			}
		}
		super.onManagedUpdate(pSecondsElapsed);
	}
}
