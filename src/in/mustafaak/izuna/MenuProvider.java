package in.mustafaak.izuna;

import in.mustafaak.izuna.entity.Menu;
import org.andengine.extension.texturepacker.opengl.texture.util.texturepacker.TexturePackTextureRegionLibrary;
import in.mustafaak.izuna.entity.Menu.ExitClickedCallback;
import in.mustafaak.izuna.entity.Menu.PlayClickedCallback;
import in.mustafaak.izuna.entity.Menu.ScoresClickedCallback;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.FontManager;
import org.andengine.opengl.texture.TextureManager;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import android.content.res.AssetManager;

public class MenuProvider {

	private static MenuProvider instance = null;

	public static MenuProvider getInstance() {
		if (instance == null) {
			throw new IllegalAccessError(
					"You should have called the getInstance(FontManager,AssetManager,VertexBufferObjectManager,TextureManager) version first.");
		}
		return instance;
	}

	public static MenuScene getPauseMenu(final Engine engine, final MainActivity owner) {
		final int MENU_RESUME = 0;
		final int MENU_QUIT = 1;

		MenuScene pauseMenu = new MenuScene(engine.getCamera());
		TextureProvider texProvider = TextureProvider.getInstance();

		final SpriteMenuItem resetMenuItem = new SpriteMenuItem(MENU_RESUME, texProvider.getmMenuResumeTextureRegion(),
				texProvider.getVertexBufferObjectManager());
		pauseMenu.addMenuItem(resetMenuItem);

		SpriteMenuItem quitMenuItem = new SpriteMenuItem(MENU_QUIT, texProvider.getmMenuExitTextureRegion(),
				texProvider.getVertexBufferObjectManager());
		pauseMenu.addMenuItem(quitMenuItem);

		pauseMenu.buildAnimations();
		pauseMenu.setBackgroundEnabled(false);

		pauseMenu.setOnMenuItemClickListener(new IOnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem, float pMenuItemLocalX,
					float pMenuItemLocalY) {
				switch (pMenuItem.getID()) {
				case MENU_RESUME:
					pMenuScene.back();
					break;
				case MENU_QUIT:
					owner.resetLevel();
					engine.setScene(owner.getMainMenu());
					break;
				}
				return false;
			}
		});

		return pauseMenu;
	}

	public static MenuProvider getInstance(FontManager fontManager, AssetManager assets,
			VertexBufferObjectManager vbom, TextureManager texManager) {
		if (instance == null) {
			instance = new MenuProvider();
		}
		return instance;
	}

	public static Menu getMainMenu(final PlayClickedCallback playClicked, final ScoresClickedCallback scoresClicked,
			final ExitClickedCallback exitClicked) {
		Menu m = new Menu();
		TextureProvider texProvider = TextureProvider.getInstance();

		TexturePackTextureRegionLibrary texPack = texProvider.getTexPackRegMainMenu();

		Sprite bgSprite = new Sprite(0, 0, texPack.get(SpriteSheet.MAIN_BG_ID),
				texProvider.getVertexBufferObjectManager());
		bgSprite.setScaleCenter(0, 0);
		bgSprite.setScale(Constants.CAMERA_WIDTH / bgSprite.getWidth());

		m.setBackground(new SpriteBackground(bgSprite));

		float x = (Constants.CAMERA_WIDTH - 312) / 2;
		float y = Constants.CAMERA_HEIGHT - 128 * 4;

		Sprite startGame = new Sprite(x, y, texPack.get(SpriteSheet.MENU_START_ID),
				texProvider.getVertexBufferObjectManager()) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				playClicked.onPlayClicked();
				return false;
			}
		};

		Sprite scores = new Sprite(x, y + 128, texPack.get(SpriteSheet.MENU_SCORES_ID),
				texProvider.getVertexBufferObjectManager()) {
			long lastClick = 0;

			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				long t = System.currentTimeMillis();
				if (t - lastClick > 1000) {
					scoresClicked.onScoresClicked();
					lastClick = t;
				}
				return false;
			}
		};

		Sprite exit = new Sprite(x, y + 128 * 2, texPack.get(SpriteSheet.MENU_EXIT_ID),
				texProvider.getVertexBufferObjectManager()) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				exitClicked.onExitClicked();
				return false;
			}
		};

		m.attachChild(startGame);
		m.attachChild(scores);
		m.attachChild(exit);

		m.registerTouchArea(startGame);
		m.registerTouchArea(scores);
		m.registerTouchArea(exit);

		return m;
	}

	public static Menu getScores() {
		Menu m = new Menu();
		TextureProvider tex = TextureProvider.getInstance();
		Sprite bg = new Sprite(0, 0, tex.getTexPackRegMainMenu().get(SpriteSheet.MAIN_BG_ID),
				tex.getVertexBufferObjectManager());
		m.setBackground(new SpriteBackground(bg));

		return m;
	}

	private MenuProvider() {

	}
}
