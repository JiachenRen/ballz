package jui_lib;

import processing.core.PConstants;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

//code refactored Jan 27th, class name changed to TextInput
//refactored Feb 4th. Debugged the issue where the textSize won't refresh, which is caused by the abstracting of the displayText() method.
public class TextInput extends Contextual implements Controllable {
    //add setAlign and shift down. Done. Jan 21.
    //add onFocus method. Idea Jan 26th.
    //considering allowing the user to change textSize with a ratio that factors into the total height. No. discouraged. Jan 26th.
    //using event listeners instead of Runnable?
    private boolean isFocusedOn;
    private boolean isLockedOn;
    private boolean displayCursor;
    private boolean shiftDown;
    private String static_content;
    private String temp;
    private String defaultContent = "text";
    private int timer;
    private int cursorColor;
    private float cursorThickness;
    public int timesSubmitted;
    private Runnable submitMethod;
    private boolean commandDown;

    public TextInput(String id, float x, float y, float w, float h) {
        super(id, x, y, w, h);
        textSize = (int) (h * 2 / 3);
        init();
    }

    public TextInput(String id, float relativeW, float relativeH) {
        super(id, relativeW, relativeH);
        init();
    }

    public TextInput(String id) {
        super(id);
        init();
    }

    private void init() {
        setBackgroundStyle(JStyle.VOLATILE);
        setTextStyle(JStyle.VOLATILE);
        setContent(defaultContent);
        static_content = "";
        submitMethod = () -> {
        };
        timer = getParent().millis();
        cursorColor = contourColor;
        cursorThickness = contourThickness;
        alignment = PConstants.LEFT;
    }

    public boolean isFocusedOn() {
        return isFocusedOn;
    }

    public void display() {
        //drawing the background
        if (font != null) getParent().textFont(font);
        super.display();
        //if focused, draw the cursor
        getParent().pushMatrix();
        getParent().textSize(textSize);
        temp = getContent();
        for (int i = 0; i < getContent().length(); i++) {
            temp = getContent().substring(getContent().length() - i - 1, getContent().length());
            if (JNode.getParent().textWidth(temp) > w) {
                temp = getContent().substring(getContent().length() - i, getContent().length());
                break;
            }
        }
        if (isFocusedOn) {
            displayCursor();
        }

        super.displayText(temp);

        getParent().popMatrix();
    }

    public TextInput onSubmit(Runnable temp_method) {
        submitMethod = temp_method;
        return this;
    }

    public TextInput setCursorColor(int c) {
        cursorColor = JNode.getParent().color(c);
        return this;
    }

    public TextInput setCursorColor(int r, int g, int b) {
        cursorColor = JNode.getParent().color(r, g, b);
        return this;
    }

    public TextInput setCursorColor(int r, int g, int b, int t) {
        cursorColor = JNode.getParent().color(r, g, b, t);
        return this;
    }

    public TextInput setCursorThickness(int strokeWeight) {
        cursorThickness = strokeWeight;
        return this;
    }

    private void displayCursor() {

        getParent().stroke(cursorColor);
        getParent().strokeWeight(cursorThickness);
        if (textSize > 0.0) getParent().textSize(textSize);

        if (displayCursor) {
            if (temp.equals("")) {
                switch (alignment) {
                    case PConstants.LEFT:
                        getParent().line(x + 2, y, x + 2, y + h);
                        break;
                    case PConstants.CENTER:
                        getParent().line(x + w / 2, y, x + w / 2, y + h);
                        break;
                    case PConstants.RIGHT:
                        getParent().line(x + w - 2, y, x + w - 2, y + h);
                        break;
                }
            } else {
                switch (alignment) {
                    case PConstants.LEFT:
                        getParent().line(x + JNode.getParent().textWidth(temp), y, x + JNode.getParent().textWidth(temp), y + h);
                        break;
                    case PConstants.CENTER:
                        getParent().line(x + w / 2 + JNode.getParent().textWidth(temp) / 2, y, x + w / 2 + JNode.getParent().textWidth(temp) / 2, y + h);
                        break;
                    case PConstants.RIGHT:
                        getParent().line(x + w - 2, y, x + w - 2, y + h);
                        break;
                }
            }
        }
        if (getParent().millis() - timer >= 500) {
            timer = getParent().millis();
            displayCursor = !displayCursor;
        }
    }

    public void keyPressed() {
        if (this.isFocusedOn) {
            switch (getParent().keyCode) {
                case 8:
                    if (getContent().length() > 0)
                        setContent(getContent().substring(0, getContent().length() - 1));
                    break;
                case 10:
                    static_content = getContent();
                    timesSubmitted++;
                    submitMethod.run();
                    break;
                case 16:
                    shiftDown = true;
                    break;
                case 157:
                    commandDown = true;
                    break;
                default:
                    if (shiftDown) {
                        //if (key <= 'z' && key >= 'a')
                        setContent(getContent() + Character.toUpperCase(getParent().key));
                    } else if (commandDown) {
                        if (getParent().key == 'v') {
                            /*modified March 8th.*/
                            try {
                                ArrayList<String> storedInputs = new ArrayList<>();
                                String data = (String) Toolkit.getDefaultToolkit()
                                        .getSystemClipboard().getData(DataFlavor.stringFlavor);
                                Scanner scanner = new Scanner(data);
                                while (scanner.hasNext()) {
                                    storedInputs.add(scanner.nextLine());
                                }
                                for (String s : storedInputs) {
                                    setContent(getContent() + s);
                                }
                            } catch (UnsupportedFlavorException | IOException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        setContent(getContent() + Character.toLowerCase(getParent().key));
                        //changed for better compatibility with FX2D. Jan 26th.

                    }
                    break;
            }
        }
    }

    public void keyReleased() {
        switch (getParent().keyCode) {
            case 16:
                shiftDown = false;
                break;
            case 157:
                commandDown = false;
                break;
        }
    }

    public void mousePressed() {
        if (isMouseOver()) this.isLockedOn = true;
        else {
            this.isLockedOn = false;
            this.isFocusedOn = false;
        }
    }

    public void mouseReleased() {
        if (isMouseOver() && isLockedOn) {
            if (this.getContent().equals(defaultContent))
                this.setContent("");
            this.isFocusedOn = true;
        } else {
            this.isLockedOn = false;
            this.isFocusedOn = false;
            if (this.getContent().equals("")) this.setContent(defaultContent);
        }
    }

    public void mouseDragged() {
        //deprecated
    }

    public TextInput setStaticContent(String temp) {
        static_content = temp;
        setContent(temp);
        return this;
    }

    public TextInput setDefaultContent(String temp) {
        this.setContent(temp);
        this.defaultContent = temp;
        return this;
    }

    public String getStaticContent() {
        return static_content;
    }

    public String getDefaultContent() {
        return defaultContent;
    }

    public int getIntValue() {
        try {
            return Integer.valueOf(getStaticContent());
        } catch (NumberFormatException e) {
            System.out.println("id = " + id);
            System.err.println("\"" + getStaticContent() + "\" can not be converted to a number.");
            return 0;
        }
    }

    public float getFloatValue() {
        try {
            return Float.valueOf(getStaticContent());
        } catch (NumberFormatException e) {
            System.out.println("id = " + id);
            System.err.println("\"" + getStaticContent() + "\" can not be converted to a number.");
            return 0;
        }
    }

    @Override
    public void resize(float w, float h) {
        super.resize(w, h);
        textSize = (int) (h * 2 / 3);
    }

    @Override
    public TextInput setMousePressedBackgroundColor(int r, int g, int b) {
        //deprecated.
        return this;
    }

    @Override
    public TextInput setTextSize(int temp) {
        return this;
    }
}