package in.mustafaak.izuna.entity;

import in.mustafaak.izuna.Constants;
import in.mustafaak.izuna.Loader;
import in.mustafaak.izuna.meta.WeaponInfo;

import org.andengine.input.touch.TouchEvent;

public class Player extends Ship {
	public boolean canFire = false;
	public long lastFire = 0;

	public Player() {
		super(Constants.PLAYER_X, Constants.PLAYER_Y, "player");
		this.health = 100;
		this.setRotation(Constants.PLAYER_ANGLE);
	}

	@Override
	public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX,
			final float pTouchAreaLocalY) {
		canFire = pSceneTouchEvent.isActionDown() || pSceneTouchEvent.isActionMove();
		// TODO: make it update local fields, then update the position in
		// onManagedUpdate
		setPosition(pSceneTouchEvent.getX() - getWidth() / 1.50f, pSceneTouchEvent.getY() - getHeight() / 2);
		return true;
	}

	public Weapon[] getWeapons() {
		// TODO: temporary, enhance for leveling up, triangle & multiple
		// shooting
		Weapon[] w = new Weapon[1];

		float y = getY();
		float x = getX();
		WeaponInfo wInfo = Loader.getInstance().getWeaponInfo("c3");
		Weapon wa = new Weapon(x + getWidth() / 2, y, x + getWidth() / 2, -200, wInfo);
		wa.setRotation(Constants.PLAYER_ANGLE);
		w[0] = wa;

		return w;
	}

}
