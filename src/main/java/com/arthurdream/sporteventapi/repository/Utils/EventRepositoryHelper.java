package com.arthurdream.sporteventapi.repository.Utils;

import com.arthurdream.sporteventapi.model.Event;
import com.arthurdream.sporteventapi.model.EventStatus;
import com.arthurdream.sporteventapi.model.SportType;
import org.springframework.data.jpa.domain.Specification;

public class EventRepositoryHelper {

    public static Specification<Event> filterByStatus(EventStatus status) {
        return (root, query, cb) -> status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<Event> filterBySportType(SportType sportType) {
        return (root, query, cb) -> sportType == null ? null : cb.equal(root.get("sport"), sportType);
    }
}
