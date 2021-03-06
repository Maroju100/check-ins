package com.objectcomputing.checkins.services.action_item;

import com.objectcomputing.checkins.services.exceptions.BadArgException;
import com.objectcomputing.checkins.services.exceptions.NotFoundException;
import com.objectcomputing.checkins.services.exceptions.PermissionException;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Controller("/services/action-item")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "action-item")
public class ActionItemController {

    private ActionItemServices actionItemServices;
    public ActionItemController(ActionItemServices actionItemServices) {
        this.actionItemServices = actionItemServices;
    }

    @Error(exception = NotFoundException.class)
    public HttpResponse<?> handleNotFound(HttpRequest<?> request, NotFoundException e) {
        JsonError error = new JsonError(e.getMessage())
                .link(Link.SELF, Link.of(request.getUri()));

        return HttpResponse.<JsonError>notFound()
                .body(error);
    }

    @Error(exception = BadArgException.class)
    public HttpResponse<?> handleBadArgs(HttpRequest<?> request, BadArgException e) {
        JsonError error = new JsonError(e.getMessage())
                .link(Link.SELF, Link.of(request.getUri()));

        return HttpResponse.<JsonError>badRequest()
                .body(error);
    }

    @Error(exception = PermissionException.class)
    public HttpResponse<?> handleBadPermissions(HttpRequest<?> request, PermissionException e) {
        JsonError error = new JsonError(e.getMessage())
                .link(Link.SELF, Link.of(request.getUri()));

        return HttpResponse.<JsonError>unauthorized()
                .body(error);
    }

    /**
     * Create and save a new actionItem.
     *
     * @param actionItem, {@link ActionItemCreateDTO}
     * @return {@link HttpResponse <ActionItem>}
     */
    @Post()
    public HttpResponse<ActionItem> createActionItem(@Body @Valid ActionItemCreateDTO actionItem,
                                                     HttpRequest<ActionItemCreateDTO> request) {
        ActionItem newActionItem = actionItemServices.save(new ActionItem(actionItem.getCheckinid(),
                actionItem.getCreatedbyid(), actionItem.getDescription()));
        return HttpResponse
                .created(newActionItem)
                .headers(headers -> headers.location(
                        URI.create(String.format("%s/%s", request.getPath(), newActionItem.getId()))));
    }

    /**
     * Update actionItem.
     *
     * @param actionItem, {@link ActionItem}
     * @return {@link HttpResponse< ActionItem >}
     */
    @Put()
    public HttpResponse<?> updateActionItem(@Body @Valid ActionItem actionItem, HttpRequest<ActionItem> request) {
        ActionItem updatedActionItem = actionItemServices.update(actionItem);
        return HttpResponse
                .ok()
                .headers(headers -> headers.location(
                        URI.create(String.format("%s/%s", request.getPath(), updatedActionItem.getId()))))
                .body(updatedActionItem);

    }

    /**
     * Delete actionItem
     *
     * @param id, id of {@link ActionItem} to delete
     */
    @Delete("/{id}")
    public HttpResponse<?> deleteActionItem(UUID id) {
        actionItemServices.delete(id);
        return HttpResponse
                .ok();
    }

    /**
     * Get ActionItem based off id
     *
     * @param id {@link UUID} of the action item entry
     * @return {@link ActionItem}
     */
    @Get("/{id}")
    public ActionItem readActionItem(UUID id) {
        return actionItemServices.read(id);
    }

    /**
     * Find action items that match all filled in parameters, return all results when given no params
     *
     * @param checkinid   {@link UUID} of checkin
     * @param createdbyid {@link UUID} of member
     * @return {@link List < CheckIn > list of checkins}
     */
    @Get("/{?checkinid,createdbyid}")
    public Set<ActionItem> findActionItems(@Nullable UUID checkinid,
                                           @Nullable UUID createdbyid) {
        return actionItemServices.findByFields(checkinid, createdbyid);
    }

}
