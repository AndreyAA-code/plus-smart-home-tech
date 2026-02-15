package controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/events")
public class EventsController {

    @PostMapping("/sensors")
    @ResponseStatus(HttpStatus.OK)
    public void sensors() {

    }
    @PostMapping("/hubs")
    @ResponseStatus(HttpStatus.OK)
    public void hubs() {

    }
}
