package org.anvard.mcnpv;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveTask;

public class NpvTask extends RecursiveTask<StatsCollector> {

	private static final long serialVersionUID = -3375054929832400598L;

	private int minChunkSize = 100;
	private int numChunks = 2;
	private double min;
	private double max;
	private int numBuckets;
	private int numIterations;
	private Distribution rate;
	private Distribution[] flows;

	public NpvTask(int numBuckets, int numIterations, Distribution rate,
			Distribution... flows) {
		this.numBuckets = numBuckets;
		this.numIterations = numIterations;
		this.rate = rate;
		this.flows = flows;
		calculateMinMax();
	}

	public NpvTask(double min, double max, int numBuckets, int numIterations,
			Distribution rate, Distribution... flows) {
		this.min = min;
		this.max = max;
		this.numBuckets = numBuckets;
		this.numIterations = numIterations;
		this.rate = rate;
		this.flows = flows;
	}

	public void setMinChunkSize(int minChunkSize) {
		this.minChunkSize = minChunkSize;
	}

	public void setNumChunks(int numChunks) {
		this.numChunks = numChunks;
	}

	public int calcNumChunks(int n) {
		int nc = (int)Math.ceil(Math.sqrt(n/minChunkSize));
		return nc;
	}
	
	@Override
	protected StatsCollector compute() {
		StatsCollector collector = new StatsCollector(min, max, numBuckets);
		int children = (numChunks < 0) ? calcNumChunks(numIterations) : numChunks;
		if (numIterations <= minChunkSize || children == 1) {
			for (int i = 0; i < numIterations; i++) {
				collector.addObs(NetPresentValue.npv(sampleFlows(),
						rate.sample()));
			}
		} else {
			List<NpvTask> subTasks = new ArrayList<>(children);
			for (int i = 0; i < children; i++) {
				NpvTask subTask = new NpvTask(min, max, numBuckets,
						numIterations / children, rate, flows);
				subTask.setMinChunkSize(minChunkSize);
				subTask.setNumChunks(numChunks);
				subTasks.add(subTask);
			}
			invokeAll(subTasks);
			for (NpvTask subTask : subTasks) {
				collector.combine(subTask.join());
			}
		}
		return collector;
	}

	private double[] sampleFlows() {
		double[] sample = new double[flows.length];
		for (int i = 0; i < flows.length; i++) {
			sample[i] = flows[i].sample();
		}
		return sample;
	}

	private void calculateMinMax() {
		double[] minFlows = new double[flows.length];
		double[] maxFlows = new double[flows.length];
		for (int i = 0; i < flows.length; i++) {
			minFlows[i] = flows[i].getMin();
			maxFlows[i] = flows[i].getMax();
		}
		this.min = NetPresentValue.npv(minFlows, rate.getMax());
		this.max = NetPresentValue.npv(maxFlows, rate.getMin());
	}

}
