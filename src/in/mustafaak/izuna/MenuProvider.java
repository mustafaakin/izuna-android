package in.mustafaak.izuna;

import in.mustafaak.izuna.entity.Menu;
import in.mustafaak.izuna.entity.Menu.ExitClickedCallback;
import in.mustafaak.izuna.entity.Menu.PlayClickedCallback;

import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.FontManager;
import org.andengine.opengl.texture.TextureManager;
import org.andengine.opengl.texture.region.TextureRegion;
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

	public static Menu getMainMenu(final PlayClickedCallback playClicked, ExitClickedCallback exitClicked) {
		Menu m = new Menu() {
			@Override
			public boolean onSceneTouchEvent(TouchEvent pSceneTouchEvent) {
				playClicked.onPlayClicked();
				return false;
			}
		};
		TextureProvider texProvider = TextureProvider.getInstance();
		TextureRegion bgTex = texProvider.getMainBackground();
		Sprite bgSprite = new Sprite(0, 0, bgTex, texProvider.getVertexBufferObjectManager());
		bgSprite.setWidth(Constants.CAMERA_WIDTH);
		bgSprite.setHeight(Constants.CAMERA_HEIGHT);
		m.setBackground(new SpriteBackground(bgSprite));
		return m;
	}

	private MenuProvider() {

	}

	public void switchScene() {

	}
}
