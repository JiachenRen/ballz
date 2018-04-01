package game_objs;

/*
 * If the user hits this, a point is added
 */

import Main;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;

/**
 * If hit, bonus one extra ball for the player.
 */
public class Score extends Block {
    private float radius;

    public Score(PApplet parent, float x, float y, float w, float h) {
        super(parent, x, y, w, h, -2);
        setBackgroundColor(50, 255, 60);
        radius = (w > h ? h : w) / 4.5f;
    }

    @Override
    public void display() {
        getParent().pushStyle();
        getParent().noFill();
        getParent().stroke(Main.scoreColor);
        getParent().strokeWeight(4);
        getParent().ellipseMode(PConstants.CENTER);
        getParent().ellipse(x + w / 2.0f, y + h / 2.0f, radius * 2, radius * 2);
        getParent().popStyle();
    }

    public boolean inRangeWith(Ball ball) {
        PVector center = new PVector(x + w / 2.0f, y + h / 2.0f);
        float dist = PApplet.dist(ball.getPos().x, ball.getPos().y, center.x, center.y);
        return dist <= radius + ball.getDiameter() / 2.0;
    }
}

