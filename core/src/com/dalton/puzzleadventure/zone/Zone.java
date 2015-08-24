package com.dalton.puzzleadventure.zone;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Array;
import com.dalton.puzzleadventure.GameWorld;

/**
 * Created by Dalton on 3/3/2015.
 *
 * Represents a zone in the world.  Entities cannot collide with this, but collision events are
 * created.
 */
public class Zone
{
    protected Body physbody;
    protected Array<Body> bodiesInZone;
    protected GameWorld world;

    public Zone(Body physbody, GameWorld world)
    {
        this.physbody = physbody;
        this.world = world;
        this.bodiesInZone = new Array<Body>();
        this.physbody.setUserData(this);
    }

    public Zone(Body physbody, GameWorld world, MapObject object)
    {
        this(physbody, world);
    }

    /**
     * Handles contacts between two fixtures.
     * @param me This zone's fixture
     * @param other The other fixture
     * @param type The type of contact.  0 = begin, 1 = end
     */
    public void handleCollide(Fixture me, Fixture other, int type)
    {
        //Gdx.app.log("Zone", "Something is colliding with/leaving me! " + type);

        Body body = other.getBody();

        if (type == 0)
        {
            //Gdx.app.log("Zone", "Collided with " + body.getUserData());

            if (!this.bodiesInZone.contains(body, true))
                this.bodiesInZone.add(body);
        }
        else if (type == 1)
        {
            if (this.bodiesInZone.contains(body, true))
                this.bodiesInZone.removeValue(body, true);
        }
    }

    /**
     * Called every tick to update the zone's logic
     * @param world The game world
     */
    public void onUpdate(GameWorld world)
    {

    }

}
