package app.po;

import org.springframework.data.annotation.Id;

public class Stage {
    @Id
    public String id;

    public String stageGroupId;

    public String getStageGroupId() {
        return stageGroupId;
    }

    public void setStageGroupId(String stageGroupId) {
        this.stageGroupId = stageGroupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
