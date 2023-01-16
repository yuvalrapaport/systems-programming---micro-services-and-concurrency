package bgu.spl.mics.application;

import bgu.spl.mics.application.objects.ConfrenceInformation;
import bgu.spl.mics.application.objects.GPU;
import bgu.spl.mics.application.objects.Student;

public class InputReader {
    Student [] Students;
    GPU.Type [] GPUS;
    int [] CPUS;
    ConfrenceInformation[] Conferences;
    int TickTime;
    int Duration;

    public Student[] getStudents() {
        return Students;
    }

    public GPU.Type[] getGPUS() {
        return GPUS;
    }

    public int[] getCPUS() {
        return CPUS;
    }

    public ConfrenceInformation[] getConferences() {
        return Conferences;
    }

    public int getTickTime() {
        return TickTime;
    }

    public int getDuration() {
        return Duration;
    }
}
