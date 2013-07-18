package in.mustafaak.izuna;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.TextureRegion;

public class MenuProvider {
	public static Scene getMainMenu(){
		Scene s = new Scene();
		TextureProvider texProvider = TextureProvider.getInstance();
		TextureRegion bgTex = texProvider.getMainBackground();
		Sprite bgSprite  = new Sprite(0,0, bgTex, texProvider.getVertexBufferObjectManager());
		bgSprite.setWidth(1280);
		bgSprite.setHeight(720);				
		s.setBackground(new SpriteBackground(bgSprite));		
		return s;
	}
}
