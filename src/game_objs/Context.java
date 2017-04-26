package game_objs;

import jui_lib.Displayable;
import jui_lib.JNode;
import jui_lib.Label;
import main_exec.Main;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;

import java.util.ArrayList;

import static main_exec.Main.highScore;

/**
 * Context class, or the game's environment.
 */
public class Context extends Displayable {
    private ArrayList<BlockRow> blockRows;
    private ArrayList<Ball> balls;
    private ArrayList<Explosion> explosions;
    private PApplet parent;
    private PVector designatedPos;
    private PVector initPos;
    private int level;
    private int maxRows;
    private float rowHeight;
    private int lastTimeFired;
    private int ballsFired;
    private int ballsRecovered;
    private boolean isFiring;
    private boolean blockAdded;
    private boolean paused;
    public static int numBalls; /*change back to private later*/
    private boolean isAnimating;

    public Context(float x, float y, float w, float h) {
        super("context", x, y, w, h);
        init();
    }

    public Context(float relativeW, float relativeH) {
        super("context", relativeW, relativeH);
        init();
    }

    public Context() {
        super("context");
        init();
    }

    private void init() {
        level = Main.level; //default 1; Try 1500
        maxRows = Main.rows;
        numBalls = 1; //default 1;
        explosions = new ArrayList<>();
        blockRows = new ArrayList<>();
        balls = new ArrayList<>();
        this.parent = getParent();
        rowHeight = h / (maxRows + 2.0f); //+2.0,1 for beginning, one for end.
        this.addBlockRow();
        Ball.setInitPos((x + w) / 2.0f, y + h); //the y coordinate is fabricated. Needs diameter.

    }

    public void addBlockRow() {
        blockRows.add(new BlockRow(parent, x, y, w, rowHeight, level));
        updateLevelUI();
        this.level++;
        isAnimating = true;
    }

    /**
     * This method describes the animation of the blocks.
     *
     * @since April 25th, fixed an issue where the rows are not arranged properly
     */
    private void animate() {
        BlockRow firstRow = blockRows.get(blockRows.size() - 1);
        if (firstRow.getY() < y + rowHeight) {
            for (BlockRow blockRow : blockRows) {
                /*change the offset to adjust the speed*/
                blockRow.setY(blockRow.getY() + 3);
            }
        } else {
            /*the animation has completed. Stop.*/
            for (int i = 0; i < blockRows.size(); i++) {
                BlockRow blockRow = blockRows.get(i);
                blockRow.setY(y + rowHeight * (blockRow.getCurrentStack() + 1));
            }
            isAnimating = false;
            for (BlockRow blockRow : blockRows)
                blockRow.stack();
        }
    }

    private void checkBounds() {
        for (Ball ball : balls) {
            /*check horizontal bound*/
            if (ball.getPos().x - ball.getDiameter() / 2.0 <= x) {
                ball.getDir().x *= -1;
                ball.getPos().x = x + ball.getDiameter() / 2.0f;
            } else if (ball.getPos().x + ball.getDiameter() / 2.0 >= x + w) {
                ball.getDir().x *= -1;
                ball.getPos().x = x + w - ball.getDiameter() / 2.0f;
            }

            /*check vertical bound*/
            if (ball.getPos().y - ball.getDiameter() / 2.0 <= y) {
                ball.getDir().y *= -1;
                ball.getPos().y = y + ball.getDiameter() / 2.0f;
            } else if (ball.getPos().y + ball.getDiameter() / 2.0 >= y + h) {
                ball.expire();
            }
        }
    }

    private void checkCollision() {
        for (int i = balls.size() - 1; i >= 0; i--) {
            Ball ball = balls.get(i);
            for (BlockRow blockRow : blockRows) {
                for (int b = blockRow.getBlocks().size() - 1; b >= 0; b--) {
                    Block block = blockRow.getBlocks().get(b);
                    if (block.isPenetrable()) {
                        switch (block.getValue()) {
                            case -1:
                                Bonus bonus = (Bonus) block;
                                if (bonus.inRangeWith(ball)) {
                                    numBalls++;
                                    ballsFired++;
                                    ballsRecovered++;
                                    blockRow.getBlocks().remove(b);
                                }
                                break;
                            case -2:
                                Score score = (Score) block;
                                if (score.inRangeWith(ball)) {
                                    blockRow.getBlocks().remove(b);
                                    Main.score++;
                                    updateScoreUI();
                                }
                                break;
                        }
                        continue;
                    }

                    float radius = ball.getDiameter() / 2.0f,
                            rightBound = block.x + block.w + radius,
                            leftBound = block.x - radius,
                            upperBound = block.y - radius,
                            lowerBound = block.y + block.h + radius,
                            ballX = ball.getPos().x, ballY = ball.getPos().y,
                            prevBallX = ball.getPrevPos().x, prevBallY = ball.getPrevPos().y;

                    if (ballX <= rightBound && ballX >= leftBound) {
                        if (ballY <= lowerBound && ballY >= upperBound) {

                            if (ballX > block.x + block.w) {
                                ball.getDir().x *= -1;
                                ball.getPos().x = rightBound;
                            } else if (ballX < block.x) {
                                ball.getDir().x *= -1;
                                ball.getPos().x = leftBound;
                            } else if (ballY < block.y) {
                                ball.getDir().y *= -1;
                                ball.getPos().y = upperBound;
                            } else if (ballY > block.y + block.h) {
                                ball.getDir().y *= -1;
                                ball.getPos().y = lowerBound;
                            } else if (prevBallX > block.x + block.w) {
                                ball.getDir().x *= -1;
                                ball.getPos().x = rightBound;
                            } else if (prevBallX < block.x) {
                                ball.getDir().x *= -1;
                                ball.getPos().x = leftBound;
                            } else if (prevBallY < block.y) {
                                ball.getDir().y *= -1;
                                ball.getPos().y = upperBound;
                            } else if (prevBallY > block.y + block.h) {
                                ball.getDir().y *= -1;
                                ball.getPos().y = lowerBound;
                            }

                            block.setValue(block.getValue() - 1);
                        }
                    }
                }
            }
        }
    }

    /**
     * removes both the expired blocks and blockRows
     */
    private void removeBlocks() {
        for (int m = blockRows.size() - 1; m >= 0; m--) {
            BlockRow blockRow = blockRows.get(m);
            ArrayList<Block> blocks = blockRow.getBlocks();
            for (int i = blocks.size() - 1; i >= 0; i--) {
                if (blocks.get(i).getValue() <= 0) {
                    if (!blocks.get(i).isPenetrable()) {
                        Block block = blocks.get(i);
                        float tx = block.getX() + block.getWidth() / 2.0f,
                                ty = block.getY() + block.getHeight() / 2.0f;

                        /*an animation of a cluster of debris is to be created in the center*/
                        explosions.add(new Explosion(parent, tx, ty, Main.debrisMaxSpeed, 30));
                        blocks.remove(i);
                    }
                }
            }

            /*in addition, the expired blockRows needs to be removed*/
            if (blockRow.getBlocks().size() == 0)
                blockRows.remove(m);
        }
    }

    /**
     * remove expired balls.
     */
    private void removeBalls() {
        for (int i = balls.size() - 1; i >= 0; i--) {
            Ball ball = balls.get(i);
            if (ball.hasExpired()) {
                balls.remove(i);
                ballsRecovered++;
            }
        }
    }

    /**
     * check to see if the player has lost; if the player has lost, then
     * record the high score and restart the game.
     */
    private void checkStatus() {
        if (blockRows.size() == 0) return;
        if (blockRows.get(0).getCurrentStack() > maxRows) {
            if (level > highScore)
                highScore = level;
            Main.record("high_score", highScore);
            getParent().setup();
        }
    }

    /*remove expired explosions*/
    private void removeExplosions() {
        for (int i = explosions.size() - 1; i >= 0; i--) {
            if (explosions.get(i).expired()) {
                explosions.remove(i);
            }
        }
    }

    private void update() {
        checkCollision();
        checkBounds();
        removeBlocks();
        removeBalls();
        removeExplosions();

        /*manage firing intervals*/
        if (isFiring && ballsFired < numBalls) {
            if (lastTimeFired == 0 || parent.millis() - lastTimeFired >= Main.firingRate)
                fire();
        } else {
            isFiring = false;
        }

        /*add a block row after a round is complete*/
        if (firingRoundCompleted() && !blockAdded) {
            blockAdded = true;
            addBlockRow();
        }

        if (isAnimating) animate();
        else checkStatus();

        /*update balls in the background*/
        for (Ball ball : balls)
            ball.update();

        /*update explosions in the background*/
        for (Explosion explosion : explosions)
            explosion.update();
    }

    public void display() {
        if (!paused) this.update();
        super.display();

        /*display rows of blocks*/
        for (BlockRow blockRow : blockRows) {
            blockRow.display();
        }

        /*display balls*/
        for (Ball ball : balls) {
            ball.display();
        }

        /*display animations including explosion*/
        for (Explosion explosion : explosions) {
            explosion.display();
        }

        /*excellent fix! Only display the anchor ball when needed*/
        if (ballsFired == ballsRecovered || Ball.initPosRecorded)
            Ball.displayBallAnchor(parent);
    }

    /**
     * This method is invoked through the JNode, where a mouse pressed event is passed down.
     */
    public void mouseReleased() {
        if (isAnimating || !isMouseOver() || paused) return;
        if (!isFiring && firingRoundCompleted()) {
            ballsRecovered = 0;
            ballsFired = 0;
            blockAdded = false;
            Ball.initPosRecorded = false;
            parent.frameRate(Main.fps);
        }
        if (!isFiring) {
            designatedPos = new PVector(parent.mouseX, parent.mouseY);
            initPos = new PVector(Ball.getInitPosX(), y + h);
        }
        isFiring = true;

    }

    private boolean firingRoundCompleted() {
        return ballsRecovered == numBalls;
    }

    private void fire() {
        PVector mouse = new PVector(designatedPos.x, designatedPos.y);
        Ball ball = new Ball(parent, initPos.x, initPos.y, new PVector());
        PVector temp = new PVector(ball.getPos().x, ball.getPos().y);
        ball.setDir(mouse.sub(temp).normalize());
        balls.add(ball);
        lastTimeFired = parent.millis();
        ballsFired++;
    }

    public ArrayList<Ball> getBalls() {
        return balls;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setNumBalls(int temp) {
        numBalls = temp;
    }

    /**
     * this method resets the dimension of current context instance and all of the blocks within
     * the method invokes the init() private method.
     *
     * @param x x coordinate
     * @param y y coordinate
     * @param w width of the context instance
     * @param h height of the context instance
     */
    private void initContext(float x, float y, float w, float h) {
        if (w <= 0 || h <= 0) return;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        init();
    }

    /*JUI specific methods*/
    private void updateLevelUI() {
        Label label = JNode.getLabelById("level");
        if (label != null) label.setContent("Level " + level);
    }

    /**
     * updates the UI that displays the current score
     */
    private void updateScoreUI() {
        Label label = JNode.getLabelById("score");
        if (label != null) label.setContent("" + Main.score);
    }

    @Override
    public void resize(float w, float h) {
        super.resize(w, h);
        initContext(x, y, w, h);
    }

    @Override
    public void relocate(float x, float y) {
        super.relocate(x, y);
        initContext(x, y, w, h);
    }

    public void setPaused(boolean temp) {
        paused = temp;
    }
}
