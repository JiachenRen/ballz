package game_objs;

import main_exec.Main;
import processing.core.PApplet;
import processing.core.PConstants;

/**
 * Represents a single chunk of rectangular debris, created April 21st, 2107 by Jiachen Ren.
 */
public class RectDebris extends Debris {
    private float w;
    private int color;

    RectDebris(PApplet parent, float speed, float x, float y, float w) {
        super(parent, speed, x, y);
        this.w = w;
        init();
    }

    private void init() {
        /*multicolor, not necessarily the best*/
        int colors[] = new int[]{255, 0, 127};
        int indexes[] = new int[]{
                (int) parent.random(0, colors.length),
                (int) parent.random(0, colors.length),
                (int) parent.random(0, colors.length),
        };
        color = parent.color(colors[indexes[0]], colors[indexes[1]], colors[indexes[2]], 160);

        /*TODO temporary*/
        color = Main.debrisColor;
    }

    @Override
    public void display() {
        parent.pushStyle();
        parent.rectMode(PConstants.CENTER);
        parent.noStroke();
        parent.fill(color);
        parent.rect(getPos().x, getPos().y, w, w);
        parent.popStyle();
    }
}
