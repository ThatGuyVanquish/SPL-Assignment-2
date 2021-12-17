package bgu.spl.mics;

import bgu.spl.mics.application.objects.ConfrenceInformation;
import bgu.spl.mics.application.objects.Model;

import javax.jws.WebParam;
import java.util.Vector;

/**
 * Broadcast implementation to publish a conference result
 * after publishing results the conference will unregister from the system.
 */

public class PublishConfrenceBroadcast implements Broadcast{

   private final Vector<Model> models;

    public PublishConfrenceBroadcast(Vector<Model> models){
        this.models = models;
    }

    public Vector<Model> getModels() {
        return models;
    }
}
