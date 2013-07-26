package in.mustafaak.izuna.entity;

import in.mustafaak.izuna.Constants;
import in.mustafaak.izuna.TextureProvider;

import java.util.Random;

import org.andengine.entity.modifier.PathModifier;
import org.andengine.entity.modifier.PathModifier.Path;
import org.andengine.entity.sprite.AnimatedSprite;

public class Bonus extends AnimatedSprite {
	// goes to 0->24->0 for saving a little tiny memory
	final static long[] pFrameDurations = { 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24,
			24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24,
			24, 24 };
	final static int[] pFrames = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22,
			23, 23, 22, 21, 20, 19, 18, 17, 16, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0 };

	private static Random randomGenerator = new Random();

	public static boolean spawnChance() {
		return randomGenerator.nextInt(100) > 30;
	}

	public static int typeChance() {
		// TODO: Actually 3, implement later when texture is ready
		return randomGenerator.nextInt(2);
	}

	private int bonusType;

	private int bonusValue;

	public Bonus(float pX, float pY, float toX, float toY, int bonusType) {
		super(pX, pY, TextureProvider.getInstance().getBonus(bonusType), TextureProvider.getInstance()
				.getVertexBufferObjectManager());

		this.bonusType = bonusType;

		Random randomGenerator = new Random();

		if (bonusType == Constants.BONUS_HEALTH) {
			bonusValue = (randomGenerator.nextInt(4) + 1) * 25;
		} else if (bonusType == Constants.BONUS_POINT) {
			bonusValue = (randomGenerator.nextInt(4) + 1) * 200;
		} else if (bonusType == Constants.BONUS_WEAPON) {
			bonusValue = 1; // Just increase the weapon level
		}

		animate(pFrameDurations, pFrames);

		Path p = new Path(2).to(getX(), getY()).to(toX, toY);
		float speed = 1000;

		this.registerEntityModifier(new PathModifier(speed / 1000.0f, p));
	}

	public void applyBonus(Player p) {
		if (bonusType == Constants.BONUS_HEALTH) {
			p.increaseHealth(bonusValue);
		} else if (bonusType == Constants.BONUS_POINT) {
			p.getScoreCounter().increaseScore(bonusValue);
		} else if (bonusType == Constants.BONUS_WEAPON) {
			p.getScoreCounter().increaseWeaponLevel();
		}

	}

}
