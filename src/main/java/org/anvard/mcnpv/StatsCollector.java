package org.anvard.mcnpv;

import org.springframework.util.Assert;

public class StatsCollector {

	private double min;
	private double range;
	private double mean;
	private int numObs;
	private int numBuckets;
	private int[] buckets;
	
	public StatsCollector(double min, double max, int numBuckets) {
		Assert.isTrue(numBuckets > 0);
		this.min = min;
		this.range = max - min;
		this.numBuckets = numBuckets;
		this.mean = 0;
		this.numObs = 0;
		this.buckets = new int[numBuckets];
	}
	
	public int[] getBuckets() {
		return buckets;
	}
	
	public void addObs(double obs) {
		mean = (obs + (numObs * mean)) / (numObs+1);
		numObs++;
		int bucket = (int) Math.floor(numBuckets * (obs - min)/range);
		buckets[bucket]++;
	}
	
	public void combine(StatsCollector collector) {
		Assert.notNull(collector);
		Assert.isTrue(Math.abs(min - collector.min) < 0.000001);
		Assert.isTrue(Math.abs(range - collector.range) < 0.000001);
		Assert.isTrue(numBuckets == collector.numBuckets);
		mean = ((numObs * mean) + (collector.numObs * collector.mean)) / numObs + collector.numObs;
		numObs += collector.numObs;
		for (int i = 0; i < numBuckets; i++) {
			buckets[i] += collector.buckets[i];
		}
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Collected Statistics");
		sb.append(System.lineSeparator());
		sb.append("--------------------");
		sb.append(System.lineSeparator());
		sb.append(System.lineSeparator());
		sb.append(String.format("Mean: %2f", mean));
		sb.append(System.lineSeparator());
		sb.append(String.format("Number of observations: %d", numObs));
		sb.append(System.lineSeparator());
		sb.append("Histogram");
		sb.append(System.lineSeparator());
		for (int i = 0; i < numBuckets; i++) {
			sb.append(String.format(" %3d    %d", i+1, buckets[i]));
			sb.append(System.lineSeparator());
		}
		sb.append(System.lineSeparator());
		return sb.toString();
	}
	
}
