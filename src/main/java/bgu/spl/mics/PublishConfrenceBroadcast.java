package bgu.spl.mics;

/**
 * Broadcast implementation to publish a conference result
 * after publishing results the conference will unregister from the system.
 * Created by us (not in the zip they uploaded to the Moodle)
 */

public class PublishConfrenceBroadcast implements Broadcast{
    private int publictionNum;
    public PublishConfrenceBroadcast(int publictionNum){
        this.publictionNum = publictionNum;
    }
    public int getPublictionNum() {
        return publictionNum;
    }
}
