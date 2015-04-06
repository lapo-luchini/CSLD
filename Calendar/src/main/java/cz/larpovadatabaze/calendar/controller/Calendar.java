package cz.larpovadatabaze.calendar.controller;

import cz.larpovadatabaze.calendar.Events;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collection;

/**
 * Controller providing functionality necessary for generating calendar.
 */
@Controller
@RequestMapping(value = "/")
public class Calendar {
    private Events events;

    @Autowired
    public Calendar(Events events){
        this.events = events;
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<String> listAll(){
        return new ResponseEntity<String>(events.serialize(events.all()), HttpStatus.OK);
    }
}
