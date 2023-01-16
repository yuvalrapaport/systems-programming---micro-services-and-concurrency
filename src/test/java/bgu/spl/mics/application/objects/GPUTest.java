package bgu.spl.mics.application.objects;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class GPUTest {

    private static GPU gpu;
    private static Cluster cluster;

    @Before
    public void setUp() throws Exception {
        gpu = new GPU(GPU.Type.GTX1080, 1);
        cluster = Cluster.getInstance();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void setModel() {
        Data data = new Data(Data.Type.Images, 20000);
        Model model = new Model(data);
        assertNull(gpu.getModel());
        gpu.setModel(model);
        assertEquals(model,gpu.getModel());
    }

    @Test
    public void splitData() {
        Data data = new Data(Data.Type.Images, 20000);
        Model model = new Model(data);
        gpu.setModel(model);
        assertEquals(0,gpu.getUnprocessedSize());
        gpu.splitData();
        assertFalse(gpu.getUnprocessedSize() == 0);
        assertEquals(data.getSize()/1000 , gpu.getBatchesToTrain());
    }

    @Test
    public void sendData() {
        Data data = new Data(Data.Type.Images, 20000);
        Model model = new Model(data);
        gpu.setModel(model);
        gpu.splitData();
        assertFalse(gpu.getUnprocessedSize() == 0);
        gpu.sendData();
        assertTrue(gpu.getUnprocessedSize() == 0);
    }

    @Test
    public void trainModel() {
        Data data = new Data(Data.Type.Images, 20000);
        Model model = new Model(data);
        gpu.setModel(model);
        assertFalse(model.getStatus() == Model.Status.Trained);
        gpu.trainModel();
        assertEquals(Model.Status.Trained, model.getStatus());
    }

    @Test
    public void decrementBatchesToTrain() {
        Data data = new Data(Data.Type.Images, 20000);
        Model model = new Model(data);
        gpu.setModel(model);
        gpu.splitData();
        int prev = gpu.getBatchesToTrain();
        gpu.addToProcessedData(new DataBatch(data, 1000));
        gpu.decrementBatchesToTrain();
        assertEquals(prev - 1, gpu.getBatchesToTrain());
    }

    @Test
    public void addToProcessedData() {
        Data data = new Data(Data.Type.Images, 20000);
        Model model = new Model(data);
        gpu.setModel(model);
        gpu.splitData();
        int prev = gpu.getProcessedSize();
        gpu.addToProcessedData(new DataBatch(data, 1000));
        assertEquals(prev + 1, gpu.getProcessedSize());
    }

    @Test
    public void incrementGpuTime() {
        AtomicInteger prevTime = cluster.getGpuTime();
        gpu.incrementGpuTime();
        assertTrue(prevTime.incrementAndGet() == cluster.getGpuTime().get());
    }
}