package RewardsCentral.RewardsCentral.entity;

import lombok.Data;

@Data
public class Reward {
  String nameOfAttraction;
  Position attractionDistance;
  Position userDistance;
  int distanceUserAttraction;
  int rewardPoint;
}
