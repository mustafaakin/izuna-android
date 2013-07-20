package in.mustafaak.izuna.entity;

import in.mustafaak.izuna.Constants;
import in.mustafaak.izuna.Loader;
import in.mustafaak.izuna.meta.EnemyInfo;
import in.mustafaak.izuna.meta.WaveEnemy;
import in.mustafaak.izuna.meta.WavePath;
import in.mustafaak.izuna.meta.WeaponInfo;

import java.util.List;

import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.PathModifier;
import org.andengine.entity.modifier.PathModifier.Path;
import org.andengine.entity.modifier.QuadraticBezierCurveMoveModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;

import android.util.Log;

public class Enemy extends Ship {
	private EnemyInfo enemyInfo;
	public long lastFire = 0;
	public Loader loader;

	public Enemy(WaveEnemy waveInfo) {
		super(waveInfo.getPaths().get(0).getStartX(), waveInfo.getPaths().get(0).getStartY(), waveInfo.getKey());
		initializePaths(waveInfo.getPaths());
		this.setRotation(Constants.ENEMY_ANGLE);

		this.enemyInfo = Loader.getInstance().getEnemyInfo(waveInfo.getKey());
		this.health = enemyInfo.getHealth();
	}

	public EnemyInfo getEnemyInfo() {
		return enemyInfo;
	}

	public IEntityModifier getPath(WavePath path) {
		if (path.getType().endsWith("linear")) {
			Path p;
			int x1, x2, y1, y2;
			x1 = path.getStartX();
			x2 = path.getEndX();
			y1 = path.getStartY();
			y2 = path.getEndY();
			float[] xs = new float[] { x1, x2 };
			float[] ys = new float[] { y1, y2 };
			p = new Path(xs, ys);
			return new PathModifier(path.getDuration() / 1000.0f, p);
		} else if (path.getType().endsWith("quadratic")) {
			int x1, x2, x3, y1, y2, y3;
			x1 = path.getStartX();
			x2 = path.getMidX();
			x3 = path.getEndX();
			y1 = path.getStartY();
			y2 = path.getMidY();
			y3 = path.getEndY();
			return new QuadraticBezierCurveMoveModifier(path.getDuration() / 1000.0f, x1, y1, x2, y2, x3, y3);
		} else {
			throw new IllegalArgumentException("Unidentified path type: " + path.getType());
		}
	}

	public Weapon getWeapon() {
		float y = getY();
		float x = getX();
		WeaponInfo wInfo = Loader.getInstance().getWeaponInfo(enemyInfo.getWeapon());
		Weapon w = new Weapon(x + getWidth() / 2, y + getHeight(), x + getWidth() / 2, Constants.CAMERA_HEIGHT + 200,
				wInfo);
		w.setRotation(Constants.ENEMY_ANGLE);
		return w;
	}

	private void initializePaths(List<WavePath> paths) {
		int size = paths.size();
		IEntityModifier[] modifier = new IEntityModifier[size];

		for (int i = 0; i < paths.size(); i++) {
			WavePath path = paths.get(i);
			if (path.getType().startsWith("loop")) {
				// IEntityModifier[] nonLoopPath = Arrays.copyOf(modifier, i);
				// damn you 2.2 and lower API
				IEntityModifier[] nonLoopPath = new IEntityModifier[i];
				for (int j = 0; j < i; j++) {
					nonLoopPath[j] = modifier[j];
				}

				IEntityModifier[] loopPath = new IEntityModifier[paths.size() - i];
				for (int j = i; j < paths.size(); j++) {
					WavePath loopFragment = paths.get(j);
					loopPath[j - i] = getPath(loopFragment);
				}
				if (nonLoopPath.length == 0) {
					modifier = new IEntityModifier[1];
					modifier[0] = new LoopEntityModifier(new SequenceEntityModifier(loopPath));
				} else {
					modifier = new IEntityModifier[2];
					modifier[0] = new SequenceEntityModifier(nonLoopPath);
					modifier[1] = new LoopEntityModifier(new SequenceEntityModifier(loopPath));
				}
				break;
			} else {
				modifier[i] = getPath(path);
			}
		}
		// Allow some time to bring enemies
		IEntityModifier[] modifierWithDelay = new IEntityModifier[2];
		WavePath firstPath = paths.get(0);
		Log.d("First Path", firstPath.getStartX() + "," + firstPath.getStartY());
		// Basically stop in initial position which should be out of the scene
		// normally
		Path p = new Path(2).to(firstPath.getStartX(), firstPath.getStartY()).to(firstPath.getStartX() + 1,
				firstPath.getStartY() + 1);

		modifierWithDelay[0] = new PathModifier(Constants.ENEMY_ENTER_DELAY, p);
		modifierWithDelay[1] = new SequenceEntityModifier(modifier);
		this.registerEntityModifier(new SequenceEntityModifier(modifierWithDelay));
	}

}
