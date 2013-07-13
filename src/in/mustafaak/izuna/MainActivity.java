package in.mustafaak.izuna;

import in.mustafaak.izuna.entity.Enemy;
import in.mustafaak.izuna.meta.EnemyInfo;
import in.mustafaak.izuna.meta.LevelInfo;
import in.mustafaak.izuna.meta.WaveEnemy;
import in.mustafaak.izuna.meta.WaveInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.texturepacker.opengl.texture.util.texturepacker.TexturePack;
import org.andengine.extension.texturepacker.opengl.texture.util.texturepacker.TexturePackLoader;
import org.andengine.extension.texturepacker.opengl.texture.util.texturepacker.TexturePackTextureRegionLibrary;
import org.andengine.extension.texturepacker.opengl.texture.util.texturepacker.exception.TexturePackParseException;
import org.andengine.input.touch.TouchEvent;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import android.hardware.SensorManager;
import android.util.Log;

public class MainActivity extends SimpleBaseGameActivity {
	private static final int CAMERA_WIDTH = 1280;
	private static final int CAMERA_HEIGHT = 720;
	private TextureProvider texProvider;
	private Loader loader;
	private int currentLevel = 0;
	private int currentWave = 0;
	private ArrayList<Enemy> enemies = new ArrayList<Enemy>();

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

	Sprite spritePlayer;

	@Override
	public Scene onCreateScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		final Scene scene = new Scene() {
			@Override
			protected void onManagedUpdate(float pSecondsElapsed) {
				for (Enemy e : enemies) {
					// Not the proper way but what the hell, it will work for
					// the code below
					if (e.collidesWith(spritePlayer)) {
						e.setVisible(!e.isVisible());
					}
				}
				super.onManagedUpdate(pSecondsElapsed);
			}
		};
		scene.setBackground(new Background(1, 1, 1));

		TextureRegion texRegPlayer = texProvider.getShip("player");
		spritePlayer = new Sprite(0, 0, texRegPlayer, this.getVertexBufferObjectManager()) {
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX,
					final float pTouchAreaLocalY) {
				this.setPosition(pSceneTouchEvent.getX() - this.getWidth() / 2,
						pSceneTouchEvent.getY() - this.getHeight() / 2);
				return true;
			}
		};

		scene.attachChild(spritePlayer);
		scene.registerTouchArea(spritePlayer);
		scene.setTouchAreaBindingOnActionDownEnabled(true);
		scene.setTouchAreaBindingOnActionMoveEnabled(true);

		populateScene(scene);

		return scene;
	}

	private void populateScene(Scene scene) {
		LevelInfo level = loader.getLevelInfo(currentLevel);
		List<WaveInfo> waves = level.getWaves();
		WaveInfo[] wavesArr = waves.toArray(new WaveInfo[waves.size()]);
		WaveInfo waveCurr = wavesArr[currentWave];
		for (WaveEnemy waveEnemy : waveCurr.getEnemies()) {

			String key = waveEnemy.getKey();
			EnemyInfo meta = loader.getEnemyInfo(key);

			TextureRegion texReg = texProvider.getShip(key);
			Enemy e = new Enemy(waveEnemy, texReg, texProvider.getVertexBufferObjectManager());
			enemies.add(e);
			scene.attachChild(e);

		}
	}
}