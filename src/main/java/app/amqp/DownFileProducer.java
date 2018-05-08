package app.amqp;

import app.Application;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DownFileProducer {
    @Autowired
    AmqpTemplate amqpTemplate;
    public void send(Object message){
        amqpTemplate.convertAndSend(Application.TEAM_FILE_DOWN_QUEUE_NAME, message);
        System.out.println(message);
    }
}
