package jui_lib;

import processing.core.PApplet;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

//created by Jiachen Ren, Jan 28th. The Table class.
//consider adding margins and spacings as do in Container class. Consider extending the Container class.
//or create an interface that emulates the margin system of the Container class and implement the Table class with it.
public class Table extends Container /*implements Controllable*/ {
    private int rows, columns;
    private ArrayList<Displayable> displayables;
    private boolean tableVisible;
    private float cellMarginX = 2, cellMarginY = 2;

    public Table(String id, float relativeW, float relativeH, int columns, int rows) {
        super(id, relativeW, relativeH);
        this.rows = rows;
        this.columns = columns;
        init();
    }

    public Table(String id, float relativeW, float relativeH) {
        this(id, relativeW, relativeH, 2, 2);
    }

    public Table(String id, float x, float y, float w, float h, int columns, int rows) {
        super(id, x, y, w, h);
        this.rows = rows;
        this.columns = columns;
        init();
    }

    public Table(String id, float x, float y, float w, float h) {
        this(id, x, y, w, h, 2, 2);
    }


    public Table(String id) {
        super(id);
        this.rows = 2;
        this.columns = 2;
        init();
    }

    public void init() {
        /* initializes the matrix that contains the displayable objects. */
        displayables = new ArrayList<>();
        for (int c = 0; c < columns; c++)
            for (int r = 0; r < rows; r++)
                displayables.add(new TextInput(c + " " + r));
        JNode.addAll(displayables);
        tableVisible = true;
    }

    /* action listeners deprecated*/

    public void display() {
        /* display the background and bounds of the table */
        if (isTableVisible()) {
            if (displayContour) {
                getParent().strokeWeight(contourThickness);
                getParent().stroke(contourColor);
            } else {
                getParent().noStroke();
            }
            getParent().fill(backgroundColor);
            if (isRounded) {
                getParent().rect(x, y, w, h, rounding);
            } else {
                getParent().rect(x, y, w, h);
            }
        }
        /* display the sub-class objects */
        if (isVisible())
            for (Displayable displayable : displayables) {
                if (displayable.isVisible())
                    displayable.run();
            }
    }

    //draws the grid for the table
    private void drawGrid() {
        /* to be implemented */
    }

    public Table setTableDimension(int columns, int rows) {
        this.columns = columns;
        this.rows = rows;
        //int removed = 0; //debugging
        fill(); //fill up the empty cell spaces.
        //displayables.forEach(displayable -> System.out.println(displayable.getId())); no problem
        for (int i = displayables.size() - 1; i >= 0; i--) {
            Displayable displayable = displayables.get(i);
            //System.out.println("pointed to : "+displayable.getId());
            int[] pos = getCellPos(displayable);
            //System.out.println("interpreted as: " +pos[0]+" "+pos[1]);
            //System.out.println(rows);
            if (pos[0] >= columns || pos[1] >= rows) {
                remove(pos[0], pos[1]);
                //System.out.println("removed from column: "+pos[0]+" row: "+pos[1]);
                //removed++;
            }
        }
        //System.out.println("removed: "+removed);
        arrangeCells();
        //remember to implement code to resize the sub-objects.
        return this;
    }

    public Table arrangeCells() {
        float cellWidth = (float) getWidth() / columns; //used Math.rint(), since needs rounding down from .5 to .0
        float cellHeight = (float) getHeight() / rows;
        for (Displayable displayable : displayables) {
            int[] pos = getCellPos(displayable);
            displayable.resize(Math.round(cellWidth) - cellMarginX * 2, Math.round(cellHeight) - cellMarginY * 2);
            displayable.relocate(getX() + Math.round(pos[0] * cellWidth) + cellMarginX, getY() + Math.round(pos[1] * cellHeight) + cellMarginY);
        }
        return this;
    }

    private int[] getCellPos(Displayable displayable) {
        String temp = displayable.getId();
        String[] pos = PApplet.split(temp, " ");
        return new int[]{Integer.valueOf(pos[0]), Integer.valueOf(pos[1])};
    }

    public Table setRows(int rows) {
        setTableDimension(this.columns, rows);
        return this;
    }

    public Table setColumns(int columns) {
        setTableDimension(columns, this.rows);
        return this;
    }

    public Table add(int column, int row, Displayable displayable) {
        /* to be implemented */
        if (get(column, row) != null) return this;
        displayable.setId(column + " " + row);
        displayables.add(displayable);
        JNode.add(displayable);
        arrangeCells();
        return this;
    }

    public Table remove(int column, int row) {
        for (int i = 0; i < displayables.size(); i++) {
            if (Arrays.equals(getCellPos(displayables.get(i)), new int[]{column, row})) {
                JNode.remove(displayables.get(i));
                displayables.remove(i);
            }
        }
        return this;
    }

    /*the column/row starts from 0*/
    public Displayable get(int column, int row) {
        for (int i = 0; i < displayables.size(); i++) {
            int[] temp = getCellPos(displayables.get(i));
            if (Arrays.equals(temp, new int[]{column, row})) {
                //System.err.println(temp[0]+" "+temp[1]);
                return displayables.get(i);
            }
        }
        return null;
    }

    public Table set(int column, int row, Displayable displayable) {
        /* to be implemented */
        displayable.setId(column + " " + row);
        Displayable designated = this.get(column, row);
        if (designated != null) {
            remove(column, row);
            add(column, row, displayable);
        }
        arrangeCells();
        return this;
    }

    /*fill in the blanks spaces with textInputs if the table is expanded.*/
    public Table fill() {
        for (int c = 0; c < columns; c++)
            for (int r = 0; r < rows; r++)
                if (get(c, r) == null) displayables.add(new TextInput(c + " " + r));
        JNode.addAll(displayables);
        arrangeCells();
        return this;
    }

    public Table setCellMarginX(float temp) {
        cellMarginX = temp;
        arrangeCells();
        return this;
    }

    public Table setCellMarginY(float temp) {
        cellMarginY = temp;
        arrangeCells();
        return this;
    }

    public Table setCellMargins(float x, float y) {
        setCellMarginX(x);
        setCellMarginY(y);
        return this;
    }

    public ArrayList<Displayable> getDisplayables() {
        return displayables;
    }

    @Deprecated
    public void setContent(String s) {
        /* to be implemented */
    }

    @Override
    public void resize(float w, float h) {
        /* to be implemented */
        super.resize(w, h);
        arrangeCells();
    }

    @Override
    public void relocate(float x, float y) {
        super.relocate(x, y);
        arrangeCells();
    }

    /*March 6th. Took me half 1 hour to find this bug!!!*/
    @Override
    public Table setVisible(boolean temp) {
        super.setVisible(temp);
        for (Displayable displayable : displayables) {
            displayable.setVisible(temp);
        }
        return this;
    }

    public boolean isTableVisible() {
        return tableVisible;
    }

    public Table setTableVisible(boolean tableVisible) {
        setContainerVisible(tableVisible);
        this.tableVisible = tableVisible;
        return this;
    }

    @Deprecated
    public float undeclaredSpace() {
        return 0;
    }

    @Deprecated
    public void arrange() {

    }

    @Deprecated
    public void syncSize() {

    }
}
