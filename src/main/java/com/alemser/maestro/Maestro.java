package com.alemser.maestro;

import com.alemser.maestro.util.SecurityUtil;
import org.activiti.api.process.model.ProcessDefinition;
import org.activiti.api.process.model.ProcessInstance;
import org.activiti.api.process.model.builders.ProcessPayloadBuilder;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.api.runtime.shared.query.Page;
import org.activiti.api.runtime.shared.query.Pageable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Maestro implements CommandLineRunner {

    private Logger logger = LoggerFactory.getLogger(Maestro.class);

    @Autowired
    private ProcessRuntime processRuntime;
    @Autowired
    private SecurityUtil securityUtil;

    public static void main(String[] args) {
        SpringApplication.run(Maestro.class, args);

    }

    @Override
    public void run(String... args) {
        securityUtil.logInAs("system");
        Page<ProcessDefinition> processDefinitionPage = processRuntime.processDefinitions(Pageable.of(0, 10));
        ProcessDefinition processDefinition = processDefinitionPage.getContent().get(0);
        logger.info("About to start process " + processDefinition.getId());

        ProcessInstance instance = processRuntime.start(ProcessPayloadBuilder
                .start()
                .withProcessDefinitionId(processDefinition.getId())
                .withProcessInstanceName("Parallel Services Process Instance")
                .withVariable("X-Something", "Some value")
                .build());
    }
}
