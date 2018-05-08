package app.amqp;

import app.vo.DownFileVo;
import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.Channel;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

@Service
public class DownFileConsumer implements ChannelAwareMessageListener {
    @Value("${team.backup.path}")
    private String backupPath;

    Logger logger = LoggerFactory.getLogger(DownFileConsumer.class);
    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        System.out.println("receiver " + message.getBody());
        DownFileVo downFileVo = null;
        try {
            downFileVo = JSON.parseObject(message.getBody(), DownFileVo.class);
            DefaultHttpRequestRetryHandler retryHandler = new DefaultHttpRequestRetryHandler(4, true);
            CloseableHttpClient httpClient =  HttpClientBuilder.create().setRetryHandler(retryHandler).build();

            HttpGet httpGet = new HttpGet(downFileVo.getUrl());
            CloseableHttpResponse response = httpClient.execute(httpGet);
            InputStream inputStream = response.getEntity().getContent();
            String pathname = backupPath + getPath(downFileVo);
            logger.info("download path {}",pathname);
            File file = new File(pathname);
            file.mkdirs();
            String fileDownName = pathname + "/" + downFileVo.getFileName();
            File fileDown = new File(fileDownName);
            FileOutputStream fos = new FileOutputStream(fileDown);
            int inByte;
            while ((inByte = inputStream.read()) != -1) {
                fos.write(inByte);
            }
            fos.close();
            inputStream.close();
            logger.info("download finish {}",fileDownName);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            e.printStackTrace();
        }



    }
    public String getPath(DownFileVo vo){
        String path = "";
        if (vo.getProject() != null && !vo.getProject().equals("")){
            path += "/" + vo.getProject();
        }
        if (vo.getGroup() != null && !vo.getGroup().equals("")){
            path += "/" + vo.getGroup();
        }
        return path;
    }

}

