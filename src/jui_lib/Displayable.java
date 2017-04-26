package jui_lib;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;

//code refactored Jan 18,the Displayable interface is changed into a superclass. Modified by Jiachen Ren
//add setBackground. Task completed. Background enum added April 22nd.
//modified April 22nd. Took me half an hour, I eliminated all rounding errors for containers!
//primitive type for coordinate and dimension is changed from int to float. Proved to be helpful!
//refresh requesting technique applied April 23rd
public class Displayable {
    public boolean displayContour = true;
    public boolean isVisible = true;
    public int backgroundColor = JNode.getParent().color(255, 255, 255, 150);
    public int mouseOverBackgroundColor = JNode.getParent().color(50, 255, 100, 180);
    public int mousePressedBackgroundColor = JNode.getParent().color(50, 255, 50, 220);
    public int contourColor = JNode.getParent().color(0);
    public float contourThickness = 1;
    public String id;
    public float x, y, w, h;
    public int rounding = JNode.UNI_ROUNDING;
    public float relativeW = 1, relativeH = 1;
    public BackgroundStyle backgroundStyle = BackgroundStyle.CONSTANT;
    public ImgStyle imgStyle = ImgStyle.RESERVED;
    public PImage backgroundImg;
    private Runnable attachedMethod;
    private boolean refreshRequested;
    // is the relative width/height going to be auto-assigned?
    // is the dimension going to be relative?

    public boolean isRelative, isDependent, isRounded;

    public enum BackgroundStyle {
        CONSTANT(0), VOLATILE(1), DISABLED(2);
        private int val;

        BackgroundStyle(int i) {
            val = i;
        }

        public int getValue() {
            return val;
        }
    }

    //TODO
    public enum ImgStyle {
        RESERVED, STRETCH
    }

    public Displayable(String id, float relativeW, float relativeH) {
        this.id = id;
        setRelativeW(relativeW);
        setRelativeH(relativeH);
        this.isRelative = true;
    }

    public Displayable(String id) {
        this.id = id;
        this.isRelative = true;
        this.isDependent = true;
    }

    public Displayable(String id, float x, float y, float w, float h) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    public boolean isMouseOver() {
        return JNode.getParent().mouseX >= x && (JNode.getParent().mouseX <= x + w && (JNode.getParent().mouseY >= y && (JNode.getParent().mouseY <= y + h)));
    }

    public boolean isDependent() {
        return isDependent;
    }

    public Displayable setDependent(boolean temp) {
        isDependent = temp;
        return this;
    }

    public Displayable setRounded(boolean temp) {
        isRounded = temp;
        return this;
    }

    public Displayable setRounding(int temp) {
        rounding = temp;
        //setRounded(true); removed April 26th.
        return this;
    }

    public boolean isRelative() {
        return isRelative;
    }

    public float getRelativeW() {
        return relativeW;
    }

    public float getRelativeH() {
        return relativeH;
    }

    public Displayable setRelative(boolean temp) {
        isRelative = temp;
        refreshRequested = true;
        return this;
    }

    public Displayable setRelativeW(float temp) {
        relativeW = temp;
        isDependent = false;
        refreshRequested = true;/*this might take long. Consider optimization.*/
        return this;
    }

    public Displayable setRelativeH(float temp) {
        relativeH = temp;
        isDependent = false;
        refreshRequested = true;
        return this;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public boolean isDisplayingCoutour() {
        return displayContour;
    }

    public Displayable setVisible(boolean temp) {
        isVisible = temp;
        return this;
    }

    public Displayable setId(String temp) {
        id = temp;
        return this;
    }

    public String getId() {
        return id;
    }

    public float[] getDimension() {
        return new float[]{w, h};
    }

    public float[] getCoordinate() {
        return new float[]{x, y};
    }

    public float getWidth() {
        return w;
    }

    public float getHeight() {
        return h;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public Displayable setContourColor(int r, int g, int b) {
        contourColor = JNode.getParent().color(r, g, b);
        return this;
    }

    public Displayable setContourColor(int c) {
        contourColor = c;
        return this;
    }

    public Displayable setContourColor(int r, int g, int b, int t) {
        contourColor = JNode.getParent().color(r, g, b, t);
        return this;
    }

    public Displayable setContourThickness(float thickness) {
        contourThickness = thickness;
        return this;
    }

    public Displayable setContourVisible(boolean temp) {
        displayContour = temp;
        return this;
    }

    public Displayable setBackgroundColor(int r, int g, int b) {
        backgroundColor = JNode.getParent().color(r, g, b);
        return this;
    }

    public Displayable setBackgroundColor(int c) {
        backgroundColor = c;
        return this;
    }

    public Displayable setBackgroundColor(int r, int g, int b, int t) {
        backgroundColor = JNode.getParent().color(r, g, b, t);
        return this;
    }

    public Displayable setMouseOverBackgroundColor(int r, int g, int b, int t) {
        mouseOverBackgroundColor = JNode.getParent().color(r, g, b, t);
        return this;
    }

    public Displayable setMouseOverBackgroundColor(int c) {
        mouseOverBackgroundColor = c;
        return this;
    }

    public Displayable setMouseOverBackgroundColor(int r, int g, int b) {
        mouseOverBackgroundColor = JNode.getParent().color(r, g, b);
        return this;
    }

    public Displayable setMousePressedBackgroundColor(int r, int g, int b, int t) {
        mousePressedBackgroundColor = JNode.getParent().color(r, g, b, t);
        return this;
    }

    public Displayable setMousePressedBackgroundColor(int c) {
        mousePressedBackgroundColor = c;
        return this;
    }

    public Displayable setMousePressedBackgroundColor(int r, int g, int b) {
        mousePressedBackgroundColor = JNode.getParent().color(r, g, b);
        return this;
    }

    /*modified March 8th*/
    public Displayable attachMethod(Runnable runnable) {
        attachedMethod = runnable;
        return this;
    }

    public Runnable getAttachedMethod() {
        return attachedMethod;
    }

    public void run() {
        display();
        if (attachedMethod != null) {
            attachedMethod.run();
        }
    }

    public void display() {
        /*default displaying method. Overriding recommended*/
        getParent().pushStyle();
        getParent().rectMode(PConstants.CORNER);
        if (displayContour) {
            getParent().strokeWeight(contourThickness);
            getParent().stroke(contourColor);
        } else {
            getParent().noStroke();
        }

        switch (backgroundStyle) {
            case CONSTANT:
                getParent().fill(backgroundColor);
                break;
            case VOLATILE:
                if (isMouseOver()) {
                    getParent().fill(getParent().mousePressed ? mousePressedBackgroundColor : mouseOverBackgroundColor);
                } else {
                    getParent().fill(backgroundColor);
                }
                break;
            case DISABLED:
                break;
        }

        if (isRounded) {
            getParent().rect(x, y, w, h, rounding);
        } else {
            getParent().rect(x, y, w, h);
        }

        if (backgroundImg != null) {
            getParent().imageMode(PConstants.CENTER);
            float tx = x + w / 2, ty = y + h / 2;
            switch (imgStyle) {
                case RESERVED:
                    float imgWidth = backgroundImg.width;
                    float imgHeight = backgroundImg.height;
                    if (imgWidth > w) {
                        float scale = w / imgWidth;
                        imgWidth = w;
                        imgHeight *= scale;
                    }
                    if (imgHeight > h) {
                        float scale = h / imgHeight;
                        imgHeight = h;
                        imgWidth *= scale;
                    }
                    getParent().image(backgroundImg, tx, ty, imgWidth, imgHeight);
                    break;
                case STRETCH:
                    break;
            }
        }
        getParent().popStyle();
    }

    public void relocate(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void resize(float w, float h) {
        this.w = w;
        this.h = h;
    }

    boolean refreshRequested() {
        return refreshRequested;
    }

    void requestProcessed() {
        refreshRequested = false;
    }

    public Displayable setBackgroundStyle(BackgroundStyle backgroundStyle) {
        this.backgroundStyle = backgroundStyle;
        return this;
    }

    public Displayable setWidth(float temp) {
        resize(temp, h);
        return this;
    }

    public Displayable setHeight(float temp) {
        resize(w, temp);
        return this;
    }

    public Displayable setX(float temp) {
        relocate(temp, y);
        return this;
    }

    public Displayable setY(float temp) {
        relocate(x, temp);
        return this;
    }

    public PApplet getParent() {
        return JNode.getParent();
    }

    public PImage getBackgroundImg() {
        return backgroundImg;
    }

    public Displayable setBackgroundImg(PImage backgroundImg) {
        this.backgroundImg = backgroundImg;
        return this;
    }
}