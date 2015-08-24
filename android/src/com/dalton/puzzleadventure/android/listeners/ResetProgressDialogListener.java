package com.dalton.puzzleadventure.android.listeners;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by Dalton on 3/13/2015.
 */
public class ResetProgressDialogListener implements DialogInterface.OnClickListener
{
    private Activity activity;

    public ResetProgressDialogListener(Activity activity)
    {
        this.activity = activity;
    }

    @Override
    public void onClick(DialogInterface dialog, int which)
    {
        if (which == -1) //The Yes button is -1 for some reason
        {
            Log.i("ResetProgress", "Resetting progress!");

            SharedPreferences preferences = activity.getSharedPreferences("Game Progress", 0);
            SharedPreferences.Editor editor = preferences.edit();

            editor.clear();
            editor.apply();
        }
    }
}
