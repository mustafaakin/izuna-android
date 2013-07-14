package in.mustafaak.izuna;

import java.util.HashMap;

import org.andengine.extension.texturepacker.opengl.texture.util.texturepacker.TexturePack;
import org.andengine.extension.texturepacker.opengl.texture.util.texturepacker.TexturePackLoader;
import org.andengine.extension.texturepacker.opengl.texture.util.texturepacker.TexturePackTextureRegionLibrary;
import org.andengine.extension.texturepacker.opengl.texture.util.texturepacker.TexturePackerTextureRegion;
import org.andengine.extension.texturepacker.opengl.texture.util.texturepacker.exception.TexturePackParseException;
import org.andengine.opengl.texture.Texture;
import org.andengine.opengl.texture.TextureManager;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import android.content.res.AssetManager;

public class TextureProvider {
	private VertexBufferObjectManager vbom;
	private AssetManager assets;
	private TexturePackTextureRegionLibrary texPack;
	private TextureManager texManager;
	private TiledTextureRegion explosionBig, explosionSmall, bonus1, bonus2;
	private HashMap<String, TiledTextureRegion> weapons;

	
	public ITextureRegion getBackground(int no){
		BitmapTextureAtlas mBitmapTextureAtlas  = new BitmapTextureAtlas(texManager, 2048, 1024, TextureOptions.BILINEAR);
		final ITextureRegion faceTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBitmapTextureAtlas, assets, "gfx/bg/" + no + ".jpg", 0, 0);
		mBitmapTextureAtlas.load();
		return faceTextureRegion;
	}
	
	
	public TextureProvider(AssetManager assets, VertexBufferObjectManager vbom, TextureManager texManager) {
		this.assets = assets;
		this.vbom = vbom;
		this.texManager = texManager;

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
}
