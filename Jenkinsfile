@Library('socrata-pipeline-library@9.6.1') _

commonPipeline(
    jobName: 'image-resize-service',
    language: 'scala',
    lintCommitMessages: false,
    projects: [
        [
            name: 'image-resize-service',
            compiled: true,
            deploymentEcosystem: 'marathon-mesos',
            paths: [
                dockerBuildContext: 'docker',
            ],
            type: 'service',
        ],
    ],
    teamsChannelWebhookId: 'WORKFLOW_ACCESS_CONTROL_NOTIFICATIONS',
)
