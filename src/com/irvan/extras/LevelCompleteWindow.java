package com.irvan.extras;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.irvan.manager.ResourcesManager;

public class LevelCompleteWindow extends Sprite {
	
	private TiledSprite star1;
	private TiledSprite star2;
	private TiledSprite star3;
	
	public enum StarsCount{
		ONE,
		TWO,
		THREE
	}
	
	
	public LevelCompleteWindow(VertexBufferObjectManager pSpriteVertexBufferObject) {
		super(0, 0, 650, 400, ResourcesManager.getInstance().complete_window_region, pSpriteVertexBufferObject);
		attachStars(pSpriteVertexBufferObject);
	}

	private void attachStars(VertexBufferObjectManager pSpriteVertexBufferObject) {
		star1 = new TiledSprite(150, 150, ResourcesManager.getInstance().complete_stars_region, pSpriteVertexBufferObject);
		star2 = new TiledSprite(325, 150, ResourcesManager.getInstance().complete_stars_region, pSpriteVertexBufferObject);
		star3 = new TiledSprite(500, 150, ResourcesManager.getInstance().complete_stars_region, pSpriteVertexBufferObject);
		
		attachChild(star1);
		attachChild(star2);
		attachChild(star3);
	}
	
	public void display(StarsCount starsCount, Scene scene, Camera camera){
		switch(starsCount){
			case ONE:
				star1.setCurrentTileIndex(0);
				star2.setCurrentTileIndex(1);
				star3.setCurrentTileIndex(1);
				break;
			case TWO:
				star1.setCurrentTileIndex(0);
				star2.setCurrentTileIndex(0);
				star3.setCurrentTileIndex(1);
				break;
			case THREE:
				star1.setCurrentTileIndex(0);
				star2.setCurrentTileIndex(0);
				star3.setCurrentTileIndex(0);
				break;
		}
		
		//menyembunyikan HUD
		camera.getHUD().setVisible(false);
		
		//menonaktifkan entity chase camera
		camera.setChaseEntity(null);
		
		//meletakkan level compele panel ditengah tampilan
		setPosition(camera.getCenterX(), camera.getCenterY());
		scene.attachChild(this);
	}	
}
