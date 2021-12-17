package bgu.spl.mics.application.objects;


import bgu.spl.mics.MessageBusImpl;

import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive object representing the cluster.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Cluster {

	private static class SingletonHolder{
		private static final Cluster instance = new Cluster();
	}

	private static final MessageBusImpl MESSAGE_BUS = MessageBusImpl.getInstance();
	private Vector<GPU> gpuVector;
	private PriorityQueue<CPU> cpuQueue;
	private final AtomicInteger cpuTimeUsed = new AtomicInteger(0);
	private final AtomicInteger gpuTimeUsed = new AtomicInteger(0);
	private final AtomicInteger batchesProcessed = new AtomicInteger(0);
	private  final Object cpuLock = new Object();
	private final Object gpuLock = new Object();

	private Cluster(){}

	/**
     * Retrieves the single instance of this class.
     */
	public static Cluster getInstance() {
		return SingletonHolder.instance;
	}

	public void addGPUS(Vector<GPU> gpus) {
		this.gpuVector = gpus;
	}

	/**
	 *
	 * @param cpus CPU Vector to assign to Cluster
	 * @pre this.cpuQueue.isEmpty()
	 * @post !this.cpuQueue.isEmpty()
	 */
	public void addCPUS(Vector<CPU> cpus) {
		this.cpuQueue = new PriorityQueue<>(cpus.size(), new CPUWorkComparator());
		this.cpuQueue.addAll(cpus);
	}

	public void processData(Vector<DataBatch> dataBatchVector) {
		synchronized (cpuLock) {
			CPU currentCPU = null;
			if (!cpuQueue.isEmpty()) currentCPU = cpuQueue.poll();
			while (!dataBatchVector.isEmpty()) {
				if (currentCPU != null) {
					if (cpuQueue.peek() != null) {
						if (currentCPU.getTimeToProcessAll() >= cpuQueue.peek().getTimeToProcessAll()) {
							cpuQueue.add(currentCPU);
							currentCPU = cpuQueue.poll();
						}
					}
					currentCPU.addDataBatch(dataBatchVector.remove(0));
				}
			}
			if (currentCPU != null) cpuQueue.add(currentCPU);
		}
	}

	public void processData(DataBatch dataBatch) {
		synchronized (cpuLock) {
			if (!cpuQueue.isEmpty()) {
				CPU currentCPU = cpuQueue.poll();
				currentCPU.addDataBatch(dataBatch);
				cpuQueue.add(currentCPU);
			}
		}
	}

	public void sendProcessedData(DataBatch dataBatch) {
		this.batchesProcessed.addAndGet( 1);
		synchronized (gpuLock) {
			dataBatch.getGPU().addBatch(dataBatch);
		}
	}

	private static class CPUWorkComparator implements Comparator<CPU> {
		@Override
		public int compare(CPU cpu1, CPU cpu2) {
			int timeComp = Long.compare(cpu1.getTimeToProcessAll(), cpu2.getTimeToProcessAll());
			if (timeComp != 0) return timeComp;
			return Integer.compare(cpu2.getCores(), cpu1.getCores());
		}
	}

	public void addGPURuntime(int runtime) {
		this.gpuTimeUsed.addAndGet(runtime);
	}

	public void addCPURuntime(int runtime) {
		this.cpuTimeUsed.addAndGet(runtime);
	}

	public long getTotalCPURuntime() {
		return this.cpuTimeUsed.get();
	}

	public long getTotalGPURuntime() {
		return this.gpuTimeUsed.get();
	}

	public long getBatchesProcessed() {
		return this.batchesProcessed.get();
	}

}