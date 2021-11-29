package com.afaqy.avl.websocket.controller;

import com.afaqy.avl.websocket.model.Greeting;
import com.afaqy.avl.websocket.model.HelloMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

@Controller
public class GreetingController {

    @MessageMapping("/unit/{unitId}")
    @SendTo("/topic/unit/updates/{unitId}")
    public Greeting greeting(HelloMessage message) throws InterruptedException {
        Thread.sleep(1000); // simulated delay
        return new Greeting("Hello, " + HtmlUtils.htmlEscape(message.getName()) + "!");
    }

}
