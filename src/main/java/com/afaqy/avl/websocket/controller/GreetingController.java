package com.afaqy.avl.websocket.controller;

import com.afaqy.avl.websocket.model.Greeting;
import com.afaqy.avl.websocket.model.HelloMessage;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

@Log4j2
@Controller
public class GreetingController {

    @MessageMapping("unit.{unitId}")
    public Greeting messageGreeting(HelloMessage message, @DestinationVariable String unitId)
            throws InterruptedException {
        log.debug("messageGreeting...");
        log.info("unitId: {}", unitId);

        Thread.sleep(1000); // simulated delay

        return new Greeting("Hello, " + HtmlUtils.htmlEscape(message.getName()) + "!");
    }

    @SubscribeMapping("unit.updates.{unitId}")
    public Greeting subscribeGreeting() throws InterruptedException {
        log.warn("subscribeGreeting...");

        Thread.sleep(1000); // simulated delay

        return new Greeting("Hello, thanks fro your subscription !");
    }

}
