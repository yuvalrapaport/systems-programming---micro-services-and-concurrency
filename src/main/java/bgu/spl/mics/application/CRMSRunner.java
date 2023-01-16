package bgu.spl.mics.application;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.*;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Vector;

import com.google.gson.*;

/** This is the Main class of Compute Resources Management System application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output a text file.
 */
public class CRMSRunner {
    public static void main(String[] args) {
        InputReader inputReader = new InputReader();
        String path = args[0];
        Gson gson = new Gson();
        try {
            Reader reader = Files.newBufferedReader(Paths.get(String.valueOf(path)));
            inputReader = gson.fromJson(reader, inputReader.getClass());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        Student[] students = inputReader.getStudents();
        GPU.Type[] gpuTypes = inputReader.getGPUS();
        int[] cpuCores = inputReader.getCPUS();
        ConfrenceInformation[] conferences = inputReader.getConferences();
        int tickTime = inputReader.getTickTime();
        int duration = inputReader.getDuration();
        Cluster cluster = Cluster.getInstance();
        MessageBusImpl.getInstance();
        Vector<Thread> threads = new Vector<>();

        Thread timeThread = new Thread(new TimeService(tickTime, duration));
        threads.add(timeThread);

        for (Student student : students) {
            for (Model model : student.getModels()){
                model.setRemains(new Data(model.getType(), model.getSize()),student, Model.Results.None, Model.Status.PreTrained);
            }
            threads.add(new Thread(new StudentService(student.getName(), student)));
        }
        for (int i = 1; i <= gpuTypes.length; i++) {
            GPU gpu = new GPU(gpuTypes[i - 1],i-1);
            threads.add(new Thread(new GPUService("GPU " + i, gpu)));
            cluster.addGPU(gpu);
        }
        for (int i = 1; i <= cpuCores.length; i++) {
            CPU cpu = new CPU(cpuCores[i - 1]);
            threads.add(new Thread(new CPUService("CPU " + i, cpu)));
            cluster.addCPU(cpu);
        }
        for (ConfrenceInformation conference : conferences) {
            threads.add(new Thread(new ConferenceService(conference.getName(), conference)));
            conference.setRemains();
        }
        for (Thread thread : threads) {
            thread.start();
        }
        try {
            timeThread.join();
        }
        catch (Exception ex){}

        for (Thread thread : threads) {
            try{
                if (!thread.equals(timeThread))
                    thread.join();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
// output file
        JsonObject output = new JsonObject();
        JsonArray outStudents = new JsonArray();
        for (Student s : students){
            JsonObject stu = new JsonObject();
            stu.addProperty("name",s.getName());
            stu.addProperty("department",s.getDepartment());
            stu.addProperty("status",s.getStatus().toString());
            stu.addProperty("publications",s.getPublications());
            stu.addProperty("papersRead", s.getPapersRead());
            JsonArray trainedModels =  new JsonArray();
            for (Model m : s.getModels()){
                JsonObject mod = new JsonObject();
                if (m.getStatus() == Model.Status.Trained || m.getStatus() == Model.Status.Tested){
                    mod.addProperty("name",m.getName());
                    JsonObject d = new JsonObject();
                    d.addProperty("type", m.getData().getType().toString());
                    d.addProperty("size",m.getData().getSize());
                    mod.add("data",d);
                    mod.addProperty("status", m.getStatus().toString());
                    mod.addProperty("results", m.getResults().toString());
                    trainedModels.add(mod);
                }
            }
            stu.add("trainedModels",trainedModels);
            outStudents.add(stu);
        }
        output.add("students",outStudents);

        JsonArray outConferences = new JsonArray();
        for (ConfrenceInformation c : conferences){
            JsonObject con = new JsonObject();
            con.addProperty("name", c.getName());
            con.addProperty("date", c.getDate());
            JsonArray published = new JsonArray();
            for (Model m : c.getSucModels()){
                JsonObject mod = new JsonObject();
                mod.addProperty("name", m.getName());
                JsonObject d = new JsonObject();
                d.addProperty("type",m.getData().getType().toString());
                d.addProperty("size",m.getData().getSize());
                mod.add("data",d);
                mod.addProperty("status", m.getStatus().toString());
                mod.addProperty("result",m.getResults().toString());
                published.add(mod);
            }
            con.add("publications",published);
            outConferences.add(con);
        }
        output.add("conferences",outConferences);
        output.addProperty( "cpuTimeUsed",cluster.getCpuTime());
        output.addProperty("gpuTimeUsed",cluster.getGpuTime());
        output.addProperty("batchesProcessed", cluster.getBatchesProcessed());

        FileWriter outputFile = null;
        try{
            outputFile = new FileWriter("output_file.json");
            Gson gson1 = new GsonBuilder().setPrettyPrinting().create();
            JsonParser jp = new JsonParser();
            JsonElement je = jp.parse(output.toString());
            String prettyJsonString = gson1.toJson(je);
            outputFile.write(prettyJsonString);
            outputFile.flush();
            outputFile.close();
        } catch (IOException e){
                e.printStackTrace();
            }
    }
}
