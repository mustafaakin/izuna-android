package in.mustafaak.izuna.entity;

import in.mustafaak.izuna.Constants;
import in.mustafaak.izuna.Loader;
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

import org.andengine.entity.modifier.MoveYModifier;
import org.andengine.entity.modifier.PathModifier;
import org.andengine.entity.modifier.PathModifier.Path;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.AnimatedSprite.IAnimationListener;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.util.HorizontalAlign;

import android.util.Log;

public class Level extends Scene {
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

	private final static boolean inCurrentView(Sprite s) {
		// basic, just used for weapons, they are so small, no need to also
		// calculate height & width
		float x = s.getX();
		float y = s.getY();
		return x > 0 && y > 0 && y < Constants.CAMERA_HEIGHT && x < Constants.CAMERA_WIDTH;
	}

	private LevelInfo levelInfo;

	private Loader loader = Loader.getInstance();
	private ScoreCounter scoreCounter;
	private TextureProvider texProvider = TextureProvider.getInstance();

	private LevelClearedCallback levelClearCallback;
	private WaveInfo[] waves;
	// Current state holders
	private Player player;
	private int currentWave = 0;

	private ArrayList<Enemy> enemies = new ArrayList<Enemy>();

	private ArrayList<Weapon> weaponsEnemy = new ArrayList<Weapon>();

	private ArrayList<Weapon> weaponsPlayer = new ArrayList<Weapon>();

	private MyBackground myBg;

	private Text txtScore;

	public Level(LevelInfo levelInfo, LevelClearedCallback levelClearedCallback, ScoreCounter scoreCounter) {
		this.levelClearCallback = levelClearedCallback;
		this.levelInfo = levelInfo;
		this.scoreCounter = scoreCounter;

		List<WaveInfo> wavesInfo = levelInfo.getWaves();
		waves = wavesInfo.toArray(new WaveInfo[wavesInfo.size()]);

		myBg = new MyBackground();
		this.attachChild(myBg);

		player = new Player();
		attachChild(player);
		registerTouchArea(player);
		setTouchAreaBindingOnActionDownEnabled(true);
		setTouchAreaBindingOnActionMoveEnabled(true);

		txtScore = new Text(Constants.SCORE_PLACE, 10, texProvider.getScoreFont(), "Score", Constants.SCORE_LENGTH,
				texProvider.getVertexBufferObjectManager());
		txtScore.setHorizontalAlign(HorizontalAlign.RIGHT);
		attachChild(txtScore);
	}

	private void addEnemies() {
		WaveInfo waveCurr = waves[currentWave];
		for (WaveEnemy waveEnemy : waveCurr.getEnemies()) {
			Enemy e = new Enemy(waveEnemy);
			enemies.add(e);
			this.attachChild(e);
		}
	}

	boolean levelFinished = false;
	@Override
	protected void onManagedUpdate(float pSecondsElapsed) {
		if ( levelFinished){
			super.onManagedUpdate(pSecondsElapsed);
			return;
		} 
		long time = System.currentTimeMillis();
		txtScore.setText(scoreCounter.getScore());
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
				levelFinished = true;
				// No more enemies, move the player to off-screen, then load new level, signal the load of the next level 
				MoveYModifier movePlayer = new MoveYModifier(5, player.getY(), -200);
				player.registerEntityModifier(movePlayer);
				new Thread(new Runnable(){
					@Override
					public void run() {
						try {
							Thread.sleep(6000);
						} catch (InterruptedException e) {
							// What can I do sometimes
						}
						levelClearCallback.onLevelCleared();
					}
				}).start();
			} else {
				addEnemies();
				currentWave++;
				myBg.initModifier();
			}
		}

		for (Iterator<Weapon> itr = weaponsEnemy.iterator(); itr.hasNext();) {
			Weapon w = itr.next();
			if (!inCurrentView(w)) {
				itr.remove();
				this.detachChild(w);
			}
		}

		for (Iterator<Weapon> itrWeapon = weaponsPlayer.iterator(); itrWeapon.hasNext();) {
			Weapon w = itrWeapon.next();

			if (!inCurrentView(w)) {
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
							scoreCounter.enemyKilled(e.getEnemyInfo());

							itrEnemy.remove();
							// Spawn big explosion animation
							AnimatedSprite as = new AnimatedSprite(e.getX(), e.getY(), texProvider.getExplosionBig(),
									texProvider.getVertexBufferObjectManager());
							final Scene s = this;
							as.animate(1000 / 24, false, new IAnimationListener() {
								@Override
								public void onAnimationFinished(AnimatedSprite pAnimatedSprite) {
									pAnimatedSprite.setVisible(false);
								}

								@Override
								public void onAnimationFrameChanged(AnimatedSprite pAnimatedSprite, int pOldFrameIndex,
										int pNewFrameIndex) {

								}

								@Override
								public void onAnimationLoopFinished(AnimatedSprite pAnimatedSprite,
										int pRemainingLoopCount, int pInitialLoopCount) {

								}

								@Override
								public void onAnimationStarted(AnimatedSprite pAnimatedSprite, int pInitialLoopCount) {
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
