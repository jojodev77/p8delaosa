package gpsUtils.gpsUtils.controller;

import gpsUtils.gpsUtils.service.GpsUtilsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GpsUtilController {

  @Autowired
  GpsUtilsService gpsUtilsService;

    @GetMapping("/test")
    public String testController () {
     return gpsUtilsService.test();
    }
}
