package io.github.lanicc.binlog.core.meta;

import io.github.lanicc.binlog.core.LegoBinlogException;
import io.github.lanicc.binlog.core.util.PropertiesUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * Created on 2024/4/11.
 *
 * @author lan
 */
public class TableMetaMemoryMixedMetaManager extends MemoryMetaManager {

    private final String sql =
            "select ORDINAL_POSITION, COLUMN_NAME\n" +
                    "from information_schema.COLUMNS\n" +
                    "where TABLE_SCHEMA = ?\n" +
                    "  and TABLE_NAME = ?\n order by ORDINAL_POSITION";
    private Properties properties;

    public TableMetaMemoryMixedMetaManager(String destination) {
        super(destination);
    }

    @Override
    protected void doInit(Properties properties) {
        this.properties = properties;
    }


    @Override
    protected List<String> getTableColumns(String database, String table) {
        List<String> result = new ArrayList<>();
        String url =
                String.format(
                        "jdbc:mysql://%s:%s",
                        PropertiesUtil.getString(properties, "hostname", "127.0.0.1"),
                        PropertiesUtil.getInt(properties, "port", 3306)
                );

        String username = PropertiesUtil.getString(properties, "username", "root");
        String password = PropertiesUtil.getString(properties, "password", "root");
        log.info("jdbc url: {}, username: {}", url, username);
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, database);
            preparedStatement.setString(2, table);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String columnName = resultSet.getString("COLUMN_NAME");
                result.add(columnName);
            }
            resultSet.close();
            connection.close();
        } catch (SQLException e) {
            throw new LegoBinlogException(e);
        }
        return Collections.unmodifiableList(result);
    }
}
