package app;

import app.amqp.DownFileConsumer;
import app.amqp.DownFileProducer;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import javax.annotation.Resource;

@ComponentScan(basePackages={"app","com"})
@EnableAutoConfiguration
public class Application {

    public static String TEAM_FILE_DOWN_QUEUE_NAME = "team_file_down_queue";
    @Bean
    public Queue queue(){
        return new Queue(TEAM_FILE_DOWN_QUEUE_NAME, true, false, false);
    }
    @Bean
    public TopicExchange topicExchange(){
        return new TopicExchange("team_exchange", true, false);
    }
    @Bean
    public Binding binding(Queue queue, TopicExchange topicExchange){
        return BindingBuilder.bind(queue).to(topicExchange).with("router_team");
    }

    @Bean
    SimpleMessageListenerContainer simpleMessageListenerContainer(ConnectionFactory connectionFactory, MessageListenerAdapter messageListenerAdapter){
        SimpleMessageListenerContainer simpleMessageListenerContainer = new SimpleMessageListenerContainer();
        simpleMessageListenerContainer.setConnectionFactory(connectionFactory);
        simpleMessageListenerContainer.setQueueNames(TEAM_FILE_DOWN_QUEUE_NAME);
        simpleMessageListenerContainer.setMessageListener(messageListenerAdapter);
        return simpleMessageListenerContainer;
    }

    @Bean
    MessageListenerAdapter messageListenerAdapter(DownFileConsumer receiver){
        return new MessageListenerAdapter(receiver);
    }
    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }

}