package in.mustafaak.izuna;

import java.util.HashMap;
import java.util.Map.Entry;

import org.andengine.entity.sprite.vbo.ISpriteVertexBufferObject;
import org.andengine.opengl.texture.TextureManager;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.BaseTextureRegion;
import org.andengine.opengl.texture.region.TextureRegion;

import android.content.res.AssetManager;

public class TextureLoader {
	enum Texture {
		BLACK, BLUE, BOSS, PLAYER, PURPLE, PURPLE2, RED, WHITE, BACKGROUND
	}

	private TextureManager tm;
	private HashMap<Texture, BitmapTextureAtlas> textureAtlas;
	private HashMap<Texture, BaseTextureRegion> textureRegion;

	final private static String GFX_EXTENSION = "png";
	private static HashMap<Texture, String> texturePath;

	static {
		texturePath = new HashMap<Texture, String>();
		texturePath.put(Texture.BLACK, "ships/black");
		texturePath.put(Texture.BLUE, "ships/blue");
		texturePath.put(Texture.BOSS, "ships/boss");
		texturePath.put(Texture.PLAYER, "ships/player");
		texturePath.put(Texture.PURPLE, "ships/purple");
		texturePath.put(Texture.PURPLE2, "ships/purple2");
		texturePath.put(Texture.RED, "ships/red");
		texturePath.put(Texture.WHITE, "ships/white");
		texturePath.put(Texture.BACKGROUND, "bg/bg1");
		
//		texturePath.put(Texture.EXPLOSION, "ships/explosion");
	}

	public TextureLoader(TextureManager tm, AssetManager asm) {
		this.tm = tm;

		textureAtlas = new HashMap<Texture, BitmapTextureAtlas>();
		textureRegion = new HashMap<Texture, BaseTextureRegion>();

		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

		for (Entry<Texture, String> e : texturePath.entrySet()) {
			Texture key = e.getKey();
			String path = e.getValue();

			BitmapTextureAtlas atlas = new BitmapTextureAtlas(tm, 1024, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
			TextureRegion region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(atlas, asm, path + "." + GFX_EXTENSION, 0, 0);

			atlas.load();
			
			textureAtlas.put(key, atlas);			
			textureRegion.put(key, region);
		}
	}
	
	public BaseTextureRegion getTextureRegion(Texture t){
		return textureRegion.get(t);
	}
}
