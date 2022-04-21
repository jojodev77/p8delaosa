package gpsUtils.gpsUtils.service;


import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import gpsUtils.gpsUtils.Tracker.Tracker;
import gpsUtils.gpsUtils.entity.User;
import gpsUtils.gpsUtils.helper.InternalTestHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import tripPricer.TripPricer;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@Service
public class GpsUtilsService {

  private Logger logger = LoggerFactory.getLogger(GpsUtilsService.class);
  private final GpsUtil gpsUtil;
  private final TripPricer tripPricer = new TripPricer();
  public final Tracker tracker = new Tracker();
  boolean testMode = true;
  InternalTestHelper internalTestHelper = new InternalTestHelper();

  public GpsUtilsService(GpsUtil gpsUtil) {
    this.gpsUtil = gpsUtil;
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
    System.out.println("---------------------------------" +  gpsUtil.getUserLocation(user.getUserId()));
    VisitedLocation visitedLocation = gpsUtil.getUserLocation(user.getUserId());
    user.addToVisitedLocations(visitedLocation);
  //  rewardsService.calculateRewards(user);
    return visitedLocation;
  }

  public VisitedLocation getUserLocation(User user) {
    if (user == null) {
      new RuntimeException("User is null");
    }
    try {
      VisitedLocation visitedLocation = (user.getVisitedLocations().size() > 0) ?
        user.getLastVisitedLocation() :
        trackUserLocation(user);
      logger.debug("visitedLocation");
      return visitedLocation;
    } finally {
    logger.debug("visitedLocation");
    }
  }

  public User getUser(String userName) {
    if (userName == null) {
      new RuntimeException("userName is null");
    }
    return internalUserMap.get(userName);
  }

  public List<User> getAllUsers() {
    return internalUserMap.values().stream().collect(Collectors.toList());
  }

  public void addUser(User user) {
    if (user == null) {
      new RuntimeException("user is null");
    }
    if(!internalUserMap.containsKey(user.getUserName())) {
      internalUserMap.put(user.getUserName(), user);
    }
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
