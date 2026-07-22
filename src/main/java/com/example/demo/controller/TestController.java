/**
 * author @bhupendrasambare
 * Date   :22/07/26
 * Time   :8:19 pm
 * Project:rag
 **/
package com.example.demo.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {

    private final ChatClient chatClient;

    @GetMapping("/chat")
    public String chat(@RequestParam(name = "query") String query){
        return chatClient.prompt(query)
                .call()
                .content();
    }

}
