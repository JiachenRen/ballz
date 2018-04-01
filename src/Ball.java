import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;

/**
 * ball class. Created April 20, 2017 by jiachen Ren
 */
public class Ball {
    private PVector pos;
    private PVector dir;
    private PVector prevPos;
    private int diameter = Main.ballDiameter; /*this represents the diameter of the ball*/
    private float speed = Main.ballInitVelocity;
    private int color;
    private static int escapeDuration; /*time for the ball to get to the place where it would not be considered as expired*/
    private int timeCreated;
    private PApplet parent;
    private boolean expired;
    private boolean animationCompleted;
    static boolean initPosRecorded = false;
    /*if the initial x coordinate is recorded. This is indicated by the first ball arrived at the base line*/
    private static float initPosX, initPosY;

    Ball(PApplet parent, float x, float y, PVector dir) {
        pos = new PVector(x, y);
        setDir(dir);
        this.parent = parent;
        init();
    }

    private void init() {
        escapeDuration = 500;
        timeCreated = parent.millis();
        color = Main.ballColor;
    }

    void update() {
        if (expired) {
            if (initPosRecorded) {
                float tempY = initPosY - diameter / 2.0f; //offsetting initY
                PVector toSelf = new PVector(pos.x, pos.y);
                PVector toAnchor = new PVector(initPosX, tempY);
                toAnchor.sub(toSelf).setMag(speed * 1.5f); //adjust this to adjust how fast it moves back to the base
                pos.add(toAnchor);
                pos.y = tempY;
                float dist = PApplet.dist(pos.x, pos.y, initPosX, tempY);
                if (dist <= speed) animationCompleted = true; /*DEBUG*/
            } else {
                initPosX = (int) pos.x;
                initPosRecorded = true;
                animationCompleted = true;
            }
        } else {
            prevPos = new PVector(pos.x, pos.y);
            pos.add(dir);

            /*if the balls were to be constantly accelerated*/
            if (Main.constAcc) {
                dir.setMag(dir.mag() * Main.acc);
                dir.limit(30); /*apply limit to the velocity*/
            }

        }
    }

    boolean hasExpired() {
        return animationCompleted && expired;
    }

    static void displayBallAnchor(PApplet parent) {
        //display the white ball at the baseline. This is not the best way to do it!
        float radius = Main.ballDiameter / 2.0f;
        Ball ball = new Ball(parent, initPosX, initPosY - radius, new PVector());
        ball.display();

        /*draw the text above it indicating the number of balls*/
        parent.pushStyle();
        parent.fill(255);
        parent.textSize(radius);
        parent.textAlign(PConstants.CENTER, PConstants.CENTER);//a very strange bug. Possibly caused by magnifying small fonts.
        parent.text("x " + Context.numBalls, initPosX, initPosY - radius * 3);
        parent.popStyle();
    }

    public void display() {
        if (pos.y > initPosY - diameter / 2.0) return;
        parent.pushStyle();
        parent.noStroke();
        parent.fill(color);
        parent.ellipseMode(PConstants.CENTER);
        parent.ellipse(pos.x, pos.y, diameter, diameter);
        parent.popStyle();
    }

    void setDir(PVector dir) {
        this.dir = dir;
        dir.setMag(speed);
    }

    public float getSpeed() {
        return speed;
    }

    PVector getDir() {
        return dir;
    }

    void setDiameter(int diameter) {
        this.diameter = diameter;
    }

    int getDiameter() {
        return diameter;
    }

    PVector getPos() {
        return pos;
    }

    static void setInitPos(float x, float y) {
        Ball.initPosX = x;
        initPosY = y;
    }

    static float getInitPosX() {
        return initPosX;
    }

    PVector getPrevPos() {
        return prevPos;
    }

    //return to base:)
    void expire() {
        if (timeElapsed() > escapeDuration) {
            expired = true;
        }
    }

    private int timeElapsed() {
        return parent.millis() - timeCreated;
    }

    void setSpeed(float speed) {
        this.speed = speed;
        dir.setMag(speed);
    }
}
