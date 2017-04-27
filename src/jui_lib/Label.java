package jui_lib;

import processing.core.PConstants;

import static processing.core.PApplet.*;

//add public abstract void setValue(float val);
//Jan 29th: deprecated all textHeight dividend variable in TextInput, Button, and Label.
//TODO the adjustTextSize method should be replaced with a better one. April 22nd.
public class Label extends Contextual {
    private float trim_w, trim_h;
    private String defaultContent = "LABEL";

    public Label(String id, float x, float y, float w, float h) {
        super(id, x, y, w, h);
        textSize = (int) (h * 2 / 3);
        init();
    }

    public Label(String id, float relativeW, float relativeH) {
        super(id, relativeW, relativeH);
        init();
    }

    public Label(String id) {
        super(id);
        init();
    }

    public void init() {
        setContent(defaultContent);
        setTextStyle(JStyle.CONSTANT);
        adjustTextSize();
        setAlign(PConstants.LEFT);
    }

    public void adjustTextSize() {
        if (h <= 0) return;
        textSize = (int) h;
        float[] dim = getTextDimension(this.getContent());
        while (dim[0] > w - trim_w || dim[1] > h - trim_h) {
            if (textSize < 6) break;
            textSize--;
            dim = getTextDimension(getContent());
        }
    }

    public void display() {
        //drawing the background
        super.display(); /*modified April 22nd*/
        if (font != null) getParent().textFont(font);
        displayText();
    }


    public Label setTrimWidth(float temp) {
        trim_w = temp;
        adjustTextSize();
        return this;
    }

    public Label setTrimHeight(float h) {
        trim_h = h;
        adjustTextSize();
        return this;
    }

    @Override
    public Label setContent(String temp) {
        super.setContent(temp);
        adjustTextSize();
        return this;
    }

    @Override
    public void resize(float w, float h) {
        super.resize(w, h);
        adjustTextSize();
    }
}