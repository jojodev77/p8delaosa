package RewardsCentral.RewardsCentral.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RewardController {
  @GetMapping("/testReward")
  public String testController () {
    return "test reward controller";
  }
}
