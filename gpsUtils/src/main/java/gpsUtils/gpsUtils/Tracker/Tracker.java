package gpsUtils.gpsUtils.Tracker;

import gpsUtils.gpsUtils.entity.User;
import gpsUtils.gpsUtils.service.GpsUtilsService;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class Tracker extends Thread {
  private Logger logger = LoggerFactory.getLogger(Tracker.class);
  private static final long trackingPollingInterval = TimeUnit.MINUTES.toSeconds(5);
  private final ExecutorService executorService = Executors.newSingleThreadExecutor();
  private boolean stop = false;

  @Autowired
  GpsUtilsService gpsUtilsService;

  public Tracker() {

    executorService.submit(this);
  }

  /**
   * Assures to shut down the Tracker thread
   */
  public void stopTracking() {
    stop = true;
    executorService.shutdownNow();
  }

  @Override
  public void run() {
    StopWatch stopWatch = new StopWatch();
    while(true) {
      if(Thread.currentThread().isInterrupted() || stop) {
        logger.debug("Tracker stopping");
        break;
      }

      List<User> users = gpsUtilsService.getAllUsers();
      logger.debug("Begin Tracker. Tracking " + users.size() + " users.");
      stopWatch.start();
      users.forEach(u -> gpsUtilsService.trackUserLocation(u));
      stopWatch.stop();
      logger.debug("Tracker Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");
      stopWatch.reset();
      try {
        logger.debug("Tracker sleeping");
        TimeUnit.SECONDS.sleep(trackingPollingInterval);
      } catch (InterruptedException e) {
        break;
      }
    }

  }
}

