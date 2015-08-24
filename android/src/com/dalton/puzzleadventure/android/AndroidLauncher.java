package com.dalton.puzzleadventure.android;

import android.content.Intent;
import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.dalton.puzzleadventure.PuzzleAdventure;

public class AndroidLauncher extends AndroidApplication
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

        config.useWakelock = true;

        //Get the level the game should load from the intent
        Intent intent = getIntent();
        String levelName = intent.getStringExtra("LevelName");

        //The level loader will crash if you give it null, if it's null, change it to Level 1
        if (levelName == null)
            levelName = "Level 1";

        initialize(new PuzzleAdventure(levelName), config);
    }
}
