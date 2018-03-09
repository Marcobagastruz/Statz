package me.staartvin.statz.cache;

import me.staartvin.statz.datamanager.player.PlayerInfo;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CachingManager {

    private ConcurrentHashMap<UUID, PlayerInfo> cachedPlayerData = new ConcurrentHashMap<>();

    /**
     * Register cached data for a player. It will overwrite any current cached data.
     *
     * @param uuid        UUID of the player.
     * @param dataToCache Data to store.
     *
     * @throws NullPointerException if data to cache is null
     */
    public void registerCachedData(UUID uuid, PlayerInfo dataToCache) throws NullPointerException {

        if (dataToCache == null) {
            throw new NullPointerException("Data to cache is null.");
        }

        // Store data in cache, overwriting previous data
        cachedPlayerData.put(uuid, dataToCache);
    }

    /**
     * Add new data to the cache. The new data will be merged with the already existing cached data.
     * The merging of cached and new data can be a quite intensive process and so should preferably be run
     * asynchronously.
     * <br>
     * If no cached data exists, this will just store the given data as new data.
     *
     * @param uuid        UUID of player that the data is for
     * @param dataToCache new data to store
     * @throws IllegalArgumentException if given data is null
     */
    public void addCachedData(UUID uuid, PlayerInfo dataToCache) throws IllegalArgumentException {
        if (dataToCache == null) {
            throw new IllegalArgumentException("Data to cache is null.");
        }

        PlayerInfo cachedData = getCachedPlayerData(uuid);

        // There is no cached data, so we just register new data.
        if (cachedData == null) {
            registerCachedData(uuid, dataToCache);
            return;
        }

        // Resolve conflicts and update cache.
        PlayerInfo resolvedCache = cachedData.resolveConflicts(dataToCache);

        // Update cache with new cached data.
        this.registerCachedData(uuid, resolvedCache);
    }

    /**
     * Get cached data of a player.
     *
     * @param uuid UUID of the player.
     *
     * @return PlayerInfo object containing cached data or null if no cached data exists.
     */
    public PlayerInfo getCachedPlayerData(UUID uuid) {

        // Do sanity check to prevent NullPointerException while calling hashmap.
        if (uuid == null) {
            return null;
        }

        return cachedPlayerData.get(uuid);
    }


}
