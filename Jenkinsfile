@Library('socrata-pipeline-library') _

def dockerize = new com.socrata.Dockerize(steps, 'image-resize-service', BUILD_NUMBER)
def marathon = new com.socrata.MarathonDeploy(steps)

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
    RUNNING_IN_CI = 'true'
    GITHUB_CREDENTIALS_ID = 'a3959698-3d22-43b9-95b1-1957f93e5a11'
    REPOSITORY_NAME = "${env.GIT_URL.tokenize('/')[3].split('\\.')[0]}"
    REPOSITORY_OWNER = "${env.GIT_URL.tokenize('/')[2]}"
    SERVICE_VERSION = '0.0.1'
    ARTIFACTORY_CREDS = credentials('shared-eng-artifactory-creds')
  }

  stages {
    stage('Initialize Job Details') {
      steps {
        script {
          currentBuild.description = "${env.SERVICE} : ${env.GIT_COMMIT[0..5]} : ${env.NODE_NAME}"
        }
      }
    }

    stage('Master') {
      when { branch 'main' }
      stages {
        stage('Build and push image') {
          steps {
            sh 'sbt assembly'

            // NOTE: If you're changing the version of this service, you'll also have to change the version here
            sh 'cp target/scala-2.12/image-resize-service-assembly-0.0.1.jar image-resize-service-assembly.jar'
            sh 'docker build --tag image-resize-service:latest .'
            sh 'rm image-resize-service-assembly.jar'

            script {
              env.DOCKER_TAG = dockerize.push_tagged_image_to_all_repos('image-resize-service:latest', env.SERVICE_VERSION, env.GIT_COMMIT)
            }
          }
          post {
            failure {
              slackSend(channel: "${env.SLACK_CHANNEL}", color: 'RED', message: "${env.SERVICE}: Building and pushing docker image has failed - ${env.BUILD_URL}")
            }
          }
        }
        stage('Deploy to staging') {
          steps {
            // this must match the "RUBY VERSION" in https://github.com/socrata/apps-marathon/blob/main/Gemfile.lock
            withRbenv("2.5.3") {
              script {
                marathon.checkoutAndInstall()
                marathon.deploy(env.SERVICE, 'staging', env.DOCKER_TAG)
              }
            }
          }
          post {
            success {
              slackSend(channel: "${env.SLACK_CHANNEL}", color: 'GREEN', message: "${env.SERVICE} (${env.DOCKER_TAG}): Successfully deployed to staging marathon")
            }
            failure {
              slackSend(channel: "${env.SLACK_CHANNEL}", color: 'RED', message: "${env.SERVICE} (${env.DOCKER_TAG}): Failed deploying to staging marathon - ${env.BUILD_URL}")
            }
          }
        }
      }
    }
  }
}
