package tests;

import jui_lib.Event;
import jui_lib.JNode;
import jui_lib.Label;
import processing.core.PApplet;

/**
 * Created by Jiachen on 30/04/2017.
 */
public class EventListenerTest extends PApplet {
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
        Label label = new Label("label", 20, 20, 90, 15);
        label.addEventListener("event1",Event.MOUSE_ENTERED,()->{
            System.out.println("mouse entered");
        });

        label.addEventListener("event1",Event.MOUSE_LEFT,()->{
            System.out.println("mouse left");
        });

        label.addEventListener("event1",Event.MOUSE_PRESSED,()->{
            System.out.println("mouse pressed");
        });

        label.addEventListener("event1",Event.MOUSE_WHEEL,()->{
            System.out.println("mouse wheeling");
        });
        JNode.add(label);
    }

    public void draw() {
        background(255);
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