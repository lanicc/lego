package io.github.lanicc.distributedlock;

import lombok.SneakyThrows;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created on 2024/4/9.
 *
 * @author lan
 */

public class DbBasedLock implements Lock {

    private final DataSource dataSource;

    private final String lockSql;

    private final ThreadLocal<LockState> lockedStack = new ThreadLocal<>();

    public DbBasedLock(DataSource dataSource) {
        this(dataSource, "select * from t_lock for update");
    }

    public DbBasedLock(DataSource dataSource, String lockSql) {
        this.dataSource = dataSource;
        this.lockSql = lockSql;
    }

    @SneakyThrows(SQLException.class)
    @Override
    public void lock() {
        LockState lockState = lockedStack.get();
        if (lockState == null) {
            Connection connection = dataSource.getConnection();
            lockState = new LockState(connection, connection.getAutoCommit());
            connection.setAutoCommit(false);
            lockedStack.set(lockState);
        }
        lockState.connection.createStatement().execute(lockSql);
        lockState.lockCount++;
    }

    @SneakyThrows(SQLException.class)
    @Override
    public void unlock() {
        LockState lockState = lockedStack.get();
        if (lockState == null || lockState.lockCount == 0) {
            throw new IllegalStateException();
        }
        lockState.lockCount--;
        if (lockState.lockCount == 0) {
            lockState.connection.commit();
            lockState.connection.setAutoCommit(lockState.autoCommit);
            lockState.connection.close();
            lockedStack.remove();
        }
    }

    static class LockState {
        private final Connection connection;
        private final boolean autoCommit;
        private int lockCount;

        LockState(Connection connection, boolean autoCommit) {
            this.connection = connection;
            this.autoCommit = autoCommit;
        }
    }
}
