package jui_lib;

import processing.core.PConstants;

//contains and arranges horizontal displayable objects.
public class HBox extends Container {
    //never use relative if this object is at the top level.

    public HBox(String id, float x, float y, float w, float h) {
        super(id, x, y, w, h);
    }

    public HBox(String id, float relativeW, float relativeH) {
        super(id, relativeW, relativeH);
    }

    public HBox(String id) {
        super(id);
    }

    public void syncSize() {
        int num = 0;
        for (Displayable displayable : displayables) {
            if (!displayable.isRelative() || displayable.isDependent())
                continue;
            num++;
            if (availableSpace() == -1 && getWidth() >= 0) {
                displayable.setWidth(0);
                continue;
            }
            displayable.setHeight((this.getHeight() - marginY * 2) * displayable.getRelativeH());
            displayable.setWidth(availableWidth() * displayable.getRelativeW());
        }
        num = displayables.size() - num;
        if (num == 0) return; // no more left. Unless you want some big errors!!!!
        float w = availableSpace() / (float) num;
        for (Displayable displayable : displayables) {
            if (!displayable.isRelative() || !displayable.isDependent()) continue;
            displayable.setHeight(this.getHeight() - marginY * 2);
            displayable.setWidth(w);
        }
    }

    public void arrange() {
        float cur_x = this.getX() + marginX, cur_y = this.getY() + marginY;
        if (alignV == PConstants.DOWN) cur_y = this.getY() + getHeight() - marginY;
        switch (alignH) {
            case PConstants.LEFT:
                for (Displayable displayable : displayables) {
                    if (alignV == PConstants.DOWN)
                        displayable.relocate(cur_x, cur_y - displayable.getHeight());
                    else displayable.relocate(cur_x, cur_y);
                    cur_x += displayable.getWidth() + spacing; // modified Jan 26th. replaced marginX with spacing
                }
                break;
            case PConstants.RIGHT:
                cur_x = this.getX() + getWidth() - marginX;
                for (Displayable displayable : displayables) {
                    cur_x -= displayable.getWidth();
                    if (alignV == PConstants.DOWN)
                        displayable.relocate(cur_x, cur_y - displayable.getHeight());
                    else displayable.relocate(cur_x, cur_y);
                    cur_x -= spacing;
                }
                break;
            default:
                System.err.println("Error: alignH-" + alignH + " not applicable for horizontal, default alignment applied.");
                for (Displayable displayable : displayables) {
                    displayable.relocate(cur_x, cur_y);
                    cur_x += displayable.getWidth() + spacing;
                }
                break;
        }
    }

    /**
     * @return the available space for formatting the displayables. (minus all spacing and margins)
     * @since April 25th differentiated between collapse invisible and retain space.
     */
    public float availableWidth() {
        /*modified Jan 26th by Jiachen Ren*/
        int sp = collapseInvisible ? visibleDisplayables() : displayables.size(); // occurrence of spacing
        return this.getWidth() - (sp - 1) * spacing - 2 * marginX;
    }

    public float availableSpace() {
        if (w <= 0) return -1;
        float occupied = 0;
        for (Displayable displayable : displayables) {
            if (!displayable.isRelative() || displayable.isDependent()) continue;
            if (collapseInvisible && !displayable.isVisible()) continue;
            occupied += availableWidth() * displayable.getRelativeW();
        }
        if (occupied > availableWidth()) {
            System.err.println(id+": not enough available space for all sub-class displayables");
            return -1;
        }
        return availableWidth() - occupied;
    }
}
