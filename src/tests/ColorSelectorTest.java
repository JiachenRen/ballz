package tests;

import jui_lib.HBox;
import jui_lib.JNode;
import jui_lib.bundles.ColorSelector;
import jui_lib.bundles.ValueSelector;
import processing.core.PApplet;

public class ColorSelectorTest extends PApplet {

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
        HBox mainFrame = new HBox("mainFrame", 0, 0, 300, 300);

        /*ColorSelector Class. Created April 22nd.*/
        ColorSelector colorSelector = new ColorSelector("colorSelector");
        colorSelector.setLinkedColorVars("color1", "color2", "color3");
        colorSelector.applyLayoutToNodes();
        mainFrame.add(colorSelector);

        /*ValueSelector Class. Created April 23rd.*/
        ValueSelector valueSelector = new ValueSelector("valueSelector",1.0f,0.2f);
        valueSelector.setTitle("Val");
        mainFrame.add(valueSelector);

        JNode.add(mainFrame);
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
