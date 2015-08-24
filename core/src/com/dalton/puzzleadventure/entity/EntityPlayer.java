package com.dalton.puzzleadventure.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.XmlReader;
import com.dalton.puzzleadventure.AssetLoader;
import com.dalton.puzzleadventure.FixtureSensorData;
import com.dalton.puzzleadventure.GameWorld;

/**
 * Created by Dalton on 2/11/2015.
 *
 * This is the player in the game.  The user can control the player with UI buttons.
 */
public class EntityPlayer extends EntityLiving
{
    public static final float MOVE_SPEED_X = 4.0F; //m/s
    public static final float MOVE_SPEED_Y = 20.0F; //m/s
    public static final float JUMP_FORCE = 1550.0F; //newtons

    //Animation
    public Animation currentAnimation = AssetLoader.playerWalkingRightAnimation;

    public EntityPlayer(GameWorld world, float x, float y, float xVel, float yVel)
    {
        super(world, x, y, xVel, yVel);
        this.friction = 0.85F;
        this.maxHealth = 20;
        this.health = this.maxHealth;
        this.spawnInWorld(x, y, xVel, yVel);
    }

    public EntityPlayer(GameWorld world, RectangleMapObject mapObject)
    {
        this(world,
                mapObject.getRectangle().x/16 + 1.0F,
                mapObject.getRectangle().y/16 + 1.0F,
                0,
                0);
    }

    @Override
    protected void spawnInWorld(float x, float y, float xVel, float yVel)
    {
        //Setup hitbox shape
        PolygonShape hitbox = new PolygonShape();
        hitbox.setAsBox(1.0F/2, 1.6F/2, new Vector2(0, 0.1F), 0);

        //Setup body definition
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);
        bodyDef.fixedRotation = true;

        //Setup physics body
        this.physBody = this.world.getPhysWorld().createBody(bodyDef);
        this.physBody.setUserData(this); //Store this object into the body so that it isn't lost

        //Setup physics fixture
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = hitbox;
        fixtureDef.density = 100.0F; //About 1 g/cm^2, which is the density of water, which is roughly the density of humans
        fixtureDef.friction = 0.0F; //The feet will handle friction with the ground //this.friction;
        fixtureDef.restitution = 0.0F; //The player has legs.  They don't bounce unless they land on something bouncy
        this.physBody.createFixture(fixtureDef);

        //Apply impulse
        this.physBody.applyLinearImpulse(xVel, yVel, 0, 0, true);

        //Make the feet
        hitbox.setAsBox(0.7F/2, 0.1F/2, new Vector2(0.0F, -1.6F/2), 0);
        fixtureDef = new FixtureDef();
        fixtureDef.shape = hitbox;
        fixtureDef.density = 100.0F;
        fixtureDef.friction = this.friction;
        fixtureDef.restitution = 0.0F;
        this.physBody.createFixture(fixtureDef);

        //Make the foot sensor
        hitbox.setAsBox(0.8F/2, 0.2F/2, new Vector2(0.0F, -1.8F/2), 0);
        fixtureDef = new FixtureDef();
        fixtureDef.shape = hitbox;
        fixtureDef.isSensor = true;
        Fixture feet = this.physBody.createFixture(fixtureDef);
        feet.setUserData(new FixtureSensorData(this, FixtureSensorData.SENSOR_FEET));

        //Dispose of the hitbox shape, which is no longer needed
        hitbox.dispose();
    }

    @Override
    public void onUpdate(float delta)
    {
        super.onUpdate(delta);
        //if (this.world.getWorldTime() % 20 == 0 && this.health > 0) //TO DO remove this!
        //    this.health--;
    }

    /**
     * Makes the player jump, if he can
     */
    public void jump()
    {
        if (this.numberThingsStoodOn > 0 && this.jumpCoolDown < 1)
        {
            this.getPhysBody().applyLinearImpulse(0.0F, JUMP_FORCE, 0, 0, true);
            this.jumpCoolDown = 20;
        }
    }

    @Override
    public void render(float delta, float runTime, SpriteBatch spriteBatch)
    {
        Vector2 velocity = this.physBody.getLinearVelocity();
        boolean shouldAnimate = false;
        int freezeFrame = 0; //Default "stand" frame

        if (Math.abs(velocity.x) > 1) //If the player is moving horizontally
        {
            if (this.numberThingsStoodOn > 0) //If the player is not in the air, play the walking animation
                shouldAnimate = true;

            if (velocity.x > 0) //Decide which animation to use
                this.currentAnimation = AssetLoader.playerWalkingRightAnimation;
            else
                this.currentAnimation = AssetLoader.playerWalkingLeftAnimation;
        }

        if (this.numberThingsStoodOn == 0) //Draw the "leap" frame
            freezeFrame = 5;

        float width = 1.0F;
        float x = this.physBody.getPosition().x - width;
        float y = this.physBody.getPosition().y - width + 0.1F;

        spriteBatch.draw(this.currentAnimation.getKeyFrame(shouldAnimate ? runTime : freezeFrame), x, y, width*2, width*2);
    }
}
