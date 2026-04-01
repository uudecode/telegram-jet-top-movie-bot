package ru.poseidonnet.jet_movie_top_bot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cache2k.Cache;
import org.cache2k.Cache2kBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.poseidonnet.jet_movie_top_bot.kinopoisk.api.MovieFeignClient;
import ru.poseidonnet.jet_movie_top_bot.kinopoisk.model.KinopoiskResponse;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static ru.poseidonnet.jet_movie_top_bot.kinopoisk.api.MovieFeignClient.DAFAULT_FIELDS;

@Slf4j
@RequiredArgsConstructor
@Service
public class MovieLinkCacheService {

    private final Cache<Integer, KinopoiskResponse.Movie> cache = new Cache2kBuilder<Integer, KinopoiskResponse.Movie>() {
    }
            .expireAfterWrite(5, TimeUnit.HOURS)
            .build();
    private final MovieFeignClient movieFeignClient;
    @Value("${kinopoisk.token}")
    private String apiKey;

    public Map<Integer, KinopoiskResponse.Movie> getByIds(List<Integer> ids) {
        log.info("Requested details for {} movie IDs", ids.size());
        List<Integer> newIds = ids.stream().filter(v -> !cache.containsKey(v)).toList();

        if (!newIds.isEmpty()) {
            try {
                log.info("Fetching {} new IDs from Kinopoisk API: {}", newIds.size(), newIds);
                KinopoiskResponse byIds = movieFeignClient.findByIds(apiKey, DAFAULT_FIELDS, newIds.size(), newIds);
                if (byIds.getTotal() > 0) {
                    log.info("API returned {} movies out of {} requested", byIds.getMovies().size(), newIds.size());
                    byIds.getMovies().forEach(m -> {
                        cache.put(m.getId(), m);
                    });
                    List<Integer> missingIds = newIds.stream()
                            .filter(id -> !cache.containsKey(id))
                            .toList();
                    if (!missingIds.isEmpty()) {
                        log.warn("Kinopoisk API did not find data for these IDs: {}", missingIds);
                    }
                }
            } catch (Exception e) {
                log.error("Error while calling Kinopoisk API for IDs {}: {}", newIds, e.getMessage());
            }
        }
        Map<Integer, KinopoiskResponse.Movie> result = ids.stream()
                .filter(Objects::nonNull)
                .filter(id -> {
                    boolean exists = cache.containsKey(id);
                    if (!exists) {
                        log.debug("Movie ID {} is missing in final result (not found in API/Cache)", id);
                    }
                    return exists;
                })
                .collect(Collectors.toMap(i -> i, cache::get, (existing, replacement) -> existing));

        log.info("Returning map with {} found movies", result.size());
        return result;
    }


}
