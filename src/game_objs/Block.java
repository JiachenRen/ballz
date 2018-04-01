package game_objs;

import jui.Contextual;
import jui.JNode;
import Main;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PFont;

/**
 * Block as a super class for Score and Ball.
 */
public class Block extends Contextual {
    private PApplet parent;
    private int value;
    private boolean isPenetrable;

    /*
    should I make this adjustable through Main? A font is created here to prevent blurring.
    */
    private static PFont textFont = JNode.getParent().createFont("Seravek-Regular", 120);

    public Block(PApplet parent, float x, float y, float w, float h, int value) {
        super("", x, y, w, h);
        this.parent = parent;
        this.value = value;
        init(); //taking a big risk here. -1 for a ball. -2 for a score
    }

    private void init() {
        switch (value) {
            case -1:
                isPenetrable = true;
                break;
            case -2:
                isPenetrable = true;
                break;
            default:
                isPenetrable = false;
                break;
        }
    }

    private void assortColor() {
        int r, g, b;
        //b1 = 150, b2 = 100, b3 = 255
        int step1 = 10, step2 = 30, step3 = 60;
        if (value < step1) {
            r = (int) PApplet.map(value, 0, step1, Main.blockColor1, Main.blockColor2);
            g = (int) PApplet.map(value, 0, step1, Main.blockColor3, Main.blockColor2);
            b = (int) PApplet.map(value, 0, step1, 50, Main.blockColor3);
        } else if (value < step2) {
            r = (int) PApplet.map(value, step1, step2, 50, Main.blockColor3);
            g = (int) PApplet.map(value, step1, step2, Main.blockColor1, Main.blockColor2);
            b = (int) PApplet.map(value, step1, step2, Main.blockColor3, Main.blockColor2);
        } else if (value < step3) {
            r = (int) PApplet.map(value, step2, step3, Main.blockColor3, Main.blockColor2);
            g = (int) PApplet.map(value, step2, step3, 50, Main.blockColor3);
            b = (int) PApplet.map(value, step2, step3, Main.blockColor1, Main.blockColor2);
        } else if (value < 200) {
            r = (int) PApplet.map(value, 90, 200, Main.blockColor3, Main.blockColor2);
            g = (int) PApplet.map(value, 90, 200, Main.blockColor3, Main.blockColor2);
            b = (int) PApplet.map(value, 90, 200, Main.blockColor3, Main.blockColor2);
        } else {
            r = (int) PApplet.map(value, 90, 2000, Main.blockColor3, Main.blockColor2);
            g = (int) PApplet.map(value, 90, 2000, 0, Main.blockColor2);
            b = (int) PApplet.map(value, 90, 2000, Main.blockColor3, Main.blockColor2);
        }
        setBackgroundColor(r, g, b, 200);
    }

    public void display() {
        /*assort color according to the value*/
        assortColor();

        parent.pushStyle();

        /*
        draw the rectangle
         */
        parent.noStroke();
        parent.fill(backgroundColor);
        parent.rectMode(PConstants.CORNER);
        parent.rect(x, y, w, h);

        /*
        draw the text
         */
        parent.textFont(textFont);
        parent.textSize(computeTextSize());
        parent.textAlign(PConstants.CENTER, PConstants.CENTER);
        parent.fill(getTextColor());
        parent.text(value, x + w / 2.0f, y + h / 2.0f);


        parent.popStyle();
    }

    private float computeTextSize() {
        float textSize = (w > h ? h : w) * 2.0f / 3.0f;
        if (Integer.toString(value).length() > 2) {
            float ts = PApplet.map(Integer.toString(value).length(), 3, 5, 1.5f, .5f);
            textSize = (w > h ? h : w) * ts / 3.0f;
        }
        return textSize < 3 ? 3 : textSize;
    }

    public int getValue() {
        return value;
    }

    public boolean isPenetrable() {
        return isPenetrable;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public PApplet getParent() {
        return parent;
    }
}
