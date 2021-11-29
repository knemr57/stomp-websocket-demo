package com.afaqy.avl.websocket.processor;

import com.afaqy.avl.websocket.model.Greeting;
import com.afaqy.avl.websocket.model.UnitUpdate;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.streams.processor.AbstractProcessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;

@Log4j2
@Component
public class UnitUpdateProcessor extends AbstractProcessor<String, UnitUpdate> {

    private final SimpMessagingTemplate simpMessagingTemplate;

    public UnitUpdateProcessor(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @Override
    public void process(String key, UnitUpdate value) {
        log.info("Received record with key: {}, and value: {}", key, value);

        String unitId = value.getId();

        // Must return same type as the websocket handler
        Greeting greeting = new Greeting("Hello, " + HtmlUtils.htmlEscape(value.getMessage()) + "!");
        simpMessagingTemplate.convertAndSend("/topic/unit/updates/" + unitId, greeting);
    }

}
