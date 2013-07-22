package in.mustafaak.izuna.entity;

import in.mustafaak.izuna.Constants;
import in.mustafaak.izuna.Loader;
import in.mustafaak.izuna.meta.WeaponInfo;

import org.andengine.entity.modifier.PathModifier;
import org.andengine.entity.modifier.PathModifier.Path;
import org.andengine.input.touch.TouchEvent;

public class Player extends Ship {
	public boolean canFire = false;
	public long lastFire = 0;
	private ScoreCounter scoreCounter;

	public Player(ScoreCounter scoreCounter) {
		super(Constants.PLAYER_X, Constants.PLAYER_Y + 300, "player");
		this.scoreCounter = scoreCounter;
		this.health = 100;
		this.setRotation(Constants.PLAYER_ANGLE);
		Path p = new Path(2).to(getX(), getY()).to(Constants.PLAYER_X, Constants.PLAYER_Y);
		this.registerEntityModifier(new PathModifier(Constants.PLAYER_ENTER_DELAY, p));
	}

	public void increaseHealth(int value) {
		health += value;
		if (health > 100)
			health = 100;
	}

	public ScoreCounter getScoreCounter() {
		return scoreCounter;
	}

	// 0: left
	// 1: straight
	// 2: right

	public final static int[][] weaponsList = { { 0, 1, 0 }, { 0, 2, 0 }, { 0, 3, 0 }, { 1, 1, 1 }, { 2, 2, 2 },
			{ 0, 4, 0 }, { 1, 4, 1 }, { 2, 4, 2 }, { 3, 4, 3 }, { 4, 4, 4 }, { 6 } };

	public final static float[][] angles = { { 65f }, { 60f, 75f}, { 50f, 65f, 80f} };

	
	public final static float Y = -200.0f;
	public final static float WEAPON_SPACING = 30f;
	
	public Weapon[] getWeapons() {
		int level = scoreCounter.getWeaponLevel();

		int[] c = weaponsList[level];
		int weaponCount = c[0] + c[1] + c[2];

		Weapon[] ws = new Weapon[weaponCount];

		float y = getY();
		float x = getX();
		float w = getWidth();
		float totalY = y + 200; 
		float center =  x + (w / 2);
		
		WeaponInfo wInfo = Loader.getInstance().getWeaponInfo("c3");
		
		for (int i = 0; i < c[0]; i++) {
			float angle = (float) Math.toRadians(angles[c[0] - 1][i]);

			float xOffset = (float) (Math.cos(angle) / Math.sin(angle))   * totalY;			
			
			Weapon wa = new Weapon(center, y, center + xOffset , -200, wInfo);
			wa.setRotation(Constants.PLAYER_ANGLE);
			ws[i] = wa;
		}

		float pXStart;
		if ( c[1] % 2 == 0){
			pXStart = center - (c[1] / 2) * WEAPON_SPACING + WEAPON_SPACING / 2;
		} else {
			pXStart = center - (c[1] / 2) * WEAPON_SPACING ;
		}
		
		for (int i = 0; i < c[1]; i++) {
			float pX = pXStart + WEAPON_SPACING * i;
			Weapon wa = new Weapon(pX, y, pX, -200, wInfo);
			wa.setRotation(Constants.PLAYER_ANGLE);
			ws[c[0] + i] = wa;
		}

		for (int i = 0; i < c[2]; i++) {
			float angle = (float) Math.toRadians(angles[c[2] - 1][i]);
			float xOffset = (float) (Math.cos(angle) / Math.sin(angle))   * totalY;			
			Weapon wa = new Weapon(center, y, center - xOffset , -200, wInfo);
			wa.setRotation(Constants.PLAYER_ANGLE);
			ws[c[0] + c[1] + i] = wa;			
		}
		return ws;
	}

	public float touchX = 0;
	public float touchY = 0;
	public boolean touchProcessed = true;
	
	@Override
	public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX,
			final float pTouchAreaLocalY) {
		canFire = pSceneTouchEvent.isActionDown() || pSceneTouchEvent.isActionMove();

		touchX = pSceneTouchEvent.getX() - getWidth() / 1.50f;
		touchY = pSceneTouchEvent.getY() - getHeight() / 2;
		touchProcessed = false;
		return true;
	}

}
