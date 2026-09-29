package com.saveql.backend;

import com.saveql.backend.entity.system.SystemInfo;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.event.Observes;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

public class Startup {

    private static final Logger LOG = Logger.getLogger(Startup.class);

    @Transactional
    public void onStart(@Observes StartupEvent event) {
        LOG.info("Checking system settings...");

        if (SystemInfo.findById(SystemInfo.SINGLETON_ID) == null) {
            SystemInfo settings = new SystemInfo();
            settings.persist();
            LOG.info("Default system settings configured!");
        }
    }

}
