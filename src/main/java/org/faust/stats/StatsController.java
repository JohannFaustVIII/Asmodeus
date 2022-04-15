package org.faust.stats;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.faust.file.WSService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@RestController
public class StatsController {

    private final WSService wsService;

    public StatsController(WSService wsService) {
        this.wsService = wsService;
    }

    @GetMapping("/")
    public String getTestMessage() {
        return "Hello World!";
    }

    @GetMapping("/ws/file")
    public ResponseEntity<?> getWsFile(HttpServletResponse response) throws IOException {
        InputStream inStream = new FileInputStream(wsService.getWsFile());
        IOUtils.copy(inStream, response.getOutputStream());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
