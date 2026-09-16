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
        stage('Run Tests') {
            steps {
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
    }
}