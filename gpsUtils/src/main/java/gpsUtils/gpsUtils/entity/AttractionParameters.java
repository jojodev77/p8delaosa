package gpsUtils.gpsUtils.entity;

import gpsUtil.location.Location;
import lombok.Data;

@Data
public class AttractionParameters {
  String nameOfAttraction;
  Location attractionPosition;
  Location userPosition;
  int distanceUserAttraction;
  int rewardPoint;
}
