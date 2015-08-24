package com.dalton.puzzleadventure.entity;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.dalton.puzzleadventure.GameWorld;

/**
 * Created by Dalton on 2/25/2015.
 *
 * Behaves like an AND logic gate, using world channels for IO.
 */
public class EntityLogicANDGate extends  EntityLogicBase
{
    private int inputA;
    private int inputB;
    private int output;

    public EntityLogicANDGate(GameWorld world, float x, float y, float xVel, float yVel)
    {
        super(world, x, y, xVel, yVel);
    }

    public EntityLogicANDGate(GameWorld world, RectangleMapObject mapObject)
    {
        this(world,
                mapObject.getRectangle().x/16 + 1.0F,
                mapObject.getRectangle().y/16 + 1.0F,
                0,
                0);

        this.inputA = Integer.parseInt((String) mapObject.getProperties().get("inputA")) - 1;
        this.inputB = Integer.parseInt((String) mapObject.getProperties().get("inputB")) - 1;
        this.output = Integer.parseInt((String) mapObject.getProperties().get("output")) - 1;
    }

    protected void spawnInWorld(float x, float y, float xVel, float yVel)
    {
        //Setup body definition
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x, y);

        //Setup physics body
        this.physBody = this.world.getPhysWorld().createBody(bodyDef);
        this.physBody.setUserData(this);
    }

    public void onUpdate(float delta)
    {
        world.logicChannels[this.output] = world.logicChannels[this.inputA] && world.logicChannels[this.inputB];
    }
}
