package com.alemser.maestro.configuration

import com.alx.etx.data.CoordinationConfiguration
import com.alx.etx.data.Participant
import com.alx.etx.service.CoordinationService
import com.alx.etx.service.CoordinationServiceImpl
import org.activiti.api.process.runtime.connector.Connector
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ProcessConfiguration {
    @Autowired
    private val coordinationService: CoordinationService? = null
    private val COORD_ID_KEY = "etx::coord-id"

    @Bean
    fun prepareOrchestration() = Connector { integrationContext ->
            val inBoundVariables = integrationContext.inBoundVariables
            coordinationService!!
                    .start(CoordinationConfiguration())
                    .subscribe { coord ->
                        println("Coordination ID " + coord.id + " started")
                        integrationContext.outBoundVariables[COORD_ID_KEY] = coord.id
                        val contentToProcess = inBoundVariables["X-Something"] as String
                        println("Preparing orchestration environment $contentToProcess")
                    }
            integrationContext
        }

    @Bean
    fun endOrchestration() = Connector { integrationContext ->
            coordinationService!!
                    .end(integrationContext.inBoundVariables[COORD_ID_KEY].toString())
                    .subscribe()
            println("Orchestration has ended")
            integrationContext
        }

    @Bean
    fun callService() = Connector {integrationContext ->
            val inBoundVariables = integrationContext.inBoundVariables

            val coordinationId = inBoundVariables[COORD_ID_KEY].toString()
            val participant = Participant(integrationContext.activityElementId)

            coordinationService!!
                    .join(coordinationId, participant)
                    .subscribe { it ->
                        val contentToProcess = inBoundVariables["X-Something"] as String
                        println("Calling service $contentToProcess")
                        coordinationService.execute(coordinationId, it.id)
                        coordinationService.confirm(coordinationId, it.id)
                    }

            integrationContext
        }

    @Bean
    fun coordinationService() = CoordinationServiceImpl()
}