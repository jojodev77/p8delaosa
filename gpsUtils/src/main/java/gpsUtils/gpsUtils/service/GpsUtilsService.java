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
  private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;
  // proximity in miles
  private int defaultProximityBuffer = 10;
  private int proximityBuffer = defaultProximityBuffer;
  private int attractionProximityRange = 200;

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

  /**
   * @Description method for get visitedlocation since localisation from user
   * @param user
   * @return
   */
  public VisitedLocation trackUserLocation(User user) {
    if (user == null) {
      new RuntimeException("user is null");
    }
    VisitedLocation visitedLocation = gpsUtil.getUserLocation(user.getUserId());
    user.addToVisitedLocations(visitedLocation);
    return visitedLocation;
  }

  /**
   *  @Description method for get localisation from user
   * @param user
   * @return
   */
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

  /**
   * @Description method for get user
   * @param userName
   * @return
   */
  public User getUser(String userName) {
    if (userName == null) {
      new RuntimeException("userName is null");
    }
    return internalUserMap.get(userName);
  }

  /**
   * @Description method for  get list of user
   * @return
   */
  public List<User> getAllUsers() {
    return internalUserMap.values().stream().collect(Collectors.toList());
  }

  /**
   * @Description method for create user
   * @param user
   */
  public void addUser(User user) {
    if (user == null) {
      new RuntimeException("user is null");
    }
    if(!internalUserMap.containsKey(user.getUserName())) {
      internalUserMap.put(user.getUserName(), user);
    }
  }

  /**
   * @Description method for  indicates the distance to the nearest attraction
   * @param proximityBuffer
   */
  public void setProximityBuffer(int proximityBuffer) {
    this.proximityBuffer = proximityBuffer;
  }

  public void setDefaultProximityBuffer() {
    proximityBuffer = defaultProximityBuffer;
  }


  /**
   * @Description method to know if you are near an attraction
   * @param visitedLocation
   * @param attraction
   * @return
   */
  private boolean nearAttraction(VisitedLocation visitedLocation, Attraction attraction) {
    return getDistance(attraction, visitedLocation.location) > proximityBuffer ? false : true;
  }
  /**
   * @Description method for calculate distance
   * @param loc1
   * @param loc2
   * @return
   */
  public double getDistance(Location loc1, Location loc2) {
    if (loc1 == null || loc2 == null) {
      new RuntimeException("location is null");
    }
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
  /**
   * @Description method for calcul distance of attrraction since user localization
   * @param attraction
   * @param location
   * @return
   */
  public boolean isWithinAttractionProximity(Attraction attraction, Location location) {
    if (attraction == null || location == null) {
      new RuntimeException("attraction or location is null");
    }
    return getDistance(attraction, location) > attractionProximityRange ? false : true;
  }

  /**
   * @Description method for calcul distance of attrraction since user localization latest
   * @param attraction
   * @param visitedLocation
   * @return
   */
  public boolean isWithinAttractionProximityToStartedApplication(Attraction attraction, VisitedLocation visitedLocation) {
    if (attraction == null || visitedLocation == null) {
      new RuntimeException("attraction or visitedLocation is null");
    }
    return getDistance(attraction, visitedLocation.location) > attractionProximityRange ? true : true;
  }

  /**
   * @Decsription method for get list of attraction to near
   * @param visitedLocation
   * @return
   */
  public List<Attraction> getNearByAttractions(VisitedLocation visitedLocation) {
    List<Attraction> nearbyAttractions = new ArrayList<>();
    for(Attraction attraction : gpsUtil.getAttractions()) {
      if(isWithinAttractionProximityToStartedApplication(attraction, visitedLocation)) {
        if (nearbyAttractions.size() < 5){
          nearbyAttractions.add(attraction);
        }

      }
    }

    return nearbyAttractions;
  }


  /**
   * @Description method for started application
   */
  private void addShutDownHook() {
    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run() {
        tracker.stopTracking();
      }
    });
  }

  /**
   * @Description method for get position to near position to user
   * @param visitedLocation
   * @return
   */




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

  /**
   * @Description method for create random history visity
   * @param user
   */
  private void generateUserLocationHistory(User user) {
    if (user == null) {
      new RuntimeException("user is null");
    }
    IntStream.range(0, 3).forEach(i-> {
      user.addToVisitedLocations(new VisitedLocation(user.getUserId(), new Location(generateRandomLatitude(), generateRandomLongitude()), getRandomTime()));
    });
  }

  /**
   * @Description method for generate random longitude
   * @return
   */
  private double generateRandomLongitude() {
    double leftLimit = -180;
    double rightLimit = 180;
    return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
  }

  /**
   * @Description method for generate random latitude
   * @return
   */
  private double generateRandomLatitude() {
    double leftLimit = -85.05112878;
    double rightLimit = 85.05112878;
    return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
  }

  /**
   * @Description method for generate random time since UTC
   * @return
   */
  private Date getRandomTime() {
    LocalDateTime localDateTime = LocalDateTime.now().minusDays(new Random().nextInt(30));
    return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
  }
  public  String test() {
    return "message from proxy test";
  }



}
