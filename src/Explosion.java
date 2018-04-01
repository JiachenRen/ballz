import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;

/**
 * Explosion Class. Simulating the debris when the blocks are destroyed. April 21st, 2017.
 */
public class Explosion {
    private PVector pos;
    private float maxSpeed;
    private int num;
    private ArrayList<Debris> cluster;
    private PApplet parent;

    Explosion(PApplet parent, float x, float y, float maxSpeed, int num) {
        pos = new PVector(x, y);
        this.maxSpeed = maxSpeed;
        this.parent = parent;
        this.num = num;
        init();
    }

    private void init() {
        cluster = new ArrayList<>();

        /*create a cluster of debris*/
        for (int i = 0; i < num; i++) {
            float speed = parent.random(maxSpeed * 2.0f / 3.0f, maxSpeed);
            /*should the debris be of different sizes too?*/
            cluster.add(new RectDebris(parent, speed, pos.x, pos.y, 10));
        }
    }

    void update() {
        for (int i = cluster.size() - 1; i >= 0; i--) {
            Debris debris = cluster.get(i);
            debris.update();
            if (debris.expired()) {
                cluster.remove(debris);
            }
        }
    }

    public void display() {
        for (Debris debris : cluster) {
            debris.display();
        }
    }

    /*if all the debris in this explosion has disappeared, the explosion itself expires.*/
    boolean expired() {
        return cluster.size() == 0;
    }

}
