package tourGuide.dto.nearbyattractions;

import gpsUtil.location.Attraction;

public class AttractionDTO {
    String attractionName;
    double attractionLongitude;
    double attractionLatitude;
    double distanceBetweenUserAndAttraction;
    int rewardPoints;

    public AttractionDTO(Attraction attraction, double distanceBetweenUserAndAttraction, int rewardPoints){
        this.attractionName = attraction.attractionName;
        this.attractionLongitude = attraction.longitude;
        this.attractionLatitude = attraction.latitude;
        this.distanceBetweenUserAndAttraction = distanceBetweenUserAndAttraction;
        this.rewardPoints = rewardPoints;
    }
}
