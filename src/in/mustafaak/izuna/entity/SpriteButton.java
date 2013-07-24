package in.mustafaak.izuna.entity;

import in.mustafaak.izuna.TextureProvider;

import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;

public class SpriteButton extends Sprite {
	public static interface SpriteClickCallback {
		public void onCalled();
	}

	private SpriteClickCallback cb;

	public SpriteButton(float pX, float pY, ITextureRegion texReg, SpriteClickCallback cb) {
		super(pX, pY, texReg, TextureProvider.getInstance().getVertexBufferObjectManager());
		this.cb = cb;
	}

	@Override
	public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
		if (pSceneTouchEvent.isActionDown()) {
			cb.onCalled();
		}
		return false;
	}

}
