package in.mustafaak.izuna;

import in.mustafaak.izuna.meta.EnemyInfo;

import java.util.Arrays;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.AnimatedSprite.IAnimationListener;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.util.FPSLogger;

import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.debug.Debug;
import org.andengine.extension.texturepacker.opengl.texture.util.texturepacker.TexturePack;
import org.andengine.extension.texturepacker.opengl.texture.util.texturepacker.TexturePackLoader;
import org.andengine.extension.texturepacker.opengl.texture.util.texturepacker.TexturePackTextureRegionLibrary;
import org.andengine.extension.texturepacker.opengl.texture.util.texturepacker.exception.TexturePackParseException;

import android.util.Log;

public class MainActivity extends SimpleBaseGameActivity {
	private static final int CAMERA_WIDTH = 1280;
	private static final int CAMERA_HEIGHT = 720;
	private TextureProvider texProvider;
	private Loader loader;
	
	@Override
	public EngineOptions onCreateEngineOptions() {
		final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		return new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new FillResolutionPolicy(), camera);
	}


	@Override
	public void onCreateResources() {
		texProvider = new TextureProvider(getAssets(), getVertexBufferObjectManager(), getTextureManager());
		loader = new Loader(getAssets());
	}

	@Override
	public Scene onCreateScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		final Scene scene = new Scene();
		scene.setBackground(new Background(1, 1, 1));

		TextureRegion faceTextureRegion = texProvider.getShip("player"); 
		final Sprite shape = new Sprite(0, 0, faceTextureRegion, this.getVertexBufferObjectManager());

		TiledTextureRegion explosiond = texProvider.getExplosionBig();
		final AnimatedSprite weapon = new AnimatedSprite(700, 300, explosiond, this.getVertexBufferObjectManager());
		weapon.animate(1000 / 25);				
		scene.attachChild(weapon);

		scene.attachChild(shape);
				
		return scene;
	}
}