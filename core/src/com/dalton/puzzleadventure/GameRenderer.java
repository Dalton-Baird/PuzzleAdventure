package com.dalton.puzzleadventure;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.utils.Array;
import com.dalton.puzzleadventure.entity.Entity;
import com.dalton.puzzleadventure.entity.EntityPlayer;
import com.dalton.puzzleadventure.screens.GameScreen;
import com.dalton.puzzleadventure.ui.ButtonState;
import com.dalton.puzzleadventure.ui.UIButton;
import com.dalton.puzzleadventure.ui.UISlider;

/**
 * Created by Dalton on 2/7/2015.
 */
public class GameRenderer
{
    private float runTime = 0.0F;

    private GameWorld world; //Reference to the game world to render
    private GameScreen gameScreen;
    private OrthographicCamera camera; //The camera
    private ShapeRenderer shapeRenderer;
    private ShapeRenderer hudShapeRenderer;
    private SpriteBatch backgroundSpriteBatch;
    private SpriteBatch spriteBatch;
    private SpriteBatch hudSpriteBatch;

    Box2DDebugRenderer debugRenderer; //Debug renderer
    OrthogonalTiledMapRenderer tiledMapRenderer;

    public Array<UIButton> uiButtons;
    public UISlider movementSlider;
    public Array<UIButton> deadScreenButtons;
    public Array<UIButton> levelCompleteButtons;

    private float worldBottomUIOffset = 4.0F;
    private  boolean playerDead = false;

    //Coordinates of the player, or the last known coordinates (like if they died or something)
    private float playerX = 0;
    private float playerY = 0;

    public GameRenderer(GameWorld world, GameScreen gameScreen)
    {
        this.world = world;
        this.gameScreen = gameScreen;
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, 40.0F, 22.5F);
        this.camera.translate(0, 0 - this.worldBottomUIOffset); //Translates camera to have the UI be below the world

        this.shapeRenderer = new ShapeRenderer();
        this.hudShapeRenderer = new ShapeRenderer();
        this.backgroundSpriteBatch = new SpriteBatch();
        this.spriteBatch = new SpriteBatch();
        this.hudSpriteBatch = new SpriteBatch();


        //Gdx.app.log("GameRenderer", "Default Projection Matrix: " + this.spriteBatch.getProjectionMatrix());
        this.spriteBatch.setProjectionMatrix(this.camera.combined);
        this.shapeRenderer.setProjectionMatrix(this.camera.combined);

        //Debug renderer
        this.debugRenderer = new Box2DDebugRenderer();
        //this.debugRenderer.setDrawVelocities(true);

        //Tiled renderer
        this.tiledMapRenderer = new OrthogonalTiledMapRenderer(world.getTiledMap(), 1/(world.getTiledMapUnitScale()));

        this.uiButtons = new Array<UIButton>();

        this.uiButtons.add(new UIButton(0, 0, 0.15F, 0.15F, "Jump")
        {
            @Override
            public void onClick(GameWorld world)
            {
                super.onClick(world);
                world.getPlayer().jump(); //Make the player jump
            }

            @Override
            public void onUpdate(GameWorld world)
            {
                super.onUpdate(world);

                if (world.getPlayer().canJump())
                    this.setDisabled(false);
                else
                    this.setDisabled(true);
            }
        });

        this.uiButtons.add(new UIButton(0, Gdx.graphics.getHeight() * 0.90F, 0.15F, 0.10F, "Quit")
        {
            private int duration = 0;

            @Override
            public void onClick(GameWorld world)
            {
                super.onClick(world);
                if (this.duration < 60) //Make it so you must hold the quit button for a whole second
                {
                    if (world.getWorldTime() % 10 == 0)
                        Gdx.app.log("Quit Button", "Held for " + this.duration + " ticks!");
                    this.duration++;
                } else
                {
                    Gdx.app.log("Quit Button", "Button Pressed for a whole second.  Stopping game!");
                    Gdx.app.exit(); //Quit the activity, calling all dispose methods and stuff
                }
            }

            public void onStoppedClicking(GameWorld world)
            {
                super.onStoppedClicking(world);
                this.duration = 0;
            }
        });

        this.movementSlider = new UISlider((1.0F - 0.30F)*Gdx.graphics.getWidth(), 0.0F, 0.30F, 0.15F)
        {
            @Override
            public void onUpdate(GameWorld world)
            {
                super.onUpdate(world);
                EntityPlayer player = world.getPlayer();

                if (this.state == ButtonState.UNPRESSED) //Make the slider smoothly reset
                {
                    if (this.sliderX > 0.0F)
                        this.sliderX -= 0.01F;
                    else if (this.sliderX < 0.0F)
                        this.sliderX += 0.01F;
                    if (Math.abs(this.sliderX) < 0.015F) //Snap to 0 when it's close enough so it doesn't vibrate
                        this.sliderX = 0.0F;
                }
                else if (Math.abs(player.getPhysBody().getLinearVelocity().x) < EntityPlayer.MOVE_SPEED_X) // && player.numberThingsStoodOn > 0)
                    player.getPhysBody().applyForceToCenter(this.sliderX*30000.0F, 0, true); //Move the player if they are slower than terminal velocity
            }
        };

        this.deadScreenButtons = new Array<UIButton>();

        this.deadScreenButtons.add(new UIButton(Gdx.graphics.getWidth()/2 - (0.25F*Gdx.graphics.getWidth())/2, Gdx.graphics.getHeight()*0.4F , 0.25F, 0.10F, "Restart")
        {
            @Override
            public void onClick(GameWorld world)
            {
                super.onClick(world);
                if (world.getPlayer().getHealth() < 1)
                {
                    world.getGameScreen().startNewWorld(world.getName()); //Restarts the world
                    Gdx.app.log("GameRenderer", "Restart level button pressed!");
                }
            }
        });

        this.levelCompleteButtons = new Array<UIButton>();

        this.levelCompleteButtons.add(new UIButton(Gdx.graphics.getWidth()/2 - (0.35F*Gdx.graphics.getWidth())/2, Gdx.graphics.getHeight()*0.3F , 0.35F, 0.10F, "Next Level")
        {
            @Override
            public void onClick(GameWorld world)
            {
                super.onClick(world);
                if (world.hasLevelEnded())
                {
                    Gdx.app.log("GameRenderer", "Next level button pressed!");

                    String nextLevel = world.getNextLevel();

                    if (nextLevel != null)
                        world.getGameScreen().startNewWorld(world.getNextLevel()); //Starts the next level
                    else
                        Gdx.app.exit();
                }
            }
        });
    }

    /**
     * Renders the world.
     */
    public void render(float delta)
    {
        this.runTime += delta; //Update the renderer's running time, used for animation

        //Finds out if the player is dead or not
        this.playerDead = this.world.getPlayer() == null || this.world.getPlayer().getHealth() < 1;

        if (!this.playerDead) //Update the player's coordinates, if the player is not dead
        {
            Vector2 position = this.world.getPlayer().getPhysBody().getPosition();
            this.playerX = position.x;
            this.playerY = position.y;
        }

        //Make the camera follow the player, but stop at the world bounds

        Vector2 playerPos = this.world.getPlayer().getPhysBody().getPosition();
        float cameraX = MathUtils.clamp(this.playerX, camera.viewportWidth/2, this.world.getWidth() - camera.viewportWidth/2);
        float cameraY = MathUtils.clamp(this.playerY, camera.viewportHeight/2 - this.worldBottomUIOffset, this.world.getHeight() - camera.viewportHeight/2); //worldBottomUIOffset makes it stop so the ground is just above the UI controls

        this.camera.position.set(cameraX, cameraY, 0);

        this.camera.update();

        //******************** Render the world ********************

        //Draw a black background
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //Render sprites
        this.spriteBatch.setProjectionMatrix(this.camera.combined);
        this.backgroundSpriteBatch.setProjectionMatrix(this.camera.combined);

        this.backgroundSpriteBatch.begin();

        //Draw the sky, with epic parallax effects
        final int backgroundSize = 10;
        int tileCountX = (int)(this.world.getWidth() / backgroundSize) + 1;
        int tileCountY = (int)(this.world.getHeight() / backgroundSize) + 1;
        float xOffset = this.camera.position.x / 2.0F;
        float yOffset = this.camera.position.y / 2.0F;

        this.backgroundSpriteBatch.draw(AssetLoader.skyTexture,
                xOffset - backgroundSize,
                yOffset - backgroundSize,
                tileCountX*backgroundSize,
                tileCountY*backgroundSize,
                0,
                0,
                tileCountX,
                tileCountY);

        //Draw the mountains, with epic parallax effects
        final float mountainRatio = 0.05F;
        final int mountainWidth = (int) (AssetLoader.mountainsTexture.getWidth()*mountainRatio);
        final int mountainHeight = (int) (AssetLoader.mountainsTexture.getHeight()*mountainRatio);
        tileCountX = (int)(this.world.getWidth() / mountainWidth) + 1;
        tileCountY = 1;
        xOffset = this.camera.position.x / 3.0F;
        yOffset = this.camera.position.y / 3.0F;

        this.backgroundSpriteBatch.draw(AssetLoader.mountainsTexture,
                xOffset - mountainWidth,
                yOffset + mountainHeight - 5, //yOffset + mountainHeight - 4,
                tileCountX*mountainWidth,
                tileCountY*mountainHeight * -1,
                0,
                0,
                tileCountX,
                tileCountY);

        this.backgroundSpriteBatch.end();

        //Render tiles
        this.tiledMapRenderer.setView(this.camera);
        this.tiledMapRenderer.render();

        this.spriteBatch.begin();

        //Draw game objects
        for (Body body : this.world.getPhysBodies())
        {
            Object userData = body.getUserData();

            if (userData instanceof Entity)
            {
                ((Entity)userData).render(delta, runTime, spriteBatch);
            }
        }

        this.spriteBatch.end();

        this.shapeRenderer.setProjectionMatrix(this.camera.combined);
        this.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        //Render dark area under world
        this.shapeRenderer.setColor(Color.valueOf("5C4937"));
        this.shapeRenderer.rect(
                this.playerX - Gdx.graphics.getWidth()/2,
                0 - this.worldBottomUIOffset,
                Gdx.graphics.getWidth(),
                this.worldBottomUIOffset);

        this.shapeRenderer.end();

        //Render debug physics
        if (GameScreen.drawBox2DDebugGraphics)
            this.debugRenderer.render(this.world.getPhysWorld(), this.camera.combined);


        this.hudShapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        //******************** Render UI ********************

        //Render dead screen, if the player is dead
        if (this.playerDead)
        {
            //Stop the shape renderer and enable OpenGL blending, then start it again
            this.hudShapeRenderer.end();
            Gdx.gl.glEnable(Gdx.gl.GL_BLEND);
            Gdx.gl.glBlendFunc(Gdx.gl.GL_SRC_ALPHA, Gdx.gl.GL_ONE_MINUS_SRC_ALPHA);
            this.hudShapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

            this.hudShapeRenderer.setColor(Color.valueOf("FF333388"));
            this.hudShapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

            //Stop the shape renderer and disable OpenGL blending, then start it again
            this.hudShapeRenderer.end();
            Gdx.gl.glDisable(Gdx.gl.GL_BLEND);
            this.hudShapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

            for (UIButton button : this.deadScreenButtons)
            {
                button.onUpdate(this.world);
                button.renderShapes(this.hudShapeRenderer);
            }
        }

        //Render level complete screen
        if (this.world.hasLevelEnded())
        {
            //Stop the shape renderer and enable OpenGL blending, then start it again
            this.hudShapeRenderer.end();
            Gdx.gl.glEnable(Gdx.gl.GL_BLEND);
            Gdx.gl.glBlendFunc(Gdx.gl.GL_SRC_ALPHA, Gdx.gl.GL_ONE_MINUS_SRC_ALPHA);
            this.hudShapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

            this.hudShapeRenderer.setColor(Color.valueOf("63C3FF88"));
            this.hudShapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

            //Stop the shape renderer and disable OpenGL blending, then start it again
            this.hudShapeRenderer.end();
            Gdx.gl.glDisable(Gdx.gl.GL_BLEND);
            this.hudShapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

            for (UIButton button : this.levelCompleteButtons)
            {
                button.onUpdate(this.world);
                button.renderShapes(this.hudShapeRenderer);
            }
        }

        for (UIButton button : this.uiButtons) //Render buttons, pass 1.  Also update button logic
        {
            button.onUpdate(this.world);
            button.renderShapes(this.hudShapeRenderer);
        }

        this.movementSlider.onUpdate(this.world);
        this.movementSlider.renderShapes(this.hudShapeRenderer);

        //Draw health bar, pass 1
        this.drawPlayerHealthBarPass1(this.hudShapeRenderer);

        this.hudShapeRenderer.end();

        this.hudSpriteBatch.begin();

        //Render dead screen, if the player is dead
        if (this.playerDead)
        {
            for (UIButton button : this.deadScreenButtons)
                button.renderSprites(this.hudSpriteBatch);

            String deathMessage = "You are Dead";
            AssetLoader.fontMainLarge.draw(this.hudSpriteBatch, deathMessage, Gdx.graphics.getWidth()/2 - AssetLoader.fontMainLarge.getBounds(deathMessage).width/2, Gdx.graphics.getHeight()*0.66F);
        }

        //Render level complete screen, if the level is complete
        if (this.world.hasLevelEnded())
        {
            for (UIButton button : this.levelCompleteButtons)
                button.renderSprites(this.hudSpriteBatch);

            String message = "Level Complete!";
            AssetLoader.fontMainLarge.draw(this.hudSpriteBatch, message, Gdx.graphics.getWidth()/2 - AssetLoader.fontMainLarge.getBounds(message).width/2, Gdx.graphics.getHeight()*0.70F);

            if (this.world.hasPlayerGottenNewHighscore())
            {
                message = "New best time!";
                AssetLoader.fontMain.setColor(1.0F, 1.0F, 0.0F, 1.0F); //Change color to yellow
                AssetLoader.fontMain.draw(this.hudSpriteBatch, message, Gdx.graphics.getWidth()/2 - AssetLoader.fontMain.getBounds(message).width/2, Gdx.graphics.getHeight()*0.48F);
                AssetLoader.fontMain.setColor(1.0F, 1.0F, 1.0F, 1.0F); //Reset color to white
            }

            message = "Time: " + GameScreen.getTimeFromTicks(this.world.getWorldTime(), false);
            AssetLoader.fontMain.draw(this.hudSpriteBatch, message, Gdx.graphics.getWidth()/2 - AssetLoader.fontMain.getBounds(message).width/2, Gdx.graphics.getHeight()*0.55F);
        }

        for (UIButton button : this.uiButtons) //Render buttons, pass 2
            button.renderSprites(this.hudSpriteBatch);

        this.movementSlider.renderSprites(this.hudSpriteBatch);

        //Draw health bar, pass 2
        this.drawPlayerHealthBarPass2(this.hudSpriteBatch);

        //Draw the debug menu, if the setting is set
        if (GameScreen.drawDebugOverlay)
            this.drawDebugMenu(this.hudSpriteBatch);

        this.hudSpriteBatch.end();
    }

    /**
     * Draws the debug menu, for debugging purposes
     */
    public void drawDebugMenu(SpriteBatch spriteBatch)
    {
        Vector2 playerPos = this.world.getPlayer().getPhysBody().getPosition();
        int x = (int)playerPos.x;
        int y = (int)playerPos.y;

        String debugString = String.format("Vel: %-2.2f m/s", this.world.getPlayer().getPhysBody().getLinearVelocity().len()); // + this.world.getName();
        debugString += String.format(" X:%-2d Y:%-2d", x, y);
        AssetLoader.fontDebug.draw(hudSpriteBatch, debugString,
                Gdx.graphics.getWidth() - AssetLoader.fontDebug.getBounds(debugString).width - 20,
                Gdx.graphics.getHeight() - 20); //Render text

        debugString = "Logic:";
        for (int i=0; i<this.world.logicChannels.length; i++)
            debugString += (this.world.logicChannels[i] ? 1 : 0);

        AssetLoader.fontDebug.draw(hudSpriteBatch, debugString,
                Gdx.graphics.getWidth() - AssetLoader.fontDebug.getBounds(debugString).width - 20,
                Gdx.graphics.getHeight() - 60); //Render text
    }

    /**
     * Draws the shapes of the player's health bar
     */
    public void drawPlayerHealthBarPass1(ShapeRenderer shapeRenderer)
    {
        int screenWidth = Gdx.graphics.getWidth();
        int screenHeight = Gdx.graphics.getHeight();
        float healthbarWidth = screenWidth*0.25F;
        float healthbarHeight = screenHeight*0.1F;
        float healthbarMargin = healthbarHeight*0.1F;
        float innerBarWidth = screenWidth/2 - healthbarMargin*2;
        int midX = screenWidth/2;

        float playerHealth = 0;
        if (this.world.getPlayer() != null)
        {
            EntityPlayer player = this.world.getPlayer();
            playerHealth = player.getHealth()/(player.getMaxHealth()*1.0F);
        }

        //Calculate the color to use
        final Color fullColor = Color.valueOf("00FF00");
        Color healthbarColor = Color.valueOf("FF0000");
        healthbarColor.lerp(fullColor, playerHealth);

        //Draw outline-background
        shapeRenderer.setColor(Color.valueOf("666666"));
        shapeRenderer.rect(midX - healthbarWidth, screenHeight - healthbarHeight, screenWidth/2, healthbarHeight);

        //Draw health bar background
        shapeRenderer.setColor(Color.valueOf("888888"));
        shapeRenderer.rect(midX - healthbarWidth + healthbarMargin,
                screenHeight - healthbarHeight + healthbarMargin,
                innerBarWidth,
                healthbarHeight - healthbarMargin*2);

        //Draw health bar background
        shapeRenderer.setColor(healthbarColor);
        shapeRenderer.rect(midX - healthbarWidth + healthbarMargin,
                screenHeight - healthbarHeight + healthbarMargin,
                innerBarWidth*playerHealth,
                healthbarHeight - healthbarMargin*2);
    }

    /**
     * Draws the sprites of the player's health bar
     */
    public void drawPlayerHealthBarPass2(SpriteBatch spriteBatch)
    {
        int screenWidth = Gdx.graphics.getWidth();
        int screenHeight = Gdx.graphics.getHeight();
        float healthbarWidth = screenWidth*0.25F;
        float healthbarHeight = screenHeight*0.1F;
        float healthbarMargin = healthbarHeight*0.25F;
        int midX = screenWidth/2;

        AssetLoader.fontMain.draw(spriteBatch, "Health",
                midX - AssetLoader.fontMain.getBounds("Health").width/2,
                screenHeight - healthbarHeight/2 + AssetLoader.fontMain.getCapHeight()/2);
    }

    /**
     * Disposes anything the GameRenderer may need to dispose.
     */
    public void dispose()
    {
        this.hudShapeRenderer.dispose();
        this.debugRenderer.dispose();
        this.tiledMapRenderer.dispose();
        this.backgroundSpriteBatch.dispose();
        this.spriteBatch.dispose();
        this.hudSpriteBatch.dispose();
    }
}
