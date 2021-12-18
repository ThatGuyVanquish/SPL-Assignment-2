package bgu.spl.mics.application;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.*;
import bgu.spl.mics.application.objects.Cluster;
import com.google.gson.*;
import org.junit.rules.Stopwatch;

import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.Timer;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

/** This is the Main class of Compute Resources Management System application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output a text file.
 */

public class CRMSRunner {
    private static final JsonParser PARSER = new JsonParser();
    private static final Cluster CLUSTER = Cluster.getInstance();
    private static final MessageBusImpl MESSAGE_BUS = MessageBusImpl.getInstance();

    public static void main(String[] args) {
        Instant start = Instant.now();
        //InputStream inputStream = CRMSRunner.class.getClassLoader().getResourceAsStream(args[0]); The line we'd probably use to compile
        InputStream inputStream = CRMSRunner.class.getClassLoader().getResourceAsStream("test.json");
        Reader reader = new InputStreamReader(inputStream);
        JsonElement rootElement = PARSER.parse(reader);
        JsonObject rootObject = rootElement.getAsJsonObject();
        Vector<Thread> threadHolder = new Vector<>();
        CountDownLatch countDownTimer;
        CountDownLatch countDownStudent;

        // Creating all of the Student Services
        Vector<StudentService> studentServiceVector = new Vector<>(); // Vector to store StudentService objects, need to move to msgBus?
        Vector<Student> studentVector = new Vector<>();
        JsonArray students = rootObject.getAsJsonArray("Students");
        for (JsonElement student : students) {
            JsonObject currentStudent = student.getAsJsonObject();
            String name = currentStudent.get("name").getAsString();
            String department = currentStudent.get("department").getAsString();
            Student.Degree deg = null;
            String status = currentStudent.get("status").getAsString();
            switch (status) {
                case "MSc" :
                    deg = Student.Degree.MSc;
                    break;
                case "PhD" :
                    deg = Student.Degree.PhD;
                    break;
            }
            Student newStudent = new Student(name, department, deg);
            studentVector.add(newStudent);

            // Creating the models which are connected to the current Student object
            Vector<Model> modelVector = new Vector<>();
            JsonArray models = currentStudent.getAsJsonArray("models");
            for (JsonElement e : models)
            {
                JsonObject currentModel = e.getAsJsonObject();
                String modelName = currentModel.get("name").getAsString();
                Data.Type type = null;
                switch (currentModel.get("type").getAsString().toLowerCase()) {
                    case "images" :
                        type = Data.Type.Images;
                        break;
                    case "text" :
                        type = Data.Type.Text;
                        break;
                    case "tabular" :
                        type = Data.Type.Tabular;
                        break;
                }
                int dataSize = currentModel.get("size").getAsInt();
                Data currentModelData = new Data(type, dataSize);
                modelVector.add(new Model(modelName, currentModelData, newStudent));
            }
            newStudent.addModels(modelVector);
        }

        // Creating the GPU Services
        JsonArray gpuArr = rootObject.getAsJsonArray("GPUS");
        Vector<GPU> gpus = new Vector<>();
        for (JsonElement e : gpuArr) {
            String gpuTypeStr = e.getAsString();
            GPU.Type gpuType = null;
            switch (gpuTypeStr) {
                case "RTX3090" :
                    gpuType = GPU.Type.RTX3090;
                    break;
                case "RTX2080" :
                    gpuType = GPU.Type.RTX2080;
                    break;
                case "GTX1080" :
                    gpuType = GPU.Type.GTX1080;
                    break;
            }
            GPU newGPU = new GPU(gpuType);
            gpus.add(newGPU);
        }
        CLUSTER.addGPUS(gpus);

        // Creating the CPU Services
        JsonArray cpuArr = rootObject.getAsJsonArray("CPUS");
        Vector<CPU> cpus = new Vector<>();
        for (JsonElement e : cpuArr) {
            int cpuCoreCount = e.getAsInt();
            CPU newCPU = new CPU(cpuCoreCount);
            cpus.add(newCPU);
        }
        CLUSTER.addCPUS(cpus);
        Vector<ConfrenceInformation> confVector = new Vector<>();
        JsonArray confArr = rootObject.getAsJsonArray("Conferences");
        for (JsonElement e : confArr) {
            String confName = e.getAsJsonObject().get("name").getAsString();
            int confDate = e.getAsJsonObject().get("date").getAsInt();
            ConfrenceInformation newConf = new ConfrenceInformation(confName, confDate);
            confVector.add(newConf);
        }
        for (Student student : studentVector) student.addConferences(confVector);

        int countDownSizeTimer = confVector.size() + gpus.size() + studentVector.size() + cpus.size(); // Making sure all mircoservices register before time service
        int countDownSizeStudent = confVector.size() + gpus.size() + cpus.size();
        countDownTimer = new CountDownLatch(countDownSizeTimer);
        countDownStudent = new CountDownLatch(countDownSizeStudent);

        for(GPU gpu : gpus){
            int i = 0;
            GPUService gpuService = new GPUService(gpu.toString(),gpu,countDownTimer,countDownStudent);
            gpu.setGpuService(gpuService);
            Thread thread = new Thread(gpuService);
            thread.setName("GPU " + i + " Thread");
            thread.start();
            i++;
        }
        for(CPU cpu : cpus){
            int i = 0;
            CPUService cpuService = new CPUService(cpu.toString(),cpu,countDownTimer,countDownStudent);
            Thread thread = new Thread(cpuService);
            threadHolder.add(thread);
            thread.setName("CPU " + i + " Thread");
            thread.start();
            i++;
        }
        for(ConfrenceInformation confInformation : confVector){
            ConferenceService confService = new ConferenceService(confInformation.getName(),confInformation,countDownTimer,countDownStudent);
            Thread thread = new Thread(confService);
            threadHolder.add(thread);
            thread.setName("Conference " + confInformation.getName() + " Thread");
            thread.start();
        }
        try {
            countDownStudent.await(); // Student threads wait for cpus, gpus and conferences to register
        }
        catch (InterruptedException ignored){}
        for(Student student:studentVector){
            student.setConferenceNum(confVector.size());
            StudentService studentService = new StudentService(student.getName(),student,countDownTimer);
            Thread thread = new Thread(studentService);
            threadHolder.add(thread);
            thread.setName("Student " + student.getName() + " Thread");
            thread.start();
        }

        // Creating the TimeService MicroService
        int tickTime = rootObject.get("TickTime").getAsInt();
        int tickDur = rootObject.get("Duration").getAsInt();
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try{
            countDownTimer.await();
        }
        catch (InterruptedException ignored){}

        TimeService _globalTimer = new TimeService(tickTime, tickDur);
        Thread thread = new Thread(_globalTimer);
        thread.setName("Sheeeeeeesh");
        threadHolder.add(thread);
        thread.start();

        for (Thread thread1 : threadHolder) { // Shut down all the threads
            try {
                thread1.join();
            } catch (InterruptedException ignored) {
            }
        }
      int realProcessed = 0;
        for (Student student : studentVector){
            System.out.println("paper "+student.getPapersRead());
            for (Model model : student.getModels()){
                System.out.println("Size: "+model.getData().getSize()+"  "+"Processed: "+model.getData().getProcessed());
                realProcessed += model.getData().getProcessed();
            }
        }
        //System.out.println(realProcessed/1000);
        //System.out.println("Message Bus: ");
        //System.out.println(MESSAGE_BUS.toString()+"\n\n");
        Instant end1 = Instant.now();
        System.out.println("Stopwatch stopped at " + Duration.between(start, end1).toMillis());
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter("Output.txt");
            fileWriter.write(studentVector.toString() + "\n");
            fileWriter.write(confVector.toString() + "\n");
            fileWriter.write("cpuTimeUsed: " + CLUSTER.getTotalCPURuntime() + "\n");
            fileWriter.write("gpuTimeUsed: " + CLUSTER.getTotalGPURuntime() + "\n");
            fileWriter.write("batchesProcessed: " + CLUSTER.getBatchesProcessed() + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (fileWriter != null)
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return;
    }
}