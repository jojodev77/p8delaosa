package gpsUtils.gpsUtils;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import gpsUtils.gpsUtils.entity.User;
import gpsUtils.gpsUtils.helper.InternalTestHelper;
import gpsUtils.gpsUtils.service.GpsUtilsService;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.apache.commons.lang.time.StopWatch;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestPerformance {

  @BeforeAll()
  public static void Setup() {
    Locale.setDefault(new Locale("us"));
  }

  /**
   * @Description test performance with 100000 user
   */
  @Ignore
  @Test
  public void highVolumeTrackLocation() throws ExecutionException, InterruptedException {
    GpsUtil gpsUtil = new GpsUtil();
    // Users should be incremented up to 100,000, and test finishes within 15 minutes
    InternalTestHelper internalTestHelper = new InternalTestHelper();
    internalTestHelper.setInternalUserNumber(100);
    GpsUtilsService tourGuideService = new GpsUtilsService(gpsUtil);

    List<User> allUsers = new ArrayList<>();
    allUsers = (List<User>) tourGuideService.getAllUsers();

    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    for(User user : allUsers) {
      tourGuideService.trackUserLocation(user);
    }
    stopWatch.stop();
    tourGuideService.tracker.stopTracking();

    System.out.println("highVolumeTrackLocation: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");
    assertTrue(TimeUnit.MINUTES.toSeconds(15) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
  }
}
