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
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestPerformance {

  @BeforeAll()
  public static void Setup() {
    Locale.setDefault(new Locale("us"));
  }
  /*
   * A note on performance improvements:
   *
   *     The number of users generated for the high volume tests can be easily adjusted via this method:
   *
   *     		InternalTestHelper.setInternalUserNumber(100000);
   *
   *
   *     These tests can be modified to suit new solutions, just as long as the performance metrics
   *     at the end of the tests remains consistent.
   *
   *     These are performance metrics that we are trying to hit:
   *
   *     highVolumeTrackLocation: 100,000 users within 15 minutes:
   *     		assertTrue(TimeUnit.MINUTES.toSeconds(15) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
   *
   *     highVolumeGetRewards: 100,000 users within 20 minutes:
   *          assertTrue(TimeUnit.MINUTES.toSeconds(20) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
   */

  @Ignore
  @Test
  public void highVolumeTrackLocation() {
    GpsUtil gpsUtil = new GpsUtil();
    // Users should be incremented up to 100,000, and test finishes within 15 minutes
    InternalTestHelper internalTestHelper = new InternalTestHelper();
    internalTestHelper.setInternalUserNumber(100);
    GpsUtilsService tourGuideService = new GpsUtilsService(gpsUtil);

    List<User> allUsers = new ArrayList<>();
    allUsers = tourGuideService.getAllUsers();

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
