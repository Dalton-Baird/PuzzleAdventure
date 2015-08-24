package com.dalton.puzzleadventure.android;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import com.dalton.puzzleadventure.android.listeners.ResetProgressDialogListener;


/**
 * An activity representing a list of Settings. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link SettingDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p/>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link SettingListFragment} and the item details
 * (if present) is a {@link SettingDetailFragment}.
 * <p/>
 * This activity also implements the required
 * {@link SettingListFragment.Callbacks} interface
 * to listen for item selections.
 */
public class SettingListActivity extends ActionBarActivity implements SettingListFragment.Callbacks
{

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_list);
        // Show the Up button in the action bar.
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (this.getActionBar() != null)
            this.getActionBar().setDisplayHomeAsUpEnabled(true);

        if (findViewById(R.id.setting_detail_container) != null)
        {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            ((SettingListFragment) getSupportFragmentManager().findFragmentById(R.id.setting_list)).setActivateOnItemClick(true);
        }

        // TODO: If exposing deep links into your app, handle intents here.
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if (id == android.R.id.home)
        {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Callback method from {@link SettingListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(String id)
    {
        saveSettings(this);

        if (mTwoPane)
        {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(SettingDetailFragment.ARG_ITEM_ID, id);
            SettingDetailFragment fragment = new SettingDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction().replace(R.id.setting_detail_container, fragment).commit();

        } else
        {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, SettingDetailActivity.class);
            detailIntent.putExtra(SettingDetailFragment.ARG_ITEM_ID, id);
            startActivity(detailIntent);
        }
    }

    @Override
    public void onStop()
    {
        super.onStop();
        saveSettings(this);
    }

    /**
     * Static method to save the settings.  Can be called from other activities.
     */
    public static void saveSettings(Activity activity)
    {
        Log.i("SAVE SETTINGS", "Saving settings...");

        SharedPreferences preferences;
        SharedPreferences.Editor editor;

        preferences = activity.getSharedPreferences("Game Settings", 0);
        editor = preferences.edit();

        Switch theSwitch;

        theSwitch = (Switch) activity.findViewById(R.id.box2d_debug_graphics);
        if (theSwitch != null)
            editor.putBoolean("Box2DDebugGraphics", theSwitch.isChecked());

        theSwitch = (Switch) activity.findViewById(R.id.debug_overlay);
        if (theSwitch != null)
            editor.putBoolean("DebugOverlay", theSwitch.isChecked());

        theSwitch = (Switch) activity.findViewById(R.id.audio_sound_enabled);
        if (theSwitch != null)
            editor.putBoolean("SoundEnabled", theSwitch.isChecked());

        theSwitch = (Switch) activity.findViewById(R.id.audio_music_enabled);
        if (theSwitch != null)
            editor.putBoolean("MusicEnabled", theSwitch.isChecked());

        editor.apply();
    }

    public void resetData(View view)
    {
        resetData(view, this);
    }

    /**
     * Deletes all game progress TODO: Confirmation!
     */
    public static void resetData(View view, Activity activity)
    {
        if (activity != null)
        {
            boolean confirmed = false;

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
            dialogBuilder.setTitle(R.string.confirm_data_reset_title);
            dialogBuilder.setMessage(R.string.confirm_data_reset);
            dialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
            dialogBuilder.setPositiveButton(R.string.yes, new ResetProgressDialogListener(activity));
            dialogBuilder.setNegativeButton(R.string.no, null);
            dialogBuilder.show();
        }
    }
}
