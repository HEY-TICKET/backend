package com.heyticket.backend.repository.place;

import com.heyticket.backend.domain.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaceRepository extends JpaRepository<Place, String>, PlaceCustomRepository {

}
