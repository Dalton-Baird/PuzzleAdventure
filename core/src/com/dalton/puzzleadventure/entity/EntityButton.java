package com.dalton.puzzleadventure.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.dalton.puzzleadventure.AssetLoader;
import com.dalton.puzzleadventure.FixtureSensorData;
import com.dalton.puzzleadventure.GameWorld;

/**
 * Created by Dalton on 2/25/2015.
 *
 * Represents a floor button
 */
public class EntityButton extends Entity
{
    private static final Vector2[] vertices = new Vector2[4];

    static
    {
        vertices[0] = new Vector2(-1, -1);
        vertices[1] = new Vector2(1, -1);
        vertices[2] = new Vector2(-0.7F, -0.8F);
        vertices[3] = new Vector2(0.7F, -0.8F);
    }

    private int outputChannel;
    private boolean activated = false;
    public int numberCollidingThings = 0;

    public EntityButton(GameWorld world, float x, float y, float xVel, float yVel)
    {
        super(world, x, y, xVel, yVel);
        this.spawnInWorld(x, y, xVel, yVel);
    }

    public EntityButton(GameWorld world, RectangleMapObject mapObject)
    {
        this(world,
                mapObject.getRectangle().x/16 + 1.0F,
                mapObject.getRectangle().y/16 + 1.0F,
                0,
                0);

        this.outputChannel = Integer.parseInt((String) mapObject.getProperties().get("channel")) - 1;


    }

    protected void spawnInWorld(float x, float y, float xVel, float yVel)
    {
        //Setup hitbox shape
        PolygonShape hitbox = new PolygonShape();
        hitbox.set(vertices);

        //Setup body definition
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
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

        //Make the button sensor
        hitbox.setAsBox(1.4F/2, 0.2F/2, new Vector2(0.0F, -0.7F), 0);
        fixtureDef = new FixtureDef();
        fixtureDef.shape = hitbox;
        fixtureDef.isSensor = true;
        Fixture sensor = this.physBody.createFixture(fixtureDef);
        sensor.setUserData(new FixtureSensorData(this, FixtureSensorData.SENSOR_BUTTON));

        //Dispose of hitbox shape
        hitbox.dispose();
    }

    public void onUpdate(float delta)
    {
        super.onUpdate(delta);

        //Handle logic
        this.activated = this.numberCollidingThings > 0;
        this.world.logicChannels[this.outputChannel] = this.activated;

        //Gdx.app.log("EntityButton", this + " Activated: " + this.activated + " Channel:" + this.outputChannel);
    }

    public void render(float delta, float runTime, SpriteBatch spriteBatch)
    {
        float width = 2.0F;
        float x = this.physBody.getPosition().x - width/2;
        float y = this.physBody.getPosition().y - width/2;

        spriteBatch.draw((this.activated ? AssetLoader.buttonOn : AssetLoader.buttonOff), x, y, width, width);
    }
}
