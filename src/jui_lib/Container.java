package jui_lib;

import jui_lib.bundles.ColorSelector;
import processing.core.PConstants;

import java.util.ArrayList;
import java.util.Iterator;

//code refactored Jan 20th
//idea: Jan 21th, granting both the VBox and HBox the ability to actualize both relative width and height.
//TODO April 23rd: add setCollapseInvisible() method
public abstract class Container extends Displayable {
    public ArrayList<Displayable> displayables;
    public boolean containerVisible; // the container is visible only if the objects it contains are visible.
    public float marginX = JNode.UNI_CONTAINER_MARGIN_X, marginY = JNode.UNI_CONTAINER_MARGIN_Y;
    public float spacing = JNode.UNI_CONTAINER_SPACING; //modified Jan 26th.
    public int alignH, alignV;
    public boolean collapseInvisible;

    public Container(String id, float x, float y, float w, float h) {
        super(id, x, y, w, h);
        init();
    }

    public Container(String id, float relativeW, float relativeH) {
        super(id, relativeW, relativeH);
        init();
    }

    public Container(String id) {
        super(id);
        init();
    }

    public void init() {
        displayables = new ArrayList<>();
        this.setAlign(PConstants.LEFT, PConstants.UP);
    }

    public void display() {
        if (containerVisible) {
            /*code cleaned up April 22nd*/
            super.display();
        }
        Iterator<Displayable> iterator = displayables.iterator();
        while (iterator.hasNext()) {
            Displayable displayable = iterator.next();
            if (displayable.isVisible())
                displayable.run();
            if (displayable.refreshRequested()) {
                displayable.requestProcessed();
                syncSize();
                arrange();
            }
        }
    }

    public Container setContainerVisible(boolean temp) {
        containerVisible = temp;
        return this;
    }

    public Container setMarginX(float temp) {
        setMargins(temp, marginY);
        return this;
    }

    public Container setMarginY(float temp) {
        setMargins(marginX, temp);
        return this;
    }

    public Container setMargins(float marginX, float marginY) {
        this.marginX = marginX;
        this.marginY = marginY;
        syncSize();
        arrange();
        return this;
    }

    public Container setSpacing(float temp) {
        this.spacing = temp;
        syncSize();
        arrange();
        return this;
    }

    public Container setAlign(int horizontal, int vertical) {
        this.alignH = horizontal;
        this.alignV = vertical;
        arrange();
        return this;
    }

    public Container setAlignH(int horizontal) {
        this.alignH = horizontal;
        arrange();
        return this;
    }

    public Container setAlignV(int vertical) {
        this.alignV = vertical;
        arrange();
        return this;
    }

    public Container setCollapseInvisible(boolean temp) {
        collapseInvisible = temp;
        syncSize();
        arrange();
        return this;
    }

    public float getMarginX() {
        return marginX;
    }

    public float getMarginY() {
        return marginY;
    }

    public boolean containerIsVisible() {
        return containerVisible;
    }

    public abstract float availableSpace();

    public Container add(Displayable displayable) {
        displayable.setRelative(true);
        if (!JNode.getDisplayables().contains(displayable))
            JNode.add(displayable);
        this.displayables.add(displayable);
        //intended to synchronize the size addthe sub-class objects according to their
        syncSize();
        //intended to arrange the coordinates of the sub-class objects accordingly with their width.
        arrange();
        return this;
    }

    public abstract void syncSize();

    public abstract void arrange();

    public boolean contains(Displayable temp) {
        if (displayables.contains(temp)) return true;
        return false;
    }

    /**
     * Method refactored April 24th. Removed an error where an ArrayIndexOutOfBounds
     * would have been thrown. The method goes through all stacks of containers and
     * removes all of the displayable instances that qualify for removal, including
     * the sub-containers themselves.
     *
     * @param obj the generic displayable obj to be removed from the stack
     */
    public Container remove(Displayable obj) {
        if (displayables == null) return this; /*again, this is here to prevent an error thrown by Table!*/
        for (int i = displayables.size() - 1; i >= 0; i--) {
            Displayable displayable = displayables.get(i);
            if (displayable instanceof Container) {
                Container c = (Container) displayable;
                c.remove(obj);
            }
            displayables.remove(obj);
            //JNode.remove(obj);
        }
        syncSize();
        arrange();
        return this;
    }

    /**
     * removes all displayables contained in this container;
     * all reference to this displayable is then removed from JNode.
     *
     * @since April 24th code cleaned up by Jiachen Ren
     */
    public Container removeAll() {
        for (int i = displayables.size() - 1; i >= 0; i--) {
            Displayable displayable = displayables.get(i);
            if (displayable instanceof Container) {
                Container c = (Container) displayable;
                c.removeAll();
            }
            this.remove(displayable);
        }
        syncSize();
        arrange();
        return this;
    }

    /**
     * the inherited method is overridden as the displayable objects
     * contained within this container would also need to be resized.
     *
     * @param w the new width of the container
     * @param h the new height of the container
     */
    @Override
    public void resize(float w, float h) {
        super.resize(w, h);
        syncSize();
        arrange();
    }

    @Override
    public void relocate(float x, float y) {
        super.relocate(x, y);
        arrange();
    }

    @Override
    public Displayable setVisible(boolean temp) {
        super.setVisible(temp);
        if (displayables == null) return this; /*this is here to prevent null pointer exception thrown by Table.*/
        for (int i = displayables.size() - 1; i >= 0; i--) {
            Displayable displayable = displayables.get(i);
            if (displayable instanceof Container) {
                Container c = (Container) displayable;
                c.setVisible(temp);
            } else {
                displayable.setVisible(temp);
            }
        }
        return this;
    }

    public static void refresh() {
        for (Container container : JNode.getContainers()) {
            container.syncSize();
            container.arrange();
        }
    }

    public Container setDisplayables(ArrayList<Displayable> displayables) {
        this.displayables = displayables;
        return this;
    }

    /**
     * Applies all the applicable layouts specific to this container to all of
     * the sub-containers/displayables. All matter considering the alignment settings
     * specific to containers objects are not applied since the distinction
     * between VBox and HBox.
     */
    public Container applyLayoutToNodes() {
        for (int i = displayables.size() - 1; i >= 0; i--) {
            Displayable displayable = displayables.get(i);
            /*instanceof, learned April 22nd.*/
            if (displayable instanceof Container) {
                Container container = (Container) displayable;
                container.setContainerVisible(containerVisible);
                container.setSpacing(spacing);
                container.setMargins(marginX, marginY);
                container.applyLayoutToNodes();
            }
        }
        return this;
    }

    public Container applyStyleToNodes() {
        for (int i = displayables.size() - 1; i >= 0; i--) {
            Displayable displayable = displayables.get(i);
            displayable.setBackgroundColor(backgroundColor);
            displayable.setMouseOverBackgroundColor(mouseOverBackgroundColor);
            displayable.setMousePressedBackgroundColor(mousePressedBackgroundColor);
            //TODO displayable.setBackgroundStyle(backgroundStyle);
            displayable.setContourVisible(displayContour);
            displayable.setContourColor(contourColor);
            displayable.setContourThickness(contourThickness);
            displayable.setRounded(isRounded);
            displayable.setRounding(rounding);

            /*instanceof, learned April 22nd.*/
            if (displayable instanceof Container) {
                Container container = (Container) displayable;
                container.applyStyleToNodes();
            }
        }
        return this;
    }

    /**
     * @return the displayable ArrayList.
     */
    public ArrayList<Displayable> getDisplayables() {
        return displayables;
    }

    /**
     * This method does not go through the stacks of containers that it contains
     *
     * @param id the id of the requested displayable obj
     * @return the first displayable obj with the correct id
     */
    public Displayable getDisplayableById(String id) {
        for (Displayable displayable : displayables)
            if (displayable.getId().equals(id))
                return displayable;
        return null;
    }

    /**
     * recursively search for Displayable objects in the subContainers
     *
     * @param id the id for the Displayable
     * @return the first Displayable with the id in the front-most stack
     */
    public Displayable search(String id) {
        for (Displayable displayable : displayables) {
            if (displayable.getId().equals(id))
                return displayable;
            else if (displayable instanceof Container) {
                Displayable displayable1 = ((Container) displayable).search(id);
                if (displayable1 != null)
                    return displayable1;
            }
        }
        return null;
    }

    public int visibleDisplayables() {
        int c = 0;
        for (Displayable displayable : displayables)
            if (displayable.isVisible())
                c++;
        return c;
    }
}


