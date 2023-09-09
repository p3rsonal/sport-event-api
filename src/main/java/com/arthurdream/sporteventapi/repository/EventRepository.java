package com.arthurdream.sporteventapi.repository;

import com.arthurdream.sporteventapi.model.Event;
import com.arthurdream.sporteventapi.model.EventStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {

    @Transactional
    @Modifying
    @Query("UPDATE Event e SET e.status = ?2 WHERE e.id = ?1")
    void updateStatus(Long eventId, EventStatus status);
}
