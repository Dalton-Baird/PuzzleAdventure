package com.dalton.puzzleadventure.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Contact;
import com.dalton.puzzleadventure.GameWorld;

/**
 * Created by Dalton on 2/11/2015.
 *
 * This type of entity represents a living entity that has health.  It has methods for handling
 * changes in health.
 */
public class EntityLiving extends Entity implements IDamageable
{
    protected int health = 1; //Stores the entity's health
    protected  int maxHealth = 1; //Stores the entity's max health
    public int numberThingsStoodOn = 0;
    protected short jumpCoolDown = 0; //The amount of ticks until the entity can jump
    protected float friction = 0.75F;
    protected boolean hitRecently = false;

    public EntityLiving(GameWorld world, float x, float y, float xVel, float yVel)
    {
        super(world, x, y, xVel, yVel);
    }

    @Override
    public void onUpdate(float delta)
    {
        super.onUpdate(delta);

        if (this.world.getWorldTime() % 10 == 0) //Only allow impact damage every 10 ticks
            this.hitRecently = false;

        if (this.health < 1)
            this.onDeath();
        if (this.jumpCoolDown > 0)
            this.jumpCoolDown--;
    }

    /**
     * Called when the entity's health is no longer larger than 0.
     */
    public void onDeath()
    {
        this.delete();
    }

    @Override
    public boolean onHurt(Entity attacker, int damage)
    {
        this.health -= Math.min(damage, this.health); //Don't deal more damage than the entity can take
        return true;
    }

    @Override
    public boolean dealImpactDamage(float newtons, Entity collider, Contact contact)
    {
        if (this.hitRecently)
        {
            //Gdx.app.log("EntityLiving", "Already hit this tick, ignoring...");
            return false;
        }

        int damage = 0;

        if (newtons > 2000)
        {
            damage = (int)(newtons/500.0F);
        }

        /*int damage = 0;

        if (collider != null)
        {
            if (newtons > 3000)
                damage = (int)(newtons/1000.0F);
        }
        else
        {
            float velocityA = contact.getFixtureA().getBody().getLinearVelocity().len();
            float velocityB = contact.getFixtureA().getBody().getLinearVelocity().len();
            float highestVelocity = Math.max(velocityA, velocityB);

            if (highestVelocity > 5.0F)
                Gdx.app.log("EntityLiving", "Highest Velocity: " + highestVelocity);

            if (highestVelocity > 10.0F)
                damage = (int)(highestVelocity/2.0F);
        }
        */
        /*else //Colliding wtih the world deals more damage (fall damage) //TODO: Handle collisions with the world based on velocity instead
        {
            if (newtons > 2000)
                damage = (int)(newtons/500.0F);
        }*/

        //Gdx.app.log("EntityLiving", this.getClass().getSimpleName() +  ".dealImpactDamage(" + newtons + ", " +
        //        (collider == null ? "null" : collider.getClass().getSimpleName()) + ") Damage: " + damage);

        if (damage > 0)
        {
            this.onHurt(collider, damage);
            this.hitRecently = true;

            return true;
        }

        return false;
    }

    /**
     * Gets the entity's jump cool down.
     */
    public short getJumpCoolDown()
    {
        return this.jumpCoolDown;
    }

    /**
     * Returns true if the entity can jump.
     */
    public boolean canJump()
    {
        return this.jumpCoolDown < 1 && this.numberThingsStoodOn > 0;
    }

    public int getHealth()
    {
        return this.health;
    }

    public int getMaxHealth()
    {
        return this.maxHealth;
    }
}
