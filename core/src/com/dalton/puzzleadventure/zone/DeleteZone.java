package com.dalton.puzzleadventure.zone;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.dalton.puzzleadventure.GameWorld;
import com.dalton.puzzleadventure.entity.Entity;
import com.dalton.puzzleadventure.entity.EntityLiving;
import com.dalton.puzzleadventure.entity.IDamageable;

/**
 * Created by Dalton on 3/10/2015.
 *
 * Deletes dynamic bodies in the zone that are not EntityLiving
 */
public class DeleteZone extends Zone
{
    public DeleteZone(Body physbody, GameWorld world, MapObject object)
    {
        super(physbody, world);
    }

    @Override
    public void onUpdate(GameWorld world)
    {
        super.onUpdate(world);

        for (Body body : this.bodiesInZone) //Deletes everything in the zone that is damageable and dynamic
        {
            Object object = body.getUserData();

            if (body.getType() == BodyDef.BodyType.DynamicBody) //Only delete dynamic bodies
                if (!(object instanceof EntityLiving)) //Don't delete EntityLiving's
                    ((Entity)object).delete();
        }
    }
}
