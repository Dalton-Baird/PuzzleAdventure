package com.dalton.puzzleadventure;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.dalton.puzzleadventure.entity.Entity;
import com.dalton.puzzleadventure.entity.EntityPlayer;
import com.dalton.puzzleadventure.entity.IDamageable;
import com.dalton.puzzleadventure.screens.GameScreen;
import com.dalton.puzzleadventure.zone.Zone;

import java.util.Random;

/**
 * Created by Dalton on 2/7/2015.
 */
public class GameWorld
{
    private World physWorld; //The physics world
    private GameScreen gameScreen;
    private Array<Body> physBodies;
    private TiledMap tiledMap;
    private float tiledMapUnitScale;

    private EntityPlayer player;
    private String name = "Unnamed";
    private float width = 10.0F;
    private float height = 10.0F;

    public boolean[] logicChannels = new boolean[16]; //Array of logic channels for entities to use

    private long worldTime;

    private boolean levelEnded = false;
    private String nextLevel = null;
    private boolean newHighScore = false;

    public GameWorld(GameScreen gameScreen)
    {
        this.gameScreen = gameScreen;
        this.worldTime = 0L;

        //Create new physics world, with standard Earth gravity
        this.physWorld = new World(new Vector2(0.0F, -9.8F), true);
        this.physWorld.setContactListener(new PhysicsContactListener());
        this.physBodies = new Array<Body>();
    }

    /**
     * Called to spawn the boundaries around the world.  Should only be called once, after the
     * world size variables have been set.
     */
    public void spawnWorldBoundaries()
    {
        //Set up boundaries
        EdgeShape edgeShape = new EdgeShape();

        //The fixture def
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 10000.0F;
        fixtureDef.friction = 0.4F;
        fixtureDef.restitution = 0.0F;

        //The body def
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;

        //The body
        Body body = this.physWorld.createBody(bodyDef);

        //Top part
        edgeShape.set(0, this.height, this.width, this.height);
        fixtureDef.shape = edgeShape;
        body.createFixture(fixtureDef);

        //Left part
        edgeShape.set(0, 0, 0, this.height);
        fixtureDef.shape = edgeShape;
        body.createFixture(fixtureDef);

        //Right part
        edgeShape.set(this.width, 0, this.width, this.height);
        fixtureDef.shape = edgeShape;
        body.createFixture(fixtureDef);

        //Bottom part
        edgeShape.set(0, 0, this.width, 0);
        fixtureDef.shape = edgeShape;
        fixtureDef.friction = 2.0F; //300.0F;
        body.createFixture(fixtureDef);

        edgeShape.dispose();
    }

    /**
     * Updates the state of the world.
     * @param delta The time since last execution
     */
    public void update(float delta)
    {
        if (!this.levelEnded)
        {
            this.physWorld.getBodies(this.physBodies); //Refreshes the physBodies array with the physWorld's bodies
            this.worldTime++;

            //Update entities and stuff
            for (Body body : this.physBodies)
            {
                Object object = body.getUserData();
                if (object instanceof Entity)
                    ((Entity)object).onUpdate(delta);
                else if (object instanceof Zone)
                    ((Zone)object).onUpdate(this);
            }


            this.physWorld.step(1/60.0F, 6, 2); //Calculate physics
        }
    }

    public World getPhysWorld()
    {
        return this.physWorld;
    }

    public EntityPlayer getPlayer()
    {
        return this.player;
    }

    public void setPlayer(EntityPlayer player)
    {
        this.player = player;
    }

    public Array<Body> getPhysBodies()
    {
        return this.physBodies;
    }

    public long getWorldTime()
    {
        return worldTime;
    }

    /**
     * Called by the game loop to destroy the world, disposing of anything left over.  Muhahahaha!
     */
    public void dispose()
    {
        this.physWorld.dispose();
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return this.name;
    }

    public void setWidth(float width)
    {
        this.width = width;
    }

    public float getWidth()
    {
        return this.width;
    }

    public void setHeight(float height)
    {
        this.height = height;
    }

    public float getHeight()
    {
        return this.height;
    }

    public TiledMap getTiledMap()
    {
        return tiledMap;
    }

    public void setTiledMap(TiledMap tiledMap)
    {
        this.tiledMap = tiledMap;
    }

    public float getTiledMapUnitScale()
    {
        return tiledMapUnitScale;
    }

    public void setTiledMapUnitScale(float tiledMapUnitScale)
    {
        this.tiledMapUnitScale = tiledMapUnitScale;
    }

    public GameScreen getGameScreen()
    {
        return this.gameScreen;
    }

    /**
     * Ends the level, optionally with a next level to go to.
     * @param nextLevel The name of the next level
     */
    public void endLevel(String nextLevel)
    {
        this.nextLevel = nextLevel;
        this.levelEnded = true;

        long time = Gdx.app.getPreferences("Game Progress").getLong(this.name);

        if (this.worldTime < time || time < 0) //Find out of the player beat their high score
            this.newHighScore = true;

        GameScreen.updateGameProgress(this.name, this.worldTime, this.nextLevel);
    }

    public boolean hasLevelEnded()
    {
        return this.levelEnded;
    }

    public String getNextLevel()
    {
        return this.nextLevel;
    }

    /**
     *  Call this only after the level has ended
     */
    public boolean hasPlayerGottenNewHighscore()
    {
        return this.newHighScore;
    }
}
