package com.dalton.puzzleadventure;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.dalton.puzzleadventure.entity.Entity;
import com.dalton.puzzleadventure.entity.EntityBolder;
import com.dalton.puzzleadventure.entity.EntityBox;
import com.dalton.puzzleadventure.entity.EntityButton;
import com.dalton.puzzleadventure.entity.EntityLiving;
import com.dalton.puzzleadventure.entity.EntityPlayer;
import com.dalton.puzzleadventure.entity.IDamageable;
import com.dalton.puzzleadventure.zone.Zone;

import java.util.Arrays;

/**
 * Created by Dalton on 2/13/2015.
 */
public class PhysicsContactListener implements ContactListener
{
    @Override
    public void beginContact(Contact contact)
    {
        Fixture contactA = contact.getFixtureA();
        Fixture contactB = contact.getFixtureB();

        //Handle contact if it contains my data object
        if (contactA.getUserData() instanceof FixtureSensorData)
            this.handleFixtureSensorData(contactA, contactB, 0);
        if (contactB.getUserData() instanceof FixtureSensorData)
            this.handleFixtureSensorData(contactB, contactA, 0);
        if (contactA.getBody() != null && contactA.getBody().getUserData() instanceof Zone)
            ((Zone) contactA.getBody().getUserData()).handleCollide(contactA, contactB, 0);
        if (contactB.getBody() != null && contactB.getBody().getUserData() instanceof Zone)
            ((Zone) contactB.getBody().getUserData()).handleCollide(contactB, contactA, 0);
    }

    @Override
    public void endContact(Contact contact)
    {
        Fixture contactA = contact.getFixtureA();
        Fixture contactB = contact.getFixtureB();

        //Handle contact if it contains my data object
        if (contactA.getUserData() instanceof FixtureSensorData)
            this.handleFixtureSensorData(contactA, contactB, 1);
        if (contactB.getUserData() instanceof FixtureSensorData)
            this.handleFixtureSensorData(contactB, contactA, 1);
        if (contactA.getBody() != null && contactA.getBody().getUserData() instanceof Zone)
            ((Zone) contactA.getBody().getUserData()).handleCollide(contactA, contactB, 1);
        if (contactB.getBody() != null && contactB.getBody().getUserData() instanceof Zone)
            ((Zone) contactB.getBody().getUserData()).handleCollide(contactB, contactA, 1);
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold)
    {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse)
    {
        Fixture contactA = contact.getFixtureA();
        Fixture contactB = contact.getFixtureB();
        Object a = contactA.getBody().getUserData();
        Object b = contactB.getBody().getUserData();

        //Add the two forces with the distance formula
        float temp = (float) Math.pow(Math.abs(impulse.getNormalImpulses()[0]), 2);
        temp += Math.pow(Math.abs(impulse.getNormalImpulses()[1]), 2);
        float force = (float) Math.sqrt(temp);

        //Check if something damageable is being collided with
        if (a instanceof IDamageable)// || b instanceof IDamageable)
        {
            Entity collider = null;
            if (b instanceof Entity)
                collider = (Entity)b;
            ((IDamageable)a).dealImpactDamage(force, collider, contact);

            //Gdx.app.log("PhysicsContactListener", "Contact between " + a.getClass().getSimpleName() + " and " + b.getClass().getSimpleName());
            //Gdx.app.log("PhysicsContactListener", "Impulses: " + Arrays.toString(impulse.getNormalImpulses()));
        }
        else if (b instanceof IDamageable)
        {
            Entity collider = null;
            if (a instanceof Entity)
                collider = (Entity)a;
            ((IDamageable)b).dealImpactDamage(force, collider, contact);
        }
    }

    /**
     * Handles contacts containing my sensor data.
     * @param contactA The colliding fixture
     * @param contactB The fixture being collided with
     * @param type The type of contact.  0 for begin contact, 1 for end contact.
     */
    private void handleFixtureSensorData(Fixture contactA, Fixture contactB, int type)
    {
        FixtureSensorData sensorData = (FixtureSensorData) contactA.getUserData();

        if (sensorData.sensorType == FixtureSensorData.SENSOR_FEET && sensorData.owner instanceof EntityLiving) //Handle jumping code
        {
            if (contactB.getBody() != null && contactB.getBody().getUserData() != sensorData.owner)
            {
                //Add or subtract from how many things are being stood on
                if (type == 0)
                {
                    ((EntityLiving)sensorData.owner).numberThingsStoodOn++;
                    //((EntityLiving)sensorData.owner).setHasJumped(false);
                }
                else if (type == 1 && ((EntityLiving)sensorData.owner).numberThingsStoodOn > 0)
                    ((EntityLiving)sensorData.owner).numberThingsStoodOn--;
                //Gdx.app.log("PHYSICS", sensorData.owner + ": Feet sensor detected ground! Ground entity: " + contactB.getBody().getUserData());
            }
        }
        else if (sensorData.sensorType == FixtureSensorData.SENSOR_BUTTON) //Handle buttons
        {
            if (sensorData.owner instanceof EntityButton)
            {
                if (contactB.getBody() != null && contactB.getBody().getUserData() != sensorData.owner)
                {
                    if (type == 0)
                    {
                        ((EntityButton)sensorData.owner).numberCollidingThings++;
                    }
                    else if (type == 1 && ((EntityButton)sensorData.owner).numberCollidingThings > 0)
                        ((EntityButton)sensorData.owner).numberCollidingThings--;
                }
            }
        }
    }
}
