package com.alemser.maestro.configuration

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import java.util.*

@Configuration
class SecurityConfiguration {

    @Bean
    fun myUserDetailsService(): UserDetailsService {

        val inMemoryUserDetailsManager = InMemoryUserDetailsManager()

        val usersGroupsAndRoles = arrayOf(arrayOf("salaboy", "password", "ROLE_ACTIVITI_USER", "GROUP_activitiTeam"), arrayOf("ryandawsonuk", "password", "ROLE_ACTIVITI_USER", "GROUP_activitiTeam"), arrayOf("erdemedeiros", "password", "ROLE_ACTIVITI_USER", "GROUP_activitiTeam"), arrayOf("other", "password", "ROLE_ACTIVITI_USER", "GROUP_otherTeam"), arrayOf("system", "password", "ROLE_ACTIVITI_USER"), arrayOf("admin", "password", "ROLE_ACTIVITI_ADMIN"))

        for (user in usersGroupsAndRoles) {
            val authoritiesStrings = Arrays.asList(*Arrays.copyOfRange(user, 2, user.size))
            println("> Registering new user: " + user[0] + " with the following Authorities[" + authoritiesStrings + "]")

            val newUser = User(user[0], passwordEncoder().encode(user[1]), authoritiesStrings.map { s -> SimpleGrantedAuthority(s) })

            inMemoryUserDetailsManager.createUser(newUser)
        }


        return inMemoryUserDetailsManager
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
}