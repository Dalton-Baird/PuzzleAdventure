package com.dalton.puzzleadventure.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.utils.Array;
import com.dalton.puzzleadventure.GameWorld;
import com.dalton.puzzleadventure.screens.GameScreen;

import java.lang.reflect.Constructor;
import java.util.Random;

/**
 * Created by Dalton on 3/10/2015.
 */
public class EntitySpawner extends Entity
{
    private Class<? extends Entity> spawnEntityClass;
    private int spawnDelay;
    private boolean hasSpawnedEntity; //Used to only spawn entities when the logic signal goes from 0 to 1
    private int maxSpawns = 10; //Maximum number of entities spawned by the spawner
    private int channel = -1;
    private Array<Entity> spawnedEntities;
    private int randomTimeOffset;
    private Vector2 randomVelocity; //Random force applied to the entity when spawning, in a random direction

    public EntitySpawner(GameWorld world, float x, float y, float xVel, float yVel)
    {
        super(world, x, y, xVel, yVel);
        this.spawnInWorld(x, y, xVel, yVel);
    }

    public EntitySpawner(GameWorld world, RectangleMapObject mapObject)
    {
        this(world,
                mapObject.getRectangle().x/16 + 1.0F,
                mapObject.getRectangle().y/16 + 1.0F,
                0,
                0);

        this.spawnEntityClass = GameScreen.getEntityFromName((String) mapObject.getProperties().get("entity"));
        this.spawnDelay = Integer.parseInt((String) mapObject.getProperties().get("spawnDelay"));

        Random random = new Random();
        this.randomTimeOffset = random.nextInt(this.spawnDelay);

        if (mapObject.getProperties().containsKey("maxSpawns"))
            this.maxSpawns = Integer.parseInt((String) mapObject.getProperties().get("maxSpawns"));
        if (mapObject.getProperties().containsKey("channel"))
            this.channel = Integer.parseInt((String) mapObject.getProperties().get("channel")) - 1;
        if (mapObject.getProperties().containsKey("randomVelocity"))
            this.randomVelocity = new Vector2(Float.parseFloat((String) mapObject.getProperties().get("randomVelocity")), 0);

        this.hasSpawnedEntity = false;

        this.spawnedEntities = new Array<Entity>();
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
        if ((this.world.getWorldTime() + this.randomTimeOffset) % this.spawnDelay == 0)
            this.spawnEntity();

        //Channel logic force spawning
        if (this.channel >=0) //Make sure that we don't cause ArrayIndexOutOfBoundsExceptions when the channel isn't set (it would be -1)
        {
            if (this.world.logicChannels[this.channel] == true && this.hasSpawnedEntity == false)
            {
                this.spawnEntity();
                this.hasSpawnedEntity = true;
            }
            else if (this.world.logicChannels[this.channel] == false)
            {
                this.hasSpawnedEntity = false;
            }
        }

        //Remove deleted entities
        for (Entity entity : this.spawnedEntities)
            if (entity == null || entity.getPhysBody() == null || entity.getPhysBody().getUserData() == null)
                this.spawnedEntities.removeValue(entity, true);
    }

    /**
     * Spawns an entity.
     */
    public void spawnEntity()
    {
        if (this.spawnedEntities.size >= this.maxSpawns) //Cancel the spawn if the spawn list is full
            return;

        try
        {
            Constructor<? extends Entity> constructor = this.spawnEntityClass.getConstructor(GameWorld.class, float.class, float.class, float.class, float.class);
            Entity entity = constructor.newInstance(this.world, this.physBody.getPosition().x, this.physBody.getPosition().y, 0, 0); //Create a new instance of the entity, it will add itself to the world
            this.spawnedEntities.add(entity);

            if (this.randomVelocity != null)
            {
                Random random = new Random();
                this.randomVelocity.rotate(random.nextInt(360)); //Randomly rotate the velocity vector
                entity.getPhysBody().setLinearVelocity(this.randomVelocity);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
