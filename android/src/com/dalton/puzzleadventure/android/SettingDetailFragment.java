package com.dalton.puzzleadventure.android;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;


import com.dalton.puzzleadventure.android.settings.SettingsChoices;

/**
 * A fragment representing a single Setting detail screen.
 * This fragment is either contained in a {@link SettingListActivity}
 * in two-pane mode (on tablets) or a {@link SettingDetailActivity}
 * on handsets.
 */
public class SettingDetailFragment extends Fragment
{
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private SettingsChoices.SettingItem mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SettingDetailFragment()
    {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID))
        {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = SettingsChoices.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        /*View rootView = inflater.inflate(R.layout.fragment_setting_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (mItem != null)
        {
            ((TextView) rootView.findViewById(R.id.setting_detail)).setText(mItem.content);
        }

        return rootView;
        */

        View rootView = null;

        //Decide which settings fragment to use

        if (this.mItem.id.equals("1")) //Game Settings
        {
            rootView = inflater.inflate(R.layout.fragment_game_settings_detail, container, false);
        }
        else if (this.mItem.id.equals("2")) //Audio Settings
        {
            rootView = inflater.inflate(R.layout.fragment_audio_settings_detail, container, false);
        }
        else if (this.mItem.id.equals("3")) //Dev Settings
        {
            rootView = inflater.inflate(R.layout.fragment_dev_settings_detail, container, false);
        }

        //Load settings
        Activity activity = this.getActivity();
        if (activity != null)
        {
            SharedPreferences preferences;
            preferences = activity.getSharedPreferences("Game Settings", 0);

            if (this.mItem.id.equals("1")) //Game Settings
            {

            }
            else if (this.mItem.id.equals("2")) //Audio Settings
            {
                Switch theSwitch;

                theSwitch = (Switch) rootView.findViewById(R.id.audio_sound_enabled);
                theSwitch.setChecked(preferences.getBoolean("SoundEnabled", true));

                theSwitch = (Switch) rootView.findViewById(R.id.audio_music_enabled);
                theSwitch.setChecked(preferences.getBoolean("MusicEnabled", true));
            }
            else if (this.mItem.id.equals("3")) //Dev Settings
            {
                Switch theSwitch;

                theSwitch = (Switch) rootView.findViewById(R.id.box2d_debug_graphics);
                theSwitch.setChecked(preferences.getBoolean("Box2DDebugGraphics", false));

                theSwitch = (Switch) rootView.findViewById(R.id.debug_overlay);
                theSwitch.setChecked(preferences.getBoolean("DebugOverlay", false));
            }
        }

        return rootView;
    }
}
