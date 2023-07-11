package tourGuide;

import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tourGuide.dto.nearbyattractions.NearByAttractionsDTO;
import tourGuide.dto.userpreferences.UserPreferencesDTO;
import tourGuide.service.TourGuideService;
import tourGuide.user.User;
import tourGuide.user.UserPreferences;
import tourGuide.user.UserReward;
import tripPricer.Provider;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
public class TourGuideController {

	@Autowired
	TourGuideService tourGuideService;
	
    @RequestMapping("/")
    public String index() {
        return "Greetings from TourGuide!";
    }
    
    @RequestMapping("/getLocation") 
    public VisitedLocation getLocation(@RequestParam String userName) {
        return tourGuideService.getUserLocation(getUser(userName));
    }

    @RequestMapping("/getNearbyAttractions") 
    public NearByAttractionsDTO getNearbyAttractions(@RequestParam String userName) {
    	return tourGuideService.getNearByAttractions(getUser(userName));
    }
    
    @RequestMapping("/getRewards") 
    public List<UserReward> getRewards(@RequestParam String userName) {
    	return tourGuideService.getUserRewards(getUser(userName));
    }

    @RequestMapping("/getAllCurrentLocations")
    public Map<UUID, Location> getAllCurrentLocations() {
        return tourGuideService.getAllUsersLocation();
    }
    
    @RequestMapping("/getTripDeals")
    public List<Provider> getTripDeals(@RequestParam String userName) {
        return tourGuideService.getTripDeals(getUser(userName));
    }
    
    private User getUser(String userName) {
    	return tourGuideService.getUser(userName);
    }

    @PostMapping("/setUserPreferences")
    public ResponseEntity<UserPreferences> updateUserPreferences(@RequestBody @Validated UserPreferencesDTO userPreferencesDTO ,
                                                                 @RequestParam String userName ) {
        tourGuideService.setUserPreferences(userPreferencesDTO, getUser(userName));
        return new ResponseEntity<>(HttpStatus.OK);
    }
}