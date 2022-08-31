package org.faust.event;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name = "events")
public class Event {

    @Id
    @GeneratedValue
    private Long id; // TODO: change to UUID?
    private Long timestamp;
    private String message;

    // TODO: add constructors and getters/ or use lombok
}
