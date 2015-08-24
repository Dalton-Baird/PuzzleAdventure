package com.dalton.puzzleadventure.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.dalton.puzzleadventure.AssetLoader;
import com.dalton.puzzleadventure.GameWorld;

/**
 * Created by Dalton on 2/11/2015.
 */
public class UIButton
{
    protected float x;
    protected float y;
    protected float widthPercent;
    protected float heightPercent;
    protected final float width;
    protected final float height;
    protected String text;
    protected long lastClicked;
    protected ButtonState state;
    protected final Color[][] defaultColors; //Index 0 is background, 1 is triangular highlight, 2 is foreground.  First array is normal colors, second is pressed colors.
    private int currentColorIndex;

    public UIButton(float x, float y, float widthPercent, float heightPercent, String text)
    {
        this.x = x;
        this.y = y;
        this.widthPercent = widthPercent;
        this.heightPercent = heightPercent;
        this.text = text;

        this.width = widthPercent * Gdx.graphics.getWidth();
        this.height = heightPercent * Gdx.graphics.getHeight();
        this.lastClicked = 0L;
        this.state = ButtonState.UNPRESSED;
        this.currentColorIndex = 0;
        this.defaultColors = new Color[][]
                {
                        {Color.valueOf("333333"), Color.valueOf("444444"), Color.valueOf("111111")},
                        {Color.valueOf("999999"), Color.valueOf("AAAAAA"), Color.valueOf("777777")},
                        {Color.valueOf("666666"), Color.valueOf("777777"), Color.valueOf("444444")}
                };
    }

    /**
     * Called to render the button's background shapes.
     */
    public void renderShapes(ShapeRenderer shapeRenderer)
    {
        float borderWidth = 0.005F*Gdx.graphics.getWidth();

        shapeRenderer.setColor(this.defaultColors[0][this.currentColorIndex]);
        shapeRenderer.rect(this.x, this.y, this.width, this.height); //Background
        shapeRenderer.setColor(this.defaultColors[1][this.currentColorIndex]);
        shapeRenderer.rect(this.x, this.y + this.height/2, this.width - this.height/2, this.height/2); //Render middle rectangle
        shapeRenderer.triangle(this.x + this.width, this.y + this.height, this.x + this.width - this.height/2, this.y + this.height/2, this.x + this.width - this.height/2, this.y + this.height); //render middle right triangle
        shapeRenderer.triangle(this.x, this.y, this.x, this.y + this.height/2, this.x + this.height/2, this.y + this.height/2); //render middle left triangle
        shapeRenderer.setColor(this.defaultColors[2][this.currentColorIndex]);
        shapeRenderer.rect(this.x + borderWidth, this.y + borderWidth, this.width - borderWidth * 2, this.height - borderWidth * 2); //Inner box
    }

    /**
     * Called to render the button's sprites.
     */
    public void renderSprites(SpriteBatch spriteBatch)
    {
        AssetLoader.fontMain.draw(spriteBatch, this.text,
                this.x + this.width/2 - AssetLoader.fontMain.getBounds(this.text).width/2,
                this.y + this.height/2 + AssetLoader.fontMain.getCapHeight()/2); //Render text
    }

    /**
     * Returns whether the coordinates are within the button's bounds.
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
    public void onClick(GameWorld world)
    {
        if (this.state != ButtonState.DISABLED)
        {
            this.lastClicked = world.getWorldTime();
            this.state = ButtonState.PRESSED;
            this.currentColorIndex = 1; //Change colors to pressed colors
        }
    }

    /**
     * Called when the button is no longer being clicked.
     */
    public void onStoppedClicking(GameWorld world)
    {
        if (this.state != ButtonState.DISABLED)
        {
            this.currentColorIndex = 0; //Reset colors back to normal
        }
    }

    @Override
    public String toString()
    {
        return super.toString() + " Text: " + this.text;
    }

    /**
     * Sets if the button is disabled.
     * @param disabled
     */
    public void setDisabled(boolean disabled)
    {
        if (disabled && this.state != ButtonState.DISABLED)
        {
            this.state = ButtonState.DISABLED;
            this.currentColorIndex = 2;
        }
        else if (!disabled && this.state == ButtonState.DISABLED)
        {
            this.state = ButtonState.UNPRESSED;
            this.currentColorIndex = 0;
        }
    }
}
