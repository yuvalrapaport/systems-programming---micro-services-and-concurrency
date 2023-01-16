package bgu.spl.mics.application.services;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.Callback;
import bgu.spl.mics.Message;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;

import java.util.*;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link TickBroadcast}.
 * This class may not hold references for objects which it is not responsible for.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService {

	private int speed;
	private int duration;

	public TimeService(int speed, int duration) {
		super("Time-Service");
		this.speed = speed;
		this.duration = duration;
	}

	@Override
	protected void initialize() {
		callbacks.put(TerminateBroadcast.class, new TickBroadcastCallback());

		//subscribe
		subscribeBroadcast(TerminateBroadcast.class, callbacks.get(TerminateBroadcast.class));

		//send ticks
		Timer timer = new Timer();
		TimerTask sendTick = new TimerTask() {
			@Override
			public void run() {
				if (duration > 0){
					sendBroadcast(new TickBroadcast(duration, speed));
					}
				else {
					sendBroadcast(new TerminateBroadcast());
					timer.cancel();
				}
					duration--;

			}
		};
		if (duration>=0)
			timer.schedule(sendTick, speed, speed);
	}
	private class TickBroadcastCallback implements Callback {
		@Override
		public void call(Object c) {
			if (c instanceof TerminateBroadcast){
				terminate();
			}
		}
	}

}


