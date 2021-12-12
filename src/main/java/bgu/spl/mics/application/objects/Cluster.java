package bgu.spl.mics.application.objects;


import bgu.spl.mics.MessageBusImpl;

import java.util.Vector;

/**
 * Passive object representing the cluster.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Cluster {

	private Cluster(){}

	private static final Cluster CLUSTER = new Cluster();
	private static final MessageBusImpl MESSAGE_BUS = MessageBusImpl.getInstance();
	private Vector<GPU> gpuVector;
	private Vector<CPU> cpuVector;
	/**
     * Retrieves the single instance of this class.
     */
	public static Cluster getInstance() {
		return CLUSTER;
	}

	public void addGPU(GPU gpu) {
		this.gpuVector.add(gpu);
	}

	public void addCPU(CPU cpu) {
		this.cpuVector.add(cpu);
	}

	public void processData(Vector<DataBatch> dataBatchVector) {
		// Should look at all CPUs and send appropriate amount of batches to CPUs
	}
	public void processData(DataBatch dataBatch) {
		// Should look at all CPUs and send appropriate amount of batches to CPUs
		/**
		 * Choice of cpu
		 */
		//someCPU.addDataBatch(dataBatch);
		//
	}

    public void addProcessedData(DataBatch dataBatch){ //
		if (dataBatch != null)
		{
			dataBatch.getData().processData();
		}
	}

	public void sendProcessedData(DataBatch dataBatch) {
		dataBatch.getGPU().addBatch(dataBatch);
	}
}
