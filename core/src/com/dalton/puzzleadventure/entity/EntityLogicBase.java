package com.dalton.puzzleadventure.entity;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.dalton.puzzleadventure.GameWorld;

/**
 * Created by Dalton on 2/25/2015.
 *
 * Represents an game logic entity.
 */
public class EntityLogicBase extends Entity
{

    public EntityLogicBase(GameWorld world, float x, float y, float xVel, float yVel)
    {
        super(world, x, y, xVel, yVel);

        this.spawnInWorld(x, y, xVel, yVel);
    }

    public EntityLogicBase(GameWorld world, RectangleMapObject mapObject)
    {
        super(world, mapObject);
    }

    protected void spawnInWorld(float x, float y, float xVel, float yVel)
    {

    }

    public void onUpdate(float delta)
    {

    }
}
