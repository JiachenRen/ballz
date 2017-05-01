package jui_lib;

/**
 * Created by Jiachen on 30/04/2017.
 */
public class EventListener {
    private Event event;
    private Runnable attachedMethod;
    private String id;

    EventListener(String id, Event event) {
        this.event = event;
        this.id = id;
    }

    public Event getEvent() {
        return event;
    }

    public EventListener setEvent(Event event){
        this.event = event;
        return this;
    }

    public void invoke(){
        if (attachedMethod!= null)
            attachedMethod.run();
    }

    public void attachMethod(Runnable runnable){
        this.attachedMethod = runnable;
    }

    public String getId(){
        return id;
    }

    public void setId(String temp){
        this.id = temp;
    }
}
