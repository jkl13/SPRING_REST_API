package me.whiteship.demoinflearnrestapi.events;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Integer> {
    public Page<Event> findByEventStatusAndBasePriceBetween(EventStatus eventStatus, int startBasePrice, int endBasePrice, Pageable pageable);
}
