package org.faust.event;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
@Entity(name = "events")
@Table(name = " PcapEvents")
public class Event {

    @Id
    @GenericGenerator(name = "UUIDGenerator", strategy = "uuid2")
    @GeneratedValue(generator = "UUIDGenerator")
    @Column(name = "eventId", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "timestamp")
    private Long timestamp = System.currentTimeMillis();

    @NonNull
    @Column(name = "eventMessage", nullable = false)
    private String message;
}
