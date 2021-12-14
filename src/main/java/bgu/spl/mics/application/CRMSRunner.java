package bgu.spl.mics.application;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.*;
import bgu.spl.mics.application.objects.Cluster;
import com.google.gson.*;

import java.io.*;
import java.util.Locale;
import java.util.Vector;

/** This is the Main class of Compute Resources Management System application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output a text file.
 */

public class CRMSRunner {
    private static final JsonParser PARSER = new JsonParser();
    private static final Cluster CLUSTER = Cluster.getInstance();
    private static final MessageBusImpl MESSAGE_BUS = MessageBusImpl.getInstance();

    public static void main(String[] args) {
        //InputStream inputStream = CRMSRunner.class.getClassLoader().getResourceAsStream(args[0]); The line we'd probably use to compile
        InputStream inputStream = CRMSRunner.class.getClassLoader().getResourceAsStream("test.json");
        Reader reader = new InputStreamReader(inputStream);
        JsonElement rootElement = PARSER.parse(reader);
        JsonObject rootObject = rootElement.getAsJsonObject();

        // Creating the TimeService MicroService
        int tickTime = rootObject.get("TickTime").getAsInt();
        int tickDur = rootObject.get("Duration").getAsInt();
        TimeService _globalTimer = new TimeService(tickTime, tickDur);
        Thread thread1 = new Thread(_globalTimer);
        thread1.start();

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
            StudentService currentStudentService = new StudentService(name, newStudent);
            studentVector.add(newStudent);
            Thread thread = new Thread(currentStudentService);
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
            thread.start();
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
            GPUService currentGPU = new GPUService(gpuTypeStr, newGPU);
            Thread thread = new Thread(currentGPU);
            thread.start();
        }
        CLUSTER.addGPUS(gpus);

        // Creating the CPU Services
        JsonArray cpuArr = rootObject.getAsJsonArray("CPUS");
        Vector<CPU> cpus = new Vector<>();
        for (JsonElement e : cpuArr) {
            int cpuCoreCount = e.getAsInt();
            CPU newCPU = new CPU(cpuCoreCount);
            cpus.add(newCPU);
            CPUService currentCPU = new CPUService(e.getAsString(), newCPU);
            Thread thread = new Thread(currentCPU);
            thread.start();
        }
        CLUSTER.addCPUS(cpus);
        Vector<ConfrenceInformation> confVector = new Vector<>();
        JsonArray confArr = rootObject.getAsJsonArray("Conferences");
        for (JsonElement e : confArr) {
            String confName = e.getAsJsonObject().get("name").getAsString();
            int confDate = e.getAsJsonObject().get("date").getAsInt();
            ConfrenceInformation newConf = new ConfrenceInformation(confName, confDate);
            ConferenceService currentConf = new ConferenceService(confName, newConf);
            Thread thread = new Thread(currentConf);
            thread.start();
            confVector.add(newConf);
        }
        MESSAGE_BUS.addConferences(confVector);

        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter("Output.json");
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