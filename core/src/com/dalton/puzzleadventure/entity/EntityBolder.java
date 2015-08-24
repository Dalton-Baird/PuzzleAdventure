package com.dalton.puzzleadventure.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.dalton.puzzleadventure.AssetLoader;
import com.dalton.puzzleadventure.GameWorld;

/**
 * Created by Dalton on 2/25/2015.
 */
public class EntityBolder extends Entity
{

    public EntityBolder(GameWorld world, float x, float y, float xVel, float yVel)
    {
        super(world, x, y, xVel, yVel);
        this.spawnInWorld(x, y, xVel, yVel);
    }

    public EntityBolder(GameWorld world, RectangleMapObject mapObject)
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
        //PolygonShape hitbox = new PolygonShape();
        /*final float[] vertices = {
                0.0F, 0.0F,
                -0.226F, 0.547F,
                0.023F, 0.180F,
                0.734F, 1.617F,
                1.344F, 1.359F,
                1.508F, 1.109F,
                1.523F, 0.328F,
                1.148F, -0.031F,
                0.586F, -0.125F};

        hitbox.set(vertices);
        */
        //hitbox.setAsBox(1.95F/2, 1.95F/2);
        CircleShape hitbox = new CircleShape();
        hitbox.setRadius(0.9F);

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
        fixtureDef.density = 500.0F;
        fixtureDef.friction = 0.75F;
        fixtureDef.restitution = 0.05F;
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

        spriteBatch.draw(AssetLoader.bolder, x, y, width/2, width/2, width, width, 1, 1, rotation*MathUtils.radiansToDegrees);
    }
}
