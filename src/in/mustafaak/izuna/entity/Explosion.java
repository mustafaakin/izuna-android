package in.mustafaak.izuna.entity;

import in.mustafaak.izuna.TextureProvider;

import org.andengine.entity.sprite.AnimatedSprite;

public class Explosion extends AnimatedSprite {
	public Explosion(float pX, float pY, boolean isBigExplosion) {
		super(pX, pY, isBigExplosion ? TextureProvider.getInstance().getExplosionBig() : TextureProvider.getInstance()
				.getExplosionSmall(), TextureProvider.getInstance().getVertexBufferObjectManager());
		animate(1000 / 24, false, new IAnimationListener() {
			@Override
			public void onAnimationFinished(AnimatedSprite pAnimatedSprite) {
				pAnimatedSprite.setVisible(false);
			}

			@Override
			public void onAnimationFrameChanged(AnimatedSprite pAnimatedSprite, int pOldFrameIndex, int pNewFrameIndex) {

			}

			@Override
			public void onAnimationLoopFinished(AnimatedSprite pAnimatedSprite, int pRemainingLoopCount,
					int pInitialLoopCount) {

			}

			@Override
			public void onAnimationStarted(AnimatedSprite pAnimatedSprite, int pInitialLoopCount) {
			}
		});
	}
}
