package app.po;

public class StageTask {
    public String isDone;
    public String content;
    public String id;
    public String taskId;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getStageGroupId() {
        return stageGroupId;
    }

    public void setStageGroupId(String stageGroupId) {
        this.stageGroupId = stageGroupId;
    }

    public String stageGroupId;
    public String getIsDone() {
        return isDone;
    }

    public void setIsDone(String isDone) {
        this.isDone = isDone;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
