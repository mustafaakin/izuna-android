package in.mustafaak.izuna.entity;

import in.mustafaak.izuna.Constants;
import in.mustafaak.izuna.meta.EnemyInfo;

public class ScoreCounter {
	private int score;
	
	public ScoreCounter(){
		score = 0;
	}
	
	public void enemyKilled(EnemyInfo e){
		score += e.getHealth();
	}
	
	public int getScoreValue() {
		return score;
	}
	
	public String getScore(){
		return String.format("%"+Constants.SCORE_LENGTH + "d", score);

	}
	
}
