package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.Student;
import bgu.spl.mics.application.objects.Model;

import java.util.LinkedList;

public class PublishResultsEvent implements Event<Boolean> {
    private Model model;

    public PublishResultsEvent(Model model){
        this.model = model;
    }

    public Model getModel() {
        return model;
    }
}
