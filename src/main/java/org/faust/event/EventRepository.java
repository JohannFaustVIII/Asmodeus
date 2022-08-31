package org.faust.event;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

// @Repository TODO: uncomment after configuring JPA datasource, to decide what database would be used, or load jdbc another way and keep it open?
public interface EventRepository extends CrudRepository<Event, Long> {
}
