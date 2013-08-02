package in.mustafaak.izuna.entity;

import in.mustafaak.izuna.TextureProvider;
import in.mustafaak.izuna.meta.WeaponInfo;

import java.util.Random;

import org.andengine.entity.modifier.PathModifier;
import org.andengine.entity.modifier.PathModifier.Path;
import org.andengine.entity.sprite.AnimatedSprite;

public class Weapon extends AnimatedSprite {
	// goes to 0->24->0 for saving a little tiny memory
	final static long[] pFrameDurations = { 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24,
			24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24,
			24, 24 };
	final static int[] pFrames = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22,
			23, 23, 22, 21, 20, 19, 18, 17, 16, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0 };

	public WeaponInfo weaponInfo;

	public Weapon(float pX, float pY, float toX, float toY, WeaponInfo info) {
		super(pX, pY, TextureProvider.getInstance().getWeapon(info.getKey()), TextureProvider.getInstance()
				.getVertexBufferObjectManager());

		this.weaponInfo = info;

		animate(pFrameDurations, pFrames);


		Path p = new Path(2).to(getX(), getY()).to(toX, toY);


		this.registerEntityModifier(new PathModifier(info.getSpeed()  / 1000.0f, p));
	}
	
	public Explosion getHitExplosion(){
		Explosion exp = new Explosion(getX(), getY(), false);
		float[] center = getSceneCenterCoordinates();
		exp.setPosition(center[0],center[1]);
		return exp;		
	}
}
