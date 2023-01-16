package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

public class TickBroadcast implements Broadcast {
    private int duration;
    private int speed;
    public TickBroadcast(int duration, int speed){
        this.duration = duration;
        this.speed = speed;
    }

    public int getDuration() {
        return duration;
    }

    public int getSpeed() {
        return speed;
    }
}
