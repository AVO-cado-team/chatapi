package sk.avo.chatapi.presentation.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sk.avo.chatapi.application.ApplicationService;

@RestController
@RequestMapping("/api/chat/")
public class Chat {
    private final ApplicationService applicationService;

    public Chat(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

}
