package com.dalton.puzzleadventure.android;

import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.GridView;
import com.dalton.puzzleadventure.android.adapters.LevelButtonAdapter;
import com.dalton.puzzleadventure.android.listeners.SelectLevelListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class SelectLevelActivity extends ActionBarActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_level);

        //Make the button onClick listener
        View.OnClickListener onClickListener = new SelectLevelListener(this);

        SharedPreferences preferences;
        preferences = this.getSharedPreferences("Game Progress", 0);

        Map<String, Long> levelRecords = (Map<String, Long>) preferences.getAll();

        ArrayList<LevelButtonAdapter.LevelRecord> unlockedLevels = new ArrayList<LevelButtonAdapter.LevelRecord>();

        //Add all of the unlocked levels to the list (those that have a non-negative completion time)
        for (String key : levelRecords.keySet())
        {
            long value = levelRecords.get(key);

            Log.d("SelectLevelActivity", "Unlocked Level: " + key + " Best Time: " + value);

            //if (value >= 0)
            unlockedLevels.add(new LevelButtonAdapter.LevelRecord(key, value));
        }

        //Add level 1 to the list so that the player can begin there
        boolean hasLevel_1 = false;

        for (LevelButtonAdapter.LevelRecord levelRecord : unlockedLevels)
        {
            if (levelRecord.levelName.equals("Level 1"))
            {
                hasLevel_1 = true;
                break;
            }
        }

        if (!hasLevel_1)
        {
            unlockedLevels.add(new LevelButtonAdapter.LevelRecord("Level 1", -1L)); //Adds Level 1 to the list

            //Makes an entry for level 1 so this doesn't happen again (unless the preferences get reset)
            SharedPreferences.Editor editor = preferences.edit();
            editor.putLong("Level 1", -1L);
            editor.apply();
        }

        Collections.sort(unlockedLevels); //Sorts the levels so that they aren't random from the set

        GridView gridView = (GridView) this.findViewById(R.id.LevelSelectGrid);
        gridView.setAdapter(new LevelButtonAdapter(this, unlockedLevels));

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_select_level, menu);
        return true;
    }
}
