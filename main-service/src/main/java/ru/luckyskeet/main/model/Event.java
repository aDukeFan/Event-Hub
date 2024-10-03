package ru.luckyskeet.main.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import ru.luckyskeet.main.util.constants_and_enums.EventState;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ToString
@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Accessors(chain = true)
@Entity
@Table(name = "events", schema = "public")
@EqualsAndHashCode
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    @Column(name = "created_date")
    LocalDateTime createdOn;
    @Column(name = "event_date")
    LocalDateTime eventDate;
    @Column(name = "published_on")
    LocalDateTime publishedOn;
    @Column(name = "title")
    String title;
    @Column(name = "annotation")
    String annotation;
    @Column(name = "description")
    String description;
    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    EventState state;
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    Category category;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    User user;
    @Column(name = "confirmed_requests")
    int confirmedRequests;
    @Column(name = "participant_limit")
    int participantLimit;
    @Column(name = "paid")
    boolean paid;
    @Column(name = "request_moderation")
    boolean requestModeration;
    @ManyToOne
    @JoinColumn(name = "location_id", nullable = false)
    Location location;
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Rating> ratings = new ArrayList<>();
}
