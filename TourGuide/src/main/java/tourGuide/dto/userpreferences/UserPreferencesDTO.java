package tourGuide.dto.userpreferences;

public class UserPreferencesDTO {
    int tripDuration;
    int numberOfAdults;
    int numberOfChildren;

    public int getTripDuration() {
        return tripDuration;
    }

    public int getNumberOfAdults() {
        return numberOfAdults;
    }

    public int getNumberOfChildren() {
        return numberOfChildren;
    }

    public UserPreferencesDTO(){
    }

    public UserPreferencesDTO(int tripDuration, int numberOfAdults, int numberOfChildren) {
        this.tripDuration = tripDuration;
        this.numberOfAdults = numberOfAdults;
        this.numberOfChildren = numberOfChildren;
    }
}