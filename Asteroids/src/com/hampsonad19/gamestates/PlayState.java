package com.hampsonad19.gamestates;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.hampsonad19.entities.Asteroid;
import com.hampsonad19.entities.Bullet;
import com.hampsonad19.entities.Particle;
import com.hampsonad19.entities.Player;
import com.hampsonad19.main.Game;
import com.hampsonad19.managers.GameKeys;
import com.hampsonad19.managers.GameStateManager;
import com.hampsonad19.managers.Jukebox;
import com.hampsonad19.managers.Save;

public class PlayState extends GameState {

	private SpriteBatch sb;
	private ShapeRenderer sr;

	private BitmapFont font;
	private Player hudPlayer;

	private Player player;
	private ArrayList<Bullet> bullets;
	private ArrayList<Asteroid> asteroids;

	private ArrayList<Particle> particles;

	private int level;
	private int totalAsteroids;
	private int numAsteroidsLeft;

	private float maxDelay;
	private float minDelay;
	private float currentDelay;
	private float bgTimer;
	private boolean playLowPulse;

	public PlayState(GameStateManager gsm) {
		super(gsm);
	}

	public void init() {
		sb = new SpriteBatch();

		sr = new ShapeRenderer();

		// Set font
		FreeTypeFontGenerator gen = new FreeTypeFontGenerator(
				Gdx.files.internal("fonts/Hyperspace Bold.ttf"));
		font = gen.generateFont(20);

		bullets = new ArrayList<Bullet>();

		player = new Player(bullets);

		asteroids = new ArrayList<Asteroid>();

		particles = new ArrayList<Particle>();

		level = 1;
		spawnAsteroids();

		hudPlayer = new Player(null);

		// Set up background music
		maxDelay = 1;
		minDelay = 0.25f;
		currentDelay = maxDelay;
		bgTimer = maxDelay;
		playLowPulse = true;
	}

	private void createParticles(float x, float y) {
		for (int i = 0; i < 6; i++) {
			particles.add(new Particle(x, y));
		}
	}

	private void splitAsteroids(Asteroid a) {
		createParticles(a.getx(), a.gety());
		numAsteroidsLeft--;
		currentDelay = ((maxDelay - minDelay) * numAsteroidsLeft
				/ totalAsteroids + minDelay);
		if (a.getType() == Asteroid.LARGE) {
			asteroids.add(new Asteroid(a.getx(), a.gety(), Asteroid.MEDIUM));
			asteroids.add(new Asteroid(a.getx(), a.gety(), Asteroid.MEDIUM));
		}
		if (a.getType() == Asteroid.MEDIUM) {
			asteroids.add(new Asteroid(a.getx(), a.gety(), Asteroid.SMALL));
			asteroids.add(new Asteroid(a.getx(), a.gety(), Asteroid.SMALL));
		}
	}

	public void spawnAsteroids() {
		asteroids.clear();
		int numToSpawn = 4 + level * 1;
		totalAsteroids = numToSpawn * 7;
		numAsteroidsLeft = totalAsteroids;
		currentDelay = maxDelay;

		for (int i = 0; i < numToSpawn; i++) {
			float x = MathUtils.random(Game.WIDTH);
			float y = MathUtils.random(Game.HEIGHT);

			float dx = x - player.getx();
			float dy = y - player.gety();
			float dist = (float) Math.sqrt(dx * dx + dy * dy);

			while (dist < 100) {
				x = MathUtils.random(Game.WIDTH);
				y = MathUtils.random(Game.HEIGHT);
				dx = x - player.getx();
				dy = y - player.gety();
				dist = (float) Math.sqrt(dx * dx + dy * dy);
			}

			asteroids.add(new Asteroid(x, y, Asteroid.LARGE));
		}

	}

	public void update(float dt) {

		// Get user input
		handleInput();

		// Generate next level
		if (asteroids.size() == 0) {
			level++;
			spawnAsteroids();
		}

		// Update player
		player.update(dt);
		if (player.isDead()) {
			if(player.getLives() == 0) {
				Jukebox.stopAll();
				Save.gd.setTenativeScore(player.getScore());
				gsm.setState(GameStateManager.GAMEOVER);
				return;
			}
			player.reset();
			player.loseLife();
			return;
		}

		// Update player bullets
		for (int i = 0; i < bullets.size(); i++) {
			bullets.get(i).update(dt);
			if (bullets.get(i).shouldRemove()) {
				bullets.remove(i);
				i--;
			}
		}

		// Update asteroids
		for (int i = 0; i < asteroids.size(); i++) {
			asteroids.get(i).update(dt);
			if (asteroids.get(i).shouldRemove()) {
				asteroids.remove(i);
				i--;
			}
		}

		// Update particles
		for (int i = 0; i < particles.size(); i++) {
			particles.get(i).update(dt);
			if (particles.get(i).shouldRemove()) {
				particles.remove(i);
				i--;
			}
		}

		// Check collision
		checkCollisions();

		// Play background music
		bgTimer += dt;
		if (!player.isHit() && bgTimer >= currentDelay) {
			if (playLowPulse) {
				Jukebox.play("pulselow");
			} else {
				Jukebox.play("pulsehigh");
			}
			playLowPulse = !playLowPulse;
			bgTimer = 0;
		}
	}

	/**
	 * Check collisions checks for collision between the player ship and other
	 * units and also ship bullets and other units.
	 * 
	 * If the collision is between a player and asteroid, the hit method will be
	 * called and the asteroid will be split.
	 * 
	 * If the collision is between a bullet and asteroid, the bullet will be
	 * removed from the screen and the asteroid will be split.
	 * 
	 */
	private void checkCollisions() {
		// Player-asteroid collision
		if (!player.isHit()) {
			for (int i = 0; i < asteroids.size(); i++) {
				Asteroid a = asteroids.get(i);
				if (a.intersects(player)) {
					player.hit();
					asteroids.remove(i);
					i--;
					splitAsteroids(a);
					Jukebox.play("explode");
					break;
				}
			}
		}

		// Bullet-asteroid collision
		for (int i = 0; i < bullets.size(); i++) {
			Bullet b = bullets.get(i);
			for (int j = 0; j < asteroids.size(); j++) {
				Asteroid a = asteroids.get(j);
				if (a.contains(b.getx(), b.gety())) {
					bullets.remove(i);
					i--;
					asteroids.remove(j);
					j--;
					splitAsteroids(a);
					// Increment score
					player.incrementScore(a.getScore());
					Jukebox.play("explode");
					break;
				}
			}
		}
	}

	public void draw() {
		
		sb.setProjectionMatrix(Game.cam.combined);
		sr.setProjectionMatrix(Game.cam.combined);
		
		// Draw player
		player.draw(sr);

		// Draw bullets
		for (int i = 0; i < bullets.size(); i++) {
			bullets.get(i).draw(sr);
		}

		// Draw asteroids
		for (int i = 0; i < asteroids.size(); i++) {
			asteroids.get(i).draw(sr);
		}

		// Draw particles
		for (int i = 0; i < particles.size(); i++) {
			particles.get(i).draw(sr);
		}

		// Draw score on screen
		sb.setColor(1, 1, 1, 1);
		sb.begin();
		font.draw(sb, Long.toString(player.getScore()), 30, 370);
		sb.end();

		// Draw lives below score
		for (int i = 0; i < player.getLives(); i++) {
			hudPlayer.setPosition(30 + i * 10, 340);
			hudPlayer.draw(sr);
		}
	}

	public void handleInput() {
		player.setLeft(GameKeys.isDown(GameKeys.LEFT));
		player.setRight(GameKeys.isDown(GameKeys.RIGHT));
		player.setUp(GameKeys.isDown(GameKeys.UP));

		if (GameKeys.isPressed(GameKeys.SPACE)) {
			player.shoot();
		}
		

		if (GameKeys.isPressed(GameKeys.ESCAPE)) {
			gsm.setState(GameStateManager.MENU);
		}
	}


	public void dispose() {
	}

}
