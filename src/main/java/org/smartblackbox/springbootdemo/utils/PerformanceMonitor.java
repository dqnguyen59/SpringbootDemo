package org.smartblackbox.springbootdemo.utils;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Copyright (C) 2024  Duy Quoc Nguyen <d.q.nguyen@smartblackbox.org>
 *
 */
@Slf4j
public class PerformanceMonitor {

	// One million nanoseconds is equal to one millisecond.
	private static final double ONE_MILLION_NANO_SECONDS = 1000000.0;

	private String key;

	private long startTime;

	private double duration;

	/**
	 * Do instantiate this directly, but call PwerformanceMonitor.start() instead!
	 * 
	 */
	private PerformanceMonitor() {
	}
	
	/**
	 * Starts the measurement and returns the PerformanceMonitor object.
	 * 
	 * @return
	 */
	public static PerformanceMonitor start(String key) {
		log.info("PerformanceMonitor Start: " + key);
		PerformanceMonitor monitor = new PerformanceMonitor();
		monitor.key = key;
		monitor.startTime = System.nanoTime();
		return monitor;
	}

	/**
	 * Stops the measurement and returns the duration of the measurement in milliseconds.
	 * 
	 * @return
	 */
	public double stop() {
		duration = (System.nanoTime() - startTime) / ONE_MILLION_NANO_SECONDS;
		return duration;
	}

	/**
	 * Stops the measurement and log the duration of the measurement in milliseconds.
	 * 
	 * @return
	 */
	public void stopAndLog() {
		double duration = stop();
		String s = String.format("PerformanceMonitor Duration of '%s': %.3f ms", key, duration);
		log.info(s);
	}

	/**
	 * Returns the duration of the measurement in milliseconds.
	 * 
	 * @return
	 */
	public double getDuration() {
		return duration;
	}

}
