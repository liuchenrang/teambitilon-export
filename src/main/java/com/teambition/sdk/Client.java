package com.teambition.sdk;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.Consts;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class Client {
    protected String access_token;
    protected Boolean delay = false;

    public Boolean getDelay() {
        return delay;
    }

    public void setDelay(Boolean delay) {
        this.delay = delay;
    }
    public void delay(){
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    private Logger logger = LoggerFactory.getLogger(Client.class);
    protected final String api_host = "https://api.teambition.com";

    public Client(String token) {
        access_token = token;
    }

    public static Client newInstance(String access_token) {
        return new Client(access_token);
    }

    public String userInfo() {
        return get("https://api.teambition.com/api/users/me?access_token=" + access_token);
    }

    public String refresh(String refresh_token, String user_id) {
        LinkedList<NameValuePair> nameValuePairs = new LinkedList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("refresh_token", refresh_token));
        nameValuePairs.add(new BasicNameValuePair("_userId", user_id));
        return post("https://account.teambition.com/oauth2/refresh_token", nameValuePairs);
    }

    public String get(String url) {
        DefaultHttpRequestRetryHandler retryHandler = new DefaultHttpRequestRetryHandler(4, true);
        CloseableHttpClient httpClient =  HttpClientBuilder.create().setRetryHandler(retryHandler).build();
        if (getDelay()) {
            delay();
        }
        HttpGet httpGet = new HttpGet(url);
        try {
            CloseableHttpResponse execute = httpClient.execute(httpGet);
            String string = EntityUtils.toString(execute.getEntity());
            logger.info("get url {} content {}", url, string);
            return string;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String post(String url, LinkedList<NameValuePair> params) {
        DefaultHttpRequestRetryHandler retryHandler = new DefaultHttpRequestRetryHandler(4, true);
        CloseableHttpClient httpClient =  HttpClientBuilder.create().setRetryHandler(retryHandler).build();
        if (getDelay()) {
            delay();
        }
        HttpPost httpPost = new HttpPost(url);
        UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(params, Consts.UTF_8);
        httpPost.setEntity(urlEncodedFormEntity);
        try {
            CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
            String string = EntityUtils.toString(httpResponse.getEntity());
            logger.info("post url {} content {}", url, string);
            return string;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String projects(String organization_id) {
        String url = api_host + "/api/organizations/" + organization_id + "/projects" + "?access_token=" + access_token;
        return get(url);
    }

    /**
     * 任务分组 获得任务下的分组
     * @param project_id
     * @return
     */
    public String taskGroupLists(String project_id) {
        String url = api_host + "/api/projects/" + project_id + "/tasklists" + "?access_token=" + access_token;
        return get(url);
    }

    /**
     * 任务分组 获得分组下的所有任务
     * @param tasklistId
     * @return
     */
    public String groupTaskListTasks(String tasklistId) {
        String url = api_host + "/api/v2/tasklists/" + tasklistId + "/tasks" + "?access_token=" + access_token;
        return get(url);
    }

    public String activities(String taskId) {
        String url = "https://api.teambition.com/api/tasks/" + taskId + "/activities" + "?access_token=" + access_token;
        return get(url);
    }
    public String stageTasks(String stage,String isDone,String page, String count){
        String url = String.format("%s/api/stages/%s/tasks?access_token=%s&isDone=%s&count=%s&page=%s",api_host, stage,access_token, isDone, count, page);
        return get(url);
    }

    public String activitiesAttachments(String taskId) {
        String url = String.format("https://api.teambition.com/api/%s/%s/activities/attachments" + "?access_token=" + access_token, "tasks", taskId);
        return get(url);
    }
}
