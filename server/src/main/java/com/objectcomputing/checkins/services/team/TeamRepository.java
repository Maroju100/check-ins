package com.objectcomputing.checkins.services.team;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.micronaut.data.annotation.Join;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface TeamRepository extends CrudRepository<Team, UUID> {

    Optional<Team> findByName(String name);

    List<Team> findByNameIlike(String name);

    @Override
    <S extends Team> List<S> saveAll(@Valid @NotNull Iterable<S> entities);

    @Override
    <S extends Team> S save(@Valid @NotNull @NonNull S entity);

    @Query("SELECT *, tm_.id as tm_id, tm_.memberid as tm_memberid, tm_.teamid as tm_teamid, tm_.lead as tm_lead, tm_.teamid as tm_team_id " +
            "FROM team t_ " +
            "INNER JOIN team_member tm_ " +
            "ON tm_.teamid = t_.id " +
            "WHERE (:name IS NULL OR t_.name = :name) " +
            "AND (:memberId IS NULL OR tm_.memberid = :memberId) ")
    @Join(value = "teamMembers", alias = "tm_")
    List<Team> search(@Nullable String name, @Nullable String memberId);
}
