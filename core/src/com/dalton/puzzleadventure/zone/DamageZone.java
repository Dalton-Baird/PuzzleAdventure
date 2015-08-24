package com.dalton.puzzleadventure.zone;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.physics.box2d.Body;
import com.dalton.puzzleadventure.GameWorld;
import com.dalton.puzzleadventure.entity.IDamageable;

/**
 * Created by Dalton on 3/3/2015.
 *
 * Damages entities within the zone that implement IDamageable.  Deals 1 point of damage every x ticks.
 */
public class DamageZone extends Zone
{
    private int damageDelay;

    public DamageZone(Body physbody, GameWorld world, MapObject object)
    {
        super(physbody, world);

        int damageDelay = 0;
        String damageProperty = object.getProperties().get("damageDelay", String.class);

        try
        {
            damageDelay = Integer.parseInt(damageProperty);
        }
        catch (NumberFormatException e)
        {
            Gdx.app.log("DamageZone", e.getMessage() + "\nInvalid damage zone, damageDelay not a parseable integer");
        }

        if (damageDelay == 0)
            throw new IllegalArgumentException("damageDelay must be greater than 0");
        this.damageDelay = damageDelay;
    }

    @Override
    public void onUpdate(GameWorld world)
    {
        super.onUpdate(world);

        if (world.getWorldTime() % this.damageDelay == 0)
            for (Body body : this.bodiesInZone) //Damage everything in the zone that is damageable
            {
                Object object = body.getUserData();

                if (object instanceof IDamageable)
                    ((IDamageable)object).onHurt(null, 1);
            }
    }
}
