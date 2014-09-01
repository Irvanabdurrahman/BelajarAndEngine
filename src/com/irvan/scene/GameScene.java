package com.irvan.scene;

import java.io.IOException;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.SAXUtils;
import org.andengine.util.adt.align.HorizontalAlign;
import org.andengine.util.adt.color.Color;
import org.andengine.util.level.EntityLoader;
import org.andengine.util.level.constants.LevelConstants;
import org.andengine.util.level.simple.SimpleLevelEntityLoaderData;
import org.andengine.util.level.simple.SimpleLevelLoader;
import org.xml.sax.Attributes;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.irvan.base.BaseScene;
import com.irvan.extras.LevelCompleteWindow;
import com.irvan.extras.LevelCompleteWindow.StarsCount;
import com.irvan.manager.SceneManager;
import com.irvan.manager.SceneManager.SceneType;
import com.irvan.object.Player;

public class GameScene extends BaseScene implements IOnSceneTouchListener{
		
	private int score = 0;
	
	private HUD gameHUD;
	private Text scoreText;
	private PhysicsWorld physicsWorld;
	private LevelCompleteWindow levelCompleteWindow;
	
	private static final String TAG_ENTITY = "entity";
	private static final String TAG_ENTITY_ATTRIBUTE_X = "x";
	private static final String TAG_ENTITY_ATTRIBUTE_Y = "y";
	private static final String TAG_ENTITY_ATTRIBUTE_TYPE = "type";
	
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLATFORM1 = "platform1";
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLATFORM2 = "platform2";
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLATFORM3 = "platform3";
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_COIN = "coin";
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLAYER = "player";
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_LEVEL_COMPLETE = "levelComplete";
	
	private Player player;
	
	private Text gameOverText;
	private boolean gameOverDisplayed = false;
	
	private boolean firstTouch = false;

	@Override
	public void createScene() {
		createBackground();
		createHUD();
		createPhysics();
		loadLevel(1);
		createGameOverText();
				
		levelCompleteWindow = new LevelCompleteWindow(vbom);
		
		setOnSceneTouchListener(this);
	}

	//kembali ke MainMenuScene.java
	@Override
	public void onBackKeyPressed() {
		SceneManager.getInstance().loadMenuScene(engine);
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_GAME;
	}

	@Override
	public void disposeScene() {
		camera.setHUD(null);
		camera.setChaseEntity(null);
		camera.setCenter(400, 240);
	}
	
	//untuk event touch hero jump
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		if(pSceneTouchEvent.isActionDown()){
			if(!firstTouch){
				player.setRunning();
				firstTouch = true;
			}else{
				player.jump();
			}
		}
		return false;
	}
	
	//untuk level permainan dalam game, land, coin
	private void loadLevel(int levelID){
		final SimpleLevelLoader levelLoader = new SimpleLevelLoader(vbom);
		
		final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(0, 0.01f, 0.5f);
		
		levelLoader.registerEntityLoader(new EntityLoader<SimpleLevelEntityLoaderData>(LevelConstants.TAG_LEVEL) {

			public IEntity onLoadEntity(final String pEntityName, final IEntity pParent, final Attributes pAttributes, final SimpleLevelEntityLoaderData pSimpleLevelEntityLoaderData) throws IOException {
				final int width = SAXUtils.getIntAttributeOrThrow(pAttributes, LevelConstants.TAG_LEVEL_ATTRIBUTE_WIDTH);
				final int height = SAXUtils.getIntAttributeOrThrow(pAttributes, LevelConstants.TAG_LEVEL_ATTRIBUTE_HEIGHT);
				
				//set Camera mengikuti hero jatuh
				camera.setBounds(0, 0, width, height);
				camera.setBoundsEnabled(true);
				
				return GameScene.this;
			}
		});
		
		levelLoader.registerEntityLoader(new EntityLoader<SimpleLevelEntityLoaderData>(TAG_ENTITY) {

			public IEntity onLoadEntity(final String pEntityName, final IEntity pParent, final Attributes pAttributes, final SimpleLevelEntityLoaderData pSimpleLevelEntityLoaderData) throws IOException {
				final int x = SAXUtils.getIntAttributeOrThrow(pAttributes, TAG_ENTITY_ATTRIBUTE_X);
				final int y = SAXUtils.getIntAttributeOrThrow(pAttributes, TAG_ENTITY_ATTRIBUTE_Y);
				final String type = SAXUtils.getAttributeOrThrow(pAttributes, TAG_ENTITY_ATTRIBUTE_TYPE);
				
				final Sprite levelObject;
				
				if(type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLATFORM1)){
					levelObject = new Sprite(x, y, resourcesManager.platform1_region, vbom);
					PhysicsFactory.createBoxBody(physicsWorld, levelObject, BodyType.StaticBody, FIXTURE_DEF).setUserData("platform1");
				}else if(type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLATFORM2)){
					levelObject = new Sprite(x, y, resourcesManager.platform2_region, vbom);
					final Body body = PhysicsFactory.createBoxBody(physicsWorld, levelObject, BodyType.StaticBody, FIXTURE_DEF);
					body.setUserData("platform2");
					physicsWorld.registerPhysicsConnector(new PhysicsConnector(levelObject, body, true, false));
				}else if(type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLATFORM3)){
					levelObject = new Sprite(x, y, resourcesManager.platform3_region, vbom);
					final Body body = PhysicsFactory.createBoxBody(physicsWorld, levelObject, BodyType.StaticBody, FIXTURE_DEF);
					body.setUserData("platform3");
					physicsWorld.registerPhysicsConnector(new PhysicsConnector(levelObject, body, true, false));
				}else if(type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_COIN)){
					levelObject = new Sprite(x, y, resourcesManager.coin_region, vbom){
						
						@Override
						protected void onManagedUpdate(float pSecondsElapsed){
							super.onManagedUpdate(pSecondsElapsed);
							if(player.collidesWith(this)){
								addToScore(10);
								this.setVisible(false);
								this.setIgnoreUpdate(true);
							}
						}
					};
					levelObject.registerEntityModifier(new LoopEntityModifier(new ScaleModifier(1, 1, 1.3f)));
				}else if(type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLAYER)){
					player = new Player(x, y, vbom, camera, physicsWorld) {
						
						@Override
						public void onDie() {
							if(!gameOverDisplayed){
								displayGameOverText();
							}
						}
					};
					levelObject = player;
				}else if(type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_LEVEL_COMPLETE)){
					levelObject = new Sprite(x, y, resourcesManager.complete_stars_region, vbom){
						@Override
						protected void onManagedUpdate(float pSecondsElapsed){
							super.onManagedUpdate(pSecondsElapsed);
							if(player.collidesWith(this)){
								levelCompleteWindow.display(StarsCount.TWO, GameScene.this, camera);
								this.setVisible(false);
								this.setIgnoreUpdate(true);
							}
						}
					};
					levelObject.registerEntityModifier(new LoopEntityModifier(new ScaleModifier(1, 1, 1.3f)));
				}else{
					throw new IllegalArgumentException();
				}
				
				levelObject.setCullingEnabled(true);
				
				return levelObject;
				
			}
			
		});
		levelLoader.loadLevelFromAsset(activity.getAssets(), "level/" + levelID + ".lvl");
	}
	
	//method text gameover
	private void createGameOverText(){
		gameOverText = new Text(0, 0, resourcesManager.font, "Game Over!", vbom);
	}
	
	//menampilakn text gameover
	private void displayGameOverText(){
		camera.setChaseEntity(null);
		gameOverText.setPosition(camera.getCenterX(), camera.getCenterY());
		attachChild(gameOverText);
		gameOverDisplayed = true;
	}
	
	//HUD untuk menampilkan game controller dan text score
	private void createHUD(){
		gameHUD = new HUD();
		camera.setHUD(gameHUD);
		
		//membuat text score
		scoreText = new Text(getX(), (getY()+50) * 8, resourcesManager.font, "Score : 0123456789", new TextOptions(HorizontalAlign.LEFT), vbom);
		scoreText.setAnchorCenter(0, 0);
		scoreText.setText("Score : 0");
		gameHUD.attachChild(scoreText);
		
		camera.setHUD(gameHUD);
	}
	
	//untuk background
	private void createBackground(){
		setBackground(new Background(Color.BLUE));
	}
	
	//untuk manipulasi score
	private void addToScore(int i){
		score += i;
		scoreText.setText("Score : " + score);
	}
	
	//untuk World pada game
	private void createPhysics(){
		physicsWorld = new FixedStepPhysicsWorld(60, new Vector2(0, -17), false);
		physicsWorld.setContactListener(contactListener());
		registerUpdateHandler(physicsWorld);
	}
	
	//Internal Class
	//untuk handle multiple jump
	private ContactListener contactListener(){
		ContactListener contactListener = new ContactListener() {
			
			@Override
			public void beginContact(Contact contact) {
				final Fixture x1 = contact.getFixtureA();
				final Fixture x2 = contact.getFixtureB();
				
				if(x1.getBody().getUserData() != null && x2.getBody().getUserData() != null){
					if(x2.getBody().getUserData().equals("player")){
						player.increaseFootContacts();
					}
					
					if(x1.getBody().getUserData().equals("platform2") && x2.getBody().getUserData().equals("player")){
						engine.registerUpdateHandler(new TimerHandler(0.2f, new ITimerCallback() {
							
							@Override
							public void onTimePassed(TimerHandler pTimerHandler) {
								pTimerHandler.reset();
								engine.unregisterUpdateHandler(pTimerHandler);
								x1.getBody().setType(BodyType.DynamicBody);
							}
						}));
					}
					
					if(x1.getBody().getUserData().equals("platform3") && x2.getBody().getUserData().equals("player")){
						x1.getBody().setType(BodyType.DynamicBody);
					}
				}
			}
			
			@Override
			public void endContact(Contact contact) {
				final Fixture x1 = contact.getFixtureA();
				final Fixture x2 = contact.getFixtureB();
				
				if(x1.getBody().getUserData() != null && x2.getBody().getUserData() != null){
					if(x2.getBody().getUserData().equals("player")){
						player.decreaseFootContacts();
					}
				}
			}

			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {
				// TODO Auto-generated method stub
			}
			
						
		};
		return contactListener;
	}
}
