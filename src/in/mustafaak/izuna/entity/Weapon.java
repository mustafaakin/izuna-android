package in.mustafaak.izuna.entity;

import java.util.Random;

import in.mustafaak.izuna.meta.WeaponInfo;

import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import org.andengine.entity.modifier.PathModifier;
import org.andengine.entity.modifier.PathModifier.Path;


public class Weapon extends AnimatedSprite {
	final static long[] pFrameDurations = { 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24,
			24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24,
			24, 24 };
	final static int[] pFrames = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22,
			23, 23, 22, 21, 20, 19, 18, 17, 16, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0 };

	public WeaponInfo weaponInfo;
	
	public Weapon(float pX, float pY, float toX, float toY, WeaponInfo info, ITiledTextureRegion pTiledTextureRegion, VertexBufferObjectManager vbom) {
		super(pX, pY, pTiledTextureRegion, vbom);
		
		this.weaponInfo = info;

		animate(pFrameDurations, pFrames);
		
		Random randomGenerator = new Random();
		
		Path p = new Path(2).to(getX(), getY()).to(toX, toY);
		float offset = randomGenerator.nextFloat();
		offset = offset == 0 ? 1.0f : offset;
		float speed = info.getSpeed() + info.getSpeed() * (offset / 4.0f);
			
		this.registerEntityModifier(new PathModifier(speed / 1000.0f, p));
	}
}
