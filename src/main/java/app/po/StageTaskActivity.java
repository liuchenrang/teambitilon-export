package app.po;

import java.util.List;

public class StageTaskActivity {
    private String stageTaskId;

    public String getStageTaskId() {
        return stageTaskId;
    }

    public void setStageTaskId(String stageTaskId) {
        this.stageTaskId = stageTaskId;
    }

    public class Attachment{
        String fileName;
        String downloadUrl;

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getDownloadUrl() {
            return downloadUrl;
        }

        public void setDownloadUrl(String downloadUrl) {
            this.downloadUrl = downloadUrl;
        }
    }
    private String id;
    private String content;
    private List<Attachment> attachments;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }
}
