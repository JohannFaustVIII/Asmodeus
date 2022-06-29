package org.faust.pcap;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

@RequestMapping("/pcap")
@RestController
public class PcapController {

    private final PcapService pcapService;

    public PcapController(PcapService pcapService) {
        this.pcapService = pcapService;
    }

    @GetMapping("/handlers")
    public Set<String> getHandlers() throws IOException {
        return pcapService.getHandlers();
    }

    @GetMapping("/file")
    public ResponseEntity<?> getWsFile(HttpServletResponse response) throws IOException {
        InputStream inStream = new FileInputStream(pcapService.getWsFile());
        IOUtils.copy(inStream, response.getOutputStream());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/file/{name}")
    public ResponseEntity<?> getSpecifiedWsFile(HttpServletResponse response, @PathVariable(name = "name") String name) throws IOException {
        InputStream inStream = new FileInputStream(pcapService.getSpecifiedWsFile(name));
        IOUtils.copy(inStream, response.getOutputStream());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}