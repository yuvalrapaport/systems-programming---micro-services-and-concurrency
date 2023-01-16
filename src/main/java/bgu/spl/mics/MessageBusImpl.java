package bgu.spl.mics;
import bgu.spl.mics.application.messages.*;

import java.util.*;
import java.util.concurrent.*;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

	private static MessageBusImpl bus;
	private ConcurrentHashMap<Class<? extends Message>, Queue<MicroService>> subscriptions;
	private ConcurrentHashMap<MicroService,Queue<Message>> mServices;
	private ConcurrentHashMap<Event, Future> connectFuture;

	private MessageBusImpl(){
		subscriptions = new ConcurrentHashMap<>();
		subscriptions.put(TrainModelEvent.class,new LinkedList<>());
		subscriptions.put(TestModelEvent.class,new LinkedList<>());
		subscriptions.put(PublishResultsEvent.class,new LinkedList<>());
		subscriptions.put(PublishConferenceBroadcast.class,new LinkedList<>());
		subscriptions.put(TickBroadcast.class,new LinkedList<>());
		subscriptions.put(TerminateBroadcast.class,new LinkedList<>());
		mServices = new ConcurrentHashMap<>();
		connectFuture = new ConcurrentHashMap<>();
	}

	public synchronized static MessageBusImpl getInstance(){
		if (bus==null)
			bus = new MessageBusImpl();
		return bus;
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		if (!m.isRegistered()){
			throw new IllegalStateException("micro service is not registered, cannot subscribe");
		}
		synchronized (subscriptions.get(type)) {
			if (!subscriptions.get(type).contains(m)) {
				subscriptions.get(type).add(m);
			}
			subscriptions.get(type).notifyAll();
		}
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		if (!m.isRegistered()){
			throw new IllegalStateException("micro service is not registered, cannot subscribe");
		}
		synchronized (subscriptions.get(type)) {
			if (!subscriptions.get(type).contains(m)) {
				subscriptions.get(type).add(m);
			}
			subscriptions.get(type).notifyAll();
		}
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		connectFuture.get(e).resolve(result);
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		Queue<MicroService> subscribed = subscriptions.get(b.getClass());
		try{
			synchronized(subscriptions.get(b.getClass())){
				for (MicroService m : subscribed){
					synchronized (mServices.get(m)) {
						mServices.get(m).add(b);
						mServices.get(m).notifyAll();
					}
				}
				subscriptions.get(b.getClass()).notifyAll();
			}
		}
		catch(Exception e){}
	}

	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		try{
			Future<T> future = null;
			synchronized (subscriptions.get(e.getClass())) {
				Queue<MicroService> subscribed = subscriptions.get(e.getClass());
				if (!subscribed.isEmpty()) {
					MicroService first = subscribed.poll();
					subscribed.add(first);
					synchronized (mServices.get(first)) {
						mServices.get(first).add(e);
						connectFuture.put(e,new Future());
						mServices.get(first).notifyAll();
					}

					future = connectFuture.get(e);
				}
				subscriptions.get(e.getClass()).notifyAll();
			}

		return future;
	}
		catch (Exception exception){
			return null;
		}
	}

	@Override
	public void register(MicroService m) {
		mServices.put(m,new LinkedList<>());
		m.setRegistered(true);
	}

	@Override
	public void unregister(MicroService m) {
		mServices.remove(m);
		for (Class<? extends Message> type : subscriptions.keySet()){
			synchronized (subscriptions.get(type)){
				if (subscriptions.get(type).contains(m))
					subscriptions.get(type).remove(m); //remove checks if object present in list
				subscriptions.get(type).notifyAll();
			}
		}
		m.setRegistered(false);
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		if (!m.isRegistered())
			throw new IllegalStateException("micro service "+m.getName()+" not registered");
		synchronized (mServices.get(m)) {
			while (mServices.get(m).isEmpty()) {
				mServices.get(m).wait();
			}
			Message message = mServices.get(m).poll();
			mServices.get(m).notifyAll();
			return message;
		}
	}


}

	


