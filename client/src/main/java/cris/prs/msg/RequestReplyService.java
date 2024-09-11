package cris.prs.msg;

import com.solace.spring.cloud.stream.binder.messaging.SolaceHeaders;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Slf4j
@Service
public class RequestReplyService {

    private final Map<String, CompletableFuture<String>> responseMap = new ConcurrentHashMap<>();

    @Autowired
    private StreamBridge sb;

    public void sendAndReceive(String payload){
        String correlationId = UUID.randomUUID().toString();
        Destination replyToTopic = JCSMPFactory.onlyInstance().createTopic("cris/reply/train456");
        log.info("Sending Message {}:{}",SolaceHeaders.CORRELATION_ID,correlationId);
        Message<String> msg = MessageBuilder.withPayload(payload)
                .setHeader(SolaceHeaders.CORRELATION_ID,correlationId)
                .setHeader(SolaceHeaders.REPLY_TO, replyToTopic)
                .build();
        sb.send("booking/train", msg);
    }

    @Bean
    public Consumer<Message<String>> bookingreply(){
        return msg -> {
            MessageHeaders headers = msg.getHeaders();
            String correlationId = headers.get(SolaceHeaders.CORRELATION_ID,String.class);
            log.info("Receiving Message {}:{}",SolaceHeaders.CORRELATION_ID,correlationId);
            String payload = msg.getPayload();
            log.info("Message Headers: {}", headers);
            log.info("Message: {}", payload);
            if("sleep".equalsIgnoreCase(payload)){
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    log.error("<Error>",e);
                }
            }
        };
    }
}
