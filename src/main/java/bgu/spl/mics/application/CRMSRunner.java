package bgu.spl.mics.application;

import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.*;
import bgu.spl.mics.application.objects.Cluster;
import com.google.gson.*;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Locale;
import java.util.Vector;

/** This is the Main class of Compute Resources Management System application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output a text file.
 */

public class CRMSRunner {
    private static JsonParser parser = new JsonParser();

    public static void main(String[] args) {
        //InputStream inputStream = CRMSRunner.class.getClassLoader().getResourceAsStream(args[0]); The line we'd probably use to compile
        InputStream inputStream = CRMSRunner.class.getClassLoader().getResourceAsStream("test.json");
        Reader reader = new InputStreamReader(inputStream);
        JsonElement rootElement = parser.parse(reader);
        JsonObject rootObject = rootElement.getAsJsonObject();
        Vector<StudentService> studentVector = new Vector<>(); // Vector to store StudentService objects, need to move to msgBus?
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
            studentVector.add(new StudentService(name, newStudent));
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
        Cluster _c = Cluster.getInstance();
        Vector<GPUService> gpus = new Vector<>();
        JsonArray gpuArr = rootObject.getAsJsonArray("GPUS");
        for (JsonElement e : gpuArr) {
            String gpuTypeStr = e.getAsString().toLowerCase();
            GPU.Type gpuType = null;
            switch (gpuTypeStr) {
                case "rtx3090" :
                    gpuType = GPU.Type.RTX3090;
                    break;
                case "gtx1080" :
                    gpuType = GPU.Type.GTX1080;
                    break;
                case "rtx2080" :
                    gpuType = GPU.Type.RTX2080;
                    break;
            }
            gpus.add(new GPUService(gpuTypeStr, new GPU(gpuType, _c)));
        }
        Vector<CPUService> cpus = new Vector<>();
        JsonArray cpuArr = rootObject.getAsJsonArray("CPUS");
        for (JsonElement e : cpuArr) {
            int cpuCoreCount = e.getAsInt();
            cpus.add(new CPUService(e.getAsString(),new CPU(cpuCoreCount)));
        }
        Vector<ConferenceService> confVector = new Vector<>();
        JsonArray confArr = rootObject.getAsJsonArray("Conferences");
        for (JsonElement e : confArr) {
            String confName = e.getAsJsonObject().get("name").getAsString();
            int confDate = e.getAsJsonObject().get("date").getAsInt();
            confVector.add(new ConferenceService(confName, new ConfrenceInformation(confName, confDate)));
        }
        int tickTime = rootObject.get("TickTime").getAsInt();
        int tickDur = rootObject.get("Duration").getAsInt();
        TimeService _globalTimer = new TimeService(tickTime, tickDur);
    }
}
