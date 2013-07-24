package in.mustafaak.izuna;

import java.util.List;

import in.mustafaak.izuna.FacebookScoreHandler.ScoreElement;
import in.mustafaak.izuna.FacebookScoreHandler.ScoreReadyCallback;
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

import com.facebook.Session;
import com.facebook.Session.StatusCallback;
import com.facebook.SessionState;

import android.content.res.AssetManager;
import android.util.Log;

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
		if ( mainMenu != null){
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
	
	public static Menu getScores(final Engine engine) {
		final Menu m = new Menu();		
		TextureProvider tex = TextureProvider.getInstance();
		Sprite bg = new Sprite(0, 0, tex.getTexPackRegMainMenu().get(SpriteSheet.MAIN_BG_ID),
				tex.getVertexBufferObjectManager());
		bg.setScaleCenter(0, 0);
		bg.setScale(Constants.CAMERA_WIDTH / bg.getWidth());	
		
		m.setBackground(new SpriteBackground(bg));		

		float x = (Constants.CAMERA_WIDTH - 275) / 2;
		float y = Constants.CAMERA_HEIGHT - 200;
		SpriteButton back = new SpriteButton(x,y, tex.getTexPackRegMainMenu().get(SpriteSheet.MENU_BACK_ID), new SpriteClickCallback() {			
			@Override
			public void onCalled() {				
				engine.setScene(mainMenu);
			}
		}); 
		m.attachChild(back);
		m.registerTouchArea(back);
		return m;
	}
	
	private MenuProvider() {

	}
}
