package tests;

import jui_lib.JNode;
import jui_lib.ProgressBar;
import jui_lib.VBox;
import jui_lib.bundles.ColorSelector;
import jui_lib.bundles.ProgressIndicator;
import processing.core.PApplet;
import sun.net.ProgressEvent;

import javax.jnlp.JNLPRandomAccessFile;

public class ProgressBarTest extends PApplet {
    private static float progress;
    private static ProgressIndicator progressIndicator;

    public static void main(String args[]) {
        System.out.println("Progress Bar Test. Created April 29th");
        String sketch = Thread.currentThread().getStackTrace()[1].getClassName();
        Thread proc = new Thread(() -> PApplet.main(sketch));
        proc.start();
    }

    public void settings() {
        size(800, 600, FX2D);
        pixelDensity(2);
    }

    public void setup() {
        JNode.init(this);
        ProgressBar progressBar = new ProgressBar("progressBar", 10, 10, 300, 20);
        JNode.add(progressBar);


        progressIndicator = new ProgressIndicator("progressIndicator", 10, 40, 300, 100);
        progressIndicator.getProgressBar().setPercentageTextStyle(ProgressBar.Style.MIDDLE);
        JNode.add(progressIndicator);

    }

    public void draw() {
        background(255);
        JNode.run();
        noFill();
        stroke(0, 0, 255);
        rect(10, 10, 300, 20);

        ProgressBar p = (ProgressBar) JNode.getById("progressBar").get(0);
        p.setCompletedPercentage(progress);

        progressIndicator.setCompletedPercentage(progress);
        progress += 0.001;


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