package jui_lib;

import processing.core.PConstants;

public class VSlider extends Slider implements Controllable {

    public VSlider(String id, float x, float y, float w, float h) {
        super(id, x, y, w, h);
    }

    public VSlider(String id, float relativeW, float relativeH) {
        super(id, relativeW, relativeH);
    }

    public VSlider(String id) {
        super(id);
    }

    public void childDefinedInit() {
        setRollerScalingHeight(.5f);
        setRollerScalingWidth(1.5f);
        setRollerScalingRadius(.6f);
        syncSettings();
    }

    //Jan 4th, TODO Roller Shape Ellipse
    public void updateRollerPos() {
        if (val > valueHigh || val < valueLow) {
            if (val != valueLow && val != valueHigh)
                System.err.println("ERROR: slider value cannot be set to " + val + ", out of range(" + valueLow + "->" + valueHigh + ")");
            return;
        }
        float temp = roller.shape == PConstants.ELLIPSE ? roller.r * 2 : roller.h;
        roller.setY(this.y + barHeight - temp / 2.0f - (val - valueLow) / (valueHigh - valueLow) * (barHeight - temp));
    }


    public void syncSettings() {
        barWidth = (int) (w * barScalingFactor);
        if (roller.shape == PConstants.RECT) roller.y = y + h - roller.h / 2;
        else roller.y = y + h - roller.r;
        roller.x = x + w / 2;
        roller.setEllipse((int) (barWidth * getRollerScalingRadius()));
        roller.setRect((int) (barWidth * getRollerScalingWidth()), (int) (barWidth * getRollerScalingHeight()));
        barHeight = h;
    }

    public VSlider setBarScalingFactor(float temp) {
        barScalingFactor = temp;
        syncSettings();
        return this;
    }

    public void mouseDragged() {
        if (isLockedOn) {
            roller.y = getParent().mouseY;
            roller.x = x + w / 2;
        }
        switch (roller.shape) {
            case PConstants.ELLIPSE:
                roller.y = roller.y < y + roller.r ? y + roller.r : roller.y;
                roller.y = roller.y > y + h - roller.r ? y + h - roller.r : roller.y;
                break;
            case PConstants.RECT:
                roller.y = roller.y < y + roller.h / 2 ? y + roller.h / 2 : roller.y;
                roller.y = roller.y > y + h - roller.h / 2 ? y + h - roller.h / 2 : roller.y;
                break;
        }
    }

    @Override
    public void mousePressed() {
        super.mousePressed();
        if (mouseOverBar())
            roller.y = getParent().mouseY;
        if(mouseOverRoller())
            onFocusMethod.run();
    }

    public void drawGrid() {
        getParent().pushStyle();
        getParent().strokeWeight(gridDotSize);
        getParent().stroke(gridDotColor);
        float d = 0;
        float curX = x + getWidth() / 2, curY = 0;
        switch (roller.shape) {
            case PConstants.ELLIPSE:
                curY = y + roller.r;
                d = gridInterval / (valueHigh - valueLow) * (h - roller.r * 2);
                break;
            case PConstants.RECT:
                curY = y + roller.h / 2;
                d = gridInterval / (valueHigh - valueLow) * (h - roller.h);
                break;
        }
        while (curY < (roller.shape == PConstants.ELLIPSE ? y + h - roller.r : y + h - roller.h / 2)) {
            getParent().point(curX, curY);
            curY += d;
        }
        getParent().popStyle();
    }

    public float getFloatValue() {
        float val = 0.0f;
        switch (roller.shape) {
            case PConstants.ELLIPSE:
                val = ((y + h - roller.r) - roller.y) / (h - roller.r * 2) * (valueHigh - valueLow) + valueLow;
                break;
            case PConstants.RECT:
                val = ((y + h - roller.h / 2) - roller.y) / (h -
                        roller.h) * (valueHigh - valueLow) + valueLow;
                break;
        }
        val = val < valueLow ? valueLow : val;
        return val > valueHigh ? valueHigh : val;
    }

}