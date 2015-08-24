package com.dalton.puzzleadventure.android.adapters;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;

import com.dalton.puzzleadventure.android.listeners.SelectLevelListener;
import com.dalton.puzzleadventure.screens.GameScreen;

import java.util.Comparator;
import java.util.List;

/**
 * Created by Dalton on 3/9/2015.
 */
public class LevelButtonAdapter extends BaseAdapter
{
    private Context mContext;
    private List<LevelRecord> buttonLabels;
    private View.OnClickListener onClickListener;

    public LevelButtonAdapter(Context context, List<LevelRecord> buttonLabels)
    {
        this.mContext = context;
        this.buttonLabels = buttonLabels;
        this.onClickListener = new SelectLevelListener((Activity)this.mContext);
    }

    @Override
    public int getCount()
    {
        return this.buttonLabels.size();
    }

    @Override
    public Object getItem(int position)
    {
        return this.buttonLabels.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Button button;
        int buttonSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, this.mContext.getResources().getDisplayMetrics());
        int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, this.mContext.getResources().getDisplayMetrics());

        if (convertView == null) //If it's not recycled, initialize some attributes
        {
            button = new Button(this.mContext);
            button.setLayoutParams(new GridView.LayoutParams(buttonSize, buttonSize));
            button.setOnClickListener(this.onClickListener);
        }
        else
        {
            button = (Button) convertView;
        }

        String levelName = this.buttonLabels.get(position).levelName;
        long time = this.buttonLabels.get(position).levelTime;
        String levelTime;

        if (time >= 0)
            levelTime = GameScreen.getTimeFromTicks(time, true);
        else
            levelTime = "No Time";

        button.setTextSize(15);
        button.setText(Html.fromHtml(levelName + "<br><small>" + levelTime + "</small>"));

        return button;
    }

    /**
     * Static inner class to represent a level record
     */
    public static class LevelRecord implements Comparable<LevelRecord>
    {
        public String levelName;
        public long levelTime;

        public LevelRecord(String levelName, long levelTime)
        {
            this.levelName = levelName;
            this.levelTime = levelTime;
        }

        @Override
        public int compareTo(LevelRecord another)
        {
            return levelName.compareTo(another.levelName);
        }
    }
}
