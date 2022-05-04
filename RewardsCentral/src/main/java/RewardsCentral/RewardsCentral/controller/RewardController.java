package RewardsCentral.RewardsCentral.controller;

import RewardsCentral.RewardsCentral.entity.User;
import RewardsCentral.RewardsCentral.service.RewardCentralService;
import com.jsoniter.output.JsonStream;
import gpsUtil.location.VisitedLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RewardController {

  @Autowired
  RewardCentralService rewardService;

  @GetMapping("/testReward")
  public String testController () {
    return "test reward controller";
  }

  @RequestMapping("/getRewards")
  public String getRewards(@RequestParam String userName) {
    return JsonStream.serialize(rewardService.getUserRewards(getUser(userName)));
  }

  @RequestMapping("/getNearbyAttractions")
  public String getNearbyAttractions(@RequestParam String userName) {
    VisitedLocation visitedLocation = rewardService.getUserLocation(getUser(userName));
    return JsonStream.serialize(rewardService.getNearByAttractions(visitedLocation));
  }

  private User getUser(String userName) {
    return rewardService.getUser(userName);
  }
}
