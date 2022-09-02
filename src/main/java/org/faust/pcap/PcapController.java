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

import org.faust.event.Event;
import org.faust.event.EventService;

@RestController
@RequestMapping("/pcap")
public class PcapController {

    private final PcapService pcapService;

    private final EventService eventService;

    public PcapController(PcapService pcapService, EventService eventService) {
        this.pcapService = pcapService;
        this.eventService = eventService;
    }

    @GetMapping("/handlers")
    public Set<String> getHandlers() {
        return pcapService.getHandlers();
    }

    @PostMapping("/handlers")
    public void addHandler() {
        // pass here DTO, map to ForwardConfig, add to service and start?
        // requires refactoring of starting forwarders
    }

    @DeleteMapping
    public void removeHandler() {
        // pass here... name of forwarded? and remove from... where?
        // required refactoring, and stopping forwarders, and waiting to drain existing connections
        // to think
    }

    @GetMapping("/file")
    public ResponseEntity<?> getWsFile(HttpServletResponse response) throws IOException {
        eventService.addEvent("Reading file with all packet captures.");
        InputStream inStream = new FileInputStream(pcapService.getWsFile());
        IOUtils.copy(inStream, response.getOutputStream());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/file/{name}")
    public ResponseEntity<?> getSpecifiedWsFile(HttpServletResponse response, @PathVariable(name = "name") String name) throws IOException {
        eventService.addEvent("Reading specified packet capture: " + name);
        InputStream inStream = new FileInputStream(pcapService.getSpecifiedWsFile(name));
        IOUtils.copy(inStream, response.getOutputStream());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/events")
    public Iterable<Event> getEvents() {
        return eventService.getEvents();
    }
}
