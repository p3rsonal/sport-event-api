package com.arthurdream.sporteventapi.mapper;

import com.arthurdream.sporteventapi.dto.EventDTO;
import com.arthurdream.sporteventapi.model.Event;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EventMapper {

    EventDTO toDTO(Event event);

    Event toEntity(EventDTO dto);
}
