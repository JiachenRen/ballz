package jui_lib;


import jui_lib.bundles.ValueSelector;
import processing.core.PConstants;

import static processing.core.PConstants.CENTER;

//code refactored Jan 30th.
public class Button extends Contextual implements Controllable {
    private boolean mousePressedOnButton;
    private boolean mouseOverTriggered;

    /*TODO add event listeners with enum*/
    private Runnable onClickMethod, mousePressedMethod, mouseHeldMethod, mouseOverMethod, mouseFloatMethod;

    public Button(String id, float x, float y, float w, float h) {
        super(id, x, y, w, h);
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
        setContent("button");
        setAlign(CENTER);
        setTextStyle(JStyle.VOLATILE);
        setBackgroundStyle(JStyle.VOLATILE);
    }

    public void display() {
        //updating the user defined methods in the background
        update();

        //drawing the background
        if (font != null) getParent().textFont(font);
        super.display();

        //render text
        super.displayText();
    }

    private void update() {
        if (isMouseOver()) {
            if (getParent().mousePressed) {
                if (mouseHeldMethod != null)
                    mouseHeldMethod.run();
            } else {
                if (mouseFloatMethod != null)
                    mouseFloatMethod.run();
                if (!mouseOverTriggered) {
                    //the mouseOverMethod should run only one time
                    if (mouseOverMethod != null)
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
            if (mousePressedMethod != null)
                mousePressedMethod.run();
        }
    }

    public void mouseReleased() {
        if (isMouseOver() && mousePressedOnButton && isVisible())
            if (onClickMethod != null)
                onClickMethod.run();
        mousePressedOnButton = false;
    }

    //deprecated action receivers for the action listener methods.
    public void keyPressed() {
    }

    public void keyReleased() {
    }

    public void mouseDragged() {

    }

    public Button setDefaultContent(String temp) {
        this.setContent(temp);
        this.defaultContent = temp;
        return this;
    }

    public Button setContent(String temp) {
        super.setContent(temp);
        return this;
    }
}
