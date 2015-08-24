package com.dalton.puzzleadventure.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;
import com.dalton.puzzleadventure.GameWorld;

/**
 * Created by Dalton on 2/10/2015.
 *
 * Represents an in game entity.  This entity could be static, dynamic, or kinematic, depending on
 * how it implements the spawnInWorld() method.  The entity should attach itself to a body in the
 * physics world to be kept within the game.
 */
public class Entity
{
    protected GameWorld world; //The world the entity is in
    protected Body physBody = null; //The entity's body in the physics world

    /**
     * Constructs a new entity, and spawns it in the world.  Note: y coordinates increase in the
     * down direction.
     * @param world  The GameWorld that the entity will spawn in.
     * @param x  The X coordinate of the entity
     * @param y  The Y coordinate of the entity
     * @param xVel  The X velocity of the entity, in m/s
     * @param yVel  The Y velocity of the entity, in m/s
     */
    public Entity(GameWorld world, float x, float y, float xVel, float yVel)
    {
        this.world = world;
        //this.spawnInWorld(x, y, xVel, yVel); //Call this manually
    }

    /**
     * Constructs a new entity from a tiled map object.
     * @param world
     * @param mapObject
     */
    public Entity(GameWorld world, RectangleMapObject mapObject) {}

    /**
     * Called to spawn the entity in the world.  The entity should create its physics body in the
     * world, and attach itself to it.  This is called by the constructor.
     */
    protected void spawnInWorld(float x, float y, float xVel, float yVel) {}

    /**
     * Updates the entity's logic.  Gets called on every tick by the world.
     * @param delta  The time in milliseconds since last update
     */
    public void onUpdate(float delta)
    {

    }

    /**
     * Called to render the entity.
     */
    public void render(float delta, float runTime, SpriteBatch spriteBatch) {}

    /**
     * Removes the entity from the world.
     */
    public void delete()
    {
        this.world.getPhysWorld().destroyBody(this.physBody);
        this.physBody.setUserData(null);
        /*
        World physWorld = this.world.getPhysWorld();
        Array<Body> physBodies = this.world.getPhysBodies();

        for (Body body : physBodies)
            if (body != null && body.getUserData() == this)
                physWorld.destroyBody(body);
        */
    }

    /**
     * @return The entity's physics body
     */
    public Body getPhysBody()
    {
        return this.physBody;
    }
}
