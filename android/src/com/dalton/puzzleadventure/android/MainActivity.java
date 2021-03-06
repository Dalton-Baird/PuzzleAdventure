package com.dalton.puzzleadventure.android;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class MainActivity extends ActionBarActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    public void startGame(View view)
    {
        //Intent intent = new Intent(this, AndroidLauncher.class);
        Intent intent = new Intent(this, SelectLevelActivity.class);
        startActivity(intent);
    }

    public void openSettings(View view)
    {
        Intent intent = new Intent(this, SettingListActivity.class);
        startActivity(intent);
    }

    public void openCredits(View view)
    {
        Intent intent = new Intent(this, CreditsActivity.class);
        startActivity(intent);
    }

    public void openInfo(View view)
    {
        Intent intent = new Intent(this, InfoActivity.class);
        startActivity(intent);
    }
}
