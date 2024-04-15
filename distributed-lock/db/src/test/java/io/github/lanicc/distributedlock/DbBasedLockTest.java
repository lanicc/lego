package io.github.lanicc.distributedlock;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

/**
 * Created on 2024/4/9.
 *
 * @author lan
 */
@Slf4j
class DbBasedLockTest extends LockTests {
    DataSource dataSource;

    String lockSql = "select * from t_lock where id = 1 for update";

    @BeforeEach
    void setUp() throws SQLException, IOException {
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("t_lock.sql");
        assert inputStream != null;
        String tLockDdl = IOUtils.toString(inputStream, StandardCharsets.UTF_8.name());
        dataSource = JdbcConnectionPool.create("jdbc:h2:~/test", "sa", "sa");
        Connection connection = dataSource.getConnection();
        log.info("tLockDdl: {}", tLockDdl);
        connection.createStatement().execute(tLockDdl);
        connection.commit();
        connection.close();
    }

    @Test
    void lock() {
        Lock lock = new DbBasedLock(dataSource, lockSql);
        runLock(lock);
    }

    @Test
    void unlockIllegalStateException() {
        Lock lock = new DbBasedLock(dataSource);
        Assertions.assertThrows(IllegalStateException.class, lock::unlock);
    }

    @Test
    void unlockIllegalStateException2() throws InterruptedException {
        Lock lock = new DbBasedLock(dataSource);
        Thread thread = new Thread(lock::lock);
        thread.start();
        thread.join();
        Assertions.assertThrows(IllegalStateException.class, lock::unlock);
    }

    @Test
    void reentrant() throws InterruptedException {
        Lock lock = new DbBasedLock(dataSource, lockSql);
        Thread thread1 = new Thread(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                lock.lock();
                log.info("locked");
                lock.lock();
                lock.lock();
                TimeUnit.SECONDS.sleep(1);
                lock.unlock();
                log.info("unlock 1");
                TimeUnit.SECONDS.sleep(2);
                lock.unlock();
                log.info("unlock 2");
                lock.unlock();
                TimeUnit.SECONDS.sleep(3);
                log.info("unlock 3");
            }
        });
        Thread thread2 = new Thread(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                lock.lock();
                log.info("locked");
                lock.lock();
                lock.lock();
                TimeUnit.SECONDS.sleep(1);
                lock.unlock();
                log.info("unlock 1");
                TimeUnit.SECONDS.sleep(2);
                lock.unlock();
                log.info("unlock 2");
                lock.unlock();
                TimeUnit.SECONDS.sleep(3);
                log.info("unlock 3");
            }
        });

        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();
        TimeUnit.SECONDS.sleep(10);
    }
}

