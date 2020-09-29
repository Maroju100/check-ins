package com.objectcomputing.checkins.services.agenda_item;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.annotation.*;
import io.micronaut.http.hateoas.JsonError;
import io.micronaut.http.hateoas.Link;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.Set;
import java.util.UUID;

import java.util.ArrayList;
import java.util.List;

@Controller("/services/agenda-item")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "agenda-item")
public class AgendaItemController {

    private AgendaItemServices agendaItemServices;

    public AgendaItemController(AgendaItemServices agendaItemServices) {
        this.agendaItemServices = agendaItemServices;
    }


    @Error(exception = AgendaItemBadArgException.class)
    public HttpResponse<?> handleBadArgs(HttpRequest<?> request, AgendaItemBadArgException e) {
        JsonError error = new JsonError(e.getMessage())
                .link(Link.SELF, Link.of(request.getUri()));

        return HttpResponse.<JsonError>badRequest()
                .body(error);
    }

    /**
     * Create and Save a new agenda item
     *
     * @param agendaItem
     * @param request
     * @return
     */
    @Post("/")
    public HttpResponse<AgendaItem> createAgendaItem(@Body @Valid AgendaItemCreateDTO agendaItem, HttpRequest<AgendaItemCreateDTO> request) {
        AgendaItem newAgendaItem = agendaItemServices.save(new AgendaItem(agendaItem.getCheckinid(), agendaItem.getCreatedbyid()
                , agendaItem.getDescription()));
        return HttpResponse.created(newAgendaItem)
                .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), newAgendaItem.getId()))));

    }

    /**
     * Update a agenda item
     *
     * @param agendaItem
     * @param request
     * @return
     */
    @Put("/")
    public HttpResponse<AgendaItem> updateAgendaItem(@Body @Valid AgendaItem agendaItem, HttpRequest<AgendaItemCreateDTO> request) {
        AgendaItem updateAgendaItem = agendaItemServices.update(agendaItem);
        return HttpResponse.ok().headers(headers -> headers.location(
                URI.create(String.format("%s/%s", request.getPath(), updateAgendaItem.getId()))))
                .body(updateAgendaItem);
    }

    /**
     * Get items by checkind or createbyid
     *
     * @param checkinid
     * @param createdbyid
     * @return
     */
    @Get("/{?checkinid,createdbyid}")
    public Set<AgendaItem> findAgendaItem(@Nullable UUID checkinid,
                                            @Nullable UUID createdbyid) {
        return agendaItemServices.findByFields(checkinid, createdbyid);
    }

    /**
     * Get agenda item from id
     *
     * @param id
     * @return
     */
    @Get("/{id}")
    public AgendaItem readAgendaItem(@NotNull UUID id) {
        return agendaItemServices.read(id);
    }

    /**
     * Delete agendaItem
     *
     * @param id, id of {@link AgendaItem} to delete
     */
    @Delete("/{id}")
    public HttpResponse<?> deleteAgendaItem(UUID id) {
        agendaItemServices.delete(id);
        return HttpResponse
                .ok();
    }

    /**
     * Load agenda items
     *
     * @param agendaItems, {@link List< AgendaItemCreateDTO > to load {@link AgendaItem agenda items}}
     * @return {@link HttpResponse<List< AgendaItem >}
     */
    @Post("/items")
    public HttpResponse<?> loadAgendaItems(@Body @Valid @NotNull List<AgendaItemCreateDTO> agendaItems,
                                           HttpRequest<List<AgendaItem>> request) {
        List<String> errors = new ArrayList<>();
        List<AgendaItem> agendaItemsCreated = new ArrayList<>();
        for (AgendaItemCreateDTO agendaItemDTO : agendaItems) {
            AgendaItem agendaItem = new AgendaItem(agendaItemDTO.getCheckinid(),
                    agendaItemDTO.getCreatedbyid(), agendaItemDTO.getDescription());
            try {
                agendaItemServices.save(agendaItem);
                agendaItemsCreated.add(agendaItem);
            } catch (AgendaItemBadArgException e) {
                errors.add(String.format("Member %s's agenda item was not added to CheckIn %s because: %s", agendaItem.getCreatedbyid(),
                        agendaItem.getCheckinid(), e.getMessage()));
            }
        }
        if (errors.isEmpty()) {
            return HttpResponse.created(agendaItemsCreated)
                    .headers(headers -> headers.location(request.getUri()));
        } else {
            return HttpResponse.badRequest(errors)
                    .headers(headers -> headers.location(request.getUri()));
        }
    }

}