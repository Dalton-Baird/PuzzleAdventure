package com.dalton.puzzleadventure.entity;

import com.badlogic.gdx.physics.box2d.Contact;

/**
 * Created by Dalton on 3/2/2015.
 *
 * Implement this to have an entity be damageable by outside forces, such as damage zones,
 * projectiles, etc.
 */
public interface IDamageable
{
    /**
     * Called by something that wants to hurt this entity.  That something can be null.
     *
     * @param attacker The attacking entity
     * @param damage The amount of damage from the attack
     * @return true if the damage was accepted, false if it was ignored
     */
    public boolean onHurt(Entity attacker, int damage);

    /**
     * Called by the physics contact listener to potentially deal damage to the entity.
     * @param newtons The amount of force of the collision
     * @param collider The entity being collided with.  This can be null if it is the ground or something that isn't an entity
     * @param  contact The physics contact
     * @return true if damage was accepted
     */
    public boolean dealImpactDamage(float newtons, Entity collider, Contact contact);
}
