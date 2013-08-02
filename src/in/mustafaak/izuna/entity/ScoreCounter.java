package in.mustafaak.izuna.entity;

import in.mustafaak.izuna.Constants;
import in.mustafaak.izuna.meta.EnemyInfo;

public class ScoreCounter {
	static public final int[] SCORES_LEVEL = { 28000, 20000, 15000, 10000, 5000, 2500, 1000, 500, 100};
	static public final String[] SCORES_TITLE = { "Luke  Skywalker", "Aviator", "Jet Pilot", "WW 2 Pilot", "Duck Hunter", "Commercial Pilot", "Pilot", "Beginner", "n00b"};

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
		if (weaponLevel < 10) {
			weaponLevel++;
		}
	}
	public void resetWeaponLevel() {
		weaponLevel = 0;
	}

}
