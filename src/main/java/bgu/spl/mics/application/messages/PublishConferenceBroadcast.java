package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.application.objects.Model;
import java.util.*;

public class PublishConferenceBroadcast implements Broadcast {
    private LinkedList<Model> sucModels;
    public PublishConferenceBroadcast(LinkedList<Model> sucModels){
        this.sucModels = sucModels;
    }
    public PublishConferenceBroadcast(){}//for testing
    public LinkedList<Model> getSucModels() {
        return sucModels;
    }
}
