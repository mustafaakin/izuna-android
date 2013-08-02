package in.mustafaak.izuna;

import java.util.HashMap;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.text.Text;
import org.andengine.extension.texturepacker.opengl.texture.util.texturepacker.TexturePack;
import org.andengine.extension.texturepacker.opengl.texture.util.texturepacker.TexturePackLoader;
import org.andengine.extension.texturepacker.opengl.texture.util.texturepacker.TexturePackTextureRegionLibrary;
import org.andengine.extension.texturepacker.opengl.texture.util.texturepacker.TexturePackerTextureRegion;
import org.andengine.extension.texturepacker.opengl.texture.util.texturepacker.exception.TexturePackParseException;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.font.FontManager;
import org.andengine.opengl.texture.TextureManager;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import android.content.res.AssetManager;

public class TextureProvider {
	private static TextureProvider instance = null;

	public static TextureProvider getInstance() {
		if (instance == null) {
			throw new IllegalAccessError(
					"You should have called the getInstance(FontManager,AssetManager,VertexBufferObjectManager,TextureManager) version first.");
		}
		return instance;
	}

	public static TextureProvider getInstance(FontManager fontManager, AssetManager assets,
			VertexBufferObjectManager vbom, TextureManager texManager) {
		if (instance == null) {
			instance = new TextureProvider(fontManager, assets, vbom, texManager);
		}
		return instance;
	}

	private AssetManager assets;
	private TiledTextureRegion explosionBig, explosionSmall, bonus1, bonus2;
	private FontManager fontManager;

	private BitmapTextureAtlas gameBackground = null;

	private Font gameOverFont;
	private Font gameplayScoreFont;
	private Font leaderboardFont;
	private Font loadingFont;

	private TextureManager texManager;

	private TexturePack texPackMainMenu;

	private TexturePackTextureRegionLibrary texPackRegMainMenu;
	private TexturePackTextureRegionLibrary texPackRegShips;
	private VertexBufferObjectManager vbom;

	private HashMap<String, TiledTextureRegion> weapons;

	private TextureProvider() {

	}

	private TextureProvider(FontManager fontManager, AssetManager assets, VertexBufferObjectManager vbom,
			TextureManager texManager) {
		this.assets = assets;
		this.vbom = vbom;
		this.texManager = texManager;
		this.fontManager = fontManager;

		TexturePack texPackShips;
		try {
			texPackShips = new TexturePackLoader(texManager, "gfx/").loadFromAsset(assets, "spritesheet1.xml");
			texPackShips.loadTexture();
			this.texPackRegShips = texPackShips.getTexturePackTextureRegionLibrary();

			texPackMainMenu = new TexturePackLoader(texManager, "gfx/").loadFromAsset(assets, "spritesheet2.xml");
			texPackMainMenu.loadTexture();
			texPackRegMainMenu = texPackMainMenu.getTexturePackTextureRegionLibrary();

		} catch (TexturePackParseException e) {
			e.printStackTrace();
		}

		explosionBig = getTiled(SpriteSheet.EXPLOSION_BIG_ID, 4, 6);
		explosionSmall = getTiled(SpriteSheet.EXPLOSION_SMALL_ID, 4, 6);

		bonus1 = getTiled(SpriteSheet.BONUS_1_ID, 4, 6);
		bonus2 = getTiled(SpriteSheet.BONUS_2_ID, 4, 6);

		weapons = new HashMap<String, TiledTextureRegion>();
		// a1-3,b1-3,c1-3
		for (char type = 'a'; type <= 'c'; type++) {
			for (int no = 1; no <= 3; no++) {
				String key = "weapon_" + type + "" + no + ".png";
				weapons.put(type + "" + no, getTiled(key, 4, 6));
			}
		}

		gameplayScoreFont = FontFactory.createFromAsset(fontManager, texManager, 256, 256, assets, "fonts/spacefr.ttf",
				44, true, android.graphics.Color.rgb(233, 137, 0));
		gameplayScoreFont.load();

		leaderboardFont = FontFactory.createFromAsset(fontManager, texManager, 256, 256, assets, "fonts/spacefr.ttf",
				36, true, android.graphics.Color.rgb(233, 230, 243));
		leaderboardFont.load();

		gameOverFont = FontFactory.createFromAsset(fontManager, texManager, 256, 256, assets, "fonts/spacefr.ttf", 60,
				true, android.graphics.Color.rgb(233, 230, 243));
		gameOverFont.load();

		loadingFont = FontFactory.createFromAsset(fontManager, texManager, 256, 256, assets, "fonts/spacefr.ttf", 96,
				true, android.graphics.Color.rgb(233, 230, 243));
		loadingFont.load();

	}

	private Scene loading;

	public Scene getLoading() {
		if (loading == null) {
			loading = new Scene();
			Text t = new Text(100, 600, loadingFont, "Loading...", getVertexBufferObjectManager());
			loading.attachChild(t);
		}
		return loading;
	}

	public ITextureRegion gePauseResumeButtonTextureRegion() {
		return texPackRegMainMenu.get(SpriteSheet.MENU_RESUME_ID);
	}

	public ITextureRegion getBackground(int no) {
		if (gameBackground != null) {
			gameBackground.unload();
		}
		gameBackground = new BitmapTextureAtlas(texManager, 1024, 2048, TextureOptions.BILINEAR);
		final ITextureRegion texRegBackground = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameBackground,
				assets, "gfx/bg/" + no + ".jpg", 0, 0);
		gameBackground.load();
		return texRegBackground;
	}

	public TiledTextureRegion getBonus(int no) {
		return no == 0 ? bonus1 : bonus2;
	}

	public TiledTextureRegion getExplosionBig() {
		return explosionBig;
	}

	public TiledTextureRegion getExplosionSmall() {
		return explosionSmall;
	}

	public FontManager getFontManager() {
		return fontManager;
	}

	public Font getGameOverFont() {
		return gameOverFont;
	}

	public Font getLeaderboardFont() {
		return leaderboardFont;
	}

	public ITextureRegion getPauseExitTextureRegion() {
		return texPackRegMainMenu.get(SpriteSheet.MENU_EXIT_ID);
	}

	public Font getScoreFont() {
		return gameplayScoreFont;
	}

	public TextureRegion getShip(String key) {
		return this.texPackRegShips.get("ship_" + key + ".png");
	}

	public TexturePackTextureRegionLibrary getTexPackRegMainMenu() {
		return texPackRegMainMenu;
	}

	// Fetched from:
	// http://stackoverflow.com/questions/12041756/animated-sprite-from-texturepacker-xml
	public TiledTextureRegion getTiled(int id, final int rows, final int columns) {
		TexturePackerTextureRegion packedTextureRegion = texPackRegShips.get(id);
		return TiledTextureRegion.create(packedTextureRegion.getTexture(), (int) packedTextureRegion.getTextureX(),
				(int) packedTextureRegion.getTextureY(), (int) packedTextureRegion.getWidth(),
				(int) packedTextureRegion.getHeight(), columns, rows, packedTextureRegion.isRotated());
	}

	// Fetched from:
	// http://stackoverflow.com/questions/12041756/animated-sprite-from-texturepacker-xml
	public TiledTextureRegion getTiled(String key, final int rows, final int columns) {
		TexturePackerTextureRegion packedTextureRegion = texPackRegShips.get(key);
		return TiledTextureRegion.create(packedTextureRegion.getTexture(), (int) packedTextureRegion.getTextureX(),
				(int) packedTextureRegion.getTextureY(), (int) packedTextureRegion.getWidth(),
				(int) packedTextureRegion.getHeight(), columns, rows, packedTextureRegion.isRotated());
	}

	public VertexBufferObjectManager getVertexBufferObjectManager() {
		return vbom;
	}

	public TiledTextureRegion getWeapon(char type, int no) {
		return weapons.get(type + "" + no);
	}

	public TiledTextureRegion getWeapon(String code) {
		return weapons.get(code);
	}
}
