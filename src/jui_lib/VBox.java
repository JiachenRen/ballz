package jui_lib;

import processing.core.PConstants;

public class VBox extends Container {
    //never use relative if this object is at the top level.
    public VBox(String id, float x, float y, float w, float h) {
        super(id, x, y, w, h);
    }

    public VBox(String id, float relativeW, float relativeH) {
        super(id, relativeW, relativeH);
        setAlignV(PConstants.DOWN);
    }

    public VBox(String id) {
        super(id);
        setAlignV(PConstants.DOWN);
    }

    public void syncSize() {
        int num = 0;
        for (Displayable displayable : displayables) {
            displayable.setWidth(this.getWidth() - marginX * 2);
            if (!displayable.isRelative() || displayable.isDependent()) continue;
            num++;
            if (availableSpace() == -1 && getWidth() >= 0) {
                displayable.setHeight(0);
                continue;
            }
            displayable.setWidth((this.getWidth() - marginX * 2) * displayable.getRelativeW());
            displayable.setHeight(availableHeight() * displayable.getRelativeH());
        }
        num = displayables.size() - num;
        if (num == 0) return;
        float h = availableSpace() / (float) num;
        for (Displayable displayable : displayables) {
            if (!displayable.isRelative() || !displayable.isDependent()) continue;
            displayable.setWidth(this.getWidth() - marginX * 2);
            displayable.setHeight(h);
        }
    }

    /**
     * @since April 25th added Horizontal align CENTER, code cleaned up.
     */
    public void arrange() {
        float cur_x, cur_y = this.getY() + marginY;
        for (Displayable displayable : displayables) {
            switch (alignH) {
                case PConstants.LEFT:
                    cur_x = this.getX() + marginX;
                    break;
                case PConstants.RIGHT:
                    cur_x = this.getX() + getWidth() - marginX - displayable.getWidth();
                    break;
                case PConstants.CENTER:
                    float temp = displayable.getWidth() / 2.0f;
                    cur_x = this.getX() + getWidth() / 2.0f - temp;
                    break;
                default:
                    cur_x = this.getX() + marginX;
            }
            switch (alignV) {
                case PConstants.DOWN:
                    displayable.relocate(cur_x, cur_y);
                    cur_y += displayable.getHeight() + spacing;
                    break;
                case PConstants.UP:
                    cur_y = this.getY() + getHeight() - marginY;
                    cur_y -= displayable.getHeight();
                    displayable.relocate(cur_x, cur_y);
                    cur_y -= spacing;
                    break;
                default:
                    displayable.relocate(cur_x, cur_y);
                    cur_y += displayable.getHeight() + spacing;
                    break;
            }
        }
    }

    public float availableHeight() {
        int sp = displayables.size() - 1;
        return this.getHeight() - sp * spacing - 2 * marginY;
    }

    public float availableSpace() {
        if (h <= 0) return -1;
        float occupied = 0;
        for (Displayable displayable : displayables) {
            if (!displayable.isRelative() || displayable.isDependent()) continue;
            occupied += availableHeight() * displayable.getRelativeH();
        }
        if (occupied > availableHeight()) {
            System.err.println(id+": not enough available space for all sub-class displayables");
            return -1;
        }
        return availableHeight() - occupied;
    }
}