package bgu.spl.mics.application.objects;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class CPUTest {
    private static CPU cpu;
    private static Cluster cluster;
    private static DataBatch DB;

    @Before
    public void setUp() throws Exception {
        cluster = Cluster.getInstance();
        cpu = new CPU(32);
        Data data = new Data(Data.Type.Images, 20000);
        DB = new DataBatch(data,1000);
        Vector<DataBatch> vec = new Vector<>();
        vec.add(DB);
        cluster.receiveDataFromGpu(vec);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void receiveData() {
        assertNull(cpu.getData());
        cpu.receiveData();
        assertNotNull(cpu.getData());
    }

    @Test
    public void incrementCpuTime() {
        AtomicInteger prevTime = cluster.getCpuTime();
        cpu.incrementCpuTime();
        assertTrue(prevTime.incrementAndGet() == cluster.getCpuTime().get());
    }

    @Test
    public void incrementBatchesProcessed() {
        AtomicInteger prevProcessed= cluster.getBatchesProcessed();
        cpu.incrementBatchesProcessed();
        assertTrue(prevProcessed.incrementAndGet() == cluster.getBatchesProcessed().get());
    }

}