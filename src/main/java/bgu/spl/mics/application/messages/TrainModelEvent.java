package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.Model;

public class TrainModelEvent implements Event<Model> {
    private Model model;

    public TrainModelEvent(Model model){
        this.model = model;
    }

    public Model getModel() {
        return model;
    }
}
