package tourGuide.controller;

import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import tourGuide.TourGuideController;
import tourGuide.dto.nearbyattractions.NearByAttractionsDTO;
import tourGuide.dto.userpreferences.UserPreferencesDTO;
import tourGuide.service.TourGuideService;
import tourGuide.user.User;
import tourGuide.user.UserPreferences;
import tourGuide.user.UserReward;
import tripPricer.Provider;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TourGuideControllerTest {
    @InjectMocks
    private TourGuideController tourGuideController;
    @Mock
    private TourGuideService tourGuideService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void indexTest() {
        //GIVEN we should get this string
        String expectedString = "Greetings from TourGuide!";

        //WHEN we call this method
        String actualString = tourGuideController.index();

        //THEN we get the correct string
        assertEquals(expectedString, actualString);
    }

    @Test
    public void getLocationTest() {
        //GIVEN we should get a VisitedLocation
        Location location = new Location(10.0, 10.0);
        Date date = Date.from(LocalDateTime.now().toInstant(ZoneOffset.UTC));
        VisitedLocation expectedVisitedLocation = new VisitedLocation(UUID.randomUUID(), location, date);
        when(tourGuideService.getUserLocation(any(User.class))).thenReturn(expectedVisitedLocation);
        User user = new User(UUID.randomUUID(), "test", "test", "test");
        when(tourGuideService.getUser("test")).thenReturn(user);

        //WHEN we call the method
        VisitedLocation actualVisitedLocation = tourGuideController.getLocation("test");

        //THEN tourGuideService.getUserLocation is called and the visitedLocation is returned
        verify(tourGuideService, times(1)).getUserLocation(any(User.class));
        assertEquals(expectedVisitedLocation, actualVisitedLocation);
    }

    @Test
    public void getNearByAttractionsTest() {
        //GIVEN we should get nearByAttractionDTO as return
        User user = new User(UUID.randomUUID(), "test", "test", "test");
        when(tourGuideService.getUser("test")).thenReturn(user);
        NearByAttractionsDTO expectedNearByAttractionsDTO = new NearByAttractionsDTO();
        when(tourGuideService.getNearByAttractions(any(User.class))).thenReturn(expectedNearByAttractionsDTO);

        //WHEN we call the method
        NearByAttractionsDTO actualNearByAttractionsDTO = tourGuideController.getNearbyAttractions("test");

        //THEN tourGuideService.getNearByAttractions is called and we get the correct return
        verify(tourGuideService, times(1)).getNearByAttractions(any(User.class));
        assertEquals(expectedNearByAttractionsDTO, actualNearByAttractionsDTO);
    }

    @Test
    public void getRewardsTest() {
        //GIVEN we should get a list of Rewards as return
        User user = new User(UUID.randomUUID(), "test", "test", "test");
        when(tourGuideService.getUser("test")).thenReturn(user);
        List<UserReward> expectedUserRewardList = new ArrayList<>();
        Location location = new Location(10.0, 10.0);
        Date date = Date.from(LocalDateTime.now().toInstant(ZoneOffset.UTC));
        VisitedLocation visitedLocation = new VisitedLocation(UUID.randomUUID(), location, date);
        Attraction attraction = new Attraction("test", "test", "test", 10.0, 10.0);
        expectedUserRewardList.add(new UserReward(visitedLocation, attraction));
        when(tourGuideService.getUserRewards(any(User.class))).thenReturn(expectedUserRewardList);

        //WHEN we call the method
        List<UserReward> acutalUserRewardList = tourGuideController.getRewards("test");

        //THEN tourGuideService.getUserRewards is called and we get the correct return
        verify(tourGuideService, times(1)).getUserRewards(any(User.class));
        assertEquals(expectedUserRewardList, acutalUserRewardList);
    }

    @Test
    public void getAllCurrentLocationsTest() {
        //GIVEN we should get a map as return
        Map<UUID, Location> expectedLocationMap = new HashMap<>();
        when(tourGuideService.getAllUsersLocation()).thenReturn(expectedLocationMap);

        //WHEN we call the method
        Map<UUID, Location> actualLocationMap = tourGuideController.getAllCurrentLocations();

        //THEN tourGuideService.getAllUsersLocation is called and we get the correct return
        verify(tourGuideService, times(1)).getAllUsersLocation();
        assertEquals(expectedLocationMap, actualLocationMap);
    }

    @Test
    public void getTripDealsTest() {
        //GIVEN we should get a list as return
        User user = new User(UUID.randomUUID(), "test", "test", "test");
        when(tourGuideService.getUser("test")).thenReturn(user);
        List<Provider> expectedProviderList = new ArrayList<>();
        when(tourGuideService.getTripDeals(any(User.class))).thenReturn(expectedProviderList);

        //WHEN we call the method
        List<Provider> actualProviderList = tourGuideController.getTripDeals("test");

        //THEN tourGuideService.getTripDeals is called and we get the correct return
        verify(tourGuideService, times(1)).getTripDeals(any(User.class));
        assertEquals(expectedProviderList, actualProviderList);
    }

    @Test
    public void updateUserPreferencesTest() {
        //GIVEN we should get a response as return
        User expectedUser = new User(UUID.randomUUID(), "test", "test", "test");
        when(tourGuideService.getUser("test")).thenReturn(expectedUser);
        when(tourGuideService.setUserPreferences(any(UserPreferencesDTO.class), any(User.class))).thenReturn(expectedUser);
        UserPreferencesDTO userPreferencesDTO = new UserPreferencesDTO();

        //WHEN we call the method
        ResponseEntity<UserPreferences> actualResponse = tourGuideController.updateUserPreferences(userPreferencesDTO, "test");

        //THEN tourGuideService.setUserPreferences is called and we get the correct response
        verify(tourGuideService, times(1)).setUserPreferences(any(UserPreferencesDTO.class), any(User.class));
        assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
    }
}
