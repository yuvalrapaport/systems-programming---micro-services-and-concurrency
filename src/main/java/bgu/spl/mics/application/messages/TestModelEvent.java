package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;

public class TestModelEvent implements Event<String> {

    private Model model;

    public TestModelEvent(Model model){
        this.model = model;
    }

    public TestModelEvent(){} //for testing

    public Model getModel() {
        return model;
    }
}
