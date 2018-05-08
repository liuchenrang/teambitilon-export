package app.po;

import org.springframework.data.annotation.Id;

public class Project {

    String name;
    @Id
    String sourceId;
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }
}
