package in.mustafaak.izuna;

import java.util.HashMap;

import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.extension.texturepacker.opengl.texture.util.texturepacker.TexturePack;
import org.andengine.extension.texturepacker.opengl.texture.util.texturepacker.TexturePackLoader;
import org.andengine.extension.texturepacker.opengl.texture.util.texturepacker.TexturePackTextureRegionLibrary;
import org.andengine.extension.texturepacker.opengl.texture.util.texturepacker.TexturePackerTextureRegion;
import org.andengine.extension.texturepacker.opengl.texture.util.texturepacker.exception.TexturePackParseException;
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
import android.opengl.GLES20;
import android.support.v4.content.AsyncTaskLoader;

public class TextureProvider {
	private VertexBufferObjectManager vbom;
	private AssetManager assets;
	private TexturePackTextureRegionLibrary texPack;
	private TextureManager texManager;
	private TiledTextureRegion explosionBig, explosionSmall, bonus1, bonus2;
	private HashMap<String, TiledTextureRegion> weapons;
	private FontManager fontManager;


	private BitmapTextureAtlas mMenuTexture;
	private ITextureRegion mMenuResumeTextureRegion;
	private ITextureRegion mMenuExitTextureRegion;
	
	private TextureRegion mainBackground;
	
	private static TextureProvider instance = null;
	
	public static TextureProvider getInstance(FontManager fontManager, AssetManager assets, VertexBufferObjectManager vbom,
			TextureManager texManager) {
		if (instance == null) {
			instance = new TextureProvider(fontManager, assets, vbom, texManager);
		}
		return instance;
	}

	public static TextureProvider getInstance() {
		if (instance == null) {
			throw new IllegalAccessError(
					"You should have called the getInstance(FontManager,AssetManager,VertexBufferObjectManager,TextureManager) version first.");
		}
		return instance;
	}

	private TextureProvider() {

	}

	public ITextureRegion getBackground(int no) {
		BitmapTextureAtlas mBitmapTextureAtlas = new BitmapTextureAtlas(texManager, 1024, 2048, TextureOptions.BILINEAR);
		final ITextureRegion faceTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				mBitmapTextureAtlas, assets, "gfx/bg/" + no + ".jpg", 0, 0);
		mBitmapTextureAtlas.load();
		return faceTextureRegion;
	}

	private TextureProvider(FontManager fontManager, AssetManager assets, VertexBufferObjectManager vbom, TextureManager texManager) {
		this.assets = assets;
		this.vbom = vbom;
		this.texManager = texManager;
		this.fontManager = fontManager;

		TexturePack spritesheetTexturePack;
		try {
			spritesheetTexturePack = new TexturePackLoader(texManager, "gfx/")
					.loadFromAsset(assets, "spritesheet1.xml");
			spritesheetTexturePack.loadTexture();
			this.texPack = spritesheetTexturePack.getTexturePackTextureRegionLibrary();
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
		
		
		BitmapTextureAtlas mBitmapTextureAtlas = new BitmapTextureAtlas(texManager, 1024, 1024, TextureOptions.BILINEAR);
		mainBackground = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				mBitmapTextureAtlas, assets, "gfx/mainscreen.jpg", 0, 0);
		mBitmapTextureAtlas.load();
			
		mMenuTexture = new BitmapTextureAtlas(texManager, 1024, 256, TextureOptions.BILINEAR);
		mMenuResumeTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mMenuTexture, assets, "gfx/menu_resume.png", 0, 0);
		mMenuExitTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mMenuTexture, assets, "gfx/menu_exit.png", 0, 111);
		mMenuTexture.load();		
	}
	

	
	public TextureRegion getMainBackground() {
		return mainBackground;
	}

	public TextureRegion getShip(String key) {
		return this.texPack.get("ship_" + key + ".png");
	}

	public TiledTextureRegion getWeapon(String code) {
		return weapons.get(code);
	}

	public TiledTextureRegion getWeapon(char type, int no) {
		return weapons.get(type + "" + no);
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

	public VertexBufferObjectManager getVertexBufferObjectManager() {
		return vbom;
	}
	
	public FontManager getFontManager() {
		return fontManager;
	}

	// Fetched from:
	// http://stackoverflow.com/questions/12041756/animated-sprite-from-texturepacker-xml
	public TiledTextureRegion getTiled(String key, final int rows, final int columns) {
		TexturePackerTextureRegion packedTextureRegion = texPack.get(key);
		return TiledTextureRegion.create(packedTextureRegion.getTexture(), (int) packedTextureRegion.getTextureX(),
				(int) packedTextureRegion.getTextureY(), (int) packedTextureRegion.getWidth(),
				(int) packedTextureRegion.getHeight(), columns, rows, packedTextureRegion.isRotated());
	}

	// Fetched from:
	// http://stackoverflow.com/questions/12041756/animated-sprite-from-texturepacker-xml
	public TiledTextureRegion getTiled(int id, final int rows, final int columns) {
		TexturePackerTextureRegion packedTextureRegion = texPack.get(id);
		return TiledTextureRegion.create(packedTextureRegion.getTexture(), (int) packedTextureRegion.getTextureX(),
				(int) packedTextureRegion.getTextureY(), (int) packedTextureRegion.getWidth(),
				(int) packedTextureRegion.getHeight(), columns, rows, packedTextureRegion.isRotated());
	}
	
	public ITextureRegion getmMenuResumeTextureRegion() {
		return mMenuResumeTextureRegion;
	}
	
	public ITextureRegion getmMenuExitTextureRegion() {
		return mMenuExitTextureRegion;
	}
}
