package com.alemser.maestro.configuration;

import com.alx.etx.data.Coordination;
import com.alx.etx.data.CoordinationConfiguration;
import com.alx.etx.service.CoordinationService;
import com.alx.etx.service.CoordinationServiceImpl;
import org.activiti.api.process.runtime.connector.Connector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class ProcessConfiguration {
    private Logger logger = LoggerFactory.getLogger(ProcessConfiguration.class);

    @Autowired
    private CoordinationService coordinationService;
    private static String COORD_ID_KEY = "etx::coord-id";

    @Bean
    public Connector prepareOrchestration() {
        return integrationContext -> {
            Map inBoundVariables = integrationContext.getInBoundVariables();
            coordinationService.start(new CoordinationConfiguration()).subscribe( coord-> {
                logger.info("Coordination ID " + coord.getId() + " started");
                integrationContext.getOutBoundVariables().put(COORD_ID_KEY, coord.getId());
                String contentToProcess = (String) inBoundVariables.get("X-Something");
                logger.info("Preparing orchestration environment " + contentToProcess);
            });

            return integrationContext;
        };
    }

    @Bean
    public Connector endOrchestration() {
        return integrationContext -> {
            coordinationService.end(integrationContext.getInBoundVariables().get(COORD_ID_KEY).toString()).subscribe();
            logger.info("Orchestration has ended");
            return integrationContext;
        };
    }

    @Bean
    public Connector callService() {
        return integrationContext -> {
            Map inBoundVariables = integrationContext.getInBoundVariables();

            String coordinationId = inBoundVariables.get(COORD_ID_KEY).toString();

            coordinationService.join(coordinationId, integrationContext.getActivityElementId()).subscribe(
                    id -> {
                        String contentToProcess = (String) inBoundVariables.get("X-Something");
                        logger.info("Calling service " + contentToProcess);
                        coordinationService.execute(coordinationId, id);
                        coordinationService.confirm(coordinationId, id);
                    }
            );

            return integrationContext;
        };
    }

    @Bean
    public CoordinationService coordinationService() {
        return new CoordinationServiceImpl();
    }
}
