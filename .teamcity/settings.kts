import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.notifications
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script
import jetbrains.buildServer.configs.kotlin.v2019_2.failureConditions.BuildFailureOnMetric
import jetbrains.buildServer.configs.kotlin.v2019_2.failureConditions.failOnMetricChange
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.schedule
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2019.2"

project {

    buildType(Build)

    features {
        feature {
            id = "PROJECT_EXT_75"
            type = "OAuthProvider"
            param("displayName", "Slack1 connection")
            param("secure:token", "credentialsJSON:b4fcd714-4a67-474e-a38d-5b5d749ada55")
            param("providerType", "slackConnection")
        }
    }
}

object Build : BuildType({
    name = "Build"

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        script {
            scriptContent = """
                echo master
                ping -n 60 127.0.0.1 > nul
            """.trimIndent()
        }
    }

    triggers {
        vcs {
        }
        schedule {
            branchFilter = """
                +:*
                -:test
            """.trimIndent()
            triggerBuild = always()
        }
    }

    failureConditions {
        failOnMetricChange {
            metric = BuildFailureOnMetric.MetricType.BUILD_DURATION
            threshold = 10
            units = BuildFailureOnMetric.MetricUnit.DEFAULT_UNIT
            comparison = BuildFailureOnMetric.MetricComparison.MORE
            compareTo = value()
        }
    }

    features {
        notifications {
            notifier = "jbSlackNotifier"
            brachFilter = "+:*"
            buildStarted = true
            buildFailed = true
            param("plugin:notificator:jbSlackNotifier:connection", "PROJECT_EXT_75")
            param("plugin:notificator:jbSlackNotifier:channel", "U037MMR1C")
        }
        notifications {
            notifier = "email"
            brachFilter = "+:*"
            param("email", "inna@test.ru")
        }
    }
})
