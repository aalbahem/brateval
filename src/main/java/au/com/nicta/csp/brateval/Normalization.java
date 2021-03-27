package au.com.nicta.csp.brateval;

public class Normalization {
    private String id;
    private String type;
    private String entity;
    private String source_id;
    private String string;

    private String file;

    public Normalization(String id, String type, String entity, String source_id, String string, String file) {
        this.id = id;
        this.type = type;
        this.entity = entity;
        this.source_id = source_id;
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

    public String getSourceId() {
        return source_id;
    }

    public String getString() {
        return string;
    }

    public String getFile() {
        return file;
    }
}