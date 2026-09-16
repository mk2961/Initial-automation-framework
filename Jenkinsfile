pipeline {
    agent any
    
    tools {
        maven 'Maven-3'
    }

    parameters {
        choice(
            name: 'TARGET_ENV',
            choices: ['dev', 'qa', 'prod'],
            description: 'Environment used by config.properties'
        )
            choice(
            name: 'TEST_GROUP',
            choices: ['smoke', 'regression'],
            description: 'JUnit tag group to run'
        )
    }

    options {
        timestamps()
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Run Tests') {
            steps {
               bat 'mvn -B clean test -Denv=%TARGET_ENV% -Dtest.groups=%TEST_GROUP%'
            }
        }
    }

    post {
        always {
            allure([
                results: [[path: 'target/allure-results']]
            ])
        }
    }
}