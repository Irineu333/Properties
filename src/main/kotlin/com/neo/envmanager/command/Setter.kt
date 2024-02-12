package com.neo.envmanager.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.neo.envmanager.com.neo.envmanager.util.extension.getEnvironments
import com.neo.envmanager.exception.error.NoEnvironmentsFound
import com.neo.envmanager.exception.error.SpecifyEnvironmentError
import com.neo.envmanager.model.Environment
import com.neo.envmanager.model.Installation
import com.neo.envmanager.model.Target
import com.neo.envmanager.util.extension.properties
import com.neo.envmanager.util.extension.requireInstall
import java.util.*

class Setter : CliktCommand(
    name = "set",
    help = "Set one or more properties",
) {

    private val properties by argument(
        help = "Properties to set, separated by space",
        helpTags = mapOf("KEY=VALUE" to "Property"),
    ).properties(required = true)

    private val tag by option(
        names = arrayOf("-t", "--tag"),
        help = "Specified environment tag; current environment is used by default"
    )

    private val all by option(
        names = arrayOf("-a", "--all"),
        help = "Set <properties> to all environments"
    ).flag()

    private val targetOnly by option(
        names = arrayOf("-o", "--target-only"),
        help = "Set <properties> to target only"
    ).flag()

    private lateinit var installation: Installation

    override fun run() {

        installation = requireInstall()

        if (targetOnly) {
            saveInTarget()
            return
        }

        if (all) {
            saveInAllEnvironments()
            return
        }

        saveInEnvironment()
    }

    private fun saveInTarget() {

        val target = Target(installation.config.targetFile)

        target.add(
            Properties().apply {
                putAll(properties)
            }
        )
    }

    private fun saveInAllEnvironments() {

        val environments = installation
            .environmentsDir
            .getEnvironments()
            .ifEmpty {
                throw NoEnvironmentsFound()
            }

        environments.forEach {
            it.add(properties.toMap())
        }

        val currentEnvironment = environments.find {
            it.tag == installation.config.currentEnv
        } ?: return

        currentEnvironment.checkout()
    }

    private fun saveInEnvironment() {

        val config = installation.config

        val tag = tag ?: config.currentEnv ?: throw SpecifyEnvironmentError()

        val environment = Environment.getOrCreate(installation.environmentsDir, tag)

        environment.add(properties.toMap())

        // Checkout when set in current environment
        if (tag == config.currentEnv) {
            environment.checkout()
        }
    }

    private fun Environment.checkout() {

        val target = Target(installation.config.targetFile)

        target.write(read().toProperties())
    }
}
