package RewardsCentral.RewardsCentral.entity;

import gpsUtil.location.VisitedLocation;
import lombok.Data;
import tripPricer.Provider;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
public class User {
  private final UUID userId;
  private final String userName;
  private String phoneNumber;
  private String emailAddress;
  private Date latestLocationTimestamp;
  private List<UserReward> userRewards = new ArrayList<>();
  private List<VisitedLocation> visitedLocations = new ArrayList<>();
  private UserPreferences userPreferences = new UserPreferences();
  private List<Provider> tripDeals = new ArrayList<>();

  public User(UUID userId, String userName, String phoneNumber, String emailAddress) {
    this.userId = userId;
    this.userName = userName;
    this.phoneNumber = phoneNumber;
    this.emailAddress = emailAddress;
  }

  public VisitedLocation getLastVisitedLocation() {
    return visitedLocations.get(visitedLocations.size() - 1);
  }

  public void addToVisitedLocations(VisitedLocation visitedLocation) {
    visitedLocations.add(visitedLocation);
  }

  public void addUserReward(UserReward userReward) {
    if(userRewards.stream().filter(r -> !r.attraction.attractionName.equals(userReward.attraction)).count() == 0) {
      userRewards.add(userReward);
    }
  }

  public List<UserReward> getUserRewards() {
    return userRewards;
  }


}

