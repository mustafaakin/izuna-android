package in.mustafaak.izuna;

import in.mustafaak.izuna.entity.Level;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.entity.util.FPSLogger;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.ui.activity.SimpleBaseGameActivity;

import android.graphics.Color;
import android.graphics.Typeface;

public class MainActivity extends SimpleBaseGameActivity {
	private static final int CAMERA_WIDTH = 1280;
	private static final int CAMERA_HEIGHT = 720;
	private TextureProvider texProvider;
	private Loader loader;
	private int currentLevel = 0;
	private Font mFont;

	@Override
	public EngineOptions onCreateEngineOptions() {
		final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		return new EngineOptions(true, ScreenOrientation.LANDSCAPE_SENSOR, new FillResolutionPolicy(), camera);
	}

	@Override
	public void onCreateResources() {
		mFont = FontFactory.createFromAsset(getFontManager(), getTextureManager(), 256, 256, getAssets(),
			    "fonts/spacefr.ttf", 44, true, android.graphics.Color.rgb(233, 137, 0));
		
		this.mFont.load();
		texProvider = TextureProvider.getInstance(getFontManager(), getAssets(), getVertexBufferObjectManager(), getTextureManager());
		loader = Loader.getInstance(getAssets());
	}

	@Override
	public Scene onCreateScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());
		// Level level = new Level(loader.getLevelInfo(currentLevel), this, loader, texProvider);
		Scene mainMenu = MenuProvider.getMainMenu();
		
		return mainMenu;
	}

	public void levelFinished() {
		currentLevel++;
		if (currentLevel >= loader.getLevelCount()) {
			Scene ending = new Scene();
			ending.setBackground(new Background(1, 1, 1));
			final Text txt = new Text(100, 40, this.mFont, "GAME OVER!", new TextOptions(),
					this.getVertexBufferObjectManager());
			ending.attachChild(txt);
			mEngine.setScene(ending);
		} else {
			Level level = new Level(loader.getLevelInfo(currentLevel), this, loader, texProvider);
			mEngine.setScene(level);
		}
	}
}