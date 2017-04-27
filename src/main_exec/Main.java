package main_exec;

import game_objs.Ball;
import game_objs.Context;
import jui_lib.*;
import jui_lib.bundles.ColorSelector;
import jui_lib.bundles.ValueSelector;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;
import processing.core.PVector;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

/*template for JUI designing created by Jiachen Ren, Jan 29th. All Rights Reserved */
/*Ballz PC/Mac replication. Created by Jiachen Ren on April 20th*/
public class Main extends PApplet {
    private static Context context;
    private static VBox gamePanel;
    private static HBox mainFrame;
    private static VBox settingsPanel;
    private static VBox advancedPanel;

    /**
     * game specific attributes
     */
    public static boolean constAcc = false;
    public static float acc = 1.01f;
    public static int ballDiameter = 16;
    public static float ballInitVelocity = 4.5f; /*this defaults to 12.5 at 60 fps*/
    public static int debrisMaxSpeed = 8;/*defaults to 20 at 60 fps*/
    public static int fps = 200;
    public static int level = 1; /*represents the current level; default 1; Try 1500*/
    public static int firingRate = 100;
    private static float gamePanelPercentage = .7f; //try .75
    private static float contextPercentage = 1.0f;
    public static int rows = 8;
    public static int columns = 7;
    public static int columnGap = 5;
    public static int rowGap = columnGap / (int) 2.0f;

    /**
     * colors
     */
    public static int ballColor;
    public static int debrisColor;
    public static int bonusColor;
    public static int scoreColor;
    public static int displayColor;
    public static int uiTextColor;
    public static int uiColor;
    public static int blockColor1 = 150, blockColor2 = 100, blockColor3 = 255;
    /**
     * the values that are to be retrieved from the records.txt
     */
    public static int highScore;
    public static int score;

    /**
     * images to be imported from /data dir
     */
    private PImage playButtonImg;
    private PImage pauseButtonImg;
    private PImage restartButtonImg;
    private PImage fastForwardButtonImg;

    /**
     * the booleans below exist to ensure that the images and settings only
     * gets imported once.
     */
    private static boolean imgLoaded = false;
    private static boolean settingsImported = false;

    public static BufferedReader bufferedReader;

    public static void main(String args[]) {
        System.out.println("Ballz Mac/PC Version. Created by Jiachen Ren on April 20th");
        String sketch = Thread.currentThread().getStackTrace()[1].getClassName();
        Thread proc = new Thread(() -> PApplet.main(sketch));
        proc.start();
    }

    public void settings() {
        /*the frame for the game is originally 380 * 600 on Iphone*/
        fullScreen(FX2D); //or P2D
        //size(1000, 850, P3D);

        /*optimized for mac retina display. Set this to 1 if you are using windows*/
        pixelDensity(1);
    }

    private void loadImages() {
        if (imgLoaded) return;
        playButtonImg = loadImage("resume_button.png");
        pauseButtonImg = loadImage("pause_button.png");
        restartButtonImg = loadImage("restart_button.png");
        fastForwardButtonImg = loadImage("fast_forward_button.png");
        imgLoaded = true;
    }

    private void importSettings() {
        if (settingsImported) return;
        ArrayList<String> lines = new ArrayList<>();
        bufferedReader = createReader("src/data/records.txt");
        while (true) {
            try {
                String line = bufferedReader.readLine();
                if (line != null) lines.add(line);
                else break;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        for (String s : lines) {
            if (s.contains("high_score")) {
                String str = s.split(":")[1];
                highScore = Integer.valueOf(str);
                println("High Score Retrieved: " + highScore);
            }
        }
        settingsImported = true;
    }

    /**
     * the method rewrites the records.txt file.
     *
     * @param keyWord the keyword(string) that precedes the value
     * @param val     the value that is been used to replace the old one
     */
    public static void record(String keyWord, Number val) {
        PApplet parent = JNode.getParent();
        String lines[] = parent.loadStrings("src/data/records.txt");
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].contains(keyWord)) {
                lines[i] = keyWord + ":" + val.toString();
            }
        }
        parent.saveStrings("src/data/records.txt", lines);
    }

    public void setup() {
        Ball.initPosRecorded = false;

        importSettings();
        loadImages();

        /*initialize JUI Main Frame*/
        JNode.init(this);
        mainFrame = new HBox("mainFrame", 0, 0, width, height);
        JNode.add(mainFrame);

        /*setup game UI*/
        gamePanel = new VBox("gameUI", gamePanelPercentage, 1.0f);
        mainFrame.add(gamePanel);

        HBox topBar = new HBox("topBar", 1.0f, .05f);
        topBar.setMarginX(0);
        gamePanel.add(topBar);

        /*button to stop, resume the game*/
        /*updated with JUI's new powerful syntax on April 25th */
        Button stop = new Button("stop", .1f, 1.0f);
        stop.setContent("").onClick(() -> {
            PImage current = stop.getBackgroundImg();
            stop.setBackgroundImg(current == pauseButtonImg ? playButtonImg : pauseButtonImg);
            context.setPaused(current == pauseButtonImg);
        }).setBackgroundImg(pauseButtonImg);
        topBar.add(stop);

        /*button to restart the game*/
        Displayable restart = new Button("restart", .1f, 1.0f)
                .setContent("")
                .onClick(this::setup)
                .setBackgroundImg(restartButtonImg);
        topBar.add(restart);

        /*label that indicates the current level*/
        Contextual level = new Label("level")
                .setContent("Level")
                .setAlign(CENTER);
        topBar.add(level);

        /*button that allows the player to fast forward.*/
        Button fastForward = new Button("fastForward", .1f, 1.0f);
        fastForward.setContent("");
        fastForward.setBackgroundImg(fastForwardButtonImg);
        fastForward.attachMethod(() -> {
            if (fastForward.isMouseOver() && mousePressed) {
                frameRate(400);
                for (Ball ball : context.getBalls()) {
                    ball.setSpeed(18);
                }
            }
        });
        topBar.add(fastForward);

        /*
        implement main game executable context, id defaults to "context",
        do not modify the properties of the context here, it would take no effect.
        modify where indicated instead.
         */
        context = new Context();
        context.setColorMode(3);
        Container contextContainer = new HBox("contextContainer")
                .setMargins(0, 0)
                .add(context);
        gamePanel.add(contextContainer);

        /*UI group that locates below the main game container*/
        Container bottomBar = new HBox("bottomBar", 1.0f, .05f)
                .setMarginX(0);
        gamePanel.add(bottomBar);

        /*label that indicates the number of balls fired*/
        Label ballsFired = new Label("ballsFired");
        ballsFired.setContent("Balls Fired:");
        bottomBar.add(ballsFired);

        /*label that indicates the high score*/
        Label highScore = new Label("highScore");
        highScore.setContent("High Score: " + Main.highScore);
        bottomBar.add(highScore);

        /*label that shows the points*/
        Label score = new Label("score", .1f, 1.0f);
        score.setContent(Main.score + "");
        score.setAlign(CENTER);
        bottomBar.add(score);

        /*label that indicates the number of balls recovered*/
        Label ballsRecovered = new Label("ballsRecovered");
        ballsRecovered.setContent("Balls Recovered: ");
        bottomBar.add(ballsRecovered);

        /*the section below is for the settings band advanced panels*/
        /*settings panel that contains game specific settings*/
        settingsPanel = new VBox("settingsPanel");
        settingsPanel.setAlignV(DOWN);
        mainFrame.add(settingsPanel);

        /*settings label and the wrapper at the top*/
        VBox settingsLabelWrapper = new VBox("settingsLabelWrapper", 1.0f, 0.05f);
        settingsPanel.add(settingsLabelWrapper);

        Label settingsLabel = new Label("settingsLabel");
        settingsLabel.setContent("Settings");
        settingsLabelWrapper.add(settingsLabel);

        /*settings items wrapper that contains all other displayables*/
        VBox settingsItemsWrapper = new VBox("settingsItemsWrapper");
        settingsItemsWrapper.setAlignV(DOWN);
        settingsPanel.add(settingsItemsWrapper);

        /*
        acceleration UI
        */
        HBox accLabelWrapper = new HBox("accLabelWrapper", 1.0f, 0.03f);
        accLabelWrapper.setMargins(0, 0);
        settingsItemsWrapper.add(accLabelWrapper);

        Label accLabel = new Label("accLabel");
        accLabel.setContent("Acc");
        accLabelWrapper.add(accLabel);

        Label accValueLabel = new Label("accValueLabel");
        String accStr = Float.toString(acc);
        accValueLabel.setContent(accStr.substring(0, accStr.length() > 5 ? 5 : accStr.length()));
        accLabelWrapper.add(accValueLabel);

        Button accEnabledButton = new Button("accEnabledButton");
        accEnabledButton.setContent(constAcc ? "On" : "Off");
        accEnabledButton.onClick(() -> {
            constAcc = !constAcc;
            accEnabledButton.setContent(constAcc ? "On" : "Off");
        });
        accLabelWrapper.add(accEnabledButton);

        HSlider accSlider = new HSlider("widthPercentageSlider", 1.0f, 0.03f);
        accSlider.setRollerShape(RECT);
        accSlider.setBarScalingFactor(0.5f);
        accSlider.setRange(1.005f, 1.018f);
        accSlider.setValue(acc);
        accSlider.onFocus(() -> {
            acc = accSlider.getFloatValue();
            String accStr1 = Float.toString(acc);
            accValueLabel.setContent(accStr1.substring(0, accStr1.length() > 5 ? 5 : accStr.length()));
        });
        settingsItemsWrapper.add(accSlider);

        /*
        fps UI
         */
        HBox fpsLabelWrapper = new HBox("fpsLabelWrapper", 1.0f, 0.03f);
        fpsLabelWrapper.setMargins(0, 0);
        settingsItemsWrapper.add(fpsLabelWrapper);

        Label fpsLabel = new Label("fpsLabel");
        fpsLabel.setContent("FPS");
        fpsLabelWrapper.add(fpsLabel);

        HSlider fpsSlider = new HSlider("fpsSlider", 1.0f, 0.03f);
        TextInput fpsTextInput = new TextInput("fpsTextInput");
        fpsTextInput.setDefaultContent(200 + "");
        fpsTextInput.onSubmit(() -> {
            fps = fpsTextInput.getIntValue();
            fpsSlider.setValue(fps);
            frameRate(fps);
        });
        fpsLabelWrapper.add(fpsTextInput);

        fpsSlider.setRollerShape(RECT);
        fpsSlider.setBarScalingFactor(0.5f);
        fpsSlider.setRange(60, 400);
        fpsSlider.setValue(fps);
        fpsSlider.onFocus(() -> {
            fps = fpsSlider.getIntValue();
            frameRate(fps);
            fpsTextInput.setContent(fps + "");
        });
        settingsItemsWrapper.add(fpsSlider);

        /*
        ball speed UI
         */
        HBox speedLabelWrapper = new HBox("speedLabelWrapper", 1.0f, 0.03f);
        speedLabelWrapper.setMargins(0, 0);
        settingsItemsWrapper.add(speedLabelWrapper);

        Label speedLabel = new Label("speedLabel");
        speedLabel.setContent("Speed");
        speedLabelWrapper.add(speedLabel);

        HSlider speedSlider = new HSlider("speedSlider", 1.0f, 0.03f);
        TextInput speedTextInput = new TextInput("speedTextInput");
        speedTextInput.setDefaultContent(ballInitVelocity + "");
        speedTextInput.onSubmit(() -> {
            ballInitVelocity = speedTextInput.getFloatValue();
            speedSlider.setValue(ballInitVelocity);
            for (Ball ball : context.getBalls())
                ball.setSpeed(ballInitVelocity);
        });
        speedLabelWrapper.add(speedTextInput);

        speedSlider.setRollerShape(RECT);
        speedSlider.setBarScalingFactor(0.5f);
        speedSlider.setRange(1, 17);
        speedSlider.setValue(ballInitVelocity);
        speedSlider.onFocus(() -> {
            ballInitVelocity = speedSlider.getFloatValue();
            String temp = ballInitVelocity + "";
            temp = temp.substring(0, temp.length() > 5 ? 5 : temp.length());
            speedTextInput.setContent(temp);
            for (Ball ball : context.getBalls())
                ball.setSpeed(ballInitVelocity);
        });
        settingsItemsWrapper.add(speedSlider);

        /*
        ball radius UI
         */
        HBox diameterLabelWrapper = new HBox("diameterLabelWrapper", 1.0f, 0.03f);
        diameterLabelWrapper.setMargins(0, 0);
        settingsItemsWrapper.add(diameterLabelWrapper);

        Label diameterLabel = new Label("diameterLabel");
        diameterLabel.setContent("Diameter");
        diameterLabelWrapper.add(diameterLabel);

        HSlider diameterSlider = new HSlider("diameterSlider", 1.0f, 0.03f);
        TextInput diameterTextInput = new TextInput("diameterTextInput");
        diameterTextInput.setDefaultContent(ballDiameter + "");
        diameterTextInput.onSubmit(() -> {
            ballDiameter = diameterTextInput.getIntValue();
            diameterSlider.setValue(ballDiameter);
            for (Ball ball : context.getBalls())
                ball.setDiameter(ballDiameter);
        });
        diameterLabelWrapper.add(diameterTextInput);

        diameterSlider.setRollerShape(RECT);
        diameterSlider.setBarScalingFactor(0.5f);
        diameterSlider.setRange(8, 30);
        diameterSlider.setValue(ballDiameter);
        diameterSlider.onFocus(() -> {
            ballDiameter = diameterSlider.getIntValue();
            diameterTextInput.setContent(ballDiameter + "");
            for (Ball ball : context.getBalls())
                ball.setDiameter(ballDiameter);
        });
        settingsItemsWrapper.add(diameterSlider);

        /*
        UI that enables the user to adjust the level.
         */
        Label adjustLevelLabel = new Label("adjustLevelLabel", 1.0f, 0.025f);
        adjustLevelLabel.setContent("Set Level");
        settingsItemsWrapper.add(adjustLevelLabel);

        TextInput adjustLevelTextInput = new TextInput("adjustLevelTextInput", 1.0f, 0.025f);
        adjustLevelTextInput.setStaticContent(Main.level + "");
        adjustLevelTextInput.onSubmit(() -> {
            Main.level = adjustLevelTextInput.getIntValue();
            context.setLevel(Main.level);
            level.setContent("Level " + Main.level);
        });
        settingsItemsWrapper.add(adjustLevelTextInput);

        /*
        UI that enables the user to adjust the number of balls.
         */
        Label adjustBallNumLabel = new Label("adjustBallNumLabel", 1.0f, 0.025f);
        adjustBallNumLabel.setContent("Balls");
        settingsItemsWrapper.add(adjustBallNumLabel);

        TextInput adjustBallNumTextInput = new TextInput("adjustBallNumTextInput", 1.0f, 0.025f);
        adjustBallNumTextInput.setStaticContent(Context.numBalls + "");
        adjustBallNumTextInput.onSubmit(() -> {
            context.setNumBalls(adjustBallNumTextInput.getIntValue());
        });
        settingsItemsWrapper.add(adjustBallNumTextInput);

        /*
        UI that controls the relative width of the context.
         */
        ValueSelector contextWidthSelector = new ValueSelector("contextWidthSelector", 1.0f, 0.08f);
        contextWidthSelector.setStyle(ValueSelector.Style.COMPOSITE);
        contextWidthSelector.setTitle("Context Width");
        contextWidthSelector.setTitlePercentage(0.2f);
        contextWidthSelector.setRange(0.3f, 1.0f);
        contextWidthSelector.setValue(Main.contextPercentage);
        contextWidthSelector.link(() -> {
            Main.contextPercentage = contextWidthSelector.getFloatValue();
            context.setRelativeW(Main.contextPercentage);
        });
        settingsItemsWrapper.add(contextWidthSelector);

        settingsItemsWrapper.add(new Displayable("spaceHolder", 1.0f, 0.05f).setVisible(false));
        settingsItemsWrapper.add(new Label("uiLayoutLabel", 1.0f, 0.04f).setContent("User Interface"));

        Button displayUiContourButton = new Button("displayUiContourButton", 1.0f, 0.025f).setContent("Display Contour");
        displayUiContourButton.onClick(() -> {
            boolean temp = displayUiContourButton.getContent().equals("Display Contour");
            displayUiContourButton.setContent(temp ? "Hide Contour" : "Display Contour");
            for (Displayable displayable : JNode.getDisplayables()) {
                displayable.setContourVisible(temp);
            }
        });
        settingsItemsWrapper.add(displayUiContourButton);

        Button uiContainerVisibleButton = new Button("uiContainerVisibleButton", 1.0f, 0.025f).setContent("Show Container");
        uiContainerVisibleButton.onClick(() -> {
            boolean temp = uiContainerVisibleButton.getContent().equals("Show Container");
            uiContainerVisibleButton.setContent(temp ? "Hide Container" : "Show Container");
            for (Displayable displayable : JNode.getDisplayables()) {
                if (displayable instanceof Container) {
                    ((Container) displayable).setContainerVisible(temp);
                }
            }
        });
        settingsItemsWrapper.add(uiContainerVisibleButton);

        Button roundedButton = new Button("roundedButton", 1.0f, 0.025f).setContent("Rounded");
        roundedButton.onClick(() -> {
            boolean temp = roundedButton.getContent().equals("Rounded");
            roundedButton.setContent(temp ? "Rectangular" : "Rounded");
            for (Displayable displayable : JNode.getDisplayables()) {
                displayable.setRounded(!temp);
            }
        });
        settingsItemsWrapper.add(roundedButton);

        ValueSelector uiRoundingSelector = new ValueSelector("uiRoundingSelector", 1.0f, 0.06f)
                .setTitle("Rounding Index")
                .setRange(3, 15)
                .setValue(5);
        uiRoundingSelector.link(() -> {
            for (Displayable displayable : JNode.getDisplayables()) {
                int temp = uiRoundingSelector.getIntValue();
                displayable.setRounding(temp);
            }
        });
        settingsItemsWrapper.add(uiRoundingSelector);

        ValueSelector uiStrokeWeightSelector = new ValueSelector("uiStrokeWeightSelector", 1.0f, 0.06f)
                .setTitle("Stroke Weight")
                .setRange(0, 3)
                .setValue(1);
        uiStrokeWeightSelector.link(() -> {
            for (Displayable displayable : JNode.getDisplayables()) {
                int temp = uiStrokeWeightSelector.getIntValue();
                displayable.setContourThickness(temp);
            }
        });
        settingsItemsWrapper.add(uiStrokeWeightSelector);

        settingsItemsWrapper.add(new Displayable("spaceHolder", 1.0f, 0.05f).setVisible(false));
        settingsItemsWrapper.add(new Label("uiLayoutLabel", 1.0f, 0.04f).setContent("General"));
        /*
        width UI
         */
        HBox widthLabelWrapper = new HBox("widthLabelWrapper", 1.0f, 0.03f);
        widthLabelWrapper.setMargins(0, 0);
        settingsItemsWrapper.add(widthLabelWrapper);

        Label widthLabel = new Label("widthLabel", .7f, 1.0f);
        widthLabel.setContent("Width");
        widthLabelWrapper.add(widthLabel);

        Label widthValueLabel = new Label("widthValueLabel");
        widthValueLabel.setContent((int) (gamePanelPercentage * 100) + "%");
        widthLabelWrapper.add(widthValueLabel);

        /*JUI slider that controls the width of the game*/
        HSlider widthPercentageSlider = new HSlider("widthPercentageSlider", 1.0f, 0.03f);
        widthPercentageSlider.setRollerShape(RECT);
        widthPercentageSlider.setBarScalingFactor(0.5f);
        widthPercentageSlider.setRange(.3f, .8f);
        widthPercentageSlider.setValue(gamePanelPercentage);
        widthPercentageSlider.onFocus(() -> {
            gamePanelPercentage = widthPercentageSlider.getFloatValue();
            widthValueLabel.setContent((int) (gamePanelPercentage * 100) + "%");
        });
        settingsItemsWrapper.add(widthPercentageSlider);

        Button applyWidthButton = new Button("applyWidthButton", 1.0f, 0.04f);
        applyWidthButton.setContent("Apply");
        applyWidthButton.onClick(this::setup);
        settingsItemsWrapper.add(applyWidthButton);



        /*advanced settings panel that provides more detailed controls*/
        advancedPanel = new VBox("advancedPanel");
        //advancedPanel.setContainerVisible(true);
        mainFrame.add(advancedPanel);

        VBox advancedLabelWrapper = new VBox("advancedLabelWrapper", 1.0f, 0.05f);
        advancedPanel.add(advancedLabelWrapper);

        Label advancedLabel = new Label("advancedLabel");
        advancedLabel.setContent("Advanced");
        advancedLabelWrapper.add(advancedLabel);

        /*Color Selector for various objects in the game*/
        ColorSelector colorSelector = new ColorSelector("colorSelector", 1.0f, .3f);
        colorSelector.setLinkedColorVars("Balls", "Debris", "Bonus", "Score", "Display", "Background", "UI Text", "UI App");
        colorSelector.setColorRGBA("Balls", 255, 255, 255, 255);
        colorSelector.setColorRGBA("Debris", 255, 200, 10, 200);
        colorSelector.setColorRGBA("Background", 0, 0, 0, 255);
        colorSelector.setColorRGBA("Bonus", 255, 255, 255, 220);
        colorSelector.setColorRGBA("Score", 102, 255, 255, 220);
        colorSelector.setColorRGBA("Display", 255, 255, 255, 255);
        colorSelector.setColorRGBA("UI Text", 0, 0, 0, 255);
        colorSelector.setColorRGBA("UI App", 50, 50, 50, 50);

        colorSelector.link("Display", () -> {
            displayColor = colorSelector.getColorRGBA("Display");
        });
        colorSelector.link("Score", () -> {
            scoreColor = colorSelector.getColorRGBA("Score");
        });
        colorSelector.link("Background", () -> {
            context.setBackgroundColor(colorSelector.getColorRGBA("Background"));
        });
        colorSelector.link("Debris", () -> {
            debrisColor = colorSelector.getColorRGBA("Debris");
        });
        colorSelector.link("Balls", () -> {
            ballColor = colorSelector.getColorRGBA("Balls");
        });

        /*a change listener, for computational expensive operations*/
        colorSelector.link("UI Text", () -> {
            int temp = colorSelector.getColorRGBA("UI Text");
            if (uiTextColor != temp) {
                for (Displayable displayable : JNode.getDisplayables()) {
                    if (displayable instanceof Contextual) {
                        ((Contextual) displayable).setTextColor(temp);
                    }
                }
                uiTextColor = temp;
            }
        });
        colorSelector.link("UI App", () -> {
            int temp = colorSelector.getColorRGBA("UI App");
            if (uiColor != temp) {
                for (Displayable displayable : JNode.getDisplayables()) {
                    displayable.setBackgroundColor(temp);
                }
                uiColor = temp;
            }
        });
        ballColor = colorSelector.getColorRGBA("Balls");
        debrisColor = colorSelector.getColorRGBA("Debris");
        bonusColor = colorSelector.getColorRGBA("Bonus");
        scoreColor = colorSelector.getColorRGBA("Score");
        displayColor = colorSelector.getColorRGBA("Display");
        advancedPanel.add(colorSelector);


        ValueSelector firingRateSelector = new ValueSelector("firingRateSelector");
        firingRateSelector.setTitle("Fire Rate(ms)");
        firingRateSelector.setTitlePercentage(0.4f);
        firingRateSelector.setRange(10, 1000);
        firingRateSelector.setValue(firingRate);
        firingRateSelector.link(() -> {
            firingRate = firingRateSelector.getIntValue();
        });
        advancedPanel.add(firingRateSelector);

        ValueSelector blockColor1Selector = new ValueSelector("blockColor1Selector");
        blockColor1Selector.setTitle("Block Color A");
        blockColor1Selector.setTitlePercentage(0.7f);
        blockColor1Selector.setRange(0, 255);
        blockColor1Selector.setValue(blockColor1);
        blockColor1Selector.link(() -> {
            blockColor1 = blockColor1Selector.getIntValue();
        });
        advancedPanel.add(blockColor1Selector);

        ValueSelector blockColor2Selector = new ValueSelector("blockColor2Selector");
        blockColor2Selector.setTitle("Block Color B");
        blockColor2Selector.setTitlePercentage(0.7f);
        blockColor2Selector.setRange(0, 255);
        blockColor2Selector.setValue(blockColor2);
        blockColor2Selector.link(() -> {
            blockColor2 = blockColor2Selector.getIntValue();
        });
        advancedPanel.add(blockColor2Selector);

        ValueSelector blockColor3Selector = new ValueSelector("blockColor3Selector");
        blockColor3Selector.setTitle("Block Color B");
        blockColor3Selector.setTitlePercentage(0.7f);
        blockColor3Selector.setRange(0, 255);
        blockColor3Selector.setValue(blockColor3);
        blockColor3Selector.link(() -> {
            blockColor3 = blockColor3Selector.getIntValue();
        });
        advancedPanel.add(blockColor3Selector);

        /*experimental: an expandable & collapsible container*/
        VBox dimensionSelectorsWrapper = new VBox("dimensionSelectorsWrapper", 1.0f, 0.35f);
        dimensionSelectorsWrapper.setAlignH(LEFT).setMargins(0, 0).setVisible(false);
        Button showMoreOptionsButton = new Button("showMoreOptionsButton", 1.0f, 0.03f)
                .setContent("More");
        showMoreOptionsButton.onClick(() -> {
            boolean temp = dimensionSelectorsWrapper.isVisible();
            dimensionSelectorsWrapper.setVisible(!temp);
            String str = showMoreOptionsButton.getContent();
            showMoreOptionsButton.setContent(str.equals("More") ? "Less" : "More");
        });
        advancedPanel.add(showMoreOptionsButton);
        advancedPanel.add(dimensionSelectorsWrapper);

        ValueSelector rowsSelector = new ValueSelector("rowsSelector");
        rowsSelector.setTitle("Rows          ");
        rowsSelector.setTitlePercentage(0.4f);
        rowsSelector.roundTo(0);
        rowsSelector.setRange(4, 20);
        rowsSelector.setValue(rows);
        dimensionSelectorsWrapper.add(rowsSelector);

        ValueSelector columnsSelector = new ValueSelector("columnsSelector");
        columnsSelector.setTitle("Columns      ");
        columnsSelector.setTitlePercentage(0.4f);
        columnsSelector.roundTo(0);
        columnsSelector.setRange(4, 20);
        columnsSelector.setValue(columns);
        dimensionSelectorsWrapper.add(columnsSelector);

        ValueSelector columnGapSelector = new ValueSelector("columnGapSelector");
        columnGapSelector.setTitle("Column Gap");
        columnGapSelector.setTitlePercentage(0.4f);
        columnGapSelector.roundTo(0);
        columnGapSelector.setRange(0, 100);
        columnGapSelector.setValue(columnGap);
        dimensionSelectorsWrapper.add(columnGapSelector);

        ValueSelector rowGapSelector = new ValueSelector("rowGapSelector");
        rowGapSelector.setTitle("Row Gap     ");
        rowGapSelector.setTitlePercentage(0.4f);
        rowGapSelector.roundTo(0);
        rowGapSelector.setRange(0, 100);
        rowGapSelector.setValue(rowGap);
        dimensionSelectorsWrapper.add(rowGapSelector);

        Button applyDimensionButton = new Button("applyDimensionButton", 1.0f, 0.1f);
        applyDimensionButton.setContent("Apply");
        applyDimensionButton.onClick(() -> {
            rowGap = rowGapSelector.getIntValue() / 2;
            columnGap = columnGapSelector.getIntValue();
            columns = columnsSelector.getIntValue();
            rows = rowsSelector.getIntValue();
            setup();
        });
        dimensionSelectorsWrapper.add(applyDimensionButton);


        /*removed April 26th. Redundant. JUI default default sheet is applied.
        mainFrame.setContainerVisible(false);
        mainFrame.setContourVisible(false);
        mainFrame.setBackgroundColor(50, 255, 100, 180);
        mainFrame.setBackgroundStyle(Displayable.BackgroundStyle.VOLATILE);
        mainFrame.setMouseOverBackgroundColor(50, 255, 100, 180);
        mainFrame.setSpacing(3);
        mainFrame.setMargins(3, 3);
        mainFrame.applyStyleToNodes();
        */

        dimensionSelectorsWrapper.setContainerVisible(true);


        context.setBackgroundColor(colorSelector.getColorRGBA("Background"));


        //TODO add container visible UI

        /*TODO adjust the frame rate!*/
        frameRate(fps);
    }

    public void draw() {
        background(Main.displayColor);
        JNode.run();
    }

    public void mousePressed() {
        JNode.mousePressed();  //linking to node
    }

    public void mouseReleased() {
        JNode.mouseReleased();
        context.mouseReleased();
    }

    public void mouseDragged() {
        JNode.mouseDragged();
    }

    public void keyPressed() {
        /*TODO this is for testing, delete*/
        switch (key) {
            case 'x':
                context.addBlockRow();
                break;
        }
        JNode.keyPressed();
    }

    public void keyReleased() {
        JNode.keyReleased();
    }

    public void mouseWheel() {
        //to be implemented. Jan 27th.
    }
}

