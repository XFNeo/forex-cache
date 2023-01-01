package ru.xfneo.repo;

import ru.xfneo.entity.ResponseData;

import javax.inject.Singleton;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class ResponseDataInMemoryRepo extends CacheRepo<ResponseData> {

    final Map<String, ResponseData> responseDataStorage = new ConcurrentHashMap<>();

    @Override
    protected ResponseData save(String key, ResponseData responseData) {
        responseDataStorage.put(key, responseData);
        return responseData;
    }

    @Override
    protected Optional<ResponseData> get(String key) {
        return Optional.ofNullable(responseDataStorage.get(key));
    }

}
