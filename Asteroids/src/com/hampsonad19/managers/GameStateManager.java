package com.hampsonad19.managers;

import com.hampsonad19.gamestates.GameOverState;
import com.hampsonad19.gamestates.GameState;
import com.hampsonad19.gamestates.HighScoreState;
import com.hampsonad19.gamestates.MenuState;
import com.hampsonad19.gamestates.PlayState;

public class GameStateManager {

	private GameState gameState;
	
	public static final int MENU = 0;
	public static final int PLAY = 1;
	public static final int HIGHSCORE = 2;
	public static final int GAMEOVER = 3;
	
	
	public GameStateManager() {
		setState(MENU);
		Save.load();
	}
	
	public void setState(int state) {
		if(gameState != null) {
			gameState.dispose();
		}
		
		if(state == MENU) {
			gameState = new MenuState(this);
		}
		if(state == HIGHSCORE) {
			gameState = new HighScoreState(this);
		}
		if(state == PLAY) {
			gameState = new PlayState(this);
		}
		if(state == GAMEOVER) {
			gameState = new GameOverState(this);
		}
	}
	
	public void update(float dt) {
		gameState.update(dt);
	}
	
	public void draw() {
		gameState.draw();
	}
}
