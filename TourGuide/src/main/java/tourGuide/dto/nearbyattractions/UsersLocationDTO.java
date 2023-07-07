package tourGuide.dto.nearbyattractions;

import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import tourGuide.user.User;

public class UsersLocationDTO {
    String userId;
    Location userLocation;

    public UsersLocationDTO(VisitedLocation visitedLocation, User user){
        this.userId = user.getUserId().toString();
        this.userLocation = visitedLocation.location;
    }
}