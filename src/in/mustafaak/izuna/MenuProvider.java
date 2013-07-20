package in.mustafaak.izuna;

import in.mustafaak.izuna.entity.Menu;
import org.andengine.extension.texturepacker.opengl.texture.util.texturepacker.TexturePack;
import org.andengine.extension.texturepacker.opengl.texture.util.texturepacker.TexturePackLoader;
import org.andengine.extension.texturepacker.opengl.texture.util.texturepacker.TexturePackTextureRegionLibrary;
import org.andengine.extension.texturepacker.opengl.texture.util.texturepacker.TexturePackerTextureRegion;
import org.andengine.extension.texturepacker.opengl.texture.util.texturepacker.exception.TexturePackParseException;

import in.mustafaak.izuna.entity.Menu.ExitClickedCallback;
import in.mustafaak.izuna.entity.Menu.PlayClickedCallback;

import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.FontManager;
import org.andengine.opengl.texture.TextureManager;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.modifier.LoopModifier;

import android.content.res.AssetManager;

import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.PathModifier;
import org.andengine.entity.modifier.PathModifier.Path;

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

	public static Menu getMainMenu(final PlayClickedCallback playClicked, final ExitClickedCallback exitClicked) {
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
				texProvider.getVertexBufferObjectManager());

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

	private MenuProvider() {

	}

	public void switchScene() {

	}
}
