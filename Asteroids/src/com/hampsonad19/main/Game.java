package com.hampsonad19.main;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.hampsonad19.managers.GameInputProcessor;
import com.hampsonad19.managers.GameKeys;
import com.hampsonad19.managers.GameStateManager;
import com.hampsonad19.managers.Jukebox;

public class Game implements ApplicationListener {
	
	public static int WIDTH;
	public static int HEIGHT;
	
	public static OrthographicCamera cam;
	
	private GameStateManager gsm;
	
	/**
	 * Create is a one time method that is called when game is started that
	 * creates initial objects used.
	 */
	public void create() {
		WIDTH = Gdx.graphics.getWidth();
		HEIGHT = Gdx.graphics.getHeight();
		
		cam = new OrthographicCamera(WIDTH, HEIGHT);
		cam.translate(WIDTH / 2, HEIGHT / 2);
		cam.update();
		
		Gdx.input.setInputProcessor(new GameInputProcessor());
		
		Jukebox.load("sounds/explode.ogg", "explode");
		Jukebox.load("sounds/extralife.ogg", "extralife");
		Jukebox.load("sounds/largesaucer.ogg", "largesaucer");
		Jukebox.load("sounds/pulsehigh.ogg", "pulsehigh");
		Jukebox.load("sounds/pulselow.ogg", "pulselow");
		Jukebox.load("sounds/saucershoot.ogg", "saucershoot");
		Jukebox.load("sounds/shoot.ogg", "shoot");
		Jukebox.load("sounds/smallsaucer.ogg", "smallsaucer");
		Jukebox.load("sounds/thruster.ogg", "thruster");

		gsm = new GameStateManager();
	}
	
	/**
	 * Render updates game object and redraws game objects as game runs.
	 * 
	 */
	public void render() {
		// Clear screen to black
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		gsm.update(Gdx.graphics.getDeltaTime());
		gsm.draw();
		
		GameKeys.update();
	}
	
	/**
	 * Resize is used when the game window dimensions are changed
	 */
	public void resize(int width, int height) {
		
	}
	
	/**
	 * Pause is generally only used in android applications when game needs to be paused for 
	 * interruptions such as a call.
	 */
	public void pause() {}
	
	/**
	 * Resume generally only used in android applications when game needs to be resumed 
	 * after being paused
	 */
	public void resume() {}
	
	/**
	 * Dispose is used only once and is called when the application is closed
	 */
	public void dispose() {}
}
