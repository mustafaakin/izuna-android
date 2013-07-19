package in.mustafaak.izuna.entity;

import in.mustafaak.izuna.Constants;
import in.mustafaak.izuna.Loader;
import in.mustafaak.izuna.MainActivity;
import in.mustafaak.izuna.TextureProvider;
import in.mustafaak.izuna.entity.Menu.LevelClearedCallback;
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
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.AnimatedSprite.IAnimationListener;
import org.andengine.entity.sprite.Sprite;

public class Level extends Scene {
	private LevelInfo levelInfo;
	private Loader loader;
	private TextureProvider texProvider;
	private LevelClearedCallback levelClearCallback;

	private WaveInfo[] waves;

	// Current state holders
	private Player player;

	private int currentWave = 0;
	private ArrayList<Enemy> enemies = new ArrayList<Enemy>();
	private ArrayList<Weapon> weaponsEnemy = new ArrayList<Weapon>();
	private ArrayList<Weapon> weaponsPlayer = new ArrayList<Weapon>();

	private MyBackground myBg;

	class MyBackground extends Sprite {
		private PathModifier modifier = null;

		public MyBackground() {
			super(0, 0, TextureProvider.getInstance().getBackground(levelInfo.getNo()), TextureProvider.getInstance()
					.getVertexBufferObjectManager());
			setScaleCenter(0, 0);
			setScale(Constants.CAMERA_WIDTH / getWidth());
			setY(-getHeightScaled() + Constants.CAMERA_HEIGHT);
			initModifier();
		}

		public void initModifier() {
			if ( modifier == null) return;
			if (modifier != null) {
				unregisterEntityModifier(modifier);
			}
			float progress = (float) (currentWave) / (waves.length);
			float nextStep = getY() * (1 - progress);

			Path p = new Path(2).to(getX(), getY()).to(getX(), nextStep);
			modifier = new PathModifier(10, p);
			this.registerEntityModifier(modifier);
		}
	}

	public Level(LevelInfo levelInfo, LevelClearedCallback levelClearedCallback, Loader loader,
			TextureProvider texProvider) {
		this.levelClearCallback = levelClearedCallback;
		this.levelInfo = levelInfo;
		this.loader = loader;
		this.texProvider = texProvider;

		List<WaveInfo> wavesInfo = levelInfo.getWaves();
		waves = wavesInfo.toArray(new WaveInfo[wavesInfo.size()]);

		myBg = new MyBackground();
		this.attachChild(myBg);

		player = new Player();
		attachChild(player);
		registerTouchArea(player);
		setTouchAreaBindingOnActionDownEnabled(true);
		setTouchAreaBindingOnActionMoveEnabled(true);

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
			Weapon ws[] = player.getWeapons();
			for (Weapon w : ws) {
				weaponsPlayer.add(w);
				attachChild(w);
			}
		}

		if (enemies.isEmpty()) {
			if (currentWave >= waves.length) {
				// No more enemies, signal the load of the next level
				levelClearCallback.onLevelCleared();
			} else {
				addEnemies();
				currentWave++;
				myBg.initModifier();
			}
		}

		for (Iterator<Weapon> itr = weaponsEnemy.iterator(); itr.hasNext();) {
			Weapon w = itr.next();
			if (w.getY() > Constants.CAMERA_HEIGHT + 100) {
				itr.remove();
				this.detachChild(w);
			}
		}

		for (Iterator<Weapon> itrWeapon = weaponsPlayer.iterator(); itrWeapon.hasNext();) {
			Weapon w = itrWeapon.next();

			if (w.getY() < -100) {
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
					Weapon w = e.getWeapon();
					this.attachChild(w);
					weaponsEnemy.add(w);
				}
			}
		}
		super.onManagedUpdate(pSecondsElapsed);
	}
}
