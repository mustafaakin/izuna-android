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
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.util.FPSLogger;
import org.andengine.ui.activity.SimpleBaseGameActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;

import com.facebook.Session;

public class MainActivity extends SimpleBaseGameActivity {

	private Loader loader;
	private int currentLevel = 0;


	private ScoreCounter scoreCounter;

	private Menu mainMenu;
	private MenuScene pauseMenu;

	public Menu getMainMenu() {
		return mainMenu;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	}

	@Override
	protected void onCreate(Bundle pSavedInstanceState) {
		super.onCreate(pSavedInstanceState);
	}

	@Override
	public EngineOptions onCreateEngineOptions() {
		final Camera camera = new Camera(0, 0, Constants.CAMERA_WIDTH, Constants.CAMERA_HEIGHT);
		return new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new FillResolutionPolicy(), camera);
	}

	@Override
	public void onCreateResources() {
		TextureProvider.getInstance(getFontManager(), getAssets(), getVertexBufferObjectManager(), getTextureManager());
		loader = Loader.getInstance(getAssets());
	}

	@Override
	public synchronized void onPauseGame() {
		if (mEngine.getScene() instanceof Level && !mEngine.getScene().hasChildScene()) {
			mEngine.getScene().setChildScene(this.pauseMenu, false, true, true);
		}
		super.onPauseGame();
	}

	@Override
	public Scene onCreateScene() {
		mEngine.registerUpdateHandler(new FPSLogger());
		pauseMenu = MenuProvider.getPauseMenu(mEngine, this);

		final LevelClearedCallback levelClear = new LevelClearedCallback() {
			@Override
			public void onLevelCleared() {
				currentLevel++;
				if (currentLevel >= loader.getLevelCount()) {
					mEngine.setScene(mainMenu);
				} else {
					Level level = new Level(loader.getLevelInfo(currentLevel), this, scoreCounter);
					mEngine.setScene(level);
				}
			}
		};

		final Activity activity = this;
		mainMenu = MenuProvider.getMainMenu(new SpriteClickCallback() {
			@Override
			public void onCalled() {
				scoreCounter = new ScoreCounter();
				resetLevel();
				Level level = new Level(loader.getLevelInfo(currentLevel), levelClear, scoreCounter);
				mEngine.setScene(level);
			}
		}, new SpriteClickCallback() {
			@Override
			public void onCalled() {
				Menu m = MenuProvider.getScores(mEngine);
				mEngine.setScene(m);
				// fbHandler.putScore(3000);
			}
		}, new SpriteClickCallback() {
			@Override
			public void onCalled() {
				activity.finish();
				System.exit(0); // Force it
			}
		});
		return mainMenu;
	}

	@Override
	public boolean onKeyDown(final int pKeyCode, final KeyEvent pEvent) {
		if (mEngine.getScene() instanceof Level) {
			if ((pKeyCode == KeyEvent.KEYCODE_MENU || pKeyCode == KeyEvent.KEYCODE_BACK)
					&& pEvent.getAction() == KeyEvent.ACTION_DOWN) {
				if (mEngine.getScene().hasChildScene()) {
					this.pauseMenu.back();
				} else {
					mEngine.getScene().setChildScene(this.pauseMenu, false, true, true);
				}
			}
			return true;
		} else {
			return super.onKeyDown(pKeyCode, pEvent);
		}
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