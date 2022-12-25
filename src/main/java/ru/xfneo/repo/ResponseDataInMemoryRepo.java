package ru.xfneo.repo;

import ru.xfneo.entity.ResponseData;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class ResponseDataInMemoryRepo extends CacheRepo<ResponseData> {

    final Map<String, ResponseData> responseDataStorage = new ConcurrentHashMap<>();

    @Override
    protected ResponseData save(String key, ResponseData responseData) {
        return responseDataStorage.put(key, responseData);

    }

    @Override
    protected Optional<ResponseData> get(String key) {
        return Optional.ofNullable(responseDataStorage.get(key));
    }

    @Override
    public List<ResponseData> getAll() {
        return new ArrayList<>(responseDataStorage.values());
    }


}
