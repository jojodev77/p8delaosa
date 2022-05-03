package RewardsCentral.RewardsCentral;

import RewardsCentral.RewardsCentral.entity.User;
import RewardsCentral.RewardsCentral.entity.UserReward;
import RewardsCentral.RewardsCentral.helper.InternalTestHelper;
import RewardsCentral.RewardsCentral.service.RewardCentralService;
import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import rewardCentral.RewardCentral;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RewardServiceTest {
  InternalTestHelper internalTestHelper = new InternalTestHelper();
  @BeforeAll()
  public static void Setup() {
    Locale.setDefault(new Locale("us"));
  }
  @Test
  public void userGetRewards() {


    GpsUtil gpsUtil = new GpsUtil();
    RewardCentralService rewardsService = new RewardCentralService(gpsUtil, new RewardCentral());

    InternalTestHelper.setInternalUserNumber(0);

    User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
    Attraction attraction = gpsUtil.getAttractions().get(0);
    user.addToVisitedLocations(new VisitedLocation(user.getUserId(), attraction, new Date()));
    rewardsService.trackUserLocation(user);
    List<UserReward> userRewards = user.getUserRewards();
    rewardsService.tracker.stopTracking();
    assertTrue(userRewards.size() == 1);
  }

  @Test
  public void isWithinAttractionProximity() {
    GpsUtil gpsUtil = new GpsUtil();
    RewardCentralService rewardsService = new RewardCentralService(gpsUtil, new RewardCentral());
    Attraction attraction = gpsUtil.getAttractions().get(0);
    assertTrue(rewardsService.isWithinAttractionProximity(attraction, attraction));
  }

 // @Ignore // Needs fixed - can throw ConcurrentModificationException
  @Test
  public void nearAllAttractions() {
    GpsUtil gpsUtil = new GpsUtil();
    RewardCentralService rewardsService = new RewardCentralService(gpsUtil, new RewardCentral());
    rewardsService.setProximityBuffer(Integer.MAX_VALUE);

    InternalTestHelper.setInternalUserNumber(1);

    rewardsService.calculateRewards(rewardsService.getAllUsers().get(0));
    List<UserReward> userRewards = rewardsService.getUserRewards(rewardsService.getAllUsers().get(0));
    rewardsService.tracker.stopTracking();

    assertEquals(gpsUtil.getAttractions().size(), userRewards.size());
  }

  @Ignore // Not yet implemented


  @Test
  public void getPriceTest() {
    GpsUtil gpsUtil = new GpsUtil();
    RewardCentralService rewardsService = new RewardCentralService(gpsUtil, new RewardCentral());
    rewardsService.setProximityBuffer(Integer.MAX_VALUE);
    Attraction attraction = gpsUtil.getAttractions().get(0);
    InternalTestHelper.setInternalUserNumber(1);

    rewardsService.getPrice(rewardsService.getAllUsers().get(0), attraction);
    assertTrue(rewardsService.getPrice(rewardsService.getAllUsers().get(0), attraction).size() > 1);
  }

}
