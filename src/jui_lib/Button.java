package jui_lib;


import jui_lib.bundles.ValueSelector;
import processing.core.PConstants;

import static processing.core.PConstants.CENTER;

//code refactored Jan 30th.
public class Button extends Contextual implements Controllable {
    private boolean mousePressedOnButton;
    private boolean mouseOverTriggered;
    private float trim_w, trim_h;
    private String defaultContent = "my button";

    /*TODO add event listeners with enum*/
    private Runnable onClickMethod, mousePressedMethod, mouseHeldMethod, mouseOverMethod, mouseFloatMethod;

    public Button(String id, float x, float y, float w, float h) {
        super(id, x, y, w, h);
        textSize = (int) h;
        init();
    }

    public Button(String id, float relativeW, float relativeH) {
        super(id, relativeW, relativeH);
        init();
    }

    public Button(String id) {
        super(id);
        init();
    }

    public void init() {
        setContent(defaultContent);
        setAlign(CENTER);
        setBackgroundStyle(BackgroundStyle.VOLATILE);
        onClickMethod = mousePressedMethod = mouseHeldMethod = mouseOverMethod = mouseFloatMethod = () -> {
        };
        setTrimWidth(7);
        setTrimHeight(8);
    }

    public void display() {
        //updating the user defined methods in the background
        update();

        //drawing the background
        if (font != null) getParent().textFont(font);
        super.display();

        //render text
        getParent().fill(getTextColor());
        super.displayText();
    }

    private void update() {
        if (isMouseOver()) {
            if (getParent().mousePressed) mouseHeldMethod.run();
            else {
                mouseFloatMethod.run();
                if (!mouseOverTriggered) {
                    //the mouseOverMethod should run only one time
                    mouseOverMethod.run();
                    mouseOverTriggered = true;
                }
            }
        } else {
            mouseOverTriggered = false;
        }
    }

    public Button onClick(Runnable temp_method) {
        onClickMethod = temp_method;
        return this;
    }

    public Button onMousePressed(Runnable temp_method) {
        mousePressedMethod = temp_method;
        return this;
    }

    public Button onMouseHeld(Runnable temp_method) {
        mouseHeldMethod = temp_method;
        return this;
    }

    public Button onMouseOver(Runnable temp_method) {
        mouseOverMethod = temp_method;
        return this;
    }

    public Button onMouseFloat(Runnable temp_method) {
        mouseFloatMethod = temp_method;
        return this;
    }

    //action receivers
    public void mousePressed() {
        if (isMouseOver() && isVisible()) {
            mousePressedOnButton = true;
            mousePressedMethod.run();
        }
    }

    public void mouseReleased() {
        if (isMouseOver() && mousePressedOnButton && isVisible()) onClickMethod.run();
        mousePressedOnButton = false;
    }

    //deprecated action receivers for the action listener methods.
    public void keyPressed() {
    }

    public void keyReleased() {
    }

    public void mouseDragged() {
    }

    private void adjustTextSize() {
        if (h <= 0) return;
        textSize = (int) h;
        float[] dim = getTextDimension(this.getContent());
        while (dim[0] > w - trim_w || dim[1] > h - trim_h) {
            if (textSize < 6) break;
            textSize--;
            dim = getTextDimension(getContent());
        }
    }

    public Button setTrimWidth(float temp) {
        trim_w = temp;
        adjustTextSize();
        return this;
    }

    public Button setTrimHeight(float h) {
        trim_h = h;
        adjustTextSize();
        return this;
    }

    public Button setDefaultContent(String temp) {
        this.setContent(temp);
        this.defaultContent = temp;
        return this;
    }


    /* overridden methods */
    @Override
    public Button setContent(String temp) {
        super.setContent(temp);
        adjustTextSize();
        return this;
    }

    @Override
    public void resize(float w, float h) {
        super.resize(w, h);
        adjustTextSize();
    }

    @Deprecated
    public Button setTextSize(int temp) {
        /* deprecated. */
        return this;
    }
}
