package in.mustafaak.izuna.entity;

import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.vbo.ISpriteVertexBufferObject;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public abstract class Ship extends Sprite {
	int health = 0;
	
	public Ship(float pX, float pY, ITextureRegion pTextureRegion,
			VertexBufferObjectManager vbom) {
		super(pX, pY, pTextureRegion, vbom);
	}

	public boolean applyDamage(int damage) {
		health = health - damage;
		return health <= 0;
	}
	
	public int getHealth() {
		return health;
	}
}
