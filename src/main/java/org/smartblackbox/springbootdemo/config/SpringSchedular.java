package org.smartblackbox.springbootdemo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 
 * @author Copyright (C) 2024  Duy Quoc Nguyen <d.q.nguyen@smartblackbox.org>
 *
 */
@Configuration
@EnableScheduling
@EnableAsync
public class SpringSchedular {

	/**
	 * Runs repeatedly with a fixed delay, but only run again if all is completed.
	 * 
	 * Uncomment @Scheduled() to turn it on.
	 */
	// @Scheduled(fixedDelay = 1000)
	public void scheduleFixedDelayTask() {
	    System.out.println("Fixed delay task - " + System.currentTimeMillis() / 1000);
	}
	
	/**
	 * Runs repeatedly with a fixed and initial delay, but only run again if all is completed.
	 * 
	 * Uncomment @Scheduled() to turn it on.
	 */
	// @Scheduled(fixedDelay = 1000, initialDelay = 1000)
	public void scheduleFixedRateWithInitialDelayTask() {
	 
	    long now = System.currentTimeMillis() / 1000;
	    System.out.println(
	      "Fixed rate task with one second initial delay - " + now);
	}
	
	/**
	 * Runs repeatedly, but only run again if all is completed.
	 * This method is called at the exact rate of the given rate.
	 * But the execution must finish within this given rate,
	 * otherwise it will end up in out of memory exception.
	 * 
	 * Uncomment @Scheduled() to turn it on.
	 */
	// @Scheduled(fixedRate = 1000)
	public void scheduleFixedRateTask() {
	    System.out.println("Fixed rate task - " + System.currentTimeMillis() / 1000);
	}

	
	/**
	 * Runs repeatedly asynchronous.
	 * This method is called at the exact rate of the given rate.
	 * It doesn't care if the execution is finished or not.
	 * 
	 * Uncomment @Scheduled() to turn it on.
	 */
    @Async
    // @Scheduled(fixedRate = 1000)
    public void scheduleFixedRateTaskAsync() throws InterruptedException {
        System.out.println(
          "Fixed rate task async - " + System.currentTimeMillis() / 1000);
        Thread.sleep(2000);
    }
	
	/**
	 * Runs a cron task.
	 * 
	 * Uncomment @Scheduled() to turn it on.
	 */
	// @Scheduled(cron = "0 15 10 15 * ?")
	public void scheduleTaskUsingCronExpression() {
	 
	    long now = System.currentTimeMillis() / 1000;
	    System.out.println(
	      "schedule tasks using cron jobs - " + now);
	}

}
