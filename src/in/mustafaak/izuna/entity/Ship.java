package in.mustafaak.izuna.entity;

import java.util.ArrayList;
import java.util.List;

import in.mustafaak.izuna.TextureProvider;

import org.andengine.entity.sprite.Sprite;

import android.util.Log;

public abstract class Ship extends Sprite {
	int health = 0;

	public Ship(float pX, float pY, String key) {
		super(pX, pY, TextureProvider.getInstance().getShip(key), TextureProvider.getInstance()
				.getVertexBufferObjectManager());
	}

	public boolean applyDamage(int damage) {
		health = health - damage;
		if (health <= 0) {
			health = 0;
			return true;
		}
		return false;
	}

	public int getHealth() {
		return health;
	}

	public Explosion getExplosion() {

		float[] center = getSceneCenterCoordinates();
		
		float sPx = center[0] - 120 / 2;
		float sPy = center[1] - 120 / 2;
		
		Explosion exp = new Explosion(sPx, sPy, true);
		exp.setScaleCenter(120 / 2, 120 / 2);
		if ( getWidth() > getHeight()){
			exp.setScale(getWidth() / 120);
		} else {
			exp.setScale(getHeight() / 120);			
		}
		
		
		return exp;
	}

}
