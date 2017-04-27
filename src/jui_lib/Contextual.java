package jui_lib;

import game_objs.Context;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PFont;

import static processing.core.PConstants.RIGHT;

//code refactored Jan 18,the Displayable interface is changed into a superclass. Modified by Jiachen Ren
//code refactored Jan 20,the superclass Displayable remained as the parent, the actual parent for all the text based objects are now changed to Contextual.
//add mouseOverTextColor & mousePressedTextColor
public abstract class Contextual extends Displayable {
    private String content;
    private int textColor = JNode.TEXT_COLOR;
    private int mouseOverTextColor = JNode.MOUSE_OVER_TEXT_COLOR;
    private int mousePressedTextColor = JNode.MOUSE_PRESSED_TEXT_COLOR;
    private JStyle textStyle = JStyle.CONSTANT;
    public int textSize;
    public float fontScalar = JNode.FONT_SCALAR;
    public PFont font = JNode.UNI_FONT;
    public String defaultContent = "";
    public int alignment;

    public Contextual(String id, float x, float y, float w, float h) {
        super(id, x, y, w, h);
        content = defaultContent;
    }

    public Contextual(String id, float relativeW, float relativeH) {
        super(id, relativeW, relativeH);
    }

    public Contextual(String id) {
        super(id);
    }

    public Contextual setAlign(int alignment) {
        this.alignment = alignment;
        return this;
    }

    public float[] getTextDimension(String temp) {
        if (textSize > 0.0) getParent().textSize(textSize); //initialize the text size
            //if (font != null) getParent().textFont(font); //initalize the text font
        else return new float[]{0.0f, 0.0f};
        return new float[]{JNode.getParent().textWidth(temp), getParent().textAscent() - getParent().textDescent() * fontScalar};
    }

    public Contextual setTextColor(int r, int g, int b) {
        textColor = JNode.getParent().color(r, g, b);
        return this;
    }

    public Contextual setTextColor(int c) {
        textColor = c;
        return this;
    }

    public Contextual setTextColor(int r, int g, int b, int t) {
        textColor = JNode.getParent().color(r, g, b, t);
        return this;
    }

    public Contextual setMousePressedTextColor(int r, int g, int b) {
        mousePressedTextColor = JNode.getParent().color(r, g, b);
        return this;
    }

    public Contextual setMousePressedTextColor(int c) {
        mousePressedTextColor = c;
        return this;
    }

    public Contextual setMousePressedTextColor(int r, int g, int b, int t) {
        mousePressedTextColor = JNode.getParent().color(r, g, b, t);
        return this;
    }

    public Contextual setMouseOverTextColor(int r, int g, int b) {
        mouseOverTextColor = JNode.getParent().color(r, g, b);
        return this;
    }

    public Contextual setMouseOverTextColor(int c) {
        mouseOverTextColor = c;
        return this;
    }

    public Contextual setMouseOverTextColor(int r, int g, int b, int t) {
        mouseOverTextColor = JNode.getParent().color(r, g, b, t);
        return this;
    }


    public int getTextColor() {
        return textColor;
    }

    ;

    public Contextual setTextSize(int temp) {
        textSize = temp;
        return this;
    }

    public Contextual setTextFont(PFont pf) {
        font = pf;
        return this;
    }

    public void display() {
        super.display();
    }

    public String getContent() {
        return content;
    }

    public Contextual setContent(String temp) {
        content = temp;
        return this;
    }

    public void displayText() {
        getParent().pushMatrix();
        if (textSize > 0.0) getParent().textSize(textSize);

        applyTextColor();

        switch (alignment) {
            case PConstants.LEFT:
                getParent().textAlign(PConstants.LEFT);
                getParent().text(content, x, y + h / 2 + getTextDimension(content)[1] / 2);
                break;
            case PConstants.CENTER:
                getParent().textAlign(PConstants.CENTER);
                getParent().text(content, x + w / 2, y + h / 2 + getTextDimension(content)[1] / 2);
                break;
            case PConstants.RIGHT:
                getParent().textAlign(RIGHT);
                getParent().text(content, x + w, y + h / 2 + getTextDimension(content)[1] / 2);
                break;
            default:
                System.err.println(id + ": align-" + alignment + "cannot be applied to Label. Default alignment applied.");
                getParent().textAlign(PConstants.LEFT);
                getParent().text(content, x, y + h / 2 + getTextDimension(content)[1] / 2);
        }

        getParent().popMatrix();
    }

    /**
     * @since April 27th an absolutely ridiculous error has been fixed. It appears to
     * me that the error happened inside of processing's PApplet. It keeps throwing a bug
     * where the pushMatrix() call has been called more than 32 times... Nevertheless, the
     * bug is actually caused by a null pointer exception as the JStyle instance's not yet
     * being initialized.
     */
    public void applyTextColor(){
        if (textStyle.equals(JStyle.CONSTANT)){
            getParent().fill(textColor);
        }else if (textStyle.equals(JStyle.VOLATILE)){
            if (isMouseOver()) {
                int color = getParent().mousePressed ? mousePressedTextColor : mouseOverTextColor;
                getParent().fill(color);
            } else {
                getParent().fill(textColor);
            }
        }
    }

    public void displayText(String s) {
        String temp = content;
        content = s;
        displayText();
        content = temp;
    }

    public Contextual setTextStyle(JStyle textStyle) {
        this.textStyle = textStyle;
        return this;
    }

    public void setFontScalar(float temp) {
        fontScalar = temp;
    }
}