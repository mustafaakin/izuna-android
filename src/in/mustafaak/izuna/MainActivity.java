package in.mustafaak.izuna;

import in.mustafaak.izuna.entity.Level;
import in.mustafaak.izuna.entity.Menu;
import in.mustafaak.izuna.entity.Menu.ExitClickedCallback;
import in.mustafaak.izuna.entity.Menu.LevelClearedCallback;
import in.mustafaak.izuna.entity.Menu.PlayClickedCallback;

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
import android.view.KeyEvent;

public class MainActivity extends SimpleBaseGameActivity {
	private TextureProvider texProvider;
	private Loader loader;
	private int currentLevel = 0;
	private Font mFont;

	@Override
	public EngineOptions onCreateEngineOptions() {
		final Camera camera = new Camera(0, 0, Constants.CAMERA_WIDTH, Constants.CAMERA_HEIGHT);
		return new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new FillResolutionPolicy(), camera);
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
	protected void onPause() {
		if ( mEngine.isRunning()){
		}
		super.onPause();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	     if (keyCode == KeyEvent.KEYCODE_BACK) {
	    	 // Prevent closing
	    	 return true;
	     }
	     return super.onKeyDown(keyCode, event);    
	}
	
	
	@Override
	public Scene onCreateScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		final LevelClearedCallback levelClear = new LevelClearedCallback() {		
			@Override
			public void onLevelCleared() {
				currentLevel++;
				if (currentLevel >= loader.getLevelCount()) {
					Scene ending = new Scene();
					ending.setBackground(new Background(1, 1, 1));
					final Text txt = new Text(100, 40, mFont, "GAME OVER!", new TextOptions(),
							TextureProvider.getInstance().getVertexBufferObjectManager());
					ending.attachChild(txt);
					mEngine.setScene(ending);
				} else {
					Level level = new Level(loader.getLevelInfo(currentLevel), this, loader, texProvider);
					mEngine.setScene(level);
				}
				
			}
		};
		
		Menu m = MenuProvider.getMainMenu(new PlayClickedCallback() {			
			@Override
			public void onPlayClicked() {
				Level level = new Level(loader.getLevelInfo(currentLevel), levelClear, loader, texProvider);				
				mEngine.setScene(level);
			}
		}, new ExitClickedCallback() {			
			@Override
			public void onExitClicked() {
			}
		});
		
		return m;
	}
}