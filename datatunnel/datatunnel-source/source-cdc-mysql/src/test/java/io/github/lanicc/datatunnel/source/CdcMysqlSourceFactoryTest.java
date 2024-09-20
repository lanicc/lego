package io.github.lanicc.datatunnel.source;

import io.github.lanicc.datatunnel.CommitLog;
import io.github.lanicc.datatunnel.Source;
import io.github.lanicc.datatunnel.config.Config;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * Created on 2024/9/19.
 *
 * @author lan
 */
@Slf4j
class CdcMysqlSourceFactoryTest {

    @Test
    @Timeout(100)
    void create() throws Exception {
        CdcMysqlSourceFactory factory = new CdcMysqlSourceFactory();
        CommitLog commitLog =
                new CommitLog() {
                    @Override
                    public <T extends Serializable> void commit(T log) {
                        System.err.println(log.toString());
                    }

                    @Override
                    public <T extends Serializable> T read() {
                        return null;
                    }
                };
        Config config = new Config();
        config.put(CdcMysqlSourceConfigItem.hostname.getKey(), "test.database3702x.scsite.net");
        config.put(CdcMysqlSourceConfigItem.port.getKey(), 3702);
        config.put(CdcMysqlSourceConfigItem.username.getKey(), "ngcanal");
        config.put(CdcMysqlSourceConfigItem.password.getKey(), "pppp");
        config.put(CdcMysqlSourceConfigItem.binlogFilename.getKey(), "mysql-bin.009001");
        config.put(CdcMysqlSourceConfigItem.binlogPosition.getKey(), 598922692L);
        Source source = factory.create(config, commitLog);
        source.open(dataObject -> log.info(dataObject.toString()));
        TimeUnit.SECONDS.sleep(50);
    }
}
