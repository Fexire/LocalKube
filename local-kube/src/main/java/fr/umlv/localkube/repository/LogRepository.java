package fr.umlv.localkube.repository;

import fr.umlv.localkube.model.Log;
import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface LogRepository {

    /**
     * Create log table if doesn't exists.
     * If table already exists, delete all tuples.
     */
    default void init() {
        createTable();
        cleanTable();
    }

    /**
     * Create log table in database.
     */
    @SqlUpdate("UPDATE sqlite_sequence SET seq = 0 WHERE name='log';CREATE TABLE IF NOT EXISTS log (id INTEGER PRIMARY KEY AUTOINCREMENT, app_id INTEGER NOT NULL, message TEXT NOT NULL, timestamp TEXT NOT NULL)")
    boolean createTable();

    /**
     * Delete all tuples from log table.
     */
    @SqlUpdate("DELETE FROM log; ")
    void cleanTable();

    /**
     * Saves a given log.
     */
    @SqlUpdate("INSERT INTO log(app_id, message, timestamp) VALUES (?, ?, ?)")
    @RegisterConstructorMapper(Log.class)
    void save(int app_id, String message, Instant timestamp);

    /**
     * Returns all instances of log.
     * @return all logs
     */
    @SqlQuery("SELECT app_id,message,timestamp  FROM log")
    @RegisterConstructorMapper(Log.class)
    List<Log> findAll();

    /**
     * Retrieves all logs since the last {@code time} minutes.
     * @param time timestamp
     * @return all logs
     */
    @SqlQuery("SELECT app_id,message,timestamp  FROM log WHERE timestamp >= ?")
    @RegisterConstructorMapper(Log.class)
    List<Log> findAll(Instant time);

    /**
     * Retrieves all logs since the last {@code time} minutes and with the same specified id.
     * @param time timestamp
     * @param id id to search
     * @return all logs
     */
    @SqlQuery("SELECT app_id,message,timestamp FROM log WHERE timestamp >= ? and app_id = ?")
    @RegisterConstructorMapper(Log.class)
    List<Log> findAllFilterById(Instant time, int id);

}
