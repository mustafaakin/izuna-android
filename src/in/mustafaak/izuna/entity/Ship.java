package in.mustafaak.izuna.entity;

import in.mustafaak.izuna.TextureProvider;

import org.andengine.entity.sprite.Sprite;

public abstract class Ship extends Sprite {
	int health = 0;

	public Ship(float pX, float pY, String key) {
		super(pX, pY, TextureProvider.getInstance().getShip(key), TextureProvider.getInstance()
				.getVertexBufferObjectManager());
	}

	public boolean applyDamage(int damage) {
		health = health - damage;
		if ( health <= 0){
			health = 0;
			return true;
		}
		return false;
	}

	public int getHealth() {
		return health;
	}
}
