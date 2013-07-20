package in.mustafaak.izuna;

import in.mustafaak.izuna.entity.Level;
import in.mustafaak.izuna.entity.Menu;
import in.mustafaak.izuna.entity.Menu.ExitClickedCallback;
import in.mustafaak.izuna.entity.Menu.LevelClearedCallback;
import in.mustafaak.izuna.entity.Menu.PlayClickedCallback;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.entity.util.FPSLogger;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.ui.activity.SimpleBaseGameActivity;

import android.app.Activity;
import android.view.KeyEvent;

public class MainActivity extends SimpleBaseGameActivity implements IOnMenuItemClickListener {
	private TextureProvider texProvider;
	private Loader loader;
	private int currentLevel = 0;
	private Font mFont;

	private Menu mainMenu;

	// Creating menu
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
	public EngineOptions onCreateEngineOptions() {
		final Camera camera = new Camera(0, 0, Constants.CAMERA_WIDTH, Constants.CAMERA_HEIGHT);
		return new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new FillResolutionPolicy(), camera);
	}

	@Override
	public void onCreateResources() {
		mFont = FontFactory.createFromAsset(getFontManager(), getTextureManager(), 256, 256, getAssets(),
				"fonts/spacefr.ttf", 44, true, android.graphics.Color.rgb(233, 137, 0));

		this.mFont.load();
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
					Scene ending = new Scene();
					ending.setBackground(new Background(1, 1, 1));
					final Text txt = new Text(100, 40, mFont, "GAME OVER!", new TextOptions(), TextureProvider
							.getInstance().getVertexBufferObjectManager());
					ending.attachChild(txt);
					mEngine.setScene(ending);
				} else {
					Level level = new Level(loader.getLevelInfo(currentLevel), this, loader, texProvider);
					mEngine.setScene(level);
				}

			}
		};

		final Activity activity = this;
		mainMenu = MenuProvider.getMainMenu(new PlayClickedCallback() {
			@Override
			public void onPlayClicked() {
				Level level = new Level(loader.getLevelInfo(currentLevel), levelClear, loader, texProvider);
				mEngine.setScene(level);
			}
		}, new ExitClickedCallback() {
			@Override
			public void onExitClicked() {
				activity.finish();
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
			this.currentLevel = 0;
			this.mEngine.setScene(mainMenu);
			break;
		}
		return false;
	}

}