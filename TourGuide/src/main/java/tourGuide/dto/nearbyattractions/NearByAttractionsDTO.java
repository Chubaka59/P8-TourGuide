package tourGuide.dto.nearbyattractions;

import gpsUtil.location.Location;

import java.util.List;

public class NearByAttractionsDTO {
    List<AttractionDTO> attractionDTOList;
    Location userLocation;

    public List<AttractionDTO> getAttractionDTOList() { return attractionDTOList; }

    public NearByAttractionsDTO(List<AttractionDTO> attractionDTOList, Location userLocation){
        this.attractionDTOList = attractionDTOList;
        this.userLocation = userLocation;
    }

    public NearByAttractionsDTO(){
    }
}
