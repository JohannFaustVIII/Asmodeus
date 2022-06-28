package org.faust.wireshark;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

@RestController
public class WiresharkController {

    private final WiresharkService wiresharkService;

    public WiresharkController(WiresharkService wiresharkService) {
        this.wiresharkService = wiresharkService;
    }

    @GetMapping("/ws/handlers")
    public Set<String> getHandlers() throws IOException {
        return wiresharkService.getHandlers();
    }

    @GetMapping("/ws/file")
    public ResponseEntity<?> getWsFile(HttpServletResponse response) throws IOException {
        InputStream inStream = new FileInputStream(wiresharkService.getWsFile());
        IOUtils.copy(inStream, response.getOutputStream());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/ws/file/{name}")
    public ResponseEntity<?> getSpecifiedWsFile(HttpServletResponse response, @PathVariable(name = "name") String name) throws IOException {
        InputStream inStream = new FileInputStream(wiresharkService.getSpecifiedWsFile(name));
        IOUtils.copy(inStream, response.getOutputStream());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
