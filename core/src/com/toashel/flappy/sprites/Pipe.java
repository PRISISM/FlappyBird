package com.toashel.flappy.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.Random;

/**
 * Created by Sheldon on 12/15/2015.
 */
public class Pipe {
    public static final int PIPE_WIDTH = 52;

    private static final int FLUCTUATION = 130;
    private static final int GAP = 100;
    private static final int LOWEST_OPENING = 120;

    private Texture topPipe, bottomPipe;
    private Vector2 posTopPipe, posBotPipe;
    private Rectangle boundsTop, boundsBot;
    private Random rand;

    public Pipe(float x) {
        topPipe = new Texture("toptube.png");
        bottomPipe = new Texture("bottomtube.png");
        rand = new Random();

        posTopPipe = new Vector2(x, rand.nextInt(FLUCTUATION) + GAP + LOWEST_OPENING);
        posBotPipe = new Vector2(x, posTopPipe.y - GAP - bottomPipe.getHeight());

        boundsTop = new Rectangle(posTopPipe.x, posTopPipe.y, topPipe.getWidth(), topPipe.getHeight());
        boundsBot = new Rectangle(posBotPipe.x, posBotPipe.y, bottomPipe.getWidth(), bottomPipe.getHeight());
    }

    public Texture getTopPipe() {
        return topPipe;
    }

    public Texture getBottomPipe() {
        return bottomPipe;
    }

    public Vector2 getPosTopPipe() {
        return posTopPipe;
    }

    public Vector2 getPosBotPipe() {
        return posBotPipe;
    }

    public void reposition(float x) {
        posTopPipe.set(x, rand.nextInt(FLUCTUATION) + GAP + LOWEST_OPENING);
        posBotPipe.set(posBotPipe = new Vector2(x, posTopPipe.y - GAP - bottomPipe.getHeight()));
        boundsTop.setPosition(posTopPipe.x, posTopPipe.y);
        boundsBot.setPosition(posBotPipe.x, posBotPipe.y);
    }

    public boolean collides(Rectangle player) {
        return player.overlaps(boundsTop) || player.overlaps(boundsBot);
    }

    public void dispose() {
        topPipe.dispose();
        bottomPipe.dispose();
    }
}
