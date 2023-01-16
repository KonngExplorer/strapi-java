package com.mystrapi.strapi.persistance;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.Optional;

/**
 * @author tangqiang
 */
@Component("strapiDateTimeProvider")
@Slf4j
public class StrapiDateTimeProvider implements DateTimeProvider {
    @Override
    public @NotNull Optional<TemporalAccessor> getNow() {
        return Optional.of(LocalDateTime.now());
    }
}
