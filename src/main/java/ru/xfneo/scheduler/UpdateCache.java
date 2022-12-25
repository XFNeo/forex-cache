package ru.xfneo.scheduler;

import io.quarkus.scheduler.Scheduled;
import org.jboss.logging.Logger;
import ru.xfneo.entity.ResponseData;
import ru.xfneo.repo.CacheRepo;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class UpdateCache {

    private static final Logger LOG = Logger.getLogger(UpdateCache.class);

    @Inject
    CacheRepo<ResponseData> repo;

    @Scheduled(every="${cache.update.period}")
    void update() {
        LOG.info("Cache update started");
        final List<ResponseData> responseDataList = repo.getAll();
        LOG.infof("Objects in cache: %d", responseDataList.size());




        LOG.info("Cache update completed");
    }
}
