package tests;

import jui_lib.*;
import processing.core.PApplet;
import processing.core.PFont;

//JC Generic Purpose User Interface for Process, project initiated by Jiachen Ren Jan 6
//JUI demo, designed by Jiachen Ren Jan 25
//modified Jan 26th
//incorporating into GitHub. Jan 27th
public class JuiTemplate extends PApplet {
    private int brightness = 100;
    private boolean updatingOnDrag; //updates the display when the slider in the middle is dragged.
    private float window_w, window_h;
    private MenuDropdown m, m1, m2;

    public static void main(String[] args) {
        System.out.println("Welcome to JUI Demo!");
        final String sketch = Thread.currentThread().getStackTrace()[1].getClassName();
        Thread proc = new Thread(() -> PApplet.main(sketch));
        proc.start();
    }


    public void settings() {
        size(900, 700, FX2D);
        pixelDensity(2);
    }


    public void setup() {
        println((Object) PFont.list());
        //fullScreen(FX2D);
        surface.setResizable(true);// this is truly exciting!!! Jan 26th.
        //JNode.setUniFont(fbig); //default font for all objects.
        //Incorporated into init().

        JNode.init(this);
        VBox b1 = new VBox("parent", 0, 0, width - 1, height - 1);
        HBox b2 = new HBox("horizontal_bottom", 1, .1f);
        HBox b3 = new HBox("left", .3f, 1);
        HBox b4 = new HBox("right");
        HBox b5 = new HBox("horizontal_middle", 1, .2f);
        VBox b6 = new VBox("hm_v1_left", .3f, 1);
        VBox b7 = new VBox("hm_v2_right");
        VBox b8 = new VBox("horizontal_up");
        HBox b10 = new HBox("hu_down");
        VBox b11 = new VBox("hu_up");
        VBox b12 = new VBox("hu_down_v_left", .2f, 1);
        VBox b13 = new VBox("hu_down_v_right");
        HBox b14 = new HBox("hu_up_h");
        //b11.setMargins(0,0);
        //b1.resize(width/2,height/2);
        b14.setMargins(0, 0);
        //b14.setSpacing(20);
        b11.setAlignV(UP);
        b2.setMargins(0, 0);
        b5.setMargins(0, 0);
        b13.setMargins(0, 0);
        b12.setAlignV(UP);
        b12.setMargins(3, 3);
        b8.setMargins(0, 0);

        HSlider s1;
        s1 = new HSlider("slider");
        s1.setRange(-10000, 10000);
        s1.setRollerShape(RECT);
        s1.setScalingFactor(.4f);
        s1.onFocus(() -> {
            Slider ts = JNode.getSliderById("slider");
            Contextual tl = JNode.getContextualById("label");
            if (tl != null)
                tl.setContent(Integer.toString(ts.getIntValue()));
        });
        b3.add(s1);

        Label l1;
        l1 = new Label("label");
        l1.setContent("Value of Slider");
        l1.setAlign(CENTER);
        b4.add(l1);

        Button bu1;
        bu1 = new Button("submit");
        bu1.setContent("SUBMIT");
        bu1.onClick(() ->
                JNode.getScrollFieldById("sc1").println("clicked button \"SUBMIT\". slider \"slider\" value: " + JNode.getSliderById("slider").getIntValue()
                ));
        b6.add(bu1);

        TextInput t1;
        t1 = new TextInput("textinput-sub");
        t1.setDefaultContent("Input:");
        t1.onSubmit(() -> {
            TextInput t = JNode.getTextInputById("textinput-sub");
            switch (t.getStaticContent()) {
                case "switch":
                    updatingOnDrag = !updatingOnDrag;
                    JNode.getScrollFieldById("sc1").println("updatingOnDrag is now: " + updatingOnDrag);
                    break;
                case "align-left":
                    JNode.getScrollFieldById("sc1").println("aligned to left");
                    JNode.getScrollFieldById("sc1").setAlign(LEFT);
                    break;
                case "align-center":
                    JNode.getScrollFieldById("sc1").println("aligned to center");
                    JNode.getScrollFieldById("sc1").setAlign(CENTER);
                    break;
                case "align-right":
                    JNode.getScrollFieldById("sc1").println("aligned to right");
                    JNode.getScrollFieldById("sc1").setAlign(RIGHT);
                    break;
                default:
                    JNode.getScrollFieldById("sc1").println("input from textInput \"textinput-sub\": " + t.getStaticContent());
            }
        });

        b6.add(t1);

        ScrollField sc1;
        sc1 = new ScrollField("sc1");
        sc1.setRuledLines(true);
        sc1.println("Welcome to JUI. Designed By Jiachen, All Rights Reserved. ");
        sc1.println("All System Operational. ");
        b7.add(sc1);

        Label l2;
        l2 = new Label("label_red", 1, .1f);
        l2.setAlign(CENTER);
        l2.setContent("RED");
        b12.add(l2);

        TextInput t2;
        t2 = new TextInput("textinput_red");
        t2.setDefaultContent("r:");
        t2.setAlign(CENTER);
        b12.add(t2);

        Label l3;
        l3 = new Label("label_green", 1, .1f);
        l3.setAlign(CENTER);
        l3.setContent("GREEN");
        b12.add(l3);

        TextInput t3;
        t3 = new TextInput("textinput_green");
        t3.setDefaultContent("g:");
        t3.setAlign(CENTER);
        b12.add(t3);

        Label l4;
        l4 = new Label("label_blue", 1, .1f);
        l4.setAlign(CENTER);
        l4.setContent("GREEN");
        b12.add(l4);

        TextInput t4;
        t4 = new TextInput("textinput_blue");
        t4.setDefaultContent("b:");
        t4.setAlign(CENTER);
        b12.add(t4);

        ScrollField sc2;
        sc2 = new ScrollField("scrollfield_color");
        sc2.println("Use the TextInput to the left to change color.");
        sc2.println("All System Operational. ");
        sc2.setAlign(CENTER);
        b13.add(sc2);

        HSlider s2;
        s2 = new HSlider("slider_brightness", 1, .2f);
        s2.setRange(0, 255);
        s2.setGridInterval(41);
        s2.setRollerShape(RECT);
        s2.setGridVisible(true);
        s2.setScalingFactor(.5f);
        s2.onFocusMethod = () -> {
            brightness = JNode.getSliderById("slider_brightness").getIntValue();
            if (updatingOnDrag);
                //refresh(); // OPTIONAL
        };
        b13.add(s2);

        Label l5;
        l5 = new Label("label_welcome", 1, .2f);
        l5.setAlign(CENTER);
        l5.setContent("Welcome to JUI");
        b13.add(l5);

        Label l6;
        l6 = new Label("label_unlock", 1, .2f);
        l6.setAlign(CENTER);
        l6.setContent("Unlock the Possiblilities with JUI");
        b11.add(l6);

        Button bu2;
        bu2 = new Button("exit", .3f, 1);
        bu2.setContent("EXIT");
        //bu2.setVisible(false); //testing bug fix Jan 28th, 11:36 PM
        bu2.onClick(() -> Runtime.getRuntime().exit(0));
        b14.add(bu2);

        Button bu3;
        bu3 = new Button("refresh");
        bu3.setContent("REFRESH");
        //bu3.onClick(this::refresh);
        b14.add(bu3);

        //created Jan 29th. 8:47PM. VSlider demo.
        VSlider vs1;
        vs1 = new VSlider("vs1",.05f,1.0f);
        vs1.setRange(-10000, 10000);
        vs1.setRollerShape(RECT);
        vs1.setScalingFactor(.4f);
        b10.add(vs1);

        VSlider vs2;
        vs2 = new VSlider("vs2",.05f,1.0f);
        vs2.setRange(-10000, 10000);
        vs2.setRollerShape(RECT);
        vs2.setScalingFactor(.4f);
        b10.add(vs2);

        VSlider vs3;
        vs3 = new VSlider("vs3",.05f,1.0f);
        vs3.setRange(-10000, 10000);
        vs3.setRollerShape(RECT);
        vs3.setScalingFactor(.4f);
        b10.add(vs3);


        b11.add(b14);

        b10.add(b12);
        b10.add(b13);

        b8.add(b10);
        b8.add(b11);

        b5.add(b6);
        b5.add(b7);

        b2.add(b3);
        b2.add(b4);

        b1.add(b2);
        b1.add(b5);
        b1.add(b8);


        b5.setContainerVisible(true);
        b2.setContainerVisible(true);
        //b1.setContainerVisible(true);
        b10.setContainerVisible(true);
        b11.setContainerVisible(true);
        b12.setContainerVisible(true);


        //b13.setContainerVisible(true);

        //Jan 28th, 9:57PM, Table class testing/debugging. 10:52PM completed!!!! Successful!!!
        Table tb1 = new Table("myTable", 1, .2f, 10, 4);
        tb1.setTableVisible(true);
        //System.out.println(tb1.getDisplayables().size());
        tb1.set(0, 1, new Button(""));
        Button tempb = ((Button) tb1.get(0, 1));
        tempb.setContent("EXIT");
        tempb.onClick(this::exit); //method reference!
        tb1.setTableDimension(20,5); //Debugging Jan 29th. Succeeded 5:42PM. 30 min
        //System.out.println(tb1.getDisplayables().size());
        //add setAlign into the Displayable interface! Jan 28th.
        //actually add interface Alignable.
        for (Displayable displayable : tb1.getDisplayables())
            if (displayable.getClass().getSimpleName().equals("TextInput")) {
                ((TextInput) displayable).setDefaultContent(Character.toString((char) ('a'+(Math.random()*26f))));
                ((TextInput) displayable).setAlign(CENTER);
            }
        Label tempLabel = new Label("");
        tempLabel.setContent("LABEL");
        tempLabel.setAlign(CENTER);
        tb1.set(5,4,tempLabel);
        //tb1.setVisible(false);
        b1.add(tb1);

        JNode.add(b1);

        //designing menu drop downs
        m = new MenuDropdown("menu1");
        m1 = new MenuDropdown("menu2");
        m2 = new MenuDropdown("menu3");

        m.add(new MenuItem("item1", "exit"));
        m.add(new MenuItem("item2", "<menu item 2>"));
        m.add(new MenuItem("item3", "<go to sub menu 2>"));
        m.add(new MenuItem("item4", "...Welcome to JUI..."));

        m1.add(new MenuItem("item1", "exit"));
        m1.add(new MenuItem("item2", "<menu item 2>"));
        m1.add(new MenuItem("item3", "<go to sub menu 3>"));
        m1.add(new MenuItem("item4", "<menu item 4>"));
        m1.add(new MenuItem("item5", "<menu item 5>"));
        m1.add(new MenuItem("item6", "<menu item 6>"));
        m1.bind("item3", m2);
        m1.onClick("item1", this::exit);

        m2.add(new MenuItem("item1", "exit"));
        m2.add(new MenuItem("item2", "<menu item 2>"));
        m2.add(new MenuItem("item3", "<menu item 3>"));

        m2.onClick("item1", this::exit);

        m.setAlign(CENTER);
        m.onClick("item1", this::exit);
        m.bind("item3", m1);
        m.setKeyTriggering(true);
        m.setTriggeringKeys(new int[]{SHIFT, ENTER});
        m.onTrigger(() -> m.relocate(mouseX, mouseY));
        m.setMouseTriggering(true);
        m.setTriggeringMouseButton(RIGHT);
        m.setTextSize(10);
        m.setTextFont(createFont("Verdana-BoldItalic",10));

        JNode.addAll(m); //only the top level MenuDropdown instance should be added into the JNode.
        //for some reason the println has to be after the setup. Resolved Jan 25th.

        window_w = width;
        window_h = height;
    }

    public void draw() {
        background(255);
        if (window_w != width || window_h != height) {
            window_w = width;
            window_h = height;
            JNode.getContainerById("parent").resize(width, height);
        }
        JNode.run();
    }
/*
    public void refresh() {

        for (Displayable d : JNode.getDisplayables()) {
            d.setRounded(true);
            d.setMousePressedBackgroundColor(0, 150, 0, brightness + 50);
            d.setMouseOverBackgroundColor(0, 200, 0, brightness + 50);
            if (d.getClass().getSuperclass().getSimpleName().equals("Slider"))
                ((Slider) d).setProgressBackgroundColor(d.mouseOverBackgroundColor);


            //d.setContourVisible(false);
            d.setContourThickness(.5f);
            d.setContourColor((int)random(0, 255), (int)random(0, 255), (int)random(0, 255), brightness);

            if (d.getClass().getSimpleName().equals("MenuDropdown"))
                d.setBackgroundColor((int) random(0, 255), (int) random(0, 255), (int) random(0, 255), brightness + 150);
            else d.setBackgroundColor((int) random(0, 255), (int) random(0, 255), (int) random(0, 255), brightness);

            //d.setMouseOverBackgroundColor((int) random(0, 255), (int) random(0, 255), (int) random(0, 255), 100);
            //d.setMousePressedBackgroundColor((int) random(0, 255), (int) random(0, 255), (int) random(0, 255), 100);
        }
        JNode.getScrollFieldById("scrollfield_color").println("Refreshed. Brightness is set to " + brightness);

    }

    /*
    public void mousePressed() {
        JNode.mousePressed();  //linking to node
    }

    public void mouseReleased() {
        JNode.mouseReleased();
    }

    public void mouseDragged() {
        JNode.mouseDragged();
    }
    */

    public void keyPressed() {
        JNode.keyPressed();
    }

    public void keyReleased() {
        JNode.keyReleased();
    }

    public void mouseWheel() {
        //to be implemented. Jan 27th.
    }

}
