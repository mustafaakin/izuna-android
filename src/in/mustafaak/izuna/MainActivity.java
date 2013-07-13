package in.mustafaak.izuna;

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

public class MainActivity extends SimpleBaseGameActivity {
	private static final int CAMERA_WIDTH = 1280;
	private static final int CAMERA_HEIGHT = 720;
	private TextureProvider texProvider;
	
	@Override
	public EngineOptions onCreateEngineOptions() {
		final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		return new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new FillResolutionPolicy(), camera);
	}


	@Override
	public void onCreateResources() {
		texProvider = new TextureProvider(this.getAssets(), this.getVertexBufferObjectManager(), this.getTextureManager());
	}

	@Override
	public Scene onCreateScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		final Scene scene = new Scene();
		scene.setBackground(new Background(1, 1, 1));

		TextureRegion faceTextureRegion = texProvider.getShip("player"); 
		final Sprite entity = new Sprite(0, 0, faceTextureRegion, this.getVertexBufferObjectManager());

		for(char type = 'a'; type <= 'c'; type++){
			for(int no = 1; no <= 3; no++){
				TiledTextureRegion tiled = texProvider.getWeapon(type, no);
				final AnimatedSprite weapon = new AnimatedSprite(200 * no, 200 * (type - 'a' + 1), tiled, this.getVertexBufferObjectManager());
				weapon.animate(1000 / 25);
				
				long[] pFrameDurations = new long[24*2];
				int[] pFrames = new int[24*2];
				Arrays.fill(pFrameDurations, 1000 / 24);
				for(int i = 0; i < 24; i++){
					pFrames[i] = i;
					pFrames[47-i] = i;
				}
				
				weapon.animate(pFrameDurations, pFrames, new IAnimationListener() {					
					@Override
					public void onAnimationStarted(AnimatedSprite pAnimatedSprite, int pInitialLoopCount) {
						
					}
					
					@Override
					public void onAnimationLoopFinished(AnimatedSprite pAnimatedSprite, int pRemainingLoopCount, int pInitialLoopCount) {
						
					}
					
					@Override
					public void onAnimationFrameChanged(AnimatedSprite pAnimatedSprite, int pOldFrameIndex, int pNewFrameIndex) {
						
					}
					
					@Override
					public void onAnimationFinished(AnimatedSprite pAnimatedSprite) {
						
					}
				});
				scene.attachChild(weapon);
			}
		}

		TiledTextureRegion explosiond = texProvider.getExplosionBig();
		final AnimatedSprite weapon = new AnimatedSprite(700, 300, explosiond, this.getVertexBufferObjectManager());
		weapon.animate(1000 / 25);				
		// scene.attachChild(weapon);

		scene.attachChild(entity);
		
		return scene;
	}
}