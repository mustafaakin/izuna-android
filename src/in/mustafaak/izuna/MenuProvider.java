package in.mustafaak.izuna;

import in.mustafaak.izuna.entity.Menu;
import in.mustafaak.izuna.entity.SpriteButton;
import in.mustafaak.izuna.entity.SpriteButton.SpriteClickCallback;

import org.andengine.engine.Engine;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.texturepacker.opengl.texture.util.texturepacker.TexturePackTextureRegionLibrary;
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

	public static MenuProvider getInstance(FontManager fontManager, AssetManager assets,
			VertexBufferObjectManager vbom, TextureManager texManager) {
		if (instance == null) {
			instance = new MenuProvider();
		}
		return instance;
	}

	public static Menu getMainMenu(SpriteClickCallback playClicked, SpriteClickCallback scoresClicked,
			SpriteClickCallback exitClicked) {
		Menu m = new Menu();
		TextureProvider texProvider = TextureProvider.getInstance();

		TexturePackTextureRegionLibrary texPack = texProvider.getTexPackRegMainMenu();

		Sprite bgSprite = new Sprite(0, 0, texPack.get(SpriteSheet.MAIN_BG_ID),
				texProvider.getVertexBufferObjectManager());
		bgSprite.setScaleCenter(0, 0);
		bgSprite.setScale(Constants.CAMERA_WIDTH / bgSprite.getWidth());

		m.setBackground(new SpriteBackground(bgSprite));

		float x = (Constants.CAMERA_WIDTH - 275) / 2;
		float y = Constants.CAMERA_HEIGHT - 128 * 4;

		SpriteButton startGame = new SpriteButton(x, y, texPack.get(SpriteSheet.MENU_START_ID), playClicked);
		SpriteButton scores = new SpriteButton(x, y + 128, texPack.get(SpriteSheet.MENU_SCORES_ID), scoresClicked);
		SpriteButton exit = new SpriteButton(x, y + 2 * 128, texPack.get(SpriteSheet.MENU_EXIT_MAIN_ID), exitClicked);

		m.attachChild(startGame);
		m.attachChild(scores);
		m.attachChild(exit);

		m.registerTouchArea(startGame);
		m.registerTouchArea(scores);
		m.registerTouchArea(exit);

		return m;
	}

	public static MenuScene getPauseMenu(final Engine engine, final MainActivity owner) {
		final int MENU_RESUME = 0;
		final int MENU_QUIT = 1;

		MenuScene pauseMenu = new MenuScene(engine.getCamera());
		TextureProvider texProvider = TextureProvider.getInstance();

		final SpriteMenuItem resetMenuItem = new SpriteMenuItem(MENU_RESUME,
				texProvider.gePauseResumeButtonTextureRegion(), texProvider.getVertexBufferObjectManager());
		pauseMenu.addMenuItem(resetMenuItem);

		SpriteMenuItem quitMenuItem = new SpriteMenuItem(MENU_QUIT, texProvider.getPauseExitTextureRegion(),
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

	public static Menu getScores() {
		final Menu m = new Menu();
		TextureProvider tex = TextureProvider.getInstance();
		Sprite bg = new Sprite(0, 0, tex.getTexPackRegMainMenu().get(SpriteSheet.MAIN_BG_ID),
				tex.getVertexBufferObjectManager());
		bg.setSize(Constants.CAMERA_WIDTH, Constants.CAMERA_HEIGHT);
		m.setBackground(new SpriteBackground(bg));

		float x = (Constants.CAMERA_WIDTH - 275) / 2;
		float y = Constants.CAMERA_HEIGHT - 200;
		Sprite back = new Sprite(x, y, tex.getTexPackRegMainMenu().get(SpriteSheet.MENU_BACK_ID),
				tex.getVertexBufferObjectManager()) {
			long lastClick = 0;

			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				long t = System.currentTimeMillis();
				if (t - lastClick > 1000) {
					lastClick = t;

				}
				return false;
			}
		};

		m.attachChild(back);

		return m;
	}

	private MenuProvider() {

	}
}
