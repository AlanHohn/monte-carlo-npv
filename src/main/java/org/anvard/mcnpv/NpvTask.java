package org.anvard.mcnpv;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveTask;

public class NpvTask extends RecursiveTask<StatsCollector> {

	private static final long serialVersionUID = -3375054929832400598L;

	private static final int MIN_CHUNK_SIZE = 1000;
	private static final int NUM_CHUNKS = 5;

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

	@Override
	protected StatsCollector compute() {
		StatsCollector collector = new StatsCollector(min, max, numBuckets);
		if (numIterations < MIN_CHUNK_SIZE) {
			for (int i = 0; i < numIterations; i++) {
				collector.addObs(NetPresentValue.npv(sampleFlows(),
						rate.sample()));
			}
		} else {
			List<NpvTask> subTasks = new ArrayList<>(NUM_CHUNKS);
			for (int i = 0; i < NUM_CHUNKS; i++) {
				NpvTask subTask = new NpvTask(min, max, numBuckets,
						numIterations / NUM_CHUNKS, rate, flows);
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
		System.out.println("Min: " + min + " Max: " + max);
	}

}
