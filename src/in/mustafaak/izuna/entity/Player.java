package in.mustafaak.izuna.entity;

import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class Player extends Ship {
	public boolean canFire = false;
	public long lastFire = 0;
	
	public Player(ITextureRegion pTextureRegion, VertexBufferObjectManager vbom) {
		super(0, 0, pTextureRegion, vbom);
		this.health = 100;
	}

	public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX,
			final float pTouchAreaLocalY) {
		canFire = pSceneTouchEvent.isActionDown() || pSceneTouchEvent.isActionMove();		
		this.setPosition(pSceneTouchEvent.getX() - this.getWidth() / 2, pSceneTouchEvent.getY() - this.getHeight() / 2);
		return true;
	}

}
