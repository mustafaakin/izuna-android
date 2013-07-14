package in.mustafaak.izuna.entity;

import java.util.List;

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
import org.andengine.entity.sprite.vbo.ISpriteVertexBufferObject;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.modifier.IModifier;
import org.andengine.util.modifier.SequenceModifier;

public class Enemy extends Ship {
	private EnemyInfo enemyInfo;
	public long lastFire = 0;	
	
	public EnemyInfo getEnemyInfo() {
		return enemyInfo;
	}

	public Enemy(WaveEnemy waveInfo, EnemyInfo enemyInfo, ITextureRegion pTextureRegion, VertexBufferObjectManager vbom) {
		super(waveInfo.getPaths().get(0).getStartX(), waveInfo.getPaths().get(0).getStartY(), pTextureRegion, vbom);
		initializePaths(waveInfo.getPaths());
		this.setRotation(180); // Because enemies should be reversed

		this.enemyInfo = enemyInfo;
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
}
