package in.mustafaak.izuna;

public interface Constants {
	public String APP_ID = "162224853965848";

	public final int BONUS_HEALTH = 0;
	public final int BONUS_POINT = 2;
	public final int BONUS_WEAPON = 1;
	public int CAMERA_HEIGHT = 1280;
	public int CAMERA_WIDTH = 720;
	public int PLAYER_ANGLE = -90;
	public float PLAYER_ENTER_DELAY = 1.0f;
	public int ENEMY_ANGLE = PLAYER_ANGLE + 180;
	public float ENEMY_ENTER_DELAY = PLAYER_ENTER_DELAY + 1.0f;
	public float HEALTH_PLACE_X = 10;
	public int PLAYER_X = CAMERA_WIDTH / 2 - 50;

	public int PLAYER_Y = CAMERA_HEIGHT - 200;
	public int SCORE_LENGTH = 10;
	public float SCORE_PLACE_X = Constants.CAMERA_WIDTH - 300;

	
}
