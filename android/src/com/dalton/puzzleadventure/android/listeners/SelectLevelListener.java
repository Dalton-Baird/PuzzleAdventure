package com.dalton.puzzleadventure.android.listeners;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

import com.dalton.puzzleadventure.android.AndroidLauncher;

/**
 * Created by Dalton on 3/9/2015.
 */
public class SelectLevelListener implements View.OnClickListener
{
    private Activity activity;

    public SelectLevelListener(Activity activity)
    {
        this.activity = activity;
    }

    @Override
    public void onClick(View v)
    {
        String buttonText = ((Button)v).getText().toString();
        int newLineIndex = buttonText.indexOf('\n');
        String levelName = buttonText.substring(0, newLineIndex);

        Intent intent = new Intent(this.activity, AndroidLauncher.class);
        intent.putExtra("LevelName", levelName);
        this.activity.startActivity(intent);
    }
}
