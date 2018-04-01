import processing.core.PApplet;
import processing.core.PVector;

/**
 * Debris Class.
 */
public abstract class Debris {
    private static final float accDamp = .95f;
    PApplet parent;
    private PVector pos;
    private PVector dir;
    private float speed;

    Debris(PApplet parent, float speed, float x, float y) {
        this.pos = new PVector(x, y);
        this.speed = speed;
        this.parent = parent;

        init();
    }

    private void init() {
        float rx = (float) Math.random() * 2.0f - 1.0f,
                ry = (float) Math.random() * 2.0f - 1.0f;

        /*initial explosion velocity*/
        dir = new PVector(rx, ry).normalize();
        dir.setMag(speed);
    }

    void update() {
        pos.add(dir);

        /*dampening acceleration*/
        dir.setMag(dir.mag() * accDamp);
    }

    PVector getPos() {
        return pos;
    }

    boolean expired() {
        return dir.mag() <= 1.0f;
    }

    public abstract void display();
}
