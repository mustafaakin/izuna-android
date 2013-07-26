package in.mustafaak.izuna;

import in.mustafaak.izuna.entity.Menu;
import in.mustafaak.izuna.entity.ScoreCounter;
import in.mustafaak.izuna.entity.SpriteButton;
import in.mustafaak.izuna.entity.SpriteButton.SpriteClickCallback;

import org.andengine.engine.Engine;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.extension.texturepacker.opengl.texture.util.texturepacker.TexturePackTextureRegionLibrary;
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

	private static Menu mainMenu = null;

	public static Menu getMainMenu(SpriteClickCallback playClicked, SpriteClickCallback scoresClicked,
			SpriteClickCallback exitClicked) {
		if (mainMenu != null) {
			return mainMenu;
		}
		mainMenu = new Menu();
		TextureProvider texProvider = TextureProvider.getInstance();

		TexturePackTextureRegionLibrary texPack = texProvider.getTexPackRegMainMenu();

		Sprite bgSprite = new Sprite(0, 0, texPack.get(SpriteSheet.MAIN_BG_ID),
				texProvider.getVertexBufferObjectManager());
		bgSprite.setScaleCenter(0, 0);
		bgSprite.setScale(Constants.CAMERA_WIDTH / bgSprite.getWidth());

		mainMenu.setBackground(new SpriteBackground(bgSprite));

		float x = (Constants.CAMERA_WIDTH - 275) / 2;
		float y = Constants.CAMERA_HEIGHT - 128 * 4;

		SpriteButton startGame = new SpriteButton(x, y, texPack.get(SpriteSheet.MENU_START_ID), playClicked);
		SpriteButton scores = new SpriteButton(x, y + 128, texPack.get(SpriteSheet.MENU_SCORES_ID), scoresClicked);
		SpriteButton exit = new SpriteButton(x, y + 2 * 128, texPack.get(SpriteSheet.MENU_EXIT_MAIN_ID), exitClicked);

		mainMenu.attachChild(startGame);
		mainMenu.attachChild(scores);
		mainMenu.attachChild(exit);

		mainMenu.registerTouchArea(startGame);
		mainMenu.registerTouchArea(scores);
		mainMenu.registerTouchArea(exit);

		return mainMenu;
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
				
				owner.getSoundPlayer().playClick();

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

	public static Menu getScores(final Engine engine, int score) {
		final Menu m = new Menu();
		TextureProvider tex = TextureProvider.getInstance();
		Sprite bg = new Sprite(0, 0, tex.getTexPackRegMainMenu().get(SpriteSheet.MAIN_BG_ID),
				tex.getVertexBufferObjectManager());
		bg.setScaleCenter(0, 0);
		bg.setScale(Constants.CAMERA_WIDTH / bg.getWidth());

		m.setBackground(new SpriteBackground(bg));

		float x = (Constants.CAMERA_WIDTH - 275) / 2;
		float y = Constants.CAMERA_HEIGHT - 200;
		SpriteButton back = new SpriteButton(x, y, tex.getTexPackRegMainMenu().get(SpriteSheet.MENU_BACK_ID),
				new SpriteClickCallback() {
					@Override
					public void onCalled() {
						engine.setScene(mainMenu);
					}
				});
		m.attachChild(back);
		m.registerTouchArea(back);

		float MARGIN_TOP = 450.0f;
		float SPACING = 50.0f;
		int[] scores = ScoreCounter.SCORES_LEVEL;
		boolean isPut = false;
		for (int i = 0; i < scores.length; i++) {
			int s = scores[i];
			if (!isPut && score < s) {
				float pY = MARGIN_TOP + (scores.length - i) * SPACING;
				Text title = new Text(30, pY, tex.getLeaderboardFont(), "YOU",
						tex.getVertexBufferObjectManager());
				isPut = true;
				Text value = new Text(580, pY, tex.getLeaderboardFont(), score
						+ "", tex.getVertexBufferObjectManager());
				
				Rectangle r = new Rectangle(0, pY, Constants.CAMERA_WIDTH, 45, tex.getVertexBufferObjectManager());
				r.setAlpha(0.8f);
				r.setColor(233f / 256, 137f / 256f, 0f);
				m.attachChild(r);
				m.attachChild(title);
				m.attachChild(value);
				
			}
			float pY = isPut ? MARGIN_TOP + (scores.length - i - 1) * SPACING : MARGIN_TOP + (scores.length - i)
					* SPACING;
			Text title = new Text(30, pY, tex.getLeaderboardFont(), ScoreCounter.SCORES_TITLE[i],
					tex.getVertexBufferObjectManager());
			Text value = new Text(580, pY, tex.getLeaderboardFont(), scores[i] + "", tex.getVertexBufferObjectManager());
			m.attachChild(value);
			m.attachChild(title);

		}

		return m;
	}

	private MenuProvider() {

	}
}
