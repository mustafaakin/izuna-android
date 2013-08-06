package in.mustafaak.izuna.entity;

import in.mustafaak.izuna.Constants;
import in.mustafaak.izuna.Loader;
import in.mustafaak.izuna.SoundPlayer;
import in.mustafaak.izuna.TextureProvider;
import in.mustafaak.izuna.entity.Menu.LevelClearedCallback;
import in.mustafaak.izuna.meta.LevelInfo;
import in.mustafaak.izuna.meta.WaveEnemy;
import in.mustafaak.izuna.meta.WaveInfo;
import in.mustafaak.izuna.meta.WeaponInfo;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.andengine.entity.modifier.MoveYModifier;
import org.andengine.entity.modifier.PathModifier;
import org.andengine.entity.modifier.PathModifier.Path;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.os.AsyncTask;
import android.util.Log;

public class Level extends Scene {
	private final boolean WAVE_DEBUG = false;

	private static interface CollisionEvent<T1, T2> {
		public boolean onCollide(T1 object1, T2 object2);
	}

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

	// For blocking the menu buttons
	private boolean animationWaiting = false;

	private List<Bonus> bonuses = new LinkedList<Bonus>();
	private int currentWave = 0;

	private List<Enemy> enemies = new LinkedList<Enemy>();
	private List<Explosion> explosions = new LinkedList<Explosion>();

	public boolean lastLevel = false;
	private LevelClearedCallback levelClearCallback;

	boolean levelFinished = false;
	private LevelInfo levelInfo;
	private MyBackground myBg;
	private Player player;

	public WeaponInfo playerWeaponInfo = Loader.getInstance().getWeaponInfo("c3");

	private ScoreCounter scoreCounter;

	private SoundPlayer soundPlayer;

	private TextureProvider texProvider = TextureProvider.getInstance();
	private Text txtHealth;
	private Text txtScore;
	private WaveInfo[] waves;

	private List<Weapon> weaponsEnemy = new LinkedList<Weapon>();

	private List<Weapon> weaponsPlayer = new LinkedList<Weapon>();
	

	public Level(boolean lastLevel, SoundPlayer soundPlayer, LevelInfo levelInfo,
			LevelClearedCallback levelClearedCallback, ScoreCounter scoreCounter) {
		this.levelClearCallback = levelClearedCallback;
		this.levelInfo = levelInfo;
		this.scoreCounter = scoreCounter;
		this.lastLevel = lastLevel;
		this.soundPlayer = soundPlayer;
		this.scoreCounter.resetWeaponLevel();
		
		List<WaveInfo> wavesInfo = levelInfo.getWaves();
		waves = wavesInfo.toArray(new WaveInfo[wavesInfo.size()]);

		myBg = new MyBackground();
		this.attachChild(myBg);

		player = new Player(scoreCounter);
		attachChild(player);
		registerTouchArea(player);
		setTouchAreaBindingOnActionDownEnabled(true);
		setTouchAreaBindingOnActionMoveEnabled(true);

		txtScore = new Text(Constants.SCORE_PLACE_X, 10, texProvider.getScoreFont(), "Score", Constants.SCORE_LENGTH,
				texProvider.getVertexBufferObjectManager());
		txtScore.setHorizontalAlign(HorizontalAlign.RIGHT);

		txtHealth = new Text(Constants.HEALTH_PLACE_X, 10, texProvider.getScoreFont(), "Health: ",
				"Health: ".length() + 4, texProvider.getVertexBufferObjectManager());
		txtHealth.setHorizontalAlign(HorizontalAlign.RIGHT);

		Rectangle r = new Rectangle(0, 0, Constants.CAMERA_WIDTH, 70, texProvider.getVertexBufferObjectManager());
		r.setColor(Color.BLACK);
		r.setAlpha(0.5f);
		attachChild(r);

		attachChild(txtScore);
		attachChild(txtHealth);

		final Scene scene = this;
		if (WAVE_DEBUG) {
			AsyncTask<Void, Void, Void> as = new AsyncTask<Void, Void, Void>() {
				@Override
				protected Void doInBackground(Void... params) {
					ServerSocket sSocket = null;
					try {
						sSocket = new ServerSocket(5000);

					} catch (Exception e) {
						e.printStackTrace();
					}

					Log.d("Server", "listening");
					while (true) {
						try {
							Socket socket = sSocket.accept();
							Log.d("New connection", socket.getInetAddress().getHostAddress());

							Serializer reader = new Persister();
							WaveInfo waveInfo = reader.read(WaveInfo.class, new BufferedInputStream(socket.getInputStream()));
							for (WaveEnemy waveEnemy : waveInfo.getEnemies()) {
								Enemy e = new Enemy(waveEnemy);
								enemies.add(e);
								scene.attachChild(e);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

				}
			};
			as.execute();
		}
	}

	private void addBigExplosion(Ship s) {
		Explosion exp = s.getExplosion();
		attachChild(exp);
		explosions.add(exp);
		soundPlayer.playBigExplosion();
	}

	private void addEnemies() {
		WaveInfo waveCurr = waves[currentWave];
		for (WaveEnemy waveEnemy : waveCurr.getEnemies()) {
			Enemy e = new Enemy(waveEnemy);
			enemies.add(e);
			this.attachChild(e);
		}
	}

	private void addEnemyWeapon(Enemy e) {
		Weapon w = e.getWeapon();
		this.attachChild(w);
		weaponsEnemy.add(w);
		soundPlayer.playLaser(w.weaponInfo.getFireSound());
	}

	private void addGameFinishedText() {
		TextureProvider tex = TextureProvider.getInstance();
		Rectangle back = new Rectangle(0, 0, Constants.CAMERA_WIDTH, Constants.CAMERA_HEIGHT,
				tex.getVertexBufferObjectManager());
		back.setColor(Color.BLACK);
		back.setAlpha(0.5f);
		attachChild(back);
		Text title = new Text(130, 500, tex.getGameOverFont(), " YOU HAVE\nCOMPLETED\n IZUNA DROP\n\n CONGRATS",
				tex.getVertexBufferObjectManager());
		attachChild(title);
	}

	private void addGameOver() {
		TextureProvider tex = TextureProvider.getInstance();
		Rectangle back = new Rectangle(0, 0, Constants.CAMERA_WIDTH, Constants.CAMERA_HEIGHT,
				tex.getVertexBufferObjectManager());
		back.setColor(Color.BLACK);
		back.setAlpha(0.5f);
		attachChild(back);
		Text title = new Text(140, 600, tex.getGameOverFont(), "GAME OVER", tex.getVertexBufferObjectManager());
		attachChild(title);
	}

	private void addHitExplosion(Weapon w) {
		Explosion exp = w.getHitExplosion();
		attachChild(exp);
		explosions.add(exp);
		soundPlayer.playHitExplosion();
	}

	private void addPlayerWeapon() {
		Weapon ws[] = player.getWeapons();
		for (Weapon w : ws) {
			weaponsPlayer.add(w);
			attachChild(w);
		}
		WeaponInfo weaponInfo = ws[0].weaponInfo; // all of them should be same
		soundPlayer.playLaser(weaponInfo.getFireSound());
	}

	private <T1 extends Sprite, T2 extends Sprite> void checkOneToAllPairCollisions(List<T1> list, T2 object,
			CollisionEvent<T1, T2> event) {
		Iterator<T1> itr = list.iterator();
		while (itr.hasNext()) {
			T1 sprite = itr.next();
			if (sprite.collidesWith(object)) {
				if (event.onCollide(sprite, object)) {
					detachChild(sprite);
					itr.remove();
				}
			}
		}
	}

	public boolean isAnimationWaiting() {
		return animationWaiting;
	}

	@Override
	protected void onManagedUpdate(float pSecondsElapsed) {
		txtScore.setText(scoreCounter.getScore());
		txtHealth.setText("Health: " + player.health);

		processAllCollisions();

		if (!player.touchProcessed) {
			player.setPosition(player.touchX, player.touchY);
		}

		if (levelFinished) {
			super.onManagedUpdate(pSecondsElapsed);
			return;
		}

		if ( !WAVE_DEBUG && player.health <= 0) {
			levelFinished = true;
			detachChild(player);
			Explosion e = new Explosion(player.getX(), player.getY(), true);
			attachChild(e);
			soundPlayer.playBigExplosion();

			new Thread(new Runnable() {
				@Override
				public void run() {
					animationWaiting = true;
					try {
						Thread.sleep(1000);
						soundPlayer.playEndingSad();
						addGameOver();
						Thread.sleep(4000);
						levelClearCallback.onLevelCleared(true);
					} catch (InterruptedException e) {
						// What can I do sometimes
					} finally {
						animationWaiting = false;
					}
				}
			}).start();
			super.onManagedUpdate(pSecondsElapsed);
			return;
		}

		long time = System.currentTimeMillis();

		if (enemies.isEmpty()) {
			if (!WAVE_DEBUG && currentWave >= waves.length) {
				levelFinished = true;
				// No more enemies, move the player to off-screen, then load new
				// level, signal the load of the next level
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							animationWaiting = true;
							Thread.sleep(2000);
							MoveYModifier movePlayer = new MoveYModifier(5, player.getY(), -200);
							player.registerEntityModifier(movePlayer);
							Thread.sleep(6000);
							if (lastLevel) {
								soundPlayer.playEndingHappy();
								addGameFinishedText();
								Thread.sleep(7000);
							}
						} catch (InterruptedException e) {
							// What can I do sometimes
						} finally {
							animationWaiting = false;
						}
						levelClearCallback.onLevelCleared(false);
					}
				}).start();
			} else {
				if (WAVE_DEBUG) {

				} else {
					addEnemies();
					currentWave++;
					myBg.initModifier();
				}
			}
		}

		for (Iterator<Enemy> itr = enemies.iterator(); itr.hasNext();) {
			Enemy e = itr.next();
			if (inCurrentView(e) && e.canFire(time)) {
				addEnemyWeapon(e);
			}
		}

		if (player.canFire(time)) {
			addPlayerWeapon();
		}

		removeNotInCurrentView(weaponsEnemy);
		removeNotInCurrentView(weaponsPlayer);
		removeNotInCurrentView(bonuses);

		removeFinishedExplosions();

		super.onManagedUpdate(pSecondsElapsed);
	}

	private void processAllCollisions() {
		final Scene scene = this;

		checkOneToAllPairCollisions(bonuses, player, new CollisionEvent<Bonus, Player>() {
			@Override
			public boolean onCollide(Bonus b, Player p) {
				b.applyBonus(player);
				soundPlayer.playBonus();
				return true;
			}
		});

		if (!WAVE_DEBUG) {
			checkOneToAllPairCollisions(weaponsEnemy, player, new CollisionEvent<Weapon, Player>() {
				@Override
				public boolean onCollide(Weapon w, Player p) {
					p.applyDamage(w.weaponInfo.getCausedDamage());
					addHitExplosion(w);
					return true;
				}
			});
		}

		checkOneToAllPairCollisions(enemies, player, new CollisionEvent<Enemy, Player>() {
			@Override
			public boolean onCollide(Enemy e, Player p) {
				addBigExplosion(e);
				addBigExplosion(p);
				e.applyDamage(500000);
				p.applyDamage(500000);
				
				return true;
			}
		});

		final Iterator<Enemy> itr = enemies.iterator();
		while (itr.hasNext()) {
			Enemy enemy = itr.next();

			checkOneToAllPairCollisions(weaponsPlayer, enemy, new CollisionEvent<Weapon, Enemy>() {
				@Override
				public boolean onCollide(Weapon w, Enemy e) {
					addHitExplosion(w);

					if (e.applyDamage(w.weaponInfo.getCausedDamage())) {
						scoreCounter.enemyKilled(e.getEnemyInfo());
						if (Bonus.spawnChance()) {
							Bonus b = new Bonus(e.getX(), e.getY(), e.getX(), Constants.CAMERA_HEIGHT + 200, Bonus
									.typeChance());
							scene.attachChild(b);
							bonuses.add(b);
						}
						// Spawn big explosion animation
						addBigExplosion(e);

						if (e.getUserData() == null) {
							e.setUserData("died");
							scene.detachChild(e);
							itr.remove();
						}
					}

					return true;
				};
			});
		}
	}

	private void removeFinishedExplosions() {
		final Iterator<Explosion> itr = explosions.iterator();
		while (itr.hasNext()) {
			Explosion exp = itr.next();
			if (!exp.isVisible()) {
				itr.remove();
				detachChild(exp);
			}
		}
	}

	private void removeNotInCurrentView(List<? extends Sprite> list) {
		Iterator<? extends Sprite> itr = list.iterator();
		while (itr.hasNext()) {
			Sprite s = itr.next();
			if (!inCurrentView(s)) {
				this.detachChild(s);
				itr.remove();
			}
		}
	}
	
	
}
