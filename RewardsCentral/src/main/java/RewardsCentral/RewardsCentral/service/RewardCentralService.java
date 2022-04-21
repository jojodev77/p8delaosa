package RewardsCentral.RewardsCentral.service;

import RewardsCentral.RewardsCentral.entity.User;
import RewardsCentral.RewardsCentral.entity.UserReward;
import RewardsCentral.RewardsCentral.helper.InternalTestHelper;
import RewardsCentral.RewardsCentral.Tracker.Tracker;
import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import rewardCentral.RewardCentral;
import tripPricer.TripPricer;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class RewardCentralService {
  private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;

  private Logger logger = LoggerFactory.getLogger(RewardCentralService.class);
  // proximity in miles
  private int defaultProximityBuffer = 10;
  private int proximityBuffer = defaultProximityBuffer;
  private int attractionProximityRange = 200;
  private final GpsUtil gpsUtil;
  private final RewardCentral rewardsCentral;
  public final Tracker tracker = new Tracker();
  private final TripPricer tripPricer = new TripPricer();
  boolean testMode = true;
  InternalTestHelper internalTestHelper = new InternalTestHelper();

  public RewardCentralService(GpsUtil gpsUtil, RewardCentral rewardCentral) {
    this.gpsUtil = gpsUtil;
    this.rewardsCentral = rewardCentral;


    if(testMode) {
      logger.info("TestMode enabled");
      logger.debug("Initializing users");
      initializeInternalUsers();
      logger.debug("Finished initializing users");
    }
    addShutDownHook();
  }

  public VisitedLocation trackUserLocation(User user) {
    if (user == null) {
      new RuntimeException("user is null");
    }
    VisitedLocation visitedLocation = gpsUtil.getUserLocation(user.getUserId());
    user.addToVisitedLocations(visitedLocation);
    calculateRewards(user);
    return visitedLocation;
  }

  public List<User> getAllUsers() {
    return internalUserMap.values().stream().collect(Collectors.toList());
  }

  public void setProximityBuffer(int proximityBuffer) {
    this.proximityBuffer = proximityBuffer;
  }

  public void setDefaultProximityBuffer() {
    proximityBuffer = defaultProximityBuffer;
  }

  public void calculateRewards(User user) {
    List<VisitedLocation> userLocations = user.getVisitedLocations();
    List<Attraction> attractions = gpsUtil.getAttractions();

    for(VisitedLocation visitedLocation : userLocations) {
      for(Attraction attraction : attractions) {
        if(user.getUserRewards().stream().filter(r -> r.attraction.attractionName.equals(attraction.attractionName)).count() == 0) {
          if(nearAttraction(visitedLocation, attraction)) {
            user.addUserReward(new UserReward(visitedLocation, attraction, getRewardPoints(attraction, user)));
          }
        }
      }
    }
  }

  public boolean isWithinAttractionProximity(Attraction attraction, Location location) {
    return getDistance(attraction, location) > attractionProximityRange ? false : true;
  }

  private boolean nearAttraction(VisitedLocation visitedLocation, Attraction attraction) {
    return getDistance(attraction, visitedLocation.location) > proximityBuffer ? false : true;
  }

  private int getRewardPoints(Attraction attraction, User user) {
    return rewardsCentral.getAttractionRewardPoints(attraction.attractionId, user.getUserId());
  }

  public double getDistance(Location loc1, Location loc2) {
    double lat1 = Math.toRadians(loc1.latitude);
    double lon1 = Math.toRadians(loc1.longitude);
    double lat2 = Math.toRadians(loc2.latitude);
    double lon2 = Math.toRadians(loc2.longitude);

    double angle = Math.acos(Math.sin(lat1) * Math.sin(lat2)
      + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));

    double nauticalMiles = 60 * Math.toDegrees(angle);
    double statuteMiles = STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMiles;
    return statuteMiles;
  }

  private void addShutDownHook() {
    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run() {
        tracker.stopTracking();
      }
    });
  }

  public List<Attraction> getNearByAttractions(VisitedLocation visitedLocation) {
    List<Attraction> nearbyAttractions = new ArrayList<>();
    for(Attraction attraction : gpsUtil.getAttractions()) {
      //  if(rewardsService.isWithinAttractionProximity(attraction, visitedLocation.location)) {
      //    nearbyAttractions.add(attraction);
      //  }
    }

    return nearbyAttractions;
  }

  /**********************************************************************************
   *
   * Methods Below: For Internal Testing
   *
   **********************************************************************************/
  private static final String tripPricerApiKey = "test-server-api-key";
  // Database connection will be used for external users, but for testing purposes internal users are provided and stored in memory
  private final Map<String, User> internalUserMap = new HashMap<>();
  private void initializeInternalUsers() {
    IntStream.range(0, internalTestHelper.getInternalUserNumber()).forEach(i -> {
      String userName = "internalUser" + i;
      String phone = "000";
      String email = userName + "@tourGuide.com";
      User user = new User(UUID.randomUUID(), userName, phone, email);
      generateUserLocationHistory(user);

      internalUserMap.put(userName, user);
    });
    logger.debug("Created " + internalTestHelper.getInternalUserNumber() + " internal test users.");
  }

  private void generateUserLocationHistory(User user) {
    if (user == null) {
      new RuntimeException("user is null");
    }
    IntStream.range(0, 3).forEach(i-> {
      user.addToVisitedLocations(new VisitedLocation(user.getUserId(), new Location(generateRandomLatitude(), generateRandomLongitude()), getRandomTime()));
    });
  }

  private double generateRandomLongitude() {
    double leftLimit = -180;
    double rightLimit = 180;
    return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
  }

  private double generateRandomLatitude() {
    double leftLimit = -85.05112878;
    double rightLimit = 85.05112878;
    return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
  }

  private Date getRandomTime() {
    LocalDateTime localDateTime = LocalDateTime.now().minusDays(new Random().nextInt(30));
    return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
  }
  public  String test() {
    return "message from proxy test";
  }



}
