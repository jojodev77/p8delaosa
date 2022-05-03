package RewardsCentral.RewardsCentral;

import RewardsCentral.RewardsCentral.entity.User;
import RewardsCentral.RewardsCentral.helper.InternalTestHelper;
import RewardsCentral.RewardsCentral.Tracker.Tracker;
import RewardsCentral.RewardsCentral.service.RewardCentralService;
import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.apache.commons.lang.time.StopWatch;
import org.junit.jupiter.api.Test;
import rewardCentral.RewardCentral;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestPerformance {

  @Ignore
  @Test
  public void highVolumeGetRewards() {
    GpsUtil gpsUtil = new GpsUtil();
    RewardCentralService rewardsService = new RewardCentralService(gpsUtil, new RewardCentral());

    // Users should be incremented up to 100,000, and test finishes within 20 minutes
    InternalTestHelper.setInternalUserNumber(100000);
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();


    Attraction attraction = gpsUtil.getAttractions().get(0);
    List<User> allUsers = new ArrayList<>();
    allUsers = rewardsService.getAllUsers();
    allUsers.forEach(u -> u.addToVisitedLocations(new VisitedLocation(u.getUserId(), attraction, new Date())));

    allUsers.forEach(u -> rewardsService.calculateRewards(u));

    for(User user : allUsers) {
      assertTrue(user.getUserRewards().size() > 0);
    }
    stopWatch.stop();
    rewardsService.tracker.stopTracking();

    System.out.println("highVolumeGetRewards: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");
    assertTrue(TimeUnit.MINUTES.toSeconds(20) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
  }
}
