package com.dalton.puzzleadventure;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.dalton.puzzleadventure.entity.Entity;
import com.dalton.puzzleadventure.entity.EntityPlayer;
import com.dalton.puzzleadventure.screens.GameScreen;
import com.dalton.puzzleadventure.util.MapBodyManager;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Dalton on 2/23/2015.
 *
 * Loads levels from TMX Tiled level editor files.
 */
public class AdvLevelLoader
{
    private String levelName;
    private FileHandle materialsFile;
    private HashMap<Integer, Class<? extends Entity>> entityTileIDs = new HashMap<Integer, Class<? extends Entity>>();

    public AdvLevelLoader(String levelName)
    {
        this.levelName = levelName;
        this.materialsFile = Gdx.files.internal("data/materials.json");
    }

    /**
     * Parses the level file and constructs a game world from it, and returns it.  Throws an
     * exception if loading failed.
     */
    public GameWorld loadWorld(GameScreen gameScreen)
    {
        Gdx.app.log("AdvLevelLoader", "Loading level \"" + this.levelName + "\"");

        MapBodyManager mapBodyManager;
        GameWorld newWorld = new GameWorld(gameScreen); //Make the new world

        //Make the map body manager that reads the map file
        float unitScale = 16.0F;
        mapBodyManager = new MapBodyManager(newWorld.getPhysWorld(), unitScale, this.materialsFile, 2);
        TiledMap map = new TmxMapLoader().load(this.levelName + ".tmx");
        mapBodyManager.createPhysics(map, "physics"); //Create physics boundaries from map

        MapProperties properties = map.getProperties();
        int tilePixelWidth = properties.get("tilewidth", Integer.class);
        int tilePixelHeight = properties.get("tileheight", Integer.class);

        //Load world properties
        newWorld.setName(this.levelName);
        newWorld.setWidth(properties.get("width", Integer.class) * 2);
        newWorld.setHeight(properties.get("height", Integer.class) * 2);
        newWorld.spawnWorldBoundaries(); //Spawn the world boundaries now that the world has a width and height
        newWorld.setTiledMap(map);
        newWorld.setTiledMapUnitScale(unitScale);

        //Load Entity Tile Data
        Gdx.app.log("AdvLevelLoader", "Parsing entity tile data");
        TiledMapTileSet entityTileSet = map.getTileSets().getTileSet("Entities");
        Iterator<TiledMapTile> entityTileIterator = entityTileSet.iterator();

        while (entityTileIterator.hasNext()) //Iterate through entity tiles
        {
            TiledMapTile tile = entityTileIterator.next();
            String type = tile.getProperties().get("type", String.class);

            if (type == null) //Only check tiles that have a type set
                continue;

            //See if there is an entity class for a given name
            Class<? extends Entity> entityClass = GameScreen.getEntityFromName(type);

            //If there is, register the ID
            if (entityClass != null)
                this.entityTileIDs.put(tile.getId(), entityClass);

            Gdx.app.log("AdvLevelLoader", "Tile ID: " + tile.getId() + " type: " + type);
        }

        //Load world entities
        Gdx.app.log("AdvLevelLoader", "Parsing world entities");
        MapLayer entityLayer = map.getLayers().get("entities");
        if (entityLayer == null)
            throw new NullPointerException("entities layer is null");

        MapObjects entityObjects = entityLayer.getObjects();
        Iterator<MapObject> entObjIterator = entityObjects.iterator();

        while (entObjIterator.hasNext())
        {
            MapObject entityObject = entObjIterator.next();

            if (entityObject instanceof RectangleMapObject)
            {
                int gid = ((RectangleMapObject)entityObject).getProperties().get("gid", Integer.class);
                Class<? extends Entity> entityClass = this.entityTileIDs.get(gid);

                if (entityClass != null)
                {
                    Gdx.app.log("AdvLevelLoader", "Attempting to spawn " + entityClass.getSimpleName());

                    try
                    {
                        Constructor<? extends Entity> constructor = entityClass.getConstructor(GameWorld.class, RectangleMapObject.class);
                        Entity entity = constructor.newInstance(newWorld, entityObject);

                        if (entity instanceof EntityPlayer) //Set the player if the entity is the player
                            newWorld.setPlayer((EntityPlayer) entity);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }

        //Load damage zones
        Gdx.app.log("AdvLevelLoader", "Parsing zones");
        mapBodyManager.createZones(map, "zones", newWorld);

        return newWorld;
    }
}
