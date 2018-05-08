package app.http.controllers;

import app.amqp.DownFileProducer;
import app.library.BackgroundBackThread;
import app.library.Utils;
import app.repository.*;
import app.service.TeamService;
import app.service.biz.BizTeamService;
import app.vo.DownFileVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.teambition.sdk.Client;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
public class TeamController {
    Logger logger = LoggerFactory.getLogger(TeamController.class);
    @Value("${server.host}")
    private String host;
    @Autowired
    private DownFileProducer downFileProducer;

    @Value("${team.backup.path}")
    private String backupPath;
    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    StageTaskRepository stageTaskRepository;
    @Autowired
    StageGroupRepository stageGroupRepository;
    @Autowired
    StageTaskActiviyRepository activiyRepository;
    @Autowired
    StageRepository stageRepository;
    @RequestMapping("/callback")
    @ResponseBody
    Object callback(@RequestParam("code") String code) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://account.teambition.com/api/oauth2/access_token");
        List<NameValuePair> list = new LinkedList<>();
        list.add(new BasicNameValuePair("code", code));
        list.add(new BasicNameValuePair("client_id", "a7b49f10-4925-11e8-b523-3dabebf8cfa1"));
        list.add(new BasicNameValuePair("client_secret", "6bce085c-6fc3-49b2-aeb4-0c73e2bf8255"));
        UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(list, Consts.UTF_8);
        httpPost.setEntity(urlEncodedFormEntity);

        try {
            CloseableHttpResponse httpResponse = httpClient.execute(httpPost);

            String jsonToken = EntityUtils.toString(httpResponse.getEntity());
            JSONObject jsonObject = JSON.parseObject(jsonToken);
            return jsonObject;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    @RequestMapping("/author")
    String author() {
        String str = "";
        String url = UriComponentsBuilder.newInstance().scheme("http").port("3200").host(host).path("/team/callback").toUriString();
        try {
            str = "https://account.teambition.com/oauth2/authorize?client_id=a7b49f10-4925-11e8-b523-3dabebf8cfa1&redirect_uri=" + URLEncoder.encode(url, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str;

    }

    @RequestMapping("/refresh_token")
    String refresh(@RequestParam("refresh_token") String refresh_token, @RequestParam("access_token") String access_token) {

        Client client = Client.newInstance(access_token);

        String jsonInfo = client.userInfo();

        JSONObject jsonObject = JSON.parseObject(jsonInfo);
        logger.info("user_id {}", jsonObject.getString("_id"));
        return client.refresh(refresh_token, jsonObject.getString("_id"));

    }

    @RequestMapping("/start")
    String start(@RequestParam("access_token") String access_token) {
        Client client = Client.newInstance(access_token);
        client.setDelay(true);
        TeamService teamService = TeamService.newInstance(client);
        BizTeamService bizTeamService = BizTeamService.newInstance(teamService);
        bizTeamService.setBackupPath(backupPath);
        bizTeamService.setDownFileProducer(downFileProducer);
        bizTeamService.setProjectRepository(projectRepository);
        bizTeamService.setStageGroupRepository(stageGroupRepository);
        bizTeamService.setStageTaskRepository(stageTaskRepository);
        bizTeamService.setTaskActiviyRepository(activiyRepository);
        bizTeamService.setStageRepository(stageRepository);
        BackgroundBackThread backgroundBackThread = new BackgroundBackThread();
        backgroundBackThread.setBizTeamService(bizTeamService);
        backgroundBackThread.start();

        return "启动备份成功!";
    }
}
