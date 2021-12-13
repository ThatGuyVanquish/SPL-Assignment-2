package bgu.spl.mics.application.objects;


import bgu.spl.mics.MessageBusImpl;

import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;
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
	private PriorityQueue<CPU> cpuQueue;
	/**
     * Retrieves the single instance of this class.
     */
	public static Cluster getInstance() {
		return CLUSTER;
	}

	public void addGPU(GPU gpu) {
		this.gpuVector.add(gpu);
	}

	public void addCPUS(Vector<CPU> cpus) {
		cpuQueue = new PriorityQueue<>(new CPUWorkComparator());
		cpuQueue.addAll(cpus);
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

	private static class CPUWorkComparator implements Comparator<CPU> {
		@Override
		public int compare(CPU cpu1, CPU cpu2) {
			int timeComp = Long.compare(cpu1.getTimeToProcessAll(), cpu2.getTimeToProcessAll());
			if (timeComp != 0) return timeComp;
			return Integer.compare(cpu2.getCores(), cpu1.getCores());
		}
	}
}
