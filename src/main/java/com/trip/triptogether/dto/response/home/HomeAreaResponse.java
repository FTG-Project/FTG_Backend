package com.trip.triptogether.dto.response.home;

import com.trip.triptogether.constant.Area;
import com.trip.triptogether.dto.response.Recommend.AttractionBelovedResponse;
import com.trip.triptogether.dto.response.Recommend.MedicalFacilityBelovedResponse;
import com.trip.triptogether.dto.response.Recommend.RestaurantBelovedResponse;
import com.trip.triptogether.dto.response.Recommend.ShowBelovedResponse;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HomeAreaResponse {
    private Area area;
    private List<AttractionBelovedResponse> attractionId;
    private List<RestaurantBelovedResponse> restaurantId;
    private List<ShowBelovedResponse> showId;
    private List<MedicalFacilityBelovedResponse> medicalFacilityId;

}
