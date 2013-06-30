package in.mustafaak.izuna;

import in.mustafaak.izuna.TextureLoader.Texture;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;

public class MainActivity extends SimpleBaseGameActivity {
	private static final int CAMERA_WIDTH = 1280;
	private static final int CAMERA_HEIGHT = 720;
	
	
	private TextureLoader textureLoader;
	
	@Override
	public EngineOptions onCreateEngineOptions() {
		final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		return new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new FillResolutionPolicy(), camera);
	}

	@Override	
	protected void onCreateResources() {
		textureLoader = new TextureLoader(getTextureManager(), this.getAssets());
	}

	@Override
	protected Scene onCreateScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());
		final Scene scene = new Scene();
		
		Sprite back = new Sprite(0,0, textureLoader.getTextureRegion(Texture.BACKGROUND), getVertexBufferObjectManager());
		back.setSize(1280, 720);
		
		Sprite e1 = new Sprite(600,250, textureLoader.getTextureRegion(Texture.RED), getVertexBufferObjectManager());
		Sprite e2 = new Sprite(600,350, textureLoader.getTextureRegion(Texture.PURPLE), getVertexBufferObjectManager());
		Sprite e3 = new Sprite(600,450, textureLoader.getTextureRegion(Texture.PURPLE2), getVertexBufferObjectManager());
		
		Sprite s2 = new Sprite(20 ,CAMERA_HEIGHT / 2, textureLoader.getTextureRegion(Texture.PLAYER), getVertexBufferObjectManager());
		Sprite s3 = new Sprite(CAMERA_WIDTH - 630 / 2 ,20, textureLoader.getTextureRegion(Texture.BOSS), getVertexBufferObjectManager());
		
		e1.setRotation(180f);
		e2.setRotation(180f);
		e3.setRotation(180f);
		
		s3.setRotation(180f);
		
		scene.attachChild(back);
		
		scene.attachChild(e1);
		scene.attachChild(e2);
		scene.attachChild(e3);
		
		scene.attachChild(s2);
		scene.attachChild(s3);
				
		return scene;
	}
}