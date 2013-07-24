package in.mustafaak.izuna.entity;

import org.andengine.entity.scene.Scene;

public class Menu extends Scene {

	public static interface LevelClearedCallback {
		public void onLevelCleared(boolean died);
	}

	public static interface WaveClearedCallback {
		public void onWaveCleared();
	}
}
