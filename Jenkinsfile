pipeline {
    agent any

    tools {
        maven 'Maven-3'
    }

    // Nightly execution supplements manually triggered runs.
    triggers {
        cron('H 2 * * *')
    }

    parameters {
        choice(
            name: 'TARGET_ENV',
            choices: ['qa', 'dev', 'local', 'prod'],
            description: 'Environment used by config-<env>.properties'
        )

        choice(
            name: 'TEST_GROUP',
            choices: ['regression', 'smoke', 'integration', 'all'],
            description: 'JUnit test suite to run'
        )
    }

    options {
        timestamps()

        // Prevent overlapping builds from competing for shared test data.
        disableConcurrentBuilds()
    }

    stages {
        stage('Validate Parameters') {
            steps {
                script {
                    /*
                     * Production is deliberately restricted to smoke tests.
                     * Broader regression and integration coverage should run
                     * before deployment rather than against production.
                     */
                    if (params.TARGET_ENV == 'prod' &&
                        params.TEST_GROUP != 'smoke') {

                        error(
                            'Production environment only allows smoke tests.'
                        )
                    }

                    /*
                     * Todo persistence tests connect directly to the local H2
                     * database, so they cannot run against QA/dev/prod.
                     */
                    if (params.TEST_GROUP == 'integration' &&
                        params.TARGET_ENV != 'local') {

                        error(
                            'Integration tests currently require TARGET_ENV=local.'
                        )
                    }

                    /*
                     * "all" includes the local database integration suite.
                     * Restrict it to local until remote environments expose
                     * an appropriate persistence layer for integration testing.
                     */
                    if (params.TEST_GROUP == 'all' &&
                        params.TARGET_ENV != 'local') {

                        error(
                            'The full test suite currently requires TARGET_ENV=local.'
                        )
                    }
                }
            }
        }

        stage('Approve Production Run') {
            when {
                expression {
                    params.TARGET_ENV == 'prod'
                }
            }

            steps {
                // Human approval adds a second guard before production traffic.
                input(
                    message: 'Run smoke tests against PRODUCTION?',
                    ok: 'Approve Production Test'
                )
            }
        }

        stage('Run Tests') {
            steps {
                echo "Environment: ${params.TARGET_ENV}"
                echo "Test Group: ${params.TEST_GROUP}"

                script {
                    /*
                     * Tagged suites use JUnit filtering.
                     *
                     * "all" intentionally omits the tag filter so every
                     * discovered test runs, including tests that may not
                     * yet have a suite tag.
                     */
                    if (params.TEST_GROUP == 'all') {
                        bat 'mvn -B clean test -Denv=%TARGET_ENV%'
                    } else {
                        bat 'mvn -B clean test -Denv=%TARGET_ENV% -Dtest.groups=%TEST_GROUP%'
                    }
                }
            }
        }
    }

    post {
        always {
            // Publish results even when tests fail so failures remain diagnosable.
            junit 'target/surefire-reports/*.xml'

            allure([
                results: [[path: 'target/allure-results']]
            ])
        }

        success {
            echo 'Automation pipeline completed successfully.'
        }

        failure {
            echo 'Automation pipeline failed. Review Jenkins and Allure results.'
        }
    }
}