package cris.prs.msg;

import com.solace.spring.cloud.stream.binder.messaging.SolaceHeaders;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Slf4j
@Component
public class MyConsumer {

    @Bean
    public Function<Message<String>, Message<String>> booking(){
        return msg -> {
            MessageHeaders headers = msg.getHeaders();
            String correlationId = headers.get(SolaceHeaders.CORRELATION_ID,String.class);
            String replyToTopic = null;

             // Check if replyTo header is of type Topic or String and handle accordingly
             Object replyToHeader = headers.get(SolaceHeaders.REPLY_TO);
             if (replyToHeader instanceof Topic) {
                 replyToTopic = ((Topic) replyToHeader).getName(); // Convert Topic to String
             } else if (replyToHeader instanceof String) {
                 replyToTopic = (String) replyToHeader;
             } else {
                 log.error("Invalid type for 'solace_replyTo' header: {}", replyToHeader);
                 throw new IllegalArgumentException("Invalid type for 'solace_replyTo' header");
             }

            log.info("Headers:{}",headers);
            log.info("Consuming Message {}:{}",SolaceHeaders.CORRELATION_ID,correlationId);
            log.info("Replying to Topic from Headers: {}", replyToTopic != null ? replyToTopic : "null");
            
            String v = msg.getPayload();
            log.info("Payload: {}",v);
            if("sleep".equals(v)){
                try {
                    log.info("Going to sleep for 10s");
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    log.error("<Error>",e);
                }
            }
            return MessageBuilder.withPayload(v.toUpperCase())
                    .setHeader(SolaceHeaders.CORRELATION_ID,correlationId)
                    .setHeader(BinderHeaders.TARGET_DESTINATION, replyToTopic)
                    .setHeader(SolaceHeaders.IS_REPLY, true)
                    .build();
        };
    }

//    @Bean
//    public Function<String, String> booking(){
//        return String::toUpperCase;
//    }

//    @Bean
//    public Consumer<String> booking(){
//        return v -> log.info("Message: {}",v) ;
//    }

//    @Bean
//    public Consumer<Message<String>> booking(){
//        return v -> log.info("Message: {}",v) ;
//    }
}
