package com.dalton.puzzleadventure.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.dalton.puzzleadventure.AssetLoader;
import com.dalton.puzzleadventure.GameWorld;

/**
 * Created by Dalton on 2/25/2015.
 */
public class EntityDoor extends Entity
{
    private enum DoorState
    {
        OPEN, CLOSED, OPENING, CLOSING
    }

    private int inputChannel;
    private DoorState doorState = DoorState.CLOSED;
    private Vector2 origin;

    public EntityDoor(GameWorld world, float x, float y, float xVel, float yVel)
    {
        super(world, x, y, xVel, yVel);

        this.spawnInWorld(x, y, xVel, yVel);
        this.origin = new Vector2(x, y);
    }

    public EntityDoor(GameWorld world, RectangleMapObject mapObject)
    {
        this(world,
                mapObject.getRectangle().x/16 + 1.0F,
                mapObject.getRectangle().y/16 + 1.0F,
                0,
                0);

        this.inputChannel = Integer.parseInt((String) mapObject.getProperties().get("channel")) - 1;
    }

    protected void spawnInWorld(float x, float y, float xVel, float yVel)
    {
        //Setup hitbox shape
        PolygonShape hitbox = new PolygonShape();
        hitbox.setAsBox(0.9F/2, 2.0F/2, new Vector2(0, 0.0F), 0);

        //Setup body definition
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        bodyDef.position.set(x, y);

        //Setup physics body
        this.physBody = this.world.getPhysWorld().createBody(bodyDef);
        this.physBody.setUserData(this);

        //Setup physics fixture
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = hitbox;
        fixtureDef.density = 100.0F;
        fixtureDef.friction = 0.75F;
        fixtureDef.restitution = 0.1F;
        this.physBody.createFixture(fixtureDef);

        //Dispose of hitbox shape
        hitbox.dispose();
    }

    public void onUpdate(float delta)
    {
        super.onUpdate(delta);

        //Handle logic
        boolean shouldBeOpen = world.logicChannels[this.inputChannel];

        if (shouldBeOpen && (this.doorState == DoorState.CLOSED || this.doorState == DoorState.CLOSING))
        {
            this.doorState = DoorState.OPENING;
            this.physBody.setLinearVelocity(0, -1);
        }
        else if (!shouldBeOpen && (this.doorState == DoorState.OPEN || this.doorState == DoorState.OPENING))
        {
            this.doorState = DoorState.CLOSING;
            this.physBody.setLinearVelocity(0, 1);
        }

        switch (this.doorState)
        {
        case CLOSED:
            this.physBody.setLinearVelocity(0, 0);
            break;
        case OPEN:
            this.physBody.setLinearVelocity(0, 0);
            break;
        case CLOSING:
            if (this.physBody.getPosition().y >= (this.origin.y))
                this.doorState = DoorState.CLOSED;
            break;
        case OPENING:
            if (this.physBody.getPosition().y <= (this.origin.y - 2))
                this.doorState = DoorState.OPEN;
            break;
        }
    }

    public void render(float delta, float runTime, SpriteBatch spriteBatch)
    {
        int freezeFrame = 0;
        boolean shouldAnimate = false;
        float width = 2.0F;
        float height = 2.0F;
        float x = this.physBody.getPosition().x - width/2;
        float y = this.physBody.getPosition().y - height/2;

        if (this.doorState == DoorState.OPENING)
            freezeFrame = 4;

        //Draw the door frame
        spriteBatch.draw(AssetLoader.wallDoorFrames[3], this.origin.x - width/2, this.origin.y - height/2, width, height);
        //Draw the door
        x = this.physBody.getPosition().x;
        y = this.physBody.getPosition().y;
        float doorWidth = width*0.4375F;
        float difference = y - this.origin.y;
        spriteBatch.draw(AssetLoader.door, x - doorWidth/2, y - height/2 - difference, doorWidth, height + difference);
    }
}
