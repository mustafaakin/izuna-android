package in.mustafaak.izuna;

import in.mustafaak.izuna.entity.Level;
import in.mustafaak.izuna.entity.Menu;
import in.mustafaak.izuna.entity.Menu.ExitClickedCallback;
import in.mustafaak.izuna.entity.Menu.LevelClearedCallback;
import in.mustafaak.izuna.entity.Menu.PlayClickedCallback;
import in.mustafaak.izuna.entity.Menu.ScoresClickedCallback;
import in.mustafaak.izuna.entity.ScoreCounter;

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

public class MainActivity extends SimpleBaseGameActivity implements IOnMenuItemClickListener {	
	
	private TextureProvider texProvider;
	private Loader loader;
	private int currentLevel = 0;

	private FacebookHandler fbHandler;
	private ScoreCounter scoreCounter;

	
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

	private Menu mainMenu;

	// Creating pause menu
	MenuScene mMenuScene;

	protected static final int MENU_RESUME = 0;
	protected static final int MENU_QUIT = 1;

	protected void createMenuScene() {
		mMenuScene = new MenuScene(mEngine.getCamera());
		final SpriteMenuItem resetMenuItem = new SpriteMenuItem(MENU_RESUME, texProvider.getmMenuResumeTextureRegion(),
				getVertexBufferObjectManager());
		mMenuScene.addMenuItem(resetMenuItem);

		SpriteMenuItem quitMenuItem = new SpriteMenuItem(MENU_QUIT, texProvider.getmMenuExitTextureRegion(),
				getVertexBufferObjectManager());
		mMenuScene.addMenuItem(quitMenuItem);

		mMenuScene.buildAnimations();
		mMenuScene.setBackgroundEnabled(false);

		this.mMenuScene.setOnMenuItemClickListener(this);
	}

	@Override
	protected void onCreate(Bundle pSavedInstanceState) {
		super.onCreate(pSavedInstanceState);
		fbHandler = new FacebookHandler(this);		
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	}
	

	

	
	@Override
	public EngineOptions onCreateEngineOptions() {
		final Camera camera = new Camera(0, 0, Constants.CAMERA_WIDTH, Constants.CAMERA_HEIGHT);
		return new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new FillResolutionPolicy(), camera);
	}

	@Override
	public void onCreateResources() {
		texProvider = TextureProvider.getInstance(getFontManager(), getAssets(), getVertexBufferObjectManager(),
				getTextureManager());
		loader = Loader.getInstance(getAssets());
	}

	@Override
	public Scene onCreateScene() {
		mEngine.registerUpdateHandler(new FPSLogger());
		createMenuScene();

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
		mainMenu = MenuProvider.getMainMenu(new PlayClickedCallback() {
			@Override
			public void onPlayClicked() {
				scoreCounter = new ScoreCounter();
				currentLevel = 0;
				Level level = new Level(loader.getLevelInfo(currentLevel), levelClear, scoreCounter);
				mEngine.setScene(level);
			}
		}, new ScoresClickedCallback() {
			@Override
			public void onScoresClicked() {
				fbHandler.login();
				// fbHandler.putScore(3000);
			}
		}, new ExitClickedCallback() {
			@Override
			public void onExitClicked() {
				// activity.finish();
				// System.exit(0); // Force it
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
					this.mMenuScene.back();
				} else {
					mEngine.getScene().setChildScene(this.mMenuScene, false, true, true);
				}
			}
			return true;
		} else {
			return super.onKeyDown(pKeyCode, pEvent);
		}
	}

	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem, float pMenuItemLocalX,
			float pMenuItemLocalY) {
		switch (pMenuItem.getID()) {
		case MENU_RESUME:
			pMenuScene.back();
			break;
		case MENU_QUIT:
			currentLevel = 0;
			mEngine.setScene(mainMenu);
			break;
		}
		return false;
	}
}