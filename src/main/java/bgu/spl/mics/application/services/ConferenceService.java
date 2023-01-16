package bgu.spl.mics.application.services;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.Callback;
import bgu.spl.mics.Message;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.PublishConferenceBroadcast;
import bgu.spl.mics.application.messages.PublishResultsEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.ConfrenceInformation;

import java.util.HashMap;

/**
 * Conference service is in charge of
 * aggregating good results and publishing them via the {@link PublishConferenceBroadcast},
 * after publishing results the conference will unregister from the system.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ConferenceService extends MicroService {

    private ConfrenceInformation info;
    private int timer;

    public ConferenceService(String name, ConfrenceInformation info) {
        super(name);
        this.info = info;
        this.timer = 0;
    }

    @Override
    protected void initialize() {
        callbacks.put(TickBroadcast.class, new TickBroadcastCallback());
        callbacks.put(PublishResultsEvent.class, new PublishResultsCallback());
        callbacks.put(TerminateBroadcast.class, new TickBroadcastCallback());

        //subscribe
        subscribeBroadcast(TickBroadcast.class, callbacks.get(TickBroadcast.class));
        subscribeEvent(PublishResultsEvent.class, callbacks.get(PublishResultsEvent.class));
        subscribeBroadcast(TerminateBroadcast.class, callbacks.get(TerminateBroadcast.class));

        //send events
    }

    private class TickBroadcastCallback implements Callback {
        @Override
        public void call(Object c) {
            timer++;
            if (c instanceof TerminateBroadcast){
                terminate();
            }
           if (timer==info.getDate()) {
                sendBroadcast(new PublishConferenceBroadcast(info.getSucModels()));
                terminate();
            }
        }
    }

    private class PublishResultsCallback implements Callback{
        @Override
        public void call(Object c) {
            PublishResultsEvent event = (PublishResultsEvent) c;
            info.setSucModels(event.getModel());
            complete(event, true);
        }
    }
}
