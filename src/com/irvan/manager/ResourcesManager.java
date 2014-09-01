package com.irvan.manager;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.BoundCamera;
import org.andengine.engine.camera.Camera;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.debug.Debug;

import android.graphics.Color;

import com.irvan.GameActivity;

public class ResourcesManager {
	// variabel
	
	private static final ResourcesManager INSTANCE = new ResourcesManager();
	
	public Engine engine;
	public GameActivity activity;
	public BoundCamera camera;
	public VertexBufferObjectManager vbom;
	
	public Font font;
	
	//TEXTURE & TEXTURE REGIONS
	public ITextureRegion splash_region;
	public ITextureRegion menu_background_region;
	public ITextureRegion play_region;
	public ITextureRegion option_region;
	
	//Game texture
	public BuildableBitmapTextureAtlas gameTextureAtlas;
	
	// Game Texture Regions
	public ITextureRegion platform1_region;
	public ITextureRegion platform2_region;
	public ITextureRegion platform3_region;
	public ITextureRegion coin_region;
	public ITiledTextureRegion player_region;
	
	private BitmapTextureAtlas splashTextureAtlas;
	private BuildableBitmapTextureAtlas menuTextureAtlas;
	
	//Level Complete
	public ITextureRegion complete_window_region;
	public ITiledTextureRegion complete_stars_region;
		
	//CLASS LOGIC
	
	//untuk mengeksekusi semua method
	public void loadMenuResources(){
		loadMenuGraphics();
		loadMenuAudio();
		loadMenuFonts();
	}
	
	//untuk mengeksekusi semua method
	public void loadGameResources(){
		loadGameGraphics();
		loadGameFonts();
		loadGameAudio();
	}
	
	//untuk tampilan menu screen 
	private void loadMenuGraphics(){
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/menu/");
		menuTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);
		menu_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "menu_background.png");
		play_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "play.png");
		option_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "options.png");
		try{
			this.menuTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.menuTextureAtlas.load();
		}catch(final TextureAtlasBuilderException e){
			Debug.e(e);
		}
	}
	
	private void loadMenuAudio(){
		
	}
	
	//Untuk Load Font
	private void loadMenuFonts(){
		FontFactory.setAssetBasePath("font/");
		final ITexture mainFontTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		
		font = FontFactory.createStrokeFromAsset(activity.getFontManager(), mainFontTexture, activity.getAssets(), "font.ttf", 50, true, Color.WHITE, 2, Color.BLACK);
		font.load();
	}
	
	//untuk load gambar world, coin dan player
	private void loadGameGraphics(){
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/game/");
		gameTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);
		
		
		platform1_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "platform1.png");
		platform2_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "platform2.png");
		platform3_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "platform3.png");
		coin_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "coin.png");
		player_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "player.png", 3, 1);
		
		complete_window_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "levelCompleteWindow.png");
		complete_stars_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "star.png", 2, 1);
		
		try{
			this.gameTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.gameTextureAtlas.load();
		}catch (final TextureAtlasBuilderException e){
			Debug.e(e);
		}
	}
	
	private void loadGameFonts(){
		
	}
	
	private void loadGameAudio(){
		
	}
	
	public void unloadGameTextures(){
		
	}
	
	//Untuk Splash Screen
	public void loadSplashScreen(){
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		splashTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR);
		splash_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(splashTextureAtlas, activity, "splash.png", 0, 0);
		splashTextureAtlas.load();
	}
	
	//untuk tidak ngeload splash screen
	public void unloadSplashScreen(){
		splashTextureAtlas.unload();
		splash_region=null;
	}
	
	//method untuk handle unloading menu
	public void unloadMenuTextures(){
		menuTextureAtlas.unload();
	}
	
	//method untuk handle loading menu
	public void loadMenuTextures(){
		menuTextureAtlas.load();
	}
	
	public static void prepareManager(Engine engine, GameActivity activity, BoundCamera camera, VertexBufferObjectManager vbom){
		getInstance().engine = engine;
		getInstance().activity = activity;
		getInstance().camera = camera;
		getInstance().vbom = vbom;
	}
	
	//GETTERS AND SETTERS
	
	public static ResourcesManager getInstance() {
		return INSTANCE;
	}
}
