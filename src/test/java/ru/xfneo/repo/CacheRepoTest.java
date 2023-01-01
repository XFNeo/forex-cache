package ru.xfneo.repo;

import org.junit.jupiter.api.Test;
import ru.xfneo.entity.Expirable;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Supplier;

import static java.time.LocalDateTime.now;
import static java.time.temporal.ChronoUnit.MINUTES;
import static org.junit.jupiter.api.Assertions.*;

class CacheRepoTest {
    public record ExpirableData(LocalDateTime expirationDate) implements Expirable {}

    static class CacheRepoTestImpl extends CacheRepo<ExpirableData> {
        private final Map<String, ExpirableData> storage;
        private final Supplier<LocalDateTime> timestamps;

        public CacheRepoTestImpl(Map<String, ExpirableData> storage) {
            this(storage,  null);
        }

        public CacheRepoTestImpl(Map<String, ExpirableData> storage, Deque<LocalDateTime> timestamps) {
            this.storage = storage;
            if (timestamps != null) {
                this.timestamps = timestamps::pop;
            } else {
                this.timestamps = LocalDateTime::now;
            }
        }

        @Override
        protected ExpirableData save(String key, ExpirableData data) {
            storage.put(key, data);
            return data;
        }

        @Override
        protected Optional<ExpirableData> get(String key) {
            return Optional.ofNullable(storage.get(key));
        }

        @Override
        protected LocalDateTime getNow() {
            return timestamps.get();
        }
    }

    private static final String KEY = "key1";

    @Test
    void withCacheTest() {
        Map<String, ExpirableData> storage = new HashMap<>();
        Deque<LocalDateTime> timestamps = new LinkedList<>();
        timestamps.add(now());
        timestamps.add(now());
        timestamps.add(now().plus(2, MINUTES));

        CacheRepo<ExpirableData> cacheRepo = new CacheRepoTestImpl(storage, timestamps);

        {
            final ExpirableData expectedExpirableData = new ExpirableData(now().plus(1, MINUTES));
            final ExpirableData actualExpirableData = cacheRepo.withCache(KEY, () -> expectedExpirableData);

            // Test: no data is in the cache
            assertEquals(1, storage.size());
            assertEquals(expectedExpirableData, storage.get(KEY));
            assertEquals(expectedExpirableData, actualExpirableData);

            // Test: data is in the cache
            assertEquals(expectedExpirableData, cacheRepo.withCache(KEY, () -> fail("Must not be called")));
            assertEquals(1, storage.size());
            assertEquals(expectedExpirableData, storage.get(KEY));
        }

        {
            // Test: expired data is in the cache
            final ExpirableData newExpectedExpirableData = new ExpirableData(now().plus(1, MINUTES));
            assertEquals(newExpectedExpirableData, cacheRepo.withCache(KEY, () -> newExpectedExpirableData));
            assertEquals(1, storage.size());
            assertEquals(newExpectedExpirableData, storage.get(KEY));
        }
    }

    @Test
    void withCacheExceptionTest() {
        Map<String, ExpirableData> storage = new HashMap<>();
        CacheRepo<ExpirableData> cacheRepo = new CacheRepoTestImpl(storage);

        assertThrows(RuntimeException.class, () -> cacheRepo.withCache(KEY, () -> {throw new Exception("Oops!");}));
    }

}