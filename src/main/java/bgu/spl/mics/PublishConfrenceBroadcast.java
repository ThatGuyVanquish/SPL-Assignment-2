package bgu.spl.mics;

/**
 * Broadcast implementation to publish a conference result
 * after publishing results the conference will unregister from the system.
 */

public class PublishConfrenceBroadcast implements Broadcast{
    private int publicationsNum;
    public PublishConfrenceBroadcast(int publicationsNum){ this.publicationsNum = publicationsNum; }
    public int getPublicationsNum() { return this.publicationsNum; }
}
