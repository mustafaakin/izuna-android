package in.mustafaak.izuna.entity;

import in.mustafaak.izuna.Constants;
import in.mustafaak.izuna.Loader;
import in.mustafaak.izuna.TextureProvider;
import in.mustafaak.izuna.meta.WeaponInfo;

import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class Player extends Ship {
	public boolean canFire = false;
	public long lastFire = 0;

	public Player() {
		super(Constants.PLAYER_X, Constants.PLAYER_Y, "player");
		this.health = 100;
		this.setRotation(Constants.PLAYER_ANGLE);
	}

	public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX,
			final float pTouchAreaLocalY) {
		canFire = pSceneTouchEvent.isActionDown() || pSceneTouchEvent.isActionMove();
		this.setPosition(pSceneTouchEvent.getX() - this.getWidth() / 2, pSceneTouchEvent.getY() - this.getHeight() / 2);
		return true;
	}
	
	public Weapon[] getWeapons(){
		Weapon[] w = new Weapon[1]; // temporary, enhance for leveling up, triangle  & multi shooting etc. 
		
		float y = getY();
		float x = getX(); 		
		WeaponInfo wInfo = Loader.getInstance().getWeaponInfo("c3");	
		Weapon wa = new Weapon(x, y + getHeight() / 2, - 200, y + getHeight() / 2, wInfo);
		wa.setRotation(Constants.PLAYER_ANGLE);
		w[0] = wa;
		
		return w;
	}

}
