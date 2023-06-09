package me.whiteship.demoinflearnrestapi.events;

import me.whiteship.demoinflearnrestapi.common.BaseControllerTest;
import me.whiteship.demoinflearnrestapi.common.TestDescription;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class EventControllerTests extends BaseControllerTest {
    @Autowired
    EventRepository eventRepository;

    @Test
    @TestDescription("정상적으로 이벤트를 생성하는 테스트")
    public void createEvent() throws Exception {
        EventDto event = EventDto.builder()
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime((LocalDateTime.of(2023, 4, 11,12,57 )))
                .closeEnrollmentDateTime((LocalDateTime.of(2023, 4, 12,12,57 )))
                .beginEventDateTime((LocalDateTime.of(2023, 4, 13,12,57 )))
                .endEventDateTime((LocalDateTime.of(2023, 4, 14,12,57 )))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역")
                .build();

        mockMvc.perform(post("/api/events/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaTypes.HAL_JSON)
                    .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect((jsonPath("id").exists()))
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("free").value(false))
                .andExpect(jsonPath("offline").value(true))
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.query-events").exists())
                .andExpect(jsonPath("_links.update-event").exists())
                .andDo(document("create-event",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("query-events").description("link to query events"),
                                linkWithRel("update-event").description("link to update an existing event"),
                                linkWithRel("profile").description("link to update an existing event")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestFields(
                                fieldWithPath("name").description("Name of new event"),
                                fieldWithPath("description").description("description of new event"),
                                fieldWithPath("beginEnrollmentDateTime").description("date time of begin of new event"),
                                fieldWithPath("closeEnrollmentDateTime").description("date time of close of new event"),
                                fieldWithPath("beginEventDateTime").description("date time of begin of new event"),
                                fieldWithPath("endEventDateTime").description("date time of end of new event"),
                                fieldWithPath("location").description("location of new event"),
                                fieldWithPath("basePrice").description("base price of new event"),
                                fieldWithPath("maxPrice").description("max price of new event"),
                                fieldWithPath("limitOfEnrollment").description("limit of enrolmment")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("Location header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        responseFields(
                                fieldWithPath("id").description("identifier of new event"),
                                fieldWithPath("name").description("Name of new event"),
                                fieldWithPath("description").description("description of new event"),
                                fieldWithPath("beginEnrollmentDateTime").description("date time of begin of new event"),
                                fieldWithPath("closeEnrollmentDateTime").description("date time of close of new event"),
                                fieldWithPath("beginEventDateTime").description("date time of begin of new event"),
                                fieldWithPath("endEventDateTime").description("date time of end of new event"),
                                fieldWithPath("location").description("location of new event"),
                                fieldWithPath("basePrice").description("base price of new event"),
                                fieldWithPath("maxPrice").description("max price of new event"),
                                fieldWithPath("limitOfEnrollment").description("limit of enrollment"),
                                fieldWithPath("free").description("it tells if this event is free or not"),
                                fieldWithPath("offline").description("it tells if this event is offline event or not"),
                                fieldWithPath("eventStatus").description("event status"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.query-events.href").description("link to query event list"),
                                fieldWithPath("_links.update-event.href").description("link to update existing event"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ))
        ;
    }

    @Test
    @TestDescription("입력 받을 수 없는 값을 사용한 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request() throws Exception {
        Event event = Event.builder()
                .id(100)
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime((LocalDateTime.of(2023, 4, 11,12,57 )))
                .closeEnrollmentDateTime((LocalDateTime.of(2023, 4, 14,12,57 )))
                .beginEventDateTime((LocalDateTime.of(2023, 4, 11,12,57 )))
                .endEventDateTime((LocalDateTime.of(2023, 4, 14,12,57 )))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역")
                .eventStatus(EventStatus.PUBLISHED)
                .build();

        mockMvc.perform(post("/api/events/")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @TestDescription("입력 값이 비어있는 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request_Empty_Input() throws Exception {
        EventDto eventDto = EventDto.builder().build();

        this.mockMvc.perform(post("/api/events")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(this.objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @TestDescription("입력 값이 잘못된 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request_Wrong_Input() throws Exception {
        EventDto eventDto = EventDto.builder()
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime((LocalDateTime.of(2023, 4, 14,12,57 )))
                .closeEnrollmentDateTime((LocalDateTime.of(2023, 4, 11,12,57 )))
                .beginEventDateTime((LocalDateTime.of(2023, 4, 14,12,57 )))
                .endEventDateTime((LocalDateTime.of(2023, 4, 11,12,57 )))
                .basePrice(10000)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역")
                .build();

        this.mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors[0].objectName").exists())
                .andExpect(jsonPath("errors[0].defaultMessage").exists())
                .andExpect(jsonPath("errors[0].code").exists())
                .andExpect(jsonPath("_links.index").exists())
        ;
    }

    @Test
    @TestDescription("30개의 이벤트를 10개씩 두번째 페이지 조회하기.")
    public void queryEvents() throws Exception {
        //Given
        IntStream.range(0, 30).forEach(this::generateEvent);

        //When & Then
        this.mockMvc.perform(get("/api/events")
                    .param("page","1")
                    .param("size", "10")
                    .param("sort", "name,DESC")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("query-events"))
        ;
    }

    @Test
    @TestDescription("기존의 이벤트를 하나 조회하기.")
    public void getEvent() throws Exception {
        //Given
        Event event = this.generateEvent(100);

        //When & Then
        this.mockMvc.perform(get("/api/events/{id}", event.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("get-an-event"))
        ;
    }
    @Test
    @TestDescription("이벤트 목록에 필터를 적용하여 조회하기.")
    public void getEventWithFilter() throws Exception {
        // Given
        IntStream.range(0, 30).forEach(this::generateEvent);

        // When & Then
        this.mockMvc.perform(
                get("/api/events")
                        .param("startBasePrice", "100")
                        .param("endBasePrice", "200")
                        .param("eventStatus", EventStatus.PUBLISHED.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
                .andExpect(jsonPath("_embedded.eventList.*.eventStatus", Matchers.anyOf(Matchers.hasItem(EventStatus.PUBLISHED.toString()))))
                .andExpect(jsonPath("_embedded.eventList.*.basePrice", Matchers.anyOf(Matchers.hasItem(
                                Matchers.allOf(Matchers.greaterThanOrEqualTo(100), Matchers.lessThanOrEqualTo(200))
                        ))))
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("query-events-filtered"))
        ;
    }

    @Test
    @TestDescription("없는 이벤트를 조회 했을 때 404 응답 받기.")
    public void getEvent404() throws Exception {
        this.mockMvc.perform(get("/api/events/1345321"))
                .andExpect(status().isNotFound())
        ;
    }

    @Test
    @TestDescription("이벤트를 정상적으로 수정하기.")
    public void updateEvent() throws Exception {
        //Given
        Event event = this.generateEvent(200);
        EventDto eventDto = this.modelMapper.map(event, EventDto.class);
        String eventName = "updated Event";
        eventDto.setName(eventName);

        //When & Then
        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect((status().isOk()))
                .andExpect((jsonPath("name").value(eventName)))
                .andExpect((jsonPath("_links.self").exists()))
        ;
    }

    @Test
    @TestDescription("이벤트를 무료로 변경하기.")
    public void updateEventFree() throws Exception {
        //Given
        Event event = this.generateEvent(200);
        EventDto eventDto = this.modelMapper.map(event, EventDto.class);
        eventDto.setMaxPrice(0);
        eventDto.setBasePrice(0);

        //When & Then
        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect((status().isOk()))
                .andExpect((jsonPath("free").value(true)))
                .andExpect((jsonPath("_links.self").exists()))
        ;
    }

    @Test
    @TestDescription("입력 값이 비어있는 경우에 이벤트 수정 실패.")
    public void updateEvent400_Empty() throws Exception {
        //Given
        Event event = this.generateEvent(200);
        EventDto eventDto = new EventDto();

        //When & Then
        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect((status().isBadRequest()))
        ;
    }

    @Test
    @TestDescription("입력 값이 잘못된 경우에 이벤트 수정 실패.")
    public void updateEvent400_Wrong() throws Exception {
        //Given
        Event event = this.generateEvent(200);
        EventDto eventDto = this.modelMapper.map(event, EventDto.class);
        eventDto.setBasePrice(20000);
        eventDto.setMaxPrice(10000);

        //When & Then
        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect((status().isBadRequest()))
        ;
    }

    @Test
    @TestDescription("존재하지 않는 이벤트 수정 실패.")
    public void updateEvent404_Wrong() throws Exception {
        //Given
        Event event = this.generateEvent(200);
        EventDto eventDto = this.modelMapper.map(event, EventDto.class);

        //When & Then
        this.mockMvc.perform(put("/api/events/1233212")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect((status().isNotFound()))
        ;
    }

    private Event generateEvent(int i) {
        List<EventStatus> eventStatusList = Arrays.asList(EventStatus.DRAFT, EventStatus.BEGAN_ENROLLMENT, EventStatus.PUBLISHED);
        Random rand = new Random();
        Event event = Event.builder()
                .name("event " + i)
                .description("test event")
                .beginEnrollmentDateTime((LocalDateTime.of(2023, 4, 11,12,57 )))
                .closeEnrollmentDateTime((LocalDateTime.of(2023, 4, 12,12,57 )))
                .beginEventDateTime((LocalDateTime.of(2023, 4, 13,12,57 )))
                .endEventDateTime((LocalDateTime.of(2023, 4, 14,12,57 )))
                .basePrice(rand.nextInt() % 200)//200미만의 랜덤 price
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역")
                .free(false)
                .offline(true)
                .eventStatus(eventStatusList.get(rand.nextInt(eventStatusList.size())))
                .build();

        return this.eventRepository.save(event);
    }

}
