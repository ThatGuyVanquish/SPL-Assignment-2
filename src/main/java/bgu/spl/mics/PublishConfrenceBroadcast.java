package bgu.spl.mics;

import bgu.spl.mics.application.objects.ConfrenceInformation;
import bgu.spl.mics.application.objects.Model;

import java.util.Vector;

/**
 * Broadcast implementation to publish a conference result
 * after publishing results the conference will unregister from the system.
 */

public class PublishConfrenceBroadcast implements Broadcast{
    private Vector<Model> publishedModels;

    public PublishConfrenceBroadcast(Vector<Model> modelVector){
        this.publishedModels = modelVector;
    }

    public Vector<Model> getPublishedModels() { return this.publishedModels; }

}
