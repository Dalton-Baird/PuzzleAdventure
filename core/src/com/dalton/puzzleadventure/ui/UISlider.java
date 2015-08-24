package com.dalton.puzzleadventure.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.dalton.puzzleadventure.AssetLoader;
import com.dalton.puzzleadventure.GameWorld;

/**
 * Created by Dalton on 2/11/2015.
 */
public class UISlider
{
    protected float x;
    protected float y;
    protected float widthPercent;
    protected float heightPercent;
    protected final float width;
    protected final float height;

    protected float sliderX;
    protected long lastClicked;
    protected ButtonState state;

    public UISlider(float x, float y, float widthPercent, float heightPercent)
    {
        this.x = x;
        this.y = y;
        this.widthPercent = widthPercent;
        this.heightPercent = heightPercent;

        this.width = widthPercent * Gdx.graphics.getWidth();
        this.height = heightPercent * Gdx.graphics.getHeight();
        this.sliderX = 0.0F;
        this.lastClicked = 0L;
        this.state = ButtonState.UNPRESSED;
    }

    /**
     * Called to render the slider's background shapes.
     */
    public void renderShapes(ShapeRenderer shapeRenderer)
    {
        float borderWidth = 0.005F*Gdx.graphics.getWidth();
        float sliderRenderRange = this.width*0.40F;

        shapeRenderer.setColor(50/255.0F, 50/255.0F, 50/255.0F, 1.0F);
        shapeRenderer.rect(this.x, this.y, this.width, this.height); //Background
        shapeRenderer.setColor(150 / 255.0F, 150 / 255.0F, 150 / 255.0F, 1.0F);
        shapeRenderer.rect(this.x, this.y + this.height/2, this.width - this.height/2, this.height/2); //Render middle rectangle
        shapeRenderer.triangle(this.x + this.width, this.y + this.height, this.x + this.width - this.height/2, this.y + this.height/2, this.x + this.width - this.height/2, this.y + this.height); //render middle right triangle
        shapeRenderer.triangle(this.x, this.y, this.x, this.y + this.height/2, this.x + this.height/2, this.y + this.height/2); //render middle left triangle
        shapeRenderer.setColor(100 / 255.0F, 100 / 255.0F, 100 / 255.0F, 1.0F);
        shapeRenderer.rect(this.x + borderWidth, this.y + borderWidth, this.width - borderWidth * 2, this.height - borderWidth * 2); //Inner box
        shapeRenderer.setColor(150/255.0F, 150/255.0F, 150/255.0F, 1.0F);
        shapeRenderer.rect(this.x + borderWidth, this.y + this.height/2 - 4, this.width - borderWidth*2, 4); //Slider bar
        shapeRenderer.setColor(75 / 255.0F, 75 / 255.0F, 75 / 255.0F, 1.0F);
        shapeRenderer.circle(this.x + this.width/2 + MathUtils.clamp(this.sliderX*this.width, sliderRenderRange*-1, sliderRenderRange), this.y + this.height/2, borderWidth*5); //Slider Handle
    }

    /**
     * Called to render the slider's sprites.
     */
    public void renderSprites(SpriteBatch spriteBatch)
    {

    }

    /**
     * Returns whether the coordinates are within the slider's bounds.
     * @param clickX The X position clicked/touched
     * @param clickY The Y position clicked/touched
     * @return Whether the position is within the button
     */
    public boolean isInBounds(float clickX, float clickY)
    {
        clickY = Gdx.graphics.getHeight() - clickY;

        return clickX >= this.x && clickX <= this.x + this.width
                && clickY >= this.y && clickY <= this.y + this.height;
    }

    /**
     * Called to update the button's logic.
     */
    public void onUpdate(GameWorld world)
    {
        if (this.state == ButtonState.PRESSED && world.getWorldTime() - this.lastClicked > 5) //Unpress the button
        {
            this.state = ButtonState.UNPRESSED;
            this.onStoppedClicking(world);
        }
    }

    /**
     * Called when the button is clicked.
     */
    public void onClick(GameWorld world, int clickX, int clickY)
    {
        float relativeX = this.x + this.width/2 - clickX;
        relativeX *= (-1/this.width);
        this.sliderX = MathUtils.clamp(relativeX, -1.0F, 1.0F);
        this.lastClicked = world.getWorldTime();
        this.state = ButtonState.PRESSED;
    }

    /**
     * Called when the slider is no longer being clicked.
     */
    public void onStoppedClicking(GameWorld world)
    {

    }
}
