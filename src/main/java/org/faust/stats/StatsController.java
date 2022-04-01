package org.faust.stats;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatsController {

    @GetMapping("/")
    public String getTestMessage() {
        return "Hello World!";
    }
}
