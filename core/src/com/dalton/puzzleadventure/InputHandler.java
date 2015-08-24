package com.dalton.puzzleadventure;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.dalton.puzzleadventure.ui.UIButton;

/**
 * Created by Dalton on 2/11/2015.
 */
public class InputHandler implements InputProcessor
{
    public static final String TAG = "Input Handler";

    private GameWorld world;
    private GameRenderer renderer;
    private Array<Vector2> touches;

    public InputHandler(GameWorld world, GameRenderer renderer)
    {
        this.world = world;
        this.renderer = renderer;
        this.touches = new Array<Vector2>();
        for (int i=0; i<20; i++)
            this.touches.add(new Vector2());
    }

    @Override
    public boolean keyDown(int keycode)
    {
        return false;
    }

    @Override
    public boolean keyUp(int keycode)
    {
        return false;
    }

    @Override
    public boolean keyTyped(char character)
    {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button)
    {
        return false;
    }

    /**
     * Custom method.  Like touchDown except it repeats.
     * @param screenX
     * @param screenY
     * @param pointer
     * @param button Not Yet Implemented
     */
    public void touched(int screenX, int screenY, int pointer, int button)
    {
        for (UIButton uiButton : this.renderer.uiButtons)
            if (uiButton.isInBounds(screenX, screenY))
                uiButton.onClick(this.world);

        for (UIButton uiButton : this.renderer.deadScreenButtons)
            if (uiButton.isInBounds(screenX, screenY))
                uiButton.onClick(this.world);

        for (UIButton uiButton : this.renderer.levelCompleteButtons)
            if (uiButton.isInBounds(screenX, screenY))
                uiButton.onClick(this.world);

        if (this.renderer.movementSlider.isInBounds(screenX, screenY))
            this.renderer.movementSlider.onClick(this.world, screenX, screenY);
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button)
    {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer)
    {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY)
    {
        return false;
    }

    @Override
    public boolean scrolled(int amount)
    {
        return false;
    }

    /**
     * Gets an array of touch coordinates.
     */
    public Array<Vector2> getTouches()
    {
        for (int i=0; i<20; i++)
            if (Gdx.input.isTouched(i))
                this.touches.get(i).set(Gdx.input.getX(i), Gdx.input.getY(i)); //Add touch data
            else
                this.touches.get(i).set(-1000, -1000); //Reset any previous touch data
        return this.touches;
    }
}
