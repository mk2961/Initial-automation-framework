pipeline {
    agent any

    tools {
        maven 'Maven-3'
    }

    triggers {
        cron('H 2 * * *')
    }

    parameters {
        choice(
            name: 'TARGET_ENV',
            choices: ['qa', 'dev', 'prod'],
            description: 'Environment used by config.properties'
        )

        choice(
            name: 'TEST_GROUP',
            choices: ['regression', 'smoke'],
            description: 'JUnit tag group to run'
        )
    }

    options {
        timestamps()
        disableConcurrentBuilds()
    }

    stages {

        stage('Validate Parameters') {
            steps {
                script {
                    if (params.TARGET_ENV == 'prod' &&
                        params.TEST_GROUP != 'smoke') {

                        error(
                            'Production environment only allows smoke tests.'
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

                bat 'mvn -B clean test -Denv=%TARGET_ENV% -Dtest.groups=%TEST_GROUP%'
            }
        }
    }

    post {
        always {
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
