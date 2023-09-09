package com.arthurdream.sporteventapi.dto;

import com.arthurdream.sporteventapi.model.EventStatus;
import com.arthurdream.sporteventapi.model.SportType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Data
public class EventDTO {

    @Null(groups = OnCreate.class, message = "ID should not be provided for creation")
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "ID should not be provided for creation")
    private Long id;

    @NotBlank(message = "Event name cannot be blank.")
    @Size(max = 255, message = "Event name should not exceed 255 characters.")
    @Pattern(regexp = "^[a-zA-Z0-9 ]*$", message = "Event name should only contain alphanumeric characters and spaces.")
    private String name;

    @Enumerated(EnumType.STRING)
    private SportType sport;

    @Enumerated(EnumType.STRING)
    private EventStatus status;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startTime;
}
