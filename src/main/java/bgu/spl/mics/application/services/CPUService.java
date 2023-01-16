package bgu.spl.mics.application.services;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.Callback;
import bgu.spl.mics.Message;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.CPU;
import bgu.spl.mics.application.objects.DataBatch;

import java.util.HashMap;

/**
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class CPUService extends MicroService {

    private CPU cpu;
    private DataBatch currData;
    private int timer;
    private int startTime;

    public CPUService(String name, CPU cpu) {
        super(name);
        this.cpu = cpu;
        this.currData = null;
        this.timer = 0;
        this.startTime =0;
    }

    @Override
    protected void initialize() {
        callbacks.put(TickBroadcast.class, new TickBroadcastCallback());
        callbacks.put(TerminateBroadcast.class, new TickBroadcastCallback());


        //subscribe
        subscribeBroadcast(TickBroadcast.class, callbacks.get(TickBroadcast.class));
        subscribeBroadcast(TerminateBroadcast.class, callbacks.get(TerminateBroadcast.class));
    }

    private class TickBroadcastCallback implements Callback {
        @Override
        public void call(Object c) {
            timer ++;
            if (c instanceof TerminateBroadcast)
                terminate();
            else{
                if (currData != null){
                   cpu.incrementCpuTime();
                   if (timer == startTime+cpu.getNumberOfTicks()){
                       startTime = 0;
                       cpu.PassData();
                       currData = null;
                       cpu.incrementBatchesProcessed();
                   }
                }
                else { //currData = null
                    currData = cpu.receiveData();
                    startTime = timer;
                }
            }
        }
    }
}
