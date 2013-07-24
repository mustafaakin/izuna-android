package in.mustafaak.izuna.entity;

import org.andengine.entity.scene.Scene;

public class Menu extends Scene {
	public static interface ExitClickedCallback {
		public void onExitClicked();
	}

	public static interface LevelClearedCallback {
		public void onLevelCleared();
	}

	public static interface PlayClickedCallback {
		public void onPlayClicked();
	}

	public static interface WaveClearedCallback {
		public void onWaveCleared();
	}
		
	public static interface ScoresClickedCallback {
		public void onScoresClicked();		
	}
}
