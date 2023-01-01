package ru.xfneo.repo;

import org.jboss.logging.Logger;
import ru.xfneo.entity.Expirable;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Function;

import static java.time.LocalDateTime.now;

public abstract class CacheRepo<T extends Expirable> {

    private static final Logger LOG = Logger.getLogger(CacheRepo.class);
    final private Object lock = new Object();

    protected abstract T save(String key, T responseData);

    protected abstract Optional<T> get(String key);

    protected LocalDateTime getNow()  {
        return now();
    }

    public T withCache(String key, OmnivoreSupplier<T> supplier) {
        var now = getNow();
        Function<Optional<T>, Boolean> existsAndNotExpired = (Optional<T> value) ->
                value.map(v -> v.expirationDate().isAfter(now)).orElse(false);

        var dataOpt = get(key);
        if (existsAndNotExpired.apply(dataOpt)) {
            return dataOpt.get();
        }

        synchronized (lock) {
            dataOpt = get(key);
            if (existsAndNotExpired.apply(dataOpt)) {
                return dataOpt.get();
            }

            try {
                dataOpt.ifPresentOrElse(
                        data -> LOG.info("Value in the cache is expired, request data from supplier."),
                        () -> LOG.info("Value does not exist in the cache, request data from supplier."));
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
