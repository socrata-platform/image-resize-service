@Library('socrata-pipeline-library')

def dockerize = new com.socrata.Dockerize(steps, 'image-resize-service', BUILD_NUMBER)

def lastStage

pipeline {
  options {
    timeout(time: 60, unit: 'MINUTES')
    ansiColor('xterm')
  }
  parameters {
    string(name: 'AGENT', defaultValue: 'build-worker', description: 'Which build agent to use?')
  }
  agent {
    label params.AGENT
  }
  triggers {
    issueCommentTrigger('^retest$')
  }
  environment {
    SERVICE = 'image-resize-service'
    SERVICE_VERSION = '0.0.1'
    WEBHOOK_ID = 'WEBHOOK_ACCESS_CONTROL_Q_AND_A'
  }
  stages {
    stage('Pull Request') {
      when { changeRequest() }
      steps {
        sh 'sbt assembly'
      }
    }
    stage('Build') {
      when { branch 'main' }
      stages {
        stage('Build and push image') {
          steps {
            script {
              lastStage = env.STAGE_NAME
              sh 'sbt assembly'

              // NOTE: If you're changing the version of this service, you'll also have to change the version here
              sh 'cp target/scala-2.12/image-resize-service-assembly-0.0.1.jar image-resize-service-assembly.jar'
              sh 'docker build --tag image-resize-service:latest .'
              sh 'rm image-resize-service-assembly.jar'

              env.DOCKER_TAG = dockerize.push_tagged_image_to_all_repos('image-resize-service:latest', env.SERVICE_VERSION, env.GIT_COMMIT)
              currentBuild.description = env.DOCKER_TAG
            }
          }
        }
        stage('Deploy to staging') {
          steps {
            script {
              lastStage = env.STAGE_NAME
              // Deploys env.SERVICE with tag env.DOCKER_TAG to staging by default
              marathonDeploy()
            }
          }
        }
      }
      post {
        failure {
          script {
            teamsMessage(message: "Build [${currentBuild.fullDisplayName}](${env.BUILD_URL}) has failed in stage ${lastStage}", webhookCredentialID: WEBHOOK_ID)
          }
        }
      }
    }
  }
}
