package com.dalton.puzzleadventure;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;


/**
 * Created by Dalton on 2/11/2015.
 */
public class AssetLoader
{
    public static BitmapFont fontMain; //The primary game font
    public static BitmapFont fontMainLarge;
    public static BitmapFont fontDebug; //The debug font

    public static Texture mainTextures;
    public static Texture skyTexture;
    public static Texture mountainsTexture;
    public static Texture playerTextures;
    public static Texture entityTextures;

    public static TextureRegion[] playerWalkingRightFrames;
    public static TextureRegion[] playerWalkingLeftFrames;
    public static TextureRegion[] wallDoorFrames;
    public static TextureRegion box;
    public static TextureRegion buttonOff;
    public static TextureRegion buttonOn;
    public static TextureRegion door;
    public static TextureRegion bolder;

    public static Animation playerWalkingRightAnimation;
    public static Animation playerWalkingLeftAnimation;
    //public static Animation wallDoorOpenAnimation;
    //public static Animation wallDoorCloseAnimation;

    public static AssetManager assetManager;

    /**
     * Loads the assets into memory.
     */
    public static void load()
    {
        fontMain = new BitmapFont(Gdx.files.internal("data/GameFont.fnt"));
        fontMain.setScale(2.0F);

        fontMainLarge = new BitmapFont(Gdx.files.internal("data/GameFont.fnt"));
        fontMainLarge.setScale(4.0F);

        fontDebug = new BitmapFont(Gdx.files.internal("data/DebugFont.fnt"));
        fontDebug.setScale(0.5F);

        //Load the textures
        mainTextures = new Texture(Gdx.files.internal("textures/MainTextures.png"));
        mainTextures.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        skyTexture = new Texture(Gdx.files.internal("textures/sky.png"));
        skyTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        skyTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        mountainsTexture = new Texture(Gdx.files.internal("textures/mountains.png"));
        mountainsTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        mountainsTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        playerTextures = new Texture(Gdx.files.internal("textures/PlayerFrames.png"));
        playerTextures.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        entityTextures = new Texture(Gdx.files.internal("textures/Entities.png"));
        entityTextures.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        //Define Texture Regions
        playerWalkingRightFrames = new TextureRegion[8];
        for (int i = 0; i < playerWalkingRightFrames.length; i++)
            playerWalkingRightFrames[i] = new TextureRegion(playerTextures, i * 32, 0, 32, 32);

        playerWalkingLeftFrames = new TextureRegion[8];
        for (int i = 0; i < playerWalkingLeftFrames.length; i++)
            playerWalkingLeftFrames[i] = new TextureRegion(playerTextures, i * 32, 32, 32, 32);

        wallDoorFrames = new TextureRegion[4];
        for (int i = 0; i< wallDoorFrames.length; i++)
            wallDoorFrames[i] = new TextureRegion(entityTextures, 384 + i*32, 0, 32, 32);

        door = new TextureRegion(entityTextures, 393, 0, 14, 32);
        box = new TextureRegion(entityTextures, 32, 0, 32, 32);
        buttonOff = new TextureRegion(entityTextures, 0, 32, 32, 32);
        buttonOn = new TextureRegion(entityTextures, 32, 32, 32, 32);
        bolder = new TextureRegion(entityTextures, 64, 0, 32, 32);

        //Define Animations
        playerWalkingRightAnimation = new Animation(0.1F, playerWalkingRightFrames);
        playerWalkingRightAnimation.setPlayMode(Animation.PlayMode.LOOP);

        playerWalkingLeftAnimation = new Animation(0.1F, playerWalkingLeftFrames);
        playerWalkingLeftAnimation.setPlayMode(Animation.PlayMode.LOOP);

        //wallDoorOpenAnimation = new Animation(0.1F, wallDoorFrames);
        //wallDoorOpenAnimation.setPlayMode(Animation.PlayMode.LOOP);

        //wallDoorCloseAnimation = new Animation(0.1F, wallDoorFrames);
        //wallDoorCloseAnimation.setPlayMode(Animation.PlayMode.LOOP_REVERSED);

        //assetManager = new AssetManager();
        //assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));

        //assetManager.load("DebugLevel.tmx", TiledMap.class);

        //assetManager.finishLoading();
    }

    /**
     * Disposes the assets.
     */
    public static void dispose()
    {
        fontMain.dispose();
    }
}
