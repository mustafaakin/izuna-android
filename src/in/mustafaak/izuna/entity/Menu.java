package in.mustafaak.izuna.entity;

import in.mustafaak.izuna.TextureProvider;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.TextureRegion;

public class Menu extends Scene {
	public static interface WaveClearedCallback {
		public void onWaveCleared();
	}

	public static interface LevelClearedCallback {
		public void onLevelCleared();
	}

	public static interface PlayClickedCallback {
		public void onPlayClicked();
	}
	
	public static interface ExitClickedCallback{
		public void onExitClicked();
	}
	

}
