package com.objectcomputing.checkins.services.skills;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface SkillRepository extends CrudRepository<Skill, UUID> {
    List<Skill> findByName(String name);
    List<Skill> findAll();
    List<Skill> findByPending(boolean pending);

        List<Skill> findBySkillid(UUID skillid);

//    @Override
//    <S extends Skill> S update(@Valid @NotNull @NonNull S entity);

    @Override
    <S extends Skill> List<S> saveAll(@Valid @NotNull Iterable<S> entities);

//    @Override
//    Optional<Skill> findById(@NotNull @NonNull UUID skillid);
    @Override
    <S extends Skill> S save(@Valid @NotNull @NonNull S entity);
}
