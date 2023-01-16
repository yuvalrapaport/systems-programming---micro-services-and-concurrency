package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;

/**
 * Student is responsible for sending the {@link TrainModelEvent},
 * {@link TestModelEvent} and {@link PublishResultsEvent}.
 * In addition, it must sign up for the conference publication broadcasts.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class StudentService extends MicroService {
    private Student student;
    private Thread sender;

    public StudentService(String name, Student student) {
        super("Student-Service " + name);
        this.student = student;

    }

    @Override
    protected void initialize() {
        callbacks.put(PublishConferenceBroadcast.class, new PublishConferenceCallback());
        callbacks.put(TerminateBroadcast.class, new TickBroadcastCallback());
        callbacks.put(TickBroadcast.class, new TickBroadcastCallback());

        //subscribe
        subscribeBroadcast(TerminateBroadcast.class, callbacks.get(TerminateBroadcast.class));
        subscribeBroadcast(PublishConferenceBroadcast.class, callbacks.get(PublishConferenceBroadcast.class));
        subscribeBroadcast(TickBroadcast.class, callbacks.get(TickBroadcast.class));

        //create new thread in charge of sending events
        sender = new Thread(() -> {
            for (Model model : student.getModels()) {
                if (terminated)
                    break;
                Future trainFuture = null;
                while (trainFuture == null)
                    trainFuture = sendEvent(new TrainModelEvent(model));
                Model mod = (Model) trainFuture.get();

                if (mod != null) {
                    model = mod;
                    Future testFuture = sendEvent(new TestModelEvent(model));
                    Future publishFuture = null;
                    if ((testFuture.get() == "GOOD")){
                         while (publishFuture == null && !terminated){
                             publishFuture = sendEvent(new PublishResultsEvent(model));
                        }
                    }
                 }
            }
        });
        sender.start();
    }

    private class TickBroadcastCallback implements Callback{
        @Override
        public void call(Object c) {
            if (c instanceof TerminateBroadcast) {
                terminate();
                sender.interrupt();
            }
        }
    }

    private class PublishConferenceCallback implements Callback{
        @Override
        public void call(Object c) {
            PublishConferenceBroadcast broadcast = (PublishConferenceBroadcast) c;
           for (Model model : broadcast.getSucModels()){
               if(!model.getStudent().equals(student))
                   student.incrementPapersRead();
               else
                   student.incrementPublications();
           }
        }
    }
}
