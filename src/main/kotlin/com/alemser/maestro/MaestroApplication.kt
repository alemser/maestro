package com.alemser.maestro

import com.alemser.maestro.util.SecurityUtil
import org.activiti.api.process.model.builders.ProcessPayloadBuilder
import org.activiti.api.process.runtime.ProcessRuntime
import org.activiti.api.runtime.shared.query.Pageable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ComponentScan(basePackages = ["com.alemser.maestro", "com.alx.etx"])
class MaestroApplication : CommandLineRunner {

    @Autowired
    private val processRuntime: ProcessRuntime? = null
    @Autowired
    private val securityUtil: SecurityUtil? = null

    override fun run(vararg args: String) {
        securityUtil!!.logInAs("system")
        val processDefinitionPage = processRuntime!!.processDefinitions(Pageable.of(0, 10))
        val processDefinition = processDefinitionPage.content[0]
        println("About to start process " + processDefinition.id)

        val instance = processRuntime.start(ProcessPayloadBuilder
                .start()
                .withProcessDefinitionId(processDefinition.id)
                .withProcessInstanceName("Parallel Services Process Instance")
                .withVariable("X-Something", "Some value")
                .build())
        println(instance)
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(MaestroApplication::class.java, *args)
}
