package tests;

import jui_lib.Canvas;
import jui_lib.JNode;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.javafx.PGraphicsFX2D;

/**
 * Created by Jiachen on 22/04/2017.
 */
public class Template extends PApplet {
    PGraphics graphics;

    public static void main(String args[]) {
        System.out.println("VSlider Driver Testing");
        String sketch = Thread.currentThread().getStackTrace()[1].getClassName();
        PApplet.main(sketch);
    }

    public void settings() {
        size(800, 600, FX2D);
        pixelDensity(2);
    }

    public void setup() {
        JNode.init(this);
        graphics = this.createGraphics(400, 800, JAVA2D);
        //graphics.pixelDensity = 2;
        //TODO override PGraphics line() and others in Canvas. 
        graphics.beginDraw();
        graphics.background(255);
        graphics.line(0, 0, graphics.width * 2, graphics.height);
        graphics.line(graphics.width, 0, 0, graphics.height);
        graphics.endDraw();
    }

    public void draw() {
        image(graphics, 10, 10, 200, 200);
        JNode.run();
    }

    public void keyPressed() {
        JNode.keyPressed();
    }

    public void keyReleased() {
        JNode.keyReleased();
    }

    public void mouseWheel() {
        //to be implemented. Jan 27th.
    }
}