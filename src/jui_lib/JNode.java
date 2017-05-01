package jui_lib;

import com.sun.istack.internal.Nullable;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PFont;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

//idea, Jan 21. Chaining up the methods so they return itself, allowing syntaxes such as setHeight().setWidth().
//spit into JNode instances and static back_end controls.
//Don't forget to add Tables!!
public class JNode {
    private static ArrayList<TextInput> textInputs;
    private static ArrayList<ScrollField> scrollFields;
    private static ArrayList<Button> buttons;
    private static ArrayList<Displayable> displayables; // the superclass
    private static ArrayList<Container> containers;
    private static ArrayList<Contextual> contextuals;
    private static ArrayList<Label> labels;
    private static ArrayList<Slider> sliders;
    private static ArrayList<MenuDropdown> menuDropdowns; //no good reason t make a getter for this
    private static ArrayList<Displayable> standbyDisplayables; //added March 8th. Not stable yet.

    //create getter and setters for the following. Modified Jan 26.
    static int UNI_MENU_TEXT_SIZE = 15; //universal menu textSize TODO remove
    public static PFont UNI_FONT;
    public static String OS = System.getProperty("os.name").toLowerCase();
    static private PApplet parent;

    /*to be imported from default.txt*/
    static boolean DISPLAY_CONTOUR;
    static boolean CONTAINER_VISIBLE;
    static boolean ROUNDED;
    static boolean AUTO_TEXT_DESCENT_COMPENSATION;
    static float CONTAINER_MARGIN_X;
    static float CONTAINER_MARGIN_Y;
    static float CONTAINER_SPACING;
    static float CONTOUR_THICKNESS;
    static float ROUNDING;
    static float CONTEXTUAL_INIT_TEXT_PERCENTAGE;

    static int COLOR_MODE;
    public static int BACKGROUND_COLOR;
    static int MOUSE_PRESSED_BACKGROUND_COLOR;
    static int MOUSE_OVER_BACKGROUND_COLOR;
    static int CONTOUR_COLOR;
    static int MOUSE_PRESSED_CONTOUR_COLOR;
    static int MOUSE_OVER_CONTOUR_COLOR;
    static int TEXT_COLOR;
    static int MOUSE_PRESSED_TEXT_COLOR;
    static int MOUSE_OVER_TEXT_COLOR;

    public static void init(PApplet p) {
        parent = p;
        textInputs = new ArrayList<>();
        scrollFields = new ArrayList<>();
        buttons = new ArrayList<>();
        displayables = new ArrayList<>();
        containers = new ArrayList<>();
        contextuals = new ArrayList<>();
        labels = new ArrayList<>();
        menuDropdowns = new ArrayList<>();
        sliders = new ArrayList<>();
        standbyDisplayables = new ArrayList<>();
        setUniFont(p.createFont(PFont.list()[1], 100));
        importStyle("default");
    }

    public static void run() {
        parent.pushStyle();
        try {
            for (int i = displayables.size() - 1; i >= 0; i--) {
                Displayable displayable = displayables.get(i);
                parent.colorMode(displayable.colorMode); //TODO not yet functional
                if (displayable.isVisible() && !displayable.isRelative())
                    displayable.run();
            }
            for (Displayable displayable : standbyDisplayables) {
                if (displayable.getAttachedMethod() != null) {
                    displayable.getAttachedMethod().run();
                }
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        parent.popStyle();
    }

    /**
     * This method would soon become one of the best features of JUI, as
     * it offers incredible customization capabilities; each individual style
     * sheet does not need to contain all the customization info; instead,
     * the user can only include the style that they are willing to change.
     * For example, a file named button_style.txt can only contain a single
     * line "mouse_pressed_background_color: 234,12,45,27", and it would be
     * considered as valid.
     *
     * @param fileName the name of your_customization_file.txt to be imported into JNode.
     * @since April 26th idea by Jiachen Ren.
     */
    public static void importStyle(String fileName) {
        String[] lines = parent.loadStrings("jui_lib/customization/" + fileName);
        System.out.println("default imported; jui_lib 2.0.1\n");
        label:
        for (String line : lines) {
            if (!line.contains(": ")) continue;
            String data = line.split(": ")[1];
            String keyWord = line.split(": ")[0];
            try {
                Field field = JNode.class.getDeclaredField(keyWord.toUpperCase());
                String fieldTypeName = field.getType().getName();
                PApplet.print(field.getName().toLowerCase() + " = ");
                switch (keyWord) {
                    case "color_mode":
                        switch (data) {
                            case "RGB":
                                field.setInt(null, PConstants.RGB);
                                break;
                            case "ARGB":
                                field.setInt(null, PConstants.ARGB);
                                break;
                            case "HSB":
                                field.setInt(null, PConstants.HSB);
                                break;
                        }
                        continue label;
                }
                switch (fieldTypeName) {
                    case "float":
                        field.setFloat(null, Float.valueOf(data));
                        PApplet.println(field.getFloat(null));
                        break;
                    case "boolean":
                        field.setBoolean(null, Boolean.valueOf(data));
                        PApplet.println(field.getBoolean(null));
                        break;
                    case "int":
                        String temp[] = data.split(",");
                        int[] rgba = new int[4];
                        for (int i = 0; i < rgba.length; i++)
                            rgba[i] = Integer.valueOf(temp[i]);
                        int color = parent.color(rgba[0], rgba[1], rgba[2], rgba[3]);
                        field.setInt(null, color);
                        PApplet.println(color);
                        break;
                    case "String":
                        PApplet.println(field.getName());
                        break;
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                /*learned "|" April 26th. Wow.*/
                e.printStackTrace();
            }
        }
    }

    public static void standby(Displayable displayable) {
        standbyDisplayables.add(displayable);
    }

    public static void add(Displayable displayable) {
        parent.noLoop();
        if (standbyDisplayables.contains(displayable))
            standbyDisplayables.remove(displayable);
        if (displayables.contains(displayable)) return;
        displayables.add(displayable);

        if (displayable instanceof ScrollField) {
            getScrollFields().add((ScrollField) displayable);
        } else if (displayable instanceof TextInput) {
            getTextInputs().add((TextInput) displayable);
        } else if (displayable instanceof Button) {
            getButtons().add((Button) displayable);
        } else if (displayable instanceof Label) {
            getLabels().add((Label) displayable);
        } else if (displayable instanceof MenuDropdown) {
            menuDropdowns.add((MenuDropdown) displayable);
        }

        if (displayable instanceof Contextual)
            getContextuals().add((Contextual) displayable);
        else if (displayable instanceof Container)
            getContainers().add((Container) displayable);
        else if (displayable instanceof Slider)
            getSliders().add((Slider) displayable);

        parent.loop();
    }

    public static void addAll(ArrayList<Displayable> displayables) {
        for (Displayable displayable : displayables) {
            add(displayable);
        }
    }

    public static void addAll(Displayable... displayables) {
        for (Displayable displayable : displayables) {
            add(displayable); //unlimited number of parameters! Learned Jan 28th.
        }
    }

    public static void remove(String id) {
        ArrayList<Displayable> selected = JNode.get(id);
        for (int i = selected.size() - 1; i >= 0; i--) {
            Displayable reference = selected.get(i);
            remove(reference);
        }
    }

    /**
     * @param obj
     */
    public static void remove(Displayable obj) {
        //removing from displayables arraylist, which contains reference to all objects
        parent.noLoop();
        /*this is here to prevent ConcurrentModificationException when another thread tries to
        remove a displayable object while the main loop of processing is iterating through it*/
        /*modified March 8th*/
        for (int i = displayables.size() - 1; i >= 0; i--)
            if (displayables.get(i) == obj) displayables.remove(i);

        //removing from contextuals arraylist, which contains reference to all contextualizable displayables
        for (int i = contextuals.size() - 1; i >= 0; i--)
            if (contextuals.get(i) == obj) contextuals.remove(i);

        //removing from specific object arraylists
        for (int i = textInputs.size() - 1; i >= 0; i--)
            if (textInputs.get(i) == obj) textInputs.remove(i);
        for (int i = scrollFields.size() - 1; i >= 0; i--)
            if (scrollFields.get(i) == obj) scrollFields.remove(i);
        for (int i = buttons.size() - 1; i >= 0; i--)
            if (buttons.get(i) == obj) buttons.remove(i);
        for (int i = labels.size() - 1; i >= 0; i--)
            if (labels.get(i) == obj) labels.remove(i);
        for (int i = menuDropdowns.size() - 1; i >= 0; i--)
            if (menuDropdowns.get(i) == obj) menuDropdowns.remove(i);
        for (int i = sliders.size() - 1; i >= 0; i--)
            if (sliders.get(i) == obj) sliders.remove(i);

        //removing from containers, call should be passed to every single sub-containers to remove all objs.
        for (int i = containers.size() - 1; i >= 0; i--) {
            Container container = containers.get(i);
            if (container == obj) {
                containers.remove(container);
                for (Container c : containers) {
                    c.syncSize();
                    c.arrange();
                }
            } else {
                container.remove(obj);
            }
        }
        parent.loop();
    }

    public static ArrayList<Displayable> get(String id) {
        ArrayList<Displayable> selected = new ArrayList<>();
        for (Displayable displayable : displayables)
            if (Objects.equals(displayable.getId(), id))
                selected.add(displayable);
        return selected;
    }

    public static ArrayList<TextInput> getTextInputs() {
        return textInputs;
    }

    public static ArrayList<ScrollField> getScrollFields() {
        return scrollFields;
    }

    public static ArrayList<Button> getButtons() {
        return buttons;
    }

    public static ArrayList<Label> getLabels() {
        return labels;
    }

    public static ArrayList<MenuDropdown> getMenuDropdowns() {
        return menuDropdowns;
    }

    public static ArrayList<Slider> getSliders() {
        return sliders;
    }

    public static ArrayList<Displayable> getDisplayables() {
        return displayables;
    }

    public static ArrayList<Container> getContainers() {
        return containers;
    }

    public static ArrayList<Contextual> getContextuals() {
        return contextuals;
    }

    //keyboard/mouseinput action receivers. Bug fixed Jan 28th 11:26PM.
    public static void mousePressed() {
        for (Displayable displayable : displayables) {
            if (!(displayable instanceof MenuDropdown))
                if (!displayable.isVisible()) continue;
            displayable.mousePressed();
        }
    }

    public static void mouseReleased() {
        for (Displayable displayable : displayables) {
            if (!(displayable instanceof MenuDropdown))
                if (!displayable.isVisible()) continue;
            displayable.mouseReleased();
        }
    }

    public static void mouseDragged() {
        for (Displayable displayable : displayables) {
            if (!(displayable instanceof MenuDropdown))
                if (!displayable.isVisible()) continue;
            displayable.mouseDragged();
        }
    }

    public static void mouseWheel() {
        for (Displayable displayable : displayables) {
            if (!(displayable instanceof MenuDropdown))
                if (!displayable.isVisible()) continue;
            displayable.mouseWheel();
        }
    }

    public static void keyPressed() {
        for (Displayable displayable : displayables) {
            if (!(displayable instanceof MenuDropdown))
                if (!displayable.isVisible()) continue;
            if (displayable.getClass().getInterfaces().length != 0) {
                if (displayable instanceof KeyControl) {
                    KeyControl c = (KeyControl) displayable;
                    c.keyPressed();
                }
            }
        }
    }

    public static void keyReleased() {
        for (Displayable displayable : displayables) {
            if (!(displayable instanceof MenuDropdown))
                if (!displayable.isVisible()) continue;
            if (displayable.getClass().getInterfaces().length != 0) {
                if (displayable instanceof KeyControl) {
                    KeyControl c = (KeyControl) displayable;
                    c.keyReleased();
                }
            }
        }
    }

    @Nullable
    public static TextInput getTextInputById(String id) {
        for (TextInput textInput : textInputs)
            if (Objects.equals(textInput.getId(), id)) return textInput;
        return null;
    }

    @Nullable
    public static ScrollField getScrollFieldById(String id) {
        for (ScrollField scrollField : scrollFields)
            if (Objects.equals(scrollField.getId(), id)) return scrollField;
        return null;
    }

    @Nullable
    public static Button getButtonById(String id) {
        for (Button button : buttons)
            if (Objects.equals(button.getId(), id)) return button;
        return null;
    }

    @Nullable
    public static Label getLabelById(String id) {
        for (Label label : labels)
            if (Objects.equals(label.getId(), id)) return label;
        return null;
    }

    @Nullable
    public static MenuDropdown getMenuDropdownById(String id) {
        for (MenuDropdown menuDropdown : menuDropdowns)
            if (Objects.equals(menuDropdown.getId(), id)) return menuDropdown;
        return null;
    }

    @Nullable
    public static Slider getSliderById(String id) {
        for (Slider slider : sliders)
            if (Objects.equals(slider.getId(), id)) return slider;
        return null;
    }

    public static Container getContainerById(String id) {
        for (Container container : containers)
            if (Objects.equals(container.getId(), id)) return container;
        return null;
    }

    public static Contextual getContextualById(String id) {
        for (Contextual contextual : contextuals)
            if (Objects.equals(contextual.getId(), id)) return contextual;
        return null;
    }

    public static ArrayList<Displayable> getById(String id) {
        ArrayList<Displayable> selected = new ArrayList<>();
        for (Displayable displayable : displayables)
            if (displayable.getId().equals(id))
                selected.add(displayable);
        return selected;
    }

    public static void setUniFont(PFont textFont) {
        UNI_FONT = textFont;
    }

    public static void setUniMenuTextSize(int textSize) {
        UNI_MENU_TEXT_SIZE = textSize;
    }

    public static PApplet getParent() {
        return parent;
    }

    /*built in tools for the JUI library*/
    public static int[] getRgb(int rgb) {
        return new int[]{(int) parent.red(rgb), (int) parent.green(rgb), (int) parent.blue(rgb)};
    }

    public static int resetAlpha(int rgb, float alpha) {
        int[] temp = getRgb(rgb);
        return parent.color(temp[0], temp[1], temp[2], alpha);
    }

    /*seed oscillation*/
    public static float oscSeed() {
        return (float) Math.random() * 2.0f - 1.0f;
    }
}