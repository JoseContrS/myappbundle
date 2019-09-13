def getColor(buildStatus) {
    def colorCode

    if (buildStatus == "STARTED") {
        colorCode = "#FFFF00"
    } else if (buildStatus == "SUCCESS") {
        colorCode = "#00FF00"
    } else if(buildStatus == "ABORTED" || buildStatus == "STOPPED" || buildStatus == "UNSTABLE") {
        colorCode = "#949393"
    } else {
        colorCode = "#FF0000"
    }

    colorCode
}

def send(String buildStatus = "STARTED") {
    def colorCode = getColor(buildStatus)
    def date = new Date().format("yyyy-MM-dd HH:mm:ss")

    def slackMessage = """
    BUILD $buildStatus:
    ${env.JOB_NAME} [${env.BUILD_NUMBER}]
    BUILD DATE:
    ${date}
    BUILD URL:
    ${env.RUN_DISPLAY_URL}
    GIT BRANCH:
    ${env.GIT_BRANCH}
    GIT AUTHOR:
    ${env.CHANGE_AUTHOR_EMAIL}
    GIT COMMIT:
    ${env.GIT_COMMIT}
    """

    sendSlack(colorCode, slackMessage, "#jenkins", "arquitectura-td", "slack_arquitecturatd_token")
    sendSlack(colorCode, slackMessage, "#app_consultoras", "hubconsultorasbelcorp", "slack_consultoras_token")
    sendSlack(colorCode, slackMessage, "#app_gana_mas", "hubconsultorasbelcorp", "slack_consultoras_token")

    def teamsMessage = """
    <b><font style="font-size: 14px;">Commited by</font></b> <br />
    <font style="font-size: 14px;">@${user}</font> <br /> <br />
    <b><font style="font-size: 14px;">Build Date</font></b> <br />
    <font style="font-size: 14px;">${date}</font> <br /> <br />
    <b><font style="font-size: 14px;">Build Url</font></b> <br />
    <font style="font-size: 14px;">[View on Jenkins](${env.RUN_DISPLAY_URL})</font> <br /> <br />
    <b><font style="font-size: 14px;">Git Branch</font></b> <br />
    <font style="font-size: 14px;">${env.GIT_BRANCH}</font> <br /> <br />
    <b><font style="font-size: 14px;">Git Commit</font></b> <br />
    <font style="font-size: 14px;">${env.GIT_COMMIT}</font> <br />
    """

    // Channel: Gana+
    sendTeams(teamsMessage, buildStatus, colorCode, "https://outlook.office.com/webhook/2a52fc6a-4080-4406-af1c-2d10ca71bf0c@e1fd30ac-0226-49d7-9516-edd8d1e0b18d/JenkinsCI/74fc611ca6a043a48f5ebc751049ea68/c27325bb-5454-4f25-baca-5120fe4d48eb")
}

def sendUrlApk(marca, url, String buildStatus = "STARTED") {
    def colorCode = getColor(buildStatus)

    def slackMessage = "BUILD $buildStatus: Job ${env.JOB_NAME} [${env.BUILD_NUMBER}] - DOWNLOAD $marca URL: $url"
    sendSlack(colorCode, slackMessage, "#jenkins", "arquitectura-td", "slack_arquitecturatd_token")
    sendSlack(colorCode, slackMessage, "#app_consultoras", "hubconsultorasbelcorp", "slack_consultoras_token")
    sendSlack(colorCode, slackMessage, "#app_gana_mas", "hubconsultorasbelcorp", "slack_consultoras_token")

    def teamsMessage = """
    <b><font style="font-size: 14px;">Download ${marca.capitalize()}</font></b> <br />
    <font style="font-size: 14px;">[${url}](${url})</font> <br /> <br />
    """

    // Channel: Gana+
    sendTeams(teamsMessage, buildStatus, colorCode, "https://outlook.office.com/webhook/2a52fc6a-4080-4406-af1c-2d10ca71bf0c@e1fd30ac-0226-49d7-9516-edd8d1e0b18d/JenkinsCI/74fc611ca6a043a48f5ebc751049ea68/c27325bb-5454-4f25-baca-5120fe4d48eb")
}

def sendSlack(color, message, channel, domain, token) {
    slackSend (
        color: color,
        message: message,
        channel: channel,
        teamDomain: domain,
        tokenCredentialId: token
    )
}

def sendTeams(message, status, color, webhookUrl) {
    office365ConnectorSend (
        message: message,
        status: status,
        webhookUrl: webhookUrl,
        color: color
    )
}

def sendEmail(from, buildStatus) {
    emailext(
        from: from,
        subject: "BUILD ${buildStatus}: Job ${env.JOB_NAME} [${env.BUILD_NUMBER}]",
        body: '${JELLY_SCRIPT,template="log"}',
        to: '$DEFAULT_RECIPIENTS',
        attachLog: true,
        compressLog: true
    )
}

return this
