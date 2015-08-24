package com.dalton.puzzleadventure.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.dalton.puzzleadventure.AdvLevelLoader;
import com.dalton.puzzleadventure.GameRenderer;
import com.dalton.puzzleadventure.GameWorld;
import com.dalton.puzzleadventure.InputHandler;
import com.dalton.puzzleadventure.entity.Entity;
import com.dalton.puzzleadventure.entity.EntityBolder;
import com.dalton.puzzleadventure.entity.EntityBox;
import com.dalton.puzzleadventure.entity.EntityButton;
import com.dalton.puzzleadventure.entity.EntityDoor;
import com.dalton.puzzleadventure.entity.EntityLogicANDGate;
import com.dalton.puzzleadventure.entity.EntityLogicBase;
import com.dalton.puzzleadventure.entity.EntityPlayer;
import com.dalton.puzzleadventure.entity.EntitySpawner;
import com.dalton.puzzleadventure.zone.DamageZone;
import com.dalton.puzzleadventure.zone.DeleteZone;
import com.dalton.puzzleadventure.zone.NextLevelZone;
import com.dalton.puzzleadventure.zone.Zone;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Dalton on 2/6/2015.
 */
public class GameScreen implements Screen
{
    public static final String TAG = "Game Screen";
    private float runTime = 0.0F;

    //The hashmap to store registered entities and zones
    private static HashMap<String, Class<? extends Entity>> entityMap = new HashMap<String, Class<? extends Entity>>();
    private static HashMap<String, Class<? extends Zone>> zoneMap = new HashMap<String, Class<? extends Zone>>();

    private GameWorld world;
    private GameRenderer renderer;
    private InputHandler inputHandler;

    //Settings
    public static boolean drawBox2DDebugGraphics;
    public static boolean drawDebugOverlay;
    public static boolean playSoundEffects;
    public static boolean playMusic;

    //Register game objects
    static
    {
        //Register entities
        registerEntity("EntityPlayer", EntityPlayer.class);
        registerEntity("EntityBox", EntityBox.class);
        registerEntity("EntityButton", EntityButton.class);
        registerEntity("EntityLogicANDGate", EntityLogicANDGate.class);
        registerEntity("EntityDoor", EntityDoor.class);
        registerEntity("EntityBolder", EntityBolder.class);
        registerEntity("EntitySpawner", EntitySpawner.class);

        //Register zones
        registerZone("Damage", DamageZone.class);
        registerZone("NextLevel", NextLevelZone.class);
        registerZone("Delete", DeleteZone.class);
    }

    public GameScreen(String levelName)
    {
        Gdx.app.log(TAG, "Game screen created");

        //Init physics
        Box2D.init();

        //Create game world and renderer
        AdvLevelLoader levelLoader = new AdvLevelLoader(levelName);

        this.world = levelLoader.loadWorld(this);
        this.renderer = new GameRenderer(this.world, this);

        //Create input handler
        Gdx.input.setInputProcessor(this.inputHandler = new InputHandler(this.world, this.renderer));

        //Settings
        Preferences preferences = Gdx.app.getPreferences("Game Settings");
        drawBox2DDebugGraphics = preferences.getBoolean("Box2DDebugGraphics", false);
        drawDebugOverlay = preferences.getBoolean("DebugOverlay", false);
        playSoundEffects = preferences.getBoolean("SoundEnabled", true);
        playMusic = preferences.getBoolean("MusicEnabled", true);

        Gdx.app.log("GameScreen", "Loading settings:");
        Map settings = preferences.get();
        for (Object key : settings.keySet())
            Gdx.app.log("GameScreen", "Key: " + key + " Value: " + settings.get(key));

        Gdx.app.log("GameScreen", "Loading game progress:");
        preferences = Gdx.app.getPreferences("Game Progress");
        settings = preferences.get();
        for (Object key : settings.keySet())
            Gdx.app.log("GameScreen", "Key: " + key + " Value: " + settings.get(key));
    }

    /**
     * Called to start a new world
     */
    public void startNewWorld(String name)
    {
        Gdx.app.log("GameScreen", "Starting new world " + name);
        this.world.dispose();
        this.world = null;
        this.renderer.dispose();
        this.renderer = null;

        AdvLevelLoader levelLoader = new AdvLevelLoader(name);

        this.world = levelLoader.loadWorld(this);
        this.renderer = new GameRenderer(this.world, this);
        Gdx.input.setInputProcessor(this.inputHandler = new InputHandler(this.world, this.renderer));
    }

    /**
     * Called to update the saved progress.
     * @param completedLevelName
     * @param time
     * @param nextLevelName
     */
    public static void updateGameProgress(String completedLevelName, long time, String nextLevelName)
    {
        Preferences preferences = Gdx.app.getPreferences("Game Progress");

        long previousTime = preferences.getLong(completedLevelName);

        if (time < previousTime || previousTime < 0) //Only update the time if it's better, or hasn't been set yet
            preferences.putLong(completedLevelName, time);

        if (!(preferences.getLong(nextLevelName) > 0)) //If there isn't already a completion status for this level, make it available
            preferences.putLong(nextLevelName, -1);

        preferences.flush();
    }

    @Override
    public void show()
    {
        Gdx.app.log(TAG, "show() called");
    }

    /**
     * Used as the main game loop.  Updates logic and draws to the screen.
     */
    @Override
    public void render(float delta)
    {
        this.runTime += delta; //TODO Is this needed? I don't think so

        //Handle repeating touch events
        Array<Vector2> touches = this.inputHandler.getTouches();

        for (int i=0; i<20; i++)
        {
            int touchX = (int)touches.get(i).x;
            int touchY = (int)touches.get(i).y;
            if (touchX >= 0 && touchY >= 0)
                this.inputHandler.touched(touchX, touchY, i, 0);
        }

        this.world.update(delta);
        this.renderer.render(delta);

        //Gdx.app.log(TAG, "FPS: " + (1/delta));
    }

    @Override
    public void resize(int width, int height)
    {
        Gdx.app.log(TAG, "resize(" + width + ", " + height + ") called");
    }

    @Override
    public void pause()
    {
        Gdx.app.log(TAG, "pause() called");
    }

    @Override
    public void resume()
    {
        Gdx.app.log(TAG, "resume() called");
    }

    @Override
    public void hide()
    {
        Gdx.app.log(TAG, "hide() called");
    }

    @Override
    public void dispose()
    {
        Gdx.app.log(TAG, "dispose() called");
        this.world.dispose();
        this.renderer.dispose();
    }

    /**
     * Registers the entity with the game.  The entity must have a "codename" to be represented in
     * XML, regardless if it is able to be loaded from XML or not.
     *
     * @param name The name of the entity, in valid XML attribute format
     * @param entity The entity class being registered
     */
    public static void registerEntity(String name, Class<? extends Entity> entity)
    {
        entityMap.put(name, entity);
    }

    /**
     * Gets the Entity class registered for the given name, or null if none were found.
     *
     * @param name The entity name to search for
     * @return The entity class, or null
     */
    public static Class<? extends Entity> getEntityFromName(String name)
    {
        return entityMap.get(name);
    }

    /**
     * Registers the zone with the game.  The zone must have a "codename" to be represented in
     * XML, regardless if it is able to be loaded from XML or not.
     *
     * @param name The name of the zone, in valid XML attribute format
     * @param zone The zone class being registered
     */
    public static void registerZone(String name, Class<? extends Zone> zone)
    {
        zoneMap.put(name, zone);
    }

    /**
     * Gets the Zone class registered for the given name, or null if none were found.
     *
     * @param name The zone name to search for
     * @return The zone class, or null
     */
    public static Class<? extends Zone> getZoneFromName(String name)
    {
        return zoneMap.get(name);
    }

    /**
     * Gets a string representation of the time in ticks
     * @param ticks The amount of ticks
     * @param shortFormat If it should be displayed as Xm Ys instead of X minutes, Y seconds
     * @return The string representation of the time
     */
    public static String getTimeFromTicks(long ticks, boolean shortFormat)
    {
        long seconds = ticks / 60;
        long minutes = seconds / 60;
        String result = "";

        if (minutes > 0)
        {
            if (shortFormat)
            {
                result += minutes + "m";
                result += " ";
            }
            else
            {
                result += minutes + " minute" + (minutes == 1 ? "" : "s");
                result += ", ";
            }
        }

        long minuteSeconds = seconds - minutes*60;

        if (shortFormat)
            result += minuteSeconds + "s";
        else
            result += minuteSeconds + " second" + (minuteSeconds == 1 ? "" : "s");

        return result;
    }
}
