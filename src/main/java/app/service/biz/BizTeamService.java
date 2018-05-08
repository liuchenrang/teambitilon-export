package app.service.biz;

import app.amqp.DownFileProducer;
import app.po.*;
import app.repository.*;
import app.service.TeamService;
import app.vo.DownFileVo;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class BizTeamService {
    Logger logger = LoggerFactory.getLogger(TeamService.class);

    public ProjectRepository getProjectRepository() {
        return projectRepository;
    }

    public void setProjectRepository(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }
    private ProjectRepository projectRepository;
    private StageGroupRepository stageGroupRepository;
    private StageTaskRepository stageTaskRepository;

    public DownFileProducer getDownFileProducer() {
        return downFileProducer;
    }

    public void setDownFileProducer(DownFileProducer downFileProducer) {
        this.downFileProducer = downFileProducer;
    }

    private DownFileProducer downFileProducer;
    public StageRepository getStageRepository() {
        return stageRepository;
    }

    public void setStageRepository(StageRepository stageRepository) {
        this.stageRepository = stageRepository;
    }

    private StageRepository stageRepository;

    public StageTaskActiviyRepository getTaskActiviyRepository() {
        return taskActiviyRepository;
    }

    public void setTaskActiviyRepository(StageTaskActiviyRepository taskActiviyRepository) {
        this.taskActiviyRepository = taskActiviyRepository;
    }

    private StageTaskActiviyRepository taskActiviyRepository;

    public StageGroupRepository getStageGroupRepository() {
        return stageGroupRepository;
    }

    public void setStageGroupRepository(StageGroupRepository stageGroupRepository) {
        this.stageGroupRepository = stageGroupRepository;
    }

    public StageTaskRepository getStageTaskRepository() {
        return stageTaskRepository;
    }

    public void setStageTaskRepository(StageTaskRepository stageTaskRepository) {
        this.stageTaskRepository = stageTaskRepository;
    }

    public String getBackupPath() {
        return backupPath;
    }

    public void setBackupPath(String backupPath) {
        this.backupPath = backupPath;
    }

    protected String backupPath;
    protected TeamService teamService;

    public BizTeamService(TeamService teamService) {
        this.teamService = teamService;
    }

    public static BizTeamService newInstance(TeamService teamService) {
        return new BizTeamService(teamService);
    }
    public void backup(){
        JSONArray projects = teamService.getProjects();
        for (int i = 0; i < projects.size(); i++) {
            JSONObject project = projects.getJSONObject(i);
            Project projectPo  = new Project();
            projectPo.setSourceId(project.getString("_id"));
            projectPo.setName(project.getString("name"));
            projectRepository.save(projectPo);
            processStageGroup(project.getString("_id"));
        }
    }
    private void processStageGroup(String projectId){
        JSONArray stageGroupArray = teamService.getStageGroup(projectId);
        for (int i = 0; i < stageGroupArray.size(); i++) {
            JSONObject jsonObject = stageGroupArray.getJSONObject(i);
            StageGroup stageGroup = new StageGroup();
            stageGroup.setProjectId(projectId);
            stageGroup.setTitle(jsonObject.getString("title"));
            String groupId = jsonObject.getString("_id");
            stageGroup.setId(groupId);
            stageGroupRepository.save(stageGroup);
            JSONArray hasStages = jsonObject.getJSONArray("hasStages");
            for (int j = 0; j < hasStages.size(); j++) {
                JSONObject stageObject  = hasStages.getJSONObject(j);
                Stage stage  = new Stage();
                stage.setId(stageObject.getString("_id"));
                stage.setName(stageObject.getString("name"));
                stage.setStageGroupId(groupId);
                stageRepository.save(stage);

                processStageGroupTask(stageObject.getString("_id"), stageObject.getString("_tasklistId"));
            }
        }
    }
    private void processStageGroupTask(String stageId, String taskId){
        List<HashMap<String, String>> stageTasks = teamService.getStageTasks(stageId);
        for (int i = 0; i < stageTasks.size(); i++) {
            HashMap<String, String> task = stageTasks.get(i);
            StageTask stageTask  = new StageTask();
            stageTask.setContent(task.get("content"));
            stageTask.setIsDone(task.get("isDone"));
            stageTask.setId(task.get("_id"));
            stageTask.setTaskId(taskId);
            stageTask.setStageGroupId(stageId);
            stageTaskRepository.save(stageTask);
            processStageTaskActivities(task.get("_id"));
        }
    }

    private void processStageTaskActivities(String taskId){
        JSONArray activities = teamService.getActivities(taskId);
        for (int i = 0; i < activities.size(); i++) {
            JSONObject jsonObject = activities.getJSONObject(i);
            StageTaskActivity stageTaskActivity  = new StageTaskActivity();
            stageTaskActivity.setContent(jsonObject.getString("title"));
            stageTaskActivity.setId(jsonObject.getString("_id"));
            stageTaskActivity.setStageTaskId(taskId);
            JSONObject content = jsonObject.getJSONObject("content");
            JSONArray attachments = content.getJSONArray("attachments");
            LinkedList<StageTaskActivity.Attachment> attachmentList = new LinkedList<>();
            if (attachments != null){
                for (int j = 0; j < attachments.size(); j++) {
                    JSONObject attachmentsJSONObject = attachments.getJSONObject(j);
                    StageTaskActivity.Attachment attachment =  stageTaskActivity.new Attachment();
                    String fileName = attachmentsJSONObject.getString("fileName");
                    attachment.setFileName(fileName);
                    String downloadUrl = attachmentsJSONObject.getString("downloadUrl");
                    attachment.setDownloadUrl(downloadUrl);
                    attachmentList.push(attachment);

                    DownFileVo message = new DownFileVo();
                    message.setFileName(attachmentsJSONObject.getString("_id") + "_" + fileName);
                    message.setUrl(downloadUrl);
                    downFileProducer.send(message.toString());
                }
            }
            stageTaskActivity.setAttachments(attachmentList);
            getTaskActiviyRepository().save(stageTaskActivity);
        }
    }


}
