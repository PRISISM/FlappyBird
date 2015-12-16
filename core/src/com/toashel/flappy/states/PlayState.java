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
    int state;
    static final int GAME_PLAYING = 0;
    static final int GAME_PAUSED = 1;
    static final int GAME_OVER = 2;

    private static final int SPACING = 125;
    private static final int COUNT = 2;
    private static final int GROUND_OFFSET = -50;
    private static int highScore = 0;

    private int cameraScroll = 80;

    private int score = 0;
    private int scoreLine;
    private int scoreGap = SPACING + Pipe.PIPE_WIDTH;

    private Bird bird;
    private Texture background, ground, gameOverLogo, gameOverPanel;
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
        gameOverLogo = new Texture("gameover.png");
        gameOverPanel = new Texture("gameoverpanel.png");

        coin = Gdx.audio.newSound(Gdx.files.internal("coin6.wav"));

        font = new BitmapFont(Gdx.files.internal("font.fnt"));
        font.getData().setScale(0.8f);
        font.setUseIntegerPositions(false);

        groundPos1 = new Vector2(camera.position.x - camera.viewportWidth / 2, GROUND_OFFSET);
        groundPos2 = new Vector2((camera.position.x - camera.viewportWidth / 2) + ground.getWidth(), GROUND_OFFSET);

        pipes = new Array<Pipe>();
        scoreLine = SPACING + Pipe.PIPE_WIDTH;

        for (int i = 1; i <= COUNT; i++) {
            pipes.add(new Pipe(i * (SPACING + Pipe.PIPE_WIDTH)));
        }

        state = GAME_PLAYING;
    }

    @Override
    protected void handleInput() {
        if (Gdx.input.justTouched()) {
            bird.jump();
        }
    }

    @Override
    public void update(float deltaTime) {
        switch (state) {
            case GAME_PLAYING:
                updatePlaying(deltaTime);
                break;

            case GAME_OVER:
                updateOver();
                break;
        }

    }

    public void updatePlaying(float deltaTime) {
        handleInput();

        if (!bird.getAlive()) {
            gsm.set(new MenuState((gsm)));
        }

        bird.update(deltaTime);
        updateGround();
        camera.position.x = bird.getPosition().x + cameraScroll;

        // Pipes
        for (int i = 0; i < pipes.size; i++) {
            Pipe pipe = pipes.get(i);

            if (camera.position.x - (camera.viewportWidth / 2) >
                    pipe.getPosTopPipe().x + pipe.getTopPipe().getWidth()) {
                pipe.reposition(pipe.getPosTopPipe().x + ((Pipe.PIPE_WIDTH + SPACING) * COUNT));
            }

            // Scoring
            if (bird.getPosition().x >= scoreLine) {
                increaseScore();
                scoreLine += scoreGap;
            }

            // Death Check
            if (bird.getPosition().y <= ground.getHeight() + GROUND_OFFSET){
                gameOver();
            }

            if (pipe.collides(bird.getBounds())) {
                gameOver();
            }

            camera.update();
        }
    }

    public void updateOver() {
        handleInput();
        if (Gdx.input.justTouched()) {
            gsm.push(new PlayState(gsm));
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        switch (state) {
            case GAME_PLAYING:
                renderPlaying(sb);
                break;

            case GAME_OVER:
                renderOver(sb);
                break;
        }
    }

    public void renderPlaying(SpriteBatch sb) {
        sb.setProjectionMatrix(camera.combined);
        sb.begin();

        // Assets
        sb.draw(background, camera.position.x - (camera.viewportWidth / 2), 0);
        sb.draw(bird.getTexture(), bird.getPosition().x, bird.getPosition().y);
        for (Pipe pipe : pipes) {
            sb.draw(pipe.getTopPipe(), pipe.getPosTopPipe().x, pipe.getPosTopPipe().y);
            sb.draw(pipe.getBottomPipe(), pipe.getPosBotPipe().x, pipe.getPosBotPipe().y);
        }
        // Ground
        sb.draw(ground, groundPos1.x, groundPos1.y);
        sb.draw(ground, groundPos2.x, groundPos2.y);

        // Text
        font.draw(sb, "" + score , camera.position.x - 6, camera.viewportHeight - 10);

        sb.end();
    }

    public void renderOver(SpriteBatch sb) {
        sb.setProjectionMatrix(camera.combined);
        sb.begin();

        // Assets
        sb.draw(background, camera.position.x - (camera.viewportWidth / 2), 0);
        sb.draw(bird.getTexture(), bird.getPosition().x, bird.getPosition().y);
        for (Pipe pipe : pipes) {
            sb.draw(pipe.getTopPipe(), pipe.getPosTopPipe().x, pipe.getPosTopPipe().y);
            sb.draw(pipe.getBottomPipe(), pipe.getPosBotPipe().x, pipe.getPosBotPipe().y);
        }
        // Ground
        sb.draw(ground, groundPos1.x, groundPos1.y);
        sb.draw(ground, groundPos2.x, groundPos2.y);

        // Game Over
        sb.draw(gameOverPanel, camera.position.x - (gameOverPanel.getWidth() / 2), camera.position.y - 10);
        sb.draw(gameOverLogo, camera.position.x - (gameOverLogo.getWidth() / 2), camera.position.y + (gameOverLogo.getHeight() * 3));

        // Score Text
        font.draw(sb, "" + score, camera.position.x - 83, camera.position.y + 75);
        font.draw(sb, "" + highScore, camera.position.x + 65, camera.position.y + 75);

        sb.end();
    }

    @Override
    public void dispose() {
        background.dispose();
        bird.dispose();
        coin.dispose();

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

    private synchronized void increaseScore() {
        score++;
        coin.play(0.3f);
    }

    private void gameOver() {
        bird.die();
        state = GAME_OVER;
        if (score > highScore)
            highScore = score;
    }
}
