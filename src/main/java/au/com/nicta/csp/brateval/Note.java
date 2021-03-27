package au.com.nicta.csp.brateval;

public class Note {
    private String id;
    private String type;
    private String entity;
    private String string;

    private String file;

    public Note(String id, String type, String entity, String string, String file) {
        this.id = id;
        this.type = type;
        this.entity = entity;
        this.string = string;
        this.file = file;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getEntity() {
        return entity;
    }

    public String getString() {
        return string;
    }

    public String getFile() {
        return file;
    }
}
