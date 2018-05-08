package app.service;

import app.http.controllers.TeamController;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.teambition.sdk.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class TeamService {
    Logger logger = LoggerFactory.getLogger(TeamService.class);

    public static String GROUP_ID_FIELD = "_tasklistId";
    protected Client client;

    public TeamService(Client client) {
        this.client = client;
    }

    public static TeamService newInstance(Client client) {
        return new TeamService(client);
    }

    public JSONArray getProjects() {
        String jsonProject = client.projects("");
        JSONArray jsonArray1 = JSON.parseArray(jsonProject);
        for (int j = 0; j < jsonArray1.size(); j++) {
            JSONObject jsonObject1 = (JSONObject) jsonArray1.get(j);
            String projectId = jsonObject1.getString("_id");
            String projectName = jsonObject1.getString("name");
            logger.info("project id {} name {}", projectId, projectName);
        }
        return jsonArray1;
    }

    public JSONArray getStageGroup(String projectId) {
        String jsonGroupLists = client.taskGroupLists(projectId);
        JSONArray jsonGroupListArray = JSON.parseArray(jsonGroupLists);
        for (int i = 0; i < jsonGroupListArray.size(); i++) {
            JSONObject o = (JSONObject) jsonGroupListArray.get(i);
            JSONArray jsonGroupArrayStage = o.getJSONArray("hasStages");
            String groupId = o.getString("_id");
            String groupTitle = o.getString("title");
            for (int j = 0; j < jsonGroupArrayStage.size(); j++) {
                JSONObject jsonObject1 = (JSONObject) jsonGroupArrayStage.get(j);
                String stageId = jsonObject1.getString("_id");
                String stageName = jsonObject1.getString("name");
                String tasklistId = jsonObject1.getString("_tasklistId");
                logger.info("project id {} groupId {}, groupTitle {} stage id {} name {} groupId {} tasklistId {}", projectId, groupId, groupTitle, stageId, stageName, groupId, tasklistId);
            }
        }

        return jsonGroupListArray;
    }

    public JSONArray getGroupTask(String groupId) {
        String jsonAllTask = client.groupTaskListTasks(groupId);

        JSONArray groupTaskList = JSON.parseArray(jsonAllTask);
        logger.info("groupId ", groupId, groupTaskList.size());

        for (int k = 0; k < groupTaskList.size(); k++) {
            JSONObject jsonObject1 = (JSONObject) groupTaskList.get(k);

            String taskId = jsonObject1.getString("_id");
            String taskContent = jsonObject1.getString("content");
            String note = jsonObject1.getString("note");
            logger.info("groupId {} taskId {}, taskContent {} note {}", groupId, taskId, taskContent, groupId, note);

        }
        return groupTaskList;
    }

    private List<HashMap<String, String>> pushList(JSONArray jsonArray, List<HashMap<String, String>> list) {
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            HashMap<String, String> item = new HashMap<>();
            item.put("_id", jsonObject.getString("_id"));
            item.put("content", jsonObject.getString("content"));
            item.put("isDone", jsonObject.getBoolean("isDone").toString());
            list.add(item);
        }
        return list;
    }

    public List<HashMap<String, String>> getStageTasks(String stageId) {
        List<HashMap<String, String>> list = new LinkedList<>();
        Integer page;
        page = 1;
        while (true) {
            Integer count = 200;
            String jsonStage = client.stageTasks(stageId, "false", page.toString(), count.toString());
            if (jsonStage.contains("NotFound")) break;
            JSONArray jsonArray = JSON.parseArray(jsonStage);
            pushList(jsonArray, list);
            page++;
            if (jsonArray.size() <= count) {
                break;
            }
        }
        page = 1;
        while (true) {
            Integer count = 200;
            String jsonStage = client.stageTasks(stageId, "true", page.toString(), count.toString());
            if (jsonStage.contains("NotFound")) break;
            JSONArray jsonArray = JSON.parseArray(jsonStage);
            pushList(jsonArray, list);
            page++;
            if (jsonArray.size() <= count) {
                break;
            }
        }
        logger.info("push list list size {}", list.size());
        for (int i = 0; i < list.size(); i++) {
            logger.info("stageId {}, task id {}, task content {} task isDone {}", stageId, list.get(i).get("_id"), list.get(i).get("content"), list.get(i).get("isDone"));
        }
        return list;
    }

    public JSONArray getActivities(String taskId) {
        String activitiesJson = client.activities(taskId);
        JSONArray jsonArray = JSON.parseArray(activitiesJson);
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject object = jsonArray.getJSONObject(i);
            logger.info("task id {} title {} activities size {}", taskId, object.get("title"), object.size());
            JSONObject content = object.getJSONObject("content");
            JSONArray attachments = content.getJSONArray("attachments");
            if (attachments != null){
                for (int j = 0; j < attachments.size(); j++) {
                    JSONObject attachmentsJSONObject = attachments.getJSONObject(j);
                    logger.info("task id {} title {} fileName {} downloadUrl {}", taskId, object.get("title"), attachmentsJSONObject.getString("fileName"), attachmentsJSONObject.getString("downloadUrl"));
                }
            }
        }
        return jsonArray;
    }

}
