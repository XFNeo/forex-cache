package ru.xfneo.repo;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import ru.xfneo.entity.Updatable;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public abstract class CacheRepo<T extends Updatable> {

    private static final Logger LOG = Logger.getLogger(CacheRepo.class);
    final private Object lock = new Object();

    @ConfigProperty(name = "cache.validation.period.minutes")
    int validationPeriodMinutes;

    protected abstract T save(String key, T responseData);

    protected abstract Optional<T> get(String key);

    public abstract List<T> getAll();

    public T withCache(String key, OmnivoreSupplier<T> supplier) {
        Function<Optional<T>, Boolean> check = (Optional<T> value) -> value.isPresent()
                && value.get().lastUpdate().plus(validationPeriodMinutes, ChronoUnit.MINUTES).isBefore(LocalDateTime.now());

        var dataOpt = get(key);
        if (check.apply(dataOpt)) {
            return dataOpt.get();
        }

        synchronized (lock) {
            dataOpt = get(key);
            if (check.apply(dataOpt)) {
                return dataOpt.get();
            }

            try {
                dataOpt.ifPresentOrElse(
                        data -> LOG.info("Value in the cache is expired. Request will be proxied to the original server, cache will be updated."),
                        () -> LOG.info("Value does not exist in the cache. Request will be proxied to the original server, response will be cached."));
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
