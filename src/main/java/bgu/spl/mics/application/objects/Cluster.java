package bgu.spl.mics.application.objects;


import bgu.spl.mics.MessageBusImpl;

/**
 * Passive object representing the cluster.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Cluster {

	// Should Cluster also have access to CPUs? Or at least to the message bus

	private static final Cluster CLUSTER = new Cluster();
	private static final MessageBusImpl MESSAGE_BUS = MessageBusImpl.getInstance();
	/**
     * Retrieves the single instance of this class.
     */
	public static Cluster getInstance() {
		return CLUSTER;
	}

	public void processData(int maxBatches) {
		// Should look at all CPUs and send appropriate amount of batches to CPUs
	}

    public void addProcessedData(DataBatch dataBatch){
		if (dataBatch != null)
		{
			dataBatch.getData().processData();
		}
	} // What is this for? Should this send the batch back to the GPU?
}
