package com.dalton.puzzleadventure;

import com.dalton.puzzleadventure.entity.Entity;

/**
 * Created by Dalton on 2/13/2015.
 */
public class FixtureSensorData
{
    public static final int SENSOR_NOTHING = 0;
    public static final int SENSOR_FEET = 1;
    public static final int SENSOR_BUTTON = 2;

    public Entity owner;
    public int sensorType;

    public FixtureSensorData(Entity owner, int sensorType)
    {
        this.owner = owner;
        this.sensorType = sensorType;
    }
}
