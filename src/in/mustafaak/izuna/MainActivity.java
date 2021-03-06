package in.mustafaak.izuna;

import in.mustafaak.izuna.entity.Level;
import in.mustafaak.izuna.entity.Menu;
import in.mustafaak.izuna.entity.Menu.LevelClearedCallback;
import in.mustafaak.izuna.entity.ScoreCounter;
import in.mustafaak.izuna.entity.SpriteButton.SpriteClickCallback;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.util.FPSLogger;
import org.andengine.ui.activity.SimpleBaseGameActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;

public class MainActivity extends SimpleBaseGameActivity {

	private int currentLevel = 0;
	private Loader loader;

	private Menu mainMenu;

	private MenuScene pauseMenu;
	private ScoreCounter scoreCounter;
	private SoundPlayer soundPlayer;

	public Menu getMainMenu() {
		return mainMenu;
	}

	public int getScore() {
		SharedPreferences settings = getSharedPreferences("scores", 0);
		int score = settings.getInt("score", 0);
		return score;
	}

	public SoundPlayer getSoundPlayer() {
		return soundPlayer;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onCreate(Bundle pSavedInstanceState) {
		super.onCreate(pSavedInstanceState);
	}

	@Override
	public EngineOptions onCreateEngineOptions() {
		final Camera camera = new Camera(0, 0, Constants.CAMERA_WIDTH, Constants.CAMERA_HEIGHT);
		EngineOptions opts = new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new FillResolutionPolicy(),
				camera);
		opts.getAudioOptions().setNeedsSound(true);
		return opts;
	}

	@Override
	public void onCreateResources() {
		TextureProvider.getInstance(getFontManager(), getAssets(), getVertexBufferObjectManager(), getTextureManager());
		loader = Loader.getInstance(getAssets());
		soundPlayer = new SoundPlayer(getSoundManager(), this);
	}

	public void setLoading() {
		mEngine.getScene().setChildSceneModal(TextureProvider.getInstance().getLoading());
	}

	public void clearLoading() {
		mEngine.getScene().clearChildScene();
	}

	@Override
	public Scene onCreateScene() {
		mEngine.registerUpdateHandler(new FPSLogger());
		pauseMenu = MenuProvider.getPauseMenu(mEngine, this);

		final LevelClearedCallback levelClear = new LevelClearedCallback() {
			@Override
			public void onLevelCleared(boolean died) {
				currentLevel++;
				if (died || currentLevel >= loader.getLevelCount()) {
					putLocalScore();

					Menu m = MenuProvider.getScores(mEngine, getScore());
					mEngine.setScene(m);
				} else {
					final boolean isLastLevel = currentLevel == loader.getLevelCount() - 1;
					setLoading();
					final LevelClearedCallback t = this;
					new Thread(new Runnable() {
						@Override
						public void run() {
							Level level = new Level(isLastLevel, soundPlayer, loader.getLevelInfo(currentLevel), t,
									scoreCounter);
							clearLoading();
							mEngine.setScene(level);
						}
					}).start();
				}
			}
		};

		final Activity activity = this;
		mainMenu = MenuProvider.getMainMenu(new SpriteClickCallback() {
			@Override
			public void onCalled() {
				setLoading();

				soundPlayer.playClick();
				scoreCounter = new ScoreCounter();
				resetLevel();
				new Thread(new Runnable() {
					@Override
					public void run() {
						Level level = new Level(false, soundPlayer, loader.getLevelInfo(currentLevel), levelClear,
								scoreCounter);
						clearLoading();
						mEngine.setScene(level);
					}
				}).start();
			}
		}, new SpriteClickCallback() {
			@Override
			public void onCalled() {
				Menu m = MenuProvider.getScores(mEngine, getScore());
				mEngine.setScene(m);
				soundPlayer.playClick();
				// fbHandler.putScore(3000);
			}
		}, new SpriteClickCallback() {
			@Override
			public void onCalled() {
				soundPlayer.playClick();
				activity.finish();
				System.exit(0); // Force it
			}
		});
		return mainMenu;
	}

	@Override
	public boolean onKeyDown(final int pKeyCode, final KeyEvent pEvent) {
		if (mEngine.getScene() instanceof Level) {
			Level level = (Level) mEngine.getScene();
			if (!level.isAnimationWaiting()) {
				if ((pKeyCode == KeyEvent.KEYCODE_MENU || pKeyCode == KeyEvent.KEYCODE_BACK)
						&& pEvent.getAction() == KeyEvent.ACTION_DOWN) {
					if (mEngine.getScene().hasChildScene()) {
						this.pauseMenu.back();
					} else {
						mEngine.getScene().setChildScene(this.pauseMenu, false, true, true);
					}
					return false;
				}
			}
		}
		if (pKeyCode == KeyEvent.KEYCODE_MENU || pKeyCode == KeyEvent.KEYCODE_BACK) {
			soundPlayer.playClick();
		}
		return super.onKeyDown(pKeyCode, pEvent);
	}

	@Override
	public synchronized void onPauseGame() {
		if (mEngine.getScene() instanceof Level && !mEngine.getScene().hasChildScene()) {
			mEngine.getScene().setChildScene(this.pauseMenu, false, true, true);
		}
		super.onPauseGame();
	}

	private boolean putLocalScore() {
		SharedPreferences settings = getSharedPreferences("scores", 0);
		int score = settings.getInt("score", 0);
		if (scoreCounter.getScoreValue() > score) {
			SharedPreferences.Editor editor = settings.edit();
			editor.putInt("score", scoreCounter.getScoreValue());
			editor.commit();
			return true;
		} else {
			return false;
		}
	}

	public void resetLevel() {
		currentLevel = 0;
	}
}