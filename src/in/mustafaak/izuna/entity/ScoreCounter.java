package in.mustafaak.izuna.entity;

import in.mustafaak.izuna.Constants;
import in.mustafaak.izuna.meta.EnemyInfo;

public class ScoreCounter {
	static public final int[] SCORES_LEVEL = { 1000, 2000, 3000, 4000, 15000 };
	static public final String[] SCORES_TITLE = { "Kid", "Beginner", "Fighter", "Jet Bomber", "Luke Skywalker" };

	private int score;

	private int weaponLevel = 0;

	public ScoreCounter() {
		score = 0;
	}

	public void enemyKilled(EnemyInfo e) {
		score += e.getHealth();
	}

	public String getScore() {
		return String.format("%" + Constants.SCORE_LENGTH + "d", score);

	}

	public int getScoreValue() {
		return score;
	}

	public int getWeaponLevel() {
		return weaponLevel;
	}

	public void increaseScore(int score) {
		this.score += score;
	}

	public void increaseWeaponLevel() {
		if (weaponLevel < 12) {
			weaponLevel++;
		}
	}
	public void resetWeaponLevel() {
		weaponLevel = 0;
	}

}
