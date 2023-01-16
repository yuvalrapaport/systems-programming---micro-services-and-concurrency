package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TestModelEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrainModelEvent;
import bgu.spl.mics.application.objects.GPU;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * GPU service is responsible for handling the
 * {@link TrainModelEvent} and {@link TestModelEvent},
 * in addition to sending the {DataPreProcessEvent}.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class GPUService extends MicroService {

    private GPU gpu;
    private LinkedList<Event> events;
    private int timer;
    private int dataBatchStart;
    private Event currentEvent;

    public GPUService(String name, GPU gpu) {
        super(name);
        this.gpu = gpu;
        this.events = new LinkedList<>();
        this.timer = 0;
        this.dataBatchStart = 0;
        this.currentEvent = null;
    }

    @Override
    protected void initialize() {
        // TODO Implement this
        callbacks.put(TickBroadcast.class, new TickBroadcastCallback());
        callbacks.put(TrainModelEvent.class, new MoveEventCallback());
        callbacks.put(TestModelEvent.class, new MoveEventCallback());
        callbacks.put(TerminateBroadcast.class, new TickBroadcastCallback());


        //subscribe
        subscribeBroadcast(TickBroadcast.class, callbacks.get(TickBroadcast.class));
        subscribeEvent(TrainModelEvent.class, callbacks.get(TrainModelEvent.class));
        subscribeEvent(TestModelEvent.class, callbacks.get(TestModelEvent.class));
        subscribeBroadcast(TerminateBroadcast.class, callbacks.get(TerminateBroadcast.class));
    }


    public int getTimer() {
        return timer;
    }

    private void testModel(TestModelEvent testEvent) {
        if (testEvent.getModel().getStudent().getStatus() == Student.Degree.MSc) {
            if (Math.random() <= 0.6) {
                testEvent.getModel().setResults(Model.Results.Good);
                complete(testEvent, "GOOD");

            } else {
                testEvent.getModel().setResults(Model.Results.Bad);
                complete(testEvent, "BAD");

            }
        } else {
            if (Math.random() <= 0.8) {
                testEvent.getModel().setResults(Model.Results.Good);
                complete(testEvent, "GOOD");

            } else {
                testEvent.getModel().setResults(Model.Results.Bad);
                complete(testEvent, "BAD");

            }
        }
        testEvent.getModel().setStatus(Model.Status.Tested);
    }

    private void preTraining() {
        TrainModelEvent trainEvent = (TrainModelEvent) events.getFirst();
        currentEvent = trainEvent;
        events.removeFirst();
        gpu.setModel(trainEvent.getModel());
        gpu.splitData();
        gpu.sendData();
        gpu.getModel().setStatus(Model.Status.Training);

    }

    private class TickBroadcastCallback implements Callback {
        @Override
        public void call(Object c) {
            timer++;
            if (c instanceof TerminateBroadcast)
                terminate();
            else {
                if (!events.isEmpty()) {
                    for (int i = 0; i < events.size(); i++) {
                        Event event = events.getLast();
                        if (event instanceof TestModelEvent) {
                            TestModelEvent testEvent = (TestModelEvent)event;
                            events.removeLast();
                            testModel(testEvent);
                        }
                        else
                            break;
                    }

                    if (!events.isEmpty() && gpu.getModel() == null)
                        preTraining();

                }

                if (gpu.getModel() != null) {
                    gpu.addProcessedData();
                    if (gpu.getProcessedSize() != 0) { //training a batch
                        gpu.incrementGpuTime();
                        if (dataBatchStart == 0) {
                            dataBatchStart = timer;
                        }
                        else if (timer == dataBatchStart + gpu.getNumberOfTicks()) {   //finished training a batch
                            gpu.decrementBatchesToTrain();
                            dataBatchStart = 0;

                            if (gpu.getBatchesToTrain() == 0) { //finished training a model
                                gpu.trainModel();
                                complete(currentEvent, gpu.getModel());
                                currentEvent = null;
                                gpu.setModel(null);

                            }
                        }
                    }
                }
            }
        }
    }

    private class MoveEventCallback implements Callback {
        @Override
        public void call(Object c) {
            if (c instanceof TrainModelEvent) {
                TrainModelEvent event = (TrainModelEvent) c;
                events.addFirst(event);
            }
            if (c instanceof TestModelEvent) {
                TestModelEvent event = (TestModelEvent) c;
                events.addLast(event);
            }
        }
    }
}
