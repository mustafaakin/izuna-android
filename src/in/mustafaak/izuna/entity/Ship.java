package in.mustafaak.izuna.entity;

import in.mustafaak.izuna.TextureProvider;

import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.vbo.ISpriteVertexBufferObject;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public abstract class Ship extends Sprite {
	int health = 0;
	
	public Ship(float pX, float pY, String key) {
		super(pX, pY, TextureProvider.getInstance().getShip(key), TextureProvider.getInstance().getVertexBufferObjectManager());
	}

	public boolean applyDamage(int damage) {
		health = health - damage;
		return health <= 0;
	}
	
	public int getHealth() {
		return health;
	}
}
