package game_objs;

import Main;
import processing.core.PApplet;
import processing.core.PConstants;

/**
 * If hit, bonus one extra ball for the player.
 */
public class Bonus extends Block {
    private float radius;
    private float noiseSeed;
    private float amp;

    public Bonus(PApplet parent, float x, float y, float w, float h) {
        super(parent, x, y, w, h, -1);
        setBackgroundColor(255);
        radius = (w > h ? h : w) / 6.0f;
        amp = radius / 1.3f+1.0f;
    }

    @Override
    public void display() {
        getParent().pushStyle();
        getParent().noStroke();
        getParent().fill(Main.bonusColor);
        getParent().ellipseMode(PConstants.CENTER);
        getParent().ellipse(x + w / 2.0f, y + h / 2.0f, radius * 2, radius * 2);
        float fluctuated = Math.abs(PApplet.cos(noiseSeed)) * amp + radius * 2+3.0f;
        getParent().noFill();
        getParent().stroke(Main.bonusColor);
        getParent().strokeWeight(3);
        getParent().ellipse(x + w / 2.0f, y + h / 2.0f, fluctuated, fluctuated);
        getParent().popStyle();

        /*adjusted to better fit the frame rate*/
        noiseSeed += PApplet.map(Main.fps,60,200,.2f,0.06f);
    }

    public boolean inRangeWith(Ball ball) {
        return PApplet.dist(ball.getPos().x, ball.getPos().y, x + w / 2.0f, y + h / 2.0f) <= radius + ball.getDiameter() / 2.0;
    }
}
