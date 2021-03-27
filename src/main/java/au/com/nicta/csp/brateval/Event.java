package au.com.nicta.csp.brateval;

import java.util.LinkedList;

public class Event {
    private String id;

    private String type;
    private String event_trigger;

    private LinkedList<String> arguments =
            new LinkedList<String>();

    private String file;

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getEventTrigger() {
        return event_trigger;
    }

    public LinkedList<String> getArguments() {
        return arguments;
    }

    public String getFile() {
        return file;
    }

    public Event(String id, String type, String event_trigger, LinkedList<String> arguments, String file) {
        this.id = id;
        this.type = type;
        this.event_trigger = event_trigger;
        this.arguments = arguments;
        this.file = file;
    }
}