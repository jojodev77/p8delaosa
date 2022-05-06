package gpsUtils.gpsUtils;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import gpsUtils.gpsUtils.entity.AttractionParameters;
import gpsUtils.gpsUtils.entity.User;
import gpsUtils.gpsUtils.helper.InternalTestHelper;
import gpsUtils.gpsUtils.service.GpsUtilsService;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tripPricer.Provider;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class testGpsUtilsService {
  InternalTestHelper internalTestHelper = new InternalTestHelper();

  @BeforeAll()
    public static void Setup() {
    Locale.setDefault(new Locale("us"));
    }
  @Test
  public void getUserLocation() {

    GpsUtil gpsUtil = new GpsUtil();
    internalTestHelper.setInternalUserNumber(0);
    GpsUtilsService tourGuideService = new GpsUtilsService(gpsUtil);
    User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

    VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);
    tourGuideService.tracker.stopTracking();
    assertTrue(visitedLocation.userId.equals(user.getUserId()));
  }

  @Test
  public void addUser() {
    GpsUtil gpsUtil = new GpsUtil();
    internalTestHelper.setInternalUserNumber(0);
    GpsUtilsService tourGuideService = new GpsUtilsService(gpsUtil);

    User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
    User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

    tourGuideService.addUser(user);
    tourGuideService.addUser(user2);

    User retrivedUser = tourGuideService.getUser(user.getUserName());
    User retrivedUser2 = tourGuideService.getUser(user2.getUserName());

    tourGuideService.tracker.stopTracking();

    assertEquals(user, retrivedUser);
    assertEquals(user2, retrivedUser2);
  }

  @Test
  public void getAllUsers() {
    GpsUtil gpsUtil = new GpsUtil();
    internalTestHelper.setInternalUserNumber(0);
    GpsUtilsService tourGuideService = new GpsUtilsService(gpsUtil);

    User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
    User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

    tourGuideService.addUser(user);
    tourGuideService.addUser(user2);

    List<User> allUsers = (List<User>) tourGuideService.getAllUsers();

    tourGuideService.tracker.stopTracking();

    assertTrue(allUsers.contains(user));
    assertTrue(allUsers.contains(user2));
  }

  @Test
  public void trackUser() {
    GpsUtil gpsUtil = new GpsUtil();
    internalTestHelper.setInternalUserNumber(0);
    GpsUtilsService tourGuideService = new GpsUtilsService(gpsUtil);

    User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
    VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);

    tourGuideService.tracker.stopTracking();

    assertEquals(user.getUserId(), visitedLocation.userId);
  }

  @Test
  public void getNearbyAttractions() {
    GpsUtil gpsUtil = new GpsUtil();
    GpsUtilsService tourGuideService = new GpsUtilsService(gpsUtil);
    InternalTestHelper.setInternalUserNumber(0);

    User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
    VisitedLocation visitedLocation = tourGuideService.getUserLocation(user);


    List<AttractionParameters> attractions = tourGuideService.getNearByAttractions(visitedLocation);

    tourGuideService.tracker.stopTracking();

    assertEquals(5, attractions.size());
  }

  @Test
  public void gestLastVisitedLocation() {
    GpsUtil gpsUtil = new GpsUtil();
    GpsUtilsService tourGuideService = new GpsUtilsService(gpsUtil);
    InternalTestHelper.setInternalUserNumber(0);

    User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
    VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);

    tourGuideService.tracker.stopTracking();
    assertTrue(user.getLastVisitedLocation().location != null);
  }


}
