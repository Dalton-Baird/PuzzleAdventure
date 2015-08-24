package com.dalton.puzzleadventure.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.dalton.puzzleadventure.AssetLoader;
import com.dalton.puzzleadventure.GameWorld;

/**
 * Created by Dalton on 2/25/2015.
 */
public class EntityBox extends Entity
{

    public EntityBox(GameWorld world, float x, float y, float xVel, float yVel)
    {
        super(world, x, y, xVel, yVel);
        this.spawnInWorld(x, y, xVel, yVel);
    }

    public EntityBox(GameWorld world, RectangleMapObject mapObject)
    {
        this(world,
                mapObject.getRectangle().x/16 + 1.0F,
                mapObject.getRectangle().y/16 + 1.0F,
                0,
                0);
    }

    protected void spawnInWorld(float x, float y, float xVel, float yVel)
    {
        //Setup hitbox shape
        PolygonShape hitbox = new PolygonShape();
        hitbox.setAsBox(1.95F/2, 1.95F/2);

        //Setup body definition
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);

        //Setup physics body
        this.physBody = this.world.getPhysWorld().createBody(bodyDef);
        this.physBody.setUserData(this);

        //Setup physics fixture
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = hitbox;
        fixtureDef.density = 100.0F;
        fixtureDef.friction = 0.75F;
        fixtureDef.restitution = 0.0F;
        this.physBody.createFixture(fixtureDef);

        //Dispose of hitbox shape
        hitbox.dispose();
    }

    public void render(float delta, float runTime, SpriteBatch spriteBatch)
    {
        float width = 2.0F;
        float x = this.physBody.getPosition().x - width/2;
        float y = this.physBody.getPosition().y - width/2;
        float rotation = this.physBody.getAngle();

        spriteBatch.draw(AssetLoader.box, x, y, width/2, width/2, width, width, 1, 1, rotation*MathUtils.radiansToDegrees);
    }
}
