package ru.xfneo.repo;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import ru.xfneo.entity.Updatable;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public abstract class CacheRepo<T extends Updatable> {

    final private Object lock = new Object();

    @ConfigProperty(name = "cache.validation.period.minutes")
    int validationPeriodMinutes;

    protected abstract T save(String key, T responseData);

    protected abstract Optional<T> get(String key);

    public abstract List<T> getAll();

    public T withCache(String key, OmnivoreSupplier<T> supplier) {
        var optionalValue = get(key);
        Function<Optional<T>, Boolean> check = (Optional<T> value) -> value.isPresent()
                    && value.get().lastUpdate().plus(validationPeriodMinutes, ChronoUnit.MINUTES).isBefore(LocalDateTime.now());
        if (check.apply(optionalValue)) {
            return optionalValue.get();
        }

        synchronized (lock) {
            optionalValue = get(key);
            if (check.apply(optionalValue)) {
                return optionalValue.get();
            }

            try {
                var data = supplier.apply();
                return save(key, data);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @FunctionalInterface
    public interface OmnivoreSupplier<T> {
        T apply() throws Exception;
    }
}
