package tests;

import jui_lib.JNode;
import processing.core.PApplet;

/**
 * Created by Jiachen on 22/04/2017.
 */
public class Template extends PApplet {
    public static void main(String args[]) {
        System.out.println("VSlider Driver Testing");
        String sketch = Thread.currentThread().getStackTrace()[1].getClassName();
        Thread proc = new Thread(() -> PApplet.main(sketch));
        proc.start();
    }

    public void settings() {
        size(800, 600, P2D);
        pixelDensity(2);
    }

    public void setup() {
        JNode.init(this);
    }

    public void draw() {
        JNode.run();
    }

    public void mousePressed() {
        JNode.mousePressed();  //linking to node
    }

    public void mouseReleased() {
        JNode.mouseReleased();
    }

    public void mouseDragged() {
        JNode.mouseDragged();
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