package tourGuide.user;

import org.javamoney.moneta.Money;
import tourGuide.dto.userpreferences.UserPreferencesDTO;

import javax.money.CurrencyUnit;
import javax.money.Monetary;


public class UserPreferences {
	
	private int attractionProximity = Integer.MAX_VALUE;
	private CurrencyUnit currency = Monetary.getCurrency("USD");
	private Money lowerPricePoint = Money.of(0, currency);
	private Money highPricePoint = Money.of(Integer.MAX_VALUE, currency);
	private int tripDuration = 1;
	private int ticketQuantity = 1;
	private int numberOfAdults = 1;
	private int numberOfChildren = 0;
	
	public UserPreferences() {
	}

	public UserPreferences update(UserPreferencesDTO userPreferencesDTO) {
		this.tripDuration = userPreferencesDTO.getTripDuration();
		this.numberOfAdults = userPreferencesDTO.getNumberOfAdults();
		this.numberOfChildren = userPreferencesDTO.getNumberOfChildren();
		return this;
	}
	
	public int getTripDuration() {
		return tripDuration;
	}
	
	public int getNumberOfAdults() {
		return numberOfAdults;
	}

	public int getNumberOfChildren() {
		return numberOfChildren;
	}

}
