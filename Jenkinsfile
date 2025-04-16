@Library('socrata-pipeline-library@6.1.0') _

commonPipeline(
    jobName: 'image-resize-service',
    language: 'scala',
    projects: [
        [
            name: 'image-resize-service',
            compiled: true,
            deploymentEcosystem: 'ecs',
            paths: [
                dockerBuildContext: 'docker',
            ],
            type: 'service',
        ],
    ],
    teamsChannelWebhookId: 'WORKFLOW_ACCESS_CONTROL_NOTIFICATIONS',
)
