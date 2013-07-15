package in.mustafaak.izuna.entity;

import java.util.List;

import in.mustafaak.izuna.Constants;
import in.mustafaak.izuna.Loader;
import in.mustafaak.izuna.meta.EnemyInfo;
import in.mustafaak.izuna.meta.WaveEnemy;
import in.mustafaak.izuna.meta.WavePath;

import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.PathModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.modifier.PathModifier.Path;

public class Enemy extends Ship {
	private EnemyInfo enemyInfo;
	public long lastFire = 0;	
	public Loader loader;
	
	public EnemyInfo getEnemyInfo() {
		return enemyInfo;
	}

	public Enemy(WaveEnemy waveInfo) {
		super(waveInfo.getPaths().get(0).getStartX(), waveInfo.getPaths().get(0).getStartY(), waveInfo.getKey());
		initializePaths(waveInfo.getPaths());
		this.setRotation(Constants.ENEMY_ANGLE); 

		this.enemyInfo = Loader.getInstance().getEnemyInfo(waveInfo.getKey());
		this.health = enemyInfo.getHealth();
	}

	private void initializePaths(List<WavePath> paths) {
		int size = paths.size();
		IEntityModifier[] modifier = new IEntityModifier[size];

		int current = 0;
		for (WavePath path : paths) {
			String type = path.getType();
			// TODO: handle loops, linear paths, quadratic paths.

			Path p;
			int x1, x2, y1, y2;
			x1 = path.getStartX();
			x2 = path.getEndX();
			y1 = path.getStartY();
			y2 = path.getEndY();
			float[] xs = new float[] { x1, x2 };
			float[] ys = new float[] { y1, y2 };

			p = new Path(xs, ys);

			modifier[current] = new PathModifier(path.getDuration() / 1000.0f, p);

			current++;
		}
		this.registerEntityModifier(new SequenceEntityModifier(modifier));
	}
	
	public Weapon getWeapon(){
		float y = getY();
		float x = getX();
		return null;
		/* Weapon w = new Weapon(x, y + getHeight() / 2, -200, y + getHeight() / 2, ,
				texProvider.getWeapon(wInfo.getKey()), texProvider.getVertexBufferObjectManager());
		w.setRotation(180);
		*/
	}
	
}
