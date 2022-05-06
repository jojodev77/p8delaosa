package gpsUtils.gpsUtils.controller;

import com.jsoniter.output.JsonStream;
import gpsUtil.location.VisitedLocation;
import gpsUtils.gpsUtils.entity.User;
import gpsUtils.gpsUtils.service.GpsUtilsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.stream.Collectors;

@RestController
public class GpsUtilController {

  @Autowired
  GpsUtilsService gpsUtilsService;

    @GetMapping("/test")
    public String testController () {
     return gpsUtilsService.test();
    }

  @PostMapping("/getLocation")
  public String getLocation(@RequestParam String userName) {
    VisitedLocation visitedLocation = gpsUtilsService.getUserLocation(getUser(userName));
    return JsonStream.serialize(visitedLocation.location);
  }

  @PostMapping("/getNearbyAttractions")
  public String getNearbyAttractions(@RequestParam String userName) {
    VisitedLocation visitedLocation = gpsUtilsService.getUserLocation(getUser(userName));
    return JsonStream.serialize(gpsUtilsService.getNearByAttractions(visitedLocation));
  }

  @RequestMapping("/getAllCurrentLocations")
  public String getAllCurrentLocations(@RequestParam String userName) {
  User user = gpsUtilsService.getUser(userName);
  if (user.getLastVisitedLocation() == null) {
    return JsonStream.serialize("Not history for this user");
  }
    return JsonStream.serialize(user.getLastVisitedLocation());
  }

  private User getUser(String userName) {
    return gpsUtilsService.getUser(userName);
  }
}


