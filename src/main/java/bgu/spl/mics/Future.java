package bgu.spl.mics;

import bgu.spl.mics.application.objects.Model;

import javax.jws.WebParam;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * A Future object represents a promised result - an object that will
 * eventually be resolved to hold a result of some operation. The class allows
 * Retrieving the result once it is available.
 * 
 * Only private methods may be added to this class.
 * No public constructor is allowed except for the empty constructor.
 */
public class Future<T> {

	private T result; //added field
	private boolean complete;

	/**
	 * This should be the the only public constructor in this class.
	 */
	public Future() {
		result = null;
		complete = false;
	}


	/**
     * retrieves the result the Future object holds if it has been resolved.
     * This is a blocking method! It waits for the computation in case it has
     * not been completed.
     * <p>
     * @return return the result of type T if it is available, if not wait until it is available.
	 * @pre: future is resolved
     */
	public synchronized T get() {
		if (result == null){
			try{
				this.wait();
			}
			catch (Exception ex){}
			this.notifyAll();
		}
		return result;
	}

	/**
     * Resolves the result of this Future object.
	 * @param result the result of this Future object
	 * @pre: complete = false, result = null
	 * @post: this.result == result
     */
	public synchronized void resolve (T result) {
		this.result=result;
		complete=true;
		this.notifyAll();
	}
	
	/**
     * @return true if this object has been resolved, false otherwise
     */
	public boolean isDone() {
		return complete;
	}
	
	/**
     * retrieves the result the Future object holds if it has been resolved,
     * This method is non-blocking, it has a limited amount of time determined
     * by {@code timeout}
     * <p>
     * @param timeout 	the maximal amount of time units to wait for the result.
     * @param unit		the {@link TimeUnit} time units to wait.
     * @return return the result of type T if it is available, if not, 
     * 	       wait for {@code timeout} TimeUnits {@code unit}. If time has
     *         elapsed, return null.
     */
	public synchronized T get(long timeout, TimeUnit unit) {
		if (result == null){
			try{
				this.wait(unit.toMillis(timeout));
			}
			catch (IllegalMonitorStateException timeException){}
			catch (InterruptedException interruptedException){
				this.notifyAll();
			}
		}
		return result;

	}
}
