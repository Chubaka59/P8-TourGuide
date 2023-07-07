package tourGuide.service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tourGuide.dto.nearbyattractions.AttractionDTO;
import tourGuide.dto.nearbyattractions.NearByAttractionsDTO;
import tourGuide.dto.nearbyattractions.UserPreferencesDTO;
import tourGuide.dto.nearbyattractions.UsersLocationDTO;
import tourGuide.helper.InternalTestHelper;
import tourGuide.tracker.Tracker;
import tourGuide.user.User;
import tourGuide.user.UserReward;
import tripPricer.Provider;
import tripPricer.TripPricer;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class TourGuideService {
	private Logger logger = LoggerFactory.getLogger(TourGuideService.class);
	private final GpsUtil gpsUtil;
	private final RewardsService rewardsService;
	private final TripPricer tripPricer = new TripPricer();
	public final Tracker tracker;
	boolean testMode = true;

	private	ExecutorService executor = Executors.newFixedThreadPool(100);
	
	public TourGuideService(GpsUtil gpsUtil, RewardsService rewardsService) {
		this.gpsUtil = gpsUtil;
		this.rewardsService = rewardsService;
		
		if(testMode) {
			logger.info("TestMode enabled");
			logger.debug("Initializing users");
			initializeInternalUsers();
			logger.debug("Finished initializing users");
		}
		tracker = new Tracker(this);
		addShutDownHook();
	}
	
	public List<UserReward> getUserRewards(User user) {
		return user.getUserRewards();
	}
	
	public VisitedLocation getUserLocation(User user) {
		VisitedLocation visitedLocation = (user.getVisitedLocations().size() > 0) ?
			user.getLastVisitedLocation() :
			trackUserLocation(user);
		return visitedLocation;
	}

	public List<UsersLocationDTO> getAllUsersLocation(){
		List<UsersLocationDTO> usersLocationDTOList = new ArrayList<>();
		List<User> userList = getAllUsers();
		for (User user : userList ) {
			VisitedLocation location = getUserLocation(user);
			usersLocationDTOList.add(new UsersLocationDTO(location, user));
		}
		return usersLocationDTOList;
	}
	
	public User getUser(String userName) {
		return internalUserMap.get(userName);
	}
	
	public List<User> getAllUsers() {
		return new ArrayList<>(internalUserMap.values());
	}
	
	public void addUser(User user) {
		if(!internalUserMap.containsKey(user.getUserName())) {
			internalUserMap.put(user.getUserName(), user);
		}
	}
	
	public List<Provider> getTripDeals(User user) {
		int cumulativeRewardPoints = user.getUserRewards().stream().mapToInt(UserReward::getRewardPoints).sum();
		List<Provider> providers = tripPricer.getPrice(tripPricerApiKey, user.getUserId(), user.getUserPreferences().getNumberOfAdults(), 
				user.getUserPreferences().getNumberOfChildren(), user.getUserPreferences().getTripDuration(), cumulativeRewardPoints);
		user.setTripDeals(providers);
		return providers;
	}

	public User setUserPreferences(UserPreferencesDTO userPreferencesDTO, User user) {
		user.getUserPreferences().update(userPreferencesDTO);
		return user;
	}
	
	public VisitedLocation trackUserLocation(User user) {
		VisitedLocation visitedLocation = gpsUtil.getUserLocation(user.getUserId());

		executor.submit(() -> {
			user.addToVisitedLocations(visitedLocation);
			rewardsService.calculateRewards(user);
		});

		return visitedLocation;
	}

	public NearByAttractionsDTO getNearByAttractions(User user) {

		List<Attraction> attractions = gpsUtil.getAttractions();
		Location location = getUserLocation(user).location;

		List<Attraction> closestAttractions = attractions
				.stream()
				.map(a -> new AttractionDistance(a, location))
				.sorted(Comparator.comparing(o -> o.distance))
				.limit(5)
				.map(a -> a.attraction)
				.collect(Collectors.toList());

		return new NearByAttractionsDTO(getAttractionDTO(closestAttractions, user), location);
	}

	private List<AttractionDTO> getAttractionDTO(List<Attraction> attractionList, User user){
		List<AttractionDTO> attractionDTOList = new ArrayList<>();
		for ( Attraction attraction : attractionList ) {
			double distance = rewardsService.getDistance(attraction, user.getLastVisitedLocation().location);
			int rewardPoints = rewardsService.getRewardPoints(attraction, user);
			attractionDTOList.add(new AttractionDTO(attraction, distance, rewardPoints));
		}
		return attractionDTOList;
	}
	
	private void addShutDownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread() { 
		      public void run() {
		        tracker.stopTracking();
		      } 
		    }); 
	}
	
	/**********************************************************************************
	 * 
	 * Methods Below: For Internal Testing
	 * 
	 **********************************************************************************/
	private static final String tripPricerApiKey = "test-server-api-key";
	// Database connection will be used for external users, but for testing purposes internal users are provided and stored in memory
	private final Map<String, User> internalUserMap = new HashMap<>();
	private void initializeInternalUsers() {
		IntStream.range(0, InternalTestHelper.getInternalUserNumber()).forEach(i -> {
			String userName = "internalUser" + i;
			String phone = "000";
			String email = userName + "@tourGuide.com";
			User user = new User(UUID.randomUUID(), userName, phone, email);
			generateUserLocationHistory(user);
			
			internalUserMap.put(userName, user);
		});
		logger.debug("Created " + InternalTestHelper.getInternalUserNumber() + " internal test users.");
	}
	
	private void generateUserLocationHistory(User user) {
		IntStream.range(0, 3).forEach(i-> {
			user.addToVisitedLocations(new VisitedLocation(user.getUserId(), new Location(generateRandomLatitude(), generateRandomLongitude()), getRandomTime()));
		});
	}
	
	private double generateRandomLongitude() {
		double leftLimit = -180;
	    double rightLimit = 180;
	    return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
	}
	
	private double generateRandomLatitude() {
		double leftLimit = -85.05112878;
	    double rightLimit = 85.05112878;
	    return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
	}
	
	private Date getRandomTime() {
		LocalDateTime localDateTime = LocalDateTime.now().minusDays(new Random().nextInt(30));
	    return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
	}

	public void stopTracking() {
		tracker.stopTracking();
		executor.shutdown();

		while (true){
			try {
				if (executor.awaitTermination(1, TimeUnit.MINUTES)) break;
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}


	}


	class AttractionDistance{
		private Attraction attraction;
		private Double distance;

		AttractionDistance(Attraction attraction, Location location){
			this.attraction = attraction;
			this.distance = rewardsService.getDistance(attraction, location);
		}

	}
}
