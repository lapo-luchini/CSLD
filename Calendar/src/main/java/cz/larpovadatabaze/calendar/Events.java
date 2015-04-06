package cz.larpovadatabaze.calendar;

import org.springframework.http.HttpStatus;

import java.util.Collection;

/**
 * Created by domaci on 6.4.2015.
 */
public interface Events {
    Collection<Event> all();

    String serialize(Collection<Event> all);

    Collection<Event> deserialize(String events);
}
