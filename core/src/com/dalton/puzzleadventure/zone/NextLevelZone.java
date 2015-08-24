package com.dalton.puzzleadventure.zone;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.physics.box2d.Body;
import com.dalton.puzzleadventure.GameWorld;
import com.dalton.puzzleadventure.entity.EntityPlayer;
import com.dalton.puzzleadventure.screens.GameScreen;

/**
 * Created by Dalton on 3/5/2015.
 */
public class NextLevelZone extends Zone
{
    private String nextLevel;

    public NextLevelZone(Body physbody, GameWorld world, MapObject object)
    {
        super(physbody, world);

        this.nextLevel = object.getProperties().get("level", String.class);
    }

    @Override
    public void onUpdate(GameWorld world)
    {
        super.onUpdate(world);

            for (Body body : this.bodiesInZone) //Damage everything in the zone that is damageable
            {
                Object object = body.getUserData();

                if (object instanceof EntityPlayer)
                {
                    //Gdx.app.log("NextLevelZone", "Attempting to go to next level: " + this.nextLevel);
                    this.world.endLevel(this.nextLevel);
                }
            }
    }
}
