package com.toashel.flappy.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.toashel.flappy.FlappyBird;
import com.toashel.flappy.sprites.Bird;
import com.toashel.flappy.sprites.Pipe;

/**
 * Created by Sheldon on 12/15/2015.
 */
public class PlayState extends State {
    private static final int SPACING = 125;
    private static final int COUNT = 4;
    private static final int GROUND_OFFSET = -50;
    private int score = 0;

    private Bird bird;
    private Texture background;
    private Texture ground;
    private Vector2 groundPos1, groundPos2;

    private BitmapFont font;
    private Sound coin;

    private Array<Pipe> pipes;

    protected PlayState(GameStateManager gsm) {
        super(gsm);
        bird = new Bird(50, 300);
        camera.setToOrtho(false, FlappyBird.WIDTH / 2, FlappyBird.HEIGHT / 2);
        background = new Texture("bg.png");
        ground = new Texture("ground.png");
        coin = Gdx.audio.newSound(Gdx.files.internal("coin6.wav"));

        font = new BitmapFont(Gdx.files.internal("font.fnt"));
        font.getData().setScale(0.8f);
        font.setUseIntegerPositions(false);

        groundPos1 = new Vector2(camera.position.x - camera.viewportWidth / 2, GROUND_OFFSET);
        groundPos2 = new Vector2((camera.position.x - camera.viewportWidth / 2) + ground.getWidth(), GROUND_OFFSET);

        pipes = new Array<Pipe>();

        for (int i = 1; i <= COUNT; i++) {
            pipes.add(new Pipe(i * (SPACING + Pipe.PIPE_WIDTH)));
        }
    }

    @Override
    protected void handleInput() {
        if (Gdx.input.justTouched()) {
            bird.jump();
        }

    }

    @Override
    public void update(float deltaTime) {
        handleInput();

        if (!bird.getAlive()) {
            gsm.set(new MenuState((gsm)));
        }
        bird.update(deltaTime);
        updateGround();
        camera.position.x = bird.getPosition().x + 80;

        for (int i = 0; i < pipes.size; i++) {
            Pipe pipe = pipes.get(i);

            if (camera.position.x - (camera.viewportWidth / 2) >
                    pipe.getPosTopPipe().x + pipe.getTopPipe().getWidth()) {
                pipe.reposition(pipe.getPosTopPipe().x + ((Pipe.PIPE_WIDTH + SPACING) * COUNT));
            }

            if (pipe.collides(bird.getBounds())) {
                bird.die();
            }

            if (bird.getPosition().x == pipe.getPosBotPipe().x) {
                increment();
                coin.play(0.3f);
            }
            System.out.println("" + (int) bird.getPosition().x + " || " + (int) pipe.getPosBotPipe().x);

            if (bird.getPosition().y <= ground.getHeight() + GROUND_OFFSET){
                bird.die();
//                gsm.set(new PlayState(gsm));
            }

            camera.update();
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(camera.combined);
        sb.begin();

        sb.draw(background, camera.position.x - (camera.viewportWidth / 2), 0);
        sb.draw(bird.getTexture(), bird.getPosition().x, bird.getPosition().y);
        for (Pipe pipe : pipes) {
            sb.draw(pipe.getTopPipe(), pipe.getPosTopPipe().x, pipe.getPosTopPipe().y);
            sb.draw(pipe.getBottomPipe(), pipe.getPosBotPipe().x, pipe.getPosBotPipe().y);
        }
        sb.draw(ground, groundPos1.x, groundPos1.y);
        sb.draw(ground, groundPos2.x, groundPos2.y);
        font.draw(sb, "" + score , camera.position.x - 6, camera.viewportHeight - 10);

        sb.end();

    }

    @Override
    public void dispose() {
        background.dispose();
        bird.dispose();

        for (Pipe pipe : pipes) {
            pipe.dispose();
        }
    }

    private void updateGround() {
        if (camera.position.x - (camera.viewportWidth / 2) > groundPos1.x + ground.getWidth()) {
            groundPos1.add(ground.getWidth() * 2, 0);
        }
        if (camera.position.x - (camera.viewportWidth / 2) > groundPos2.x + ground.getWidth()) {
            groundPos2.add(ground.getWidth() * 2, 0);
        }
    }

    private synchronized void increment() {
        score++;
    }
}
