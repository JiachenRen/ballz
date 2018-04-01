import jui.Displayable;
import processing.core.PApplet;

import java.util.ArrayList;

/**
 * BlockRow class that contains and arranges each block.
 */
public class BlockRow extends Displayable {
    private ArrayList<Block> blocks;
    private int level;
    private int numBlocks;
    private int currentStack; //keep track of the level.
    private float spaceBetweenBlock;
    private float spaceBetweenRow;
    private PApplet parent;

    BlockRow(PApplet parent, float x, float y, float w, float h, int level) {
        super("", x, y, w, h);
        blocks = new ArrayList<>();
        this.parent = parent;
        this.level = level;
        this.numBlocks = Main.columns; /*default 7, this is the same as the mobile version*/
        currentStack = 0;
        spaceBetweenBlock = Main.columnGap;
        spaceBetweenRow = Main.rowGap;
        init();
    }

    private void init() {
        /*add blocks randomly with spaces in between*/
        float temp = w - spaceBetweenBlock * (numBlocks - 1.0f);
        float blockWidth = temp / (float) numBlocks;
        float tempY = y + spaceBetweenRow, tempH = h - spaceBetweenRow * 2;
        for (int i = 0; i < numBlocks; i++) {
            float tempX = x + i * ((w + spaceBetweenBlock) / (float) numBlocks);
            /*random assortment of the type of block to be created*/
            double seed = Math.random();
            if (seed <= .5) {
                int lev = seed <= .4 ? level : level * 2;
                blocks.add(new Block(parent, tempX, tempY, blockWidth, tempH, lev));
            } else if (seed <= .60) {
                blocks.add(new Bonus(parent, tempX, tempY, blockWidth, tempH));
            } else if (seed <= .65) {
                blocks.add(new Score(parent, tempX, tempY, blockWidth, tempH));
            }
        }
    }

    ArrayList<Block> getBlocks() {
        return blocks;
    }

    @Override
    public BlockRow setY(float temp) {
        this.y = temp;
        for (Block block : blocks) {
            block.setY(y + spaceBetweenRow);
            block.setHeight(h - 2 * spaceBetweenRow);
        }
        return this;
    }

    public void display() {
        for (Block block : blocks) {
            block.display();
        }
    }

    int getCurrentStack() {
        return currentStack;
    }

    void stack() {
        currentStack++;
    }
}
