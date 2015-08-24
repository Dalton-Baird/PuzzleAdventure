package com.dalton.puzzleadventure;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.dalton.puzzleadventure.screens.GameScreen;

/*public class PuzzleAdventure extends ApplicationAdapter
{
    SpriteBatch batch;
    Texture img;

    @Override
    public void create()
    {
        batch = new SpriteBatch();
        img = new Texture("badlogic.jpg");
    }

    @Override
    public void render()
    {
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(img, 0, 0);
        batch.end();
    }
}*/

public class PuzzleAdventure extends Game
{
    public static final String TAG = "Puzzle Adventure";
    private String levelName;

    public PuzzleAdventure(String levelName)
    {
        super();
        this.levelName = levelName;
    }

    @Override
    public void create()
    {
        Gdx.app.log(TAG, "Game started");
        AssetLoader.load();
        this.setScreen(new GameScreen(this.levelName));
    }

    @Override
    public void dispose()
    {
        super.dispose();
        AssetLoader.dispose();
    }
}
