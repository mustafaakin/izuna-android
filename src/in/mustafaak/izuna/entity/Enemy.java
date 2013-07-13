package in.mustafaak.izuna.entity;

import in.mustafaak.izuna.TextureProvider;
import in.mustafaak.izuna.meta.EnemyInfo;
import in.mustafaak.izuna.meta.WaveEnemy;
import in.mustafaak.izuna.meta.WavePath;

import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.PathModifier;
import org.andengine.entity.modifier.QuadraticBezierCurveMoveModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.modifier.PathModifier.Path;
import org.andengine.entity.sprite.Sprite;
import org.andengine.util.modifier.IModifier;
import org.andengine.util.modifier.SequenceModifier;

public class Enemy extends Sprite {
	private int health;
	private WaveEnemy waveInfo;

	public Enemy(EnemyInfo enemyInfo, WaveEnemy waveInfo, TextureProvider texProvider) {
		super(waveInfo.getPaths().get(0).getStartX(), waveInfo.getPaths().get(0).getStartY(), texProvider
				.getShip(waveInfo.getKey()), texProvider.getVertexBufferObjectManager());
		this.health = enemyInfo.getHealth();
		this.waveInfo = waveInfo;
		
		initializePaths();		
	}

	private void initializePaths() {
		int size = waveInfo.getPaths().size();
		IModifier[] modifier = new IModifier[size];

		int current = 0;
		for (WavePath path : waveInfo.getPaths()) {
			String type = path.getType();
			Path p;
			if (type.equals("linear")) {
				int x1, x2, y1, y2;
				x1 = path.getStartX();
				x2 = path.getEndX();
				y1 = path.getStartY();
				y2 = path.getStartY();
				float[] xs = new float[] { x1, x2 };
				float[] ys = new float[] { y1, y2 };
				p = new Path(xs, ys);
			} else {
				// Not implemented yet.
			}
			current++;
		}

		/*
		 * Path a = new Path(2).to(0, 0).to(400, 0); Path b = new
		 * Path(2).to(400, 0).to(800, 0); Path c = new Path(2).to(800,
		 * 0).to(400, 0); QuadraticBezierCurveMoveModifier q = new
		 * QuadraticBezierCurveMoveModifier(2, 800, 0, 600, 400, 400, 0);
		 * 
		 * SequenceEntityModifier loopSeq = new SequenceEntityModifier(new
		 * IEntityModifier[] { new PathModifier(0.5f, b), q });
		 * 
		 * IEntityModifier[] modifier = new IEntityModifier[2]; modifier[0] =
		 * new PathModifier(1, a); modifier[1] = new
		 * LoopEntityModifier(loopSeq); IEntityModifier entMod = new
		 * SequenceEntityModifier(modifier);
		 * entity.registerEntityModifier(entMod);
		 */
	}

}
