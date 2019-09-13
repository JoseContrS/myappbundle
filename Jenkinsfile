pipeline {
    agent {
        label 'master'
    }

    environment {
        BUILD_TYPE = buildType()
    }

    stages {
        stage("Checkout") {
            agent {
                label 'ec2-linux-spot-slave'
            }
            steps {
                script {
                    notifyBuild()
                    config()
                }
            }
        }

        stage("Build") {
            agent {
                label "ec2-linux-spot-slave"
            }
            steps {
                script {
                    withAndroid {
                        sh "./gradlew bundle${BUILD_TYPE.capitalize()}"
                        upload()
                    }
                }
            }
        }

        stage("Test") {
            when {
                expression { isDevelop() }
            }
            agent {
                label "ec2-linux-spot-slave"
            }
            steps {
                echo "Test"
            }
        }

        stage("Deploy") {
            agent {
                label "ec2-linux-spot-slave"
            }
            steps {
                script {
                    download()
                    def (nameEsika, apkEsika, mappingEsika) = filesEsikaBundle()
                    def (nameLBel, apkLBel, mappingLBel) = filesLBelBundle()



                    withFastlane {
                        if (isMaster()) {
                            //sh "fastlane android release marca:'esika' apk:'$apkEsika' mapping:'$mappingEsika'"
                            //sh "fastlane android release marca:'lbel' apk:'$apkLBel' mapping:'$mappingLBel'"
                            sh "fastlane android release marca:'esika' aab:'$apkEsika' mapping:'$mappingEsika'"
                            sh "fastlane android release marca:'lbel' aab:'$apkLBel' mapping:'$mappingLBel'"
                        }
                        else if (isStage()) {
                            //sh "fastlane android beta marca:'esika' apk:'$apkEsika' mapping:'$mappingEsika'"
                            //sh "fastlane android beta marca:'lbel' apk:'$apkLBel' mapping:'$mappingLBel'"
                            sh "fastlane android beta marca:'esika' aab:'$apkEsika' mapping:'$mappingEsika'"
                            sh "fastlane android beta marca:'lbel' aab:'$apkLBel' mapping:'$mappingLBel'"
                        } else if (isDevelop()) {
                            //sh "fastlane android internal marca:'esika' apk:'$apkEsika' mapping:'$mappingEsika'"
                            //sh "fastlane android internal marca:'lbel' apk:'$apkLBel' mapping:'$mappingLBel'"
                            sh "fastlane android internal marca:'esika' aab:'$apkEsika' mapping:'$mappingEsika'"
                            sh "fastlane android internal marca:'lbel' aab:'$apkLBel' mapping:'$mappingLBel'"

                        }
                        notifyUrl("esika", nameEsika)
                        notifyUrl("lbel", nameLBel)
                    }
                }
            }
        }
    }

    post {
        success {
            script {
                notifyBuild("SUCCESS")
                deleteDir()
            }
        }

        aborted {
            script {
                notifyBuild("ABORTED")
                deleteDir()
            }
        }

        failure {
            script {
                notifyBuild("FAILED")
                deleteDir()
            }
        }

        unstable {
            script {
                notifyBuild("UNSTABLE")
                deleteDir()
            }
        }
    }
}

/* ──────────────────────────────────────────────────────── */

def isMaster() {
    return env.BRANCH_NAME == "master"
}

def isStage() {
    return env.BRANCH_NAME.contains("release")
}

def isDevelop() {
    return env.BRANCH_NAME == "develop"
}

def isReview() {
    return env.BRANCH_NAME.contains("review")
}

def isIntegration() {
    return env.BRANCH_NAME.contains("integracion")
}

def buildType() {
    def branch = env.BRANCH_NAME
    def name

    if (isMaster())
        name = "release"
    else if (isStage())
        name = "stage"
    else if (isDevelop())
        name = "develop"
    else if (isIntegration())
        name = "develop"
    else
        name = "review"

    name
}

def config() {
    def CONFIG_URL = "https://s3-us-west-2.amazonaws.com/belcorp-apps/keys"
    def CONFIG_PROP = "key.properties"
    def CONFIG_KEY = "release.jks"

    sh "chmod +x ./gradlew"
    sh "mkdir -p config"
    sh "curl -o config/$CONFIG_PROP $CONFIG_URL/release/$CONFIG_PROP"
    sh "curl -o config/$CONFIG_KEY $CONFIG_URL/release/$CONFIG_KEY"
    sh "curl -o config/google_play.json $CONFIG_URL/google_play.json"
}

def library() {
    sh "rm -rf ../BelcorpLibrary-Android"

    withCredentials([usernamePassword(credentialsId: "github_beltdfabrica_credentials", usernameVariable: "USERNAME", passwordVariable: "PASSWORD")]) {
        sh 'git clone -b qas_consultoras https://${USERNAME}:${PASSWORD}@github.com/TDFabrica/BelcorpLibrary-Android.git ../BelcorpLibrary-Android'
    }
}

def withAndroid(unit) {
  def image = docker.build("consultoras/android", "./docker/android")

  image.inside("-u 0") {
    withEnv(["CI=true"]) {
        config()
        library()

        sh "./gradlew clean"
        unit()
    }
  }
}

def withFastlane(unit) {
  def image = docker.build("consultoras/fastlane", "./docker/fastlane")

  image.inside("-u 0") {
    withEnv(["CI=true"]) {
        config()
        library()

        unit()
    }
  }
}

/* ──────────────────────────────────────────────────────── */

def notifyBuild(String buildStatus = "STARTED") {
  def not = load "ci/notify.groovy"
  not.send(buildStatus)
}

def notifyUrl(marca, app) {
  def not = load "ci/notify.groovy"
  def BUILD_TYPE = buildType()
  def date = new Date().format("yyyyMMdd")
  def url = "https://s3-us-west-2.amazonaws.com/belcorp-apps/Consultoras/$marca/$BUILD_TYPE/$date/$BUILD_TYPE/$app"

  not.sendUrlApk(marca, url, "SUCCESS")
}

def upload() {
  def s3 = load "ci/s3.groovy"
  def BUILD_TYPE = buildType()
  s3.upload("Consultoras", BUILD_TYPE)
}

def download() {
  def s3 = load "ci/s3.groovy"
  def BUILD_TYPE = buildType()
  s3.download("Consultoras", BUILD_TYPE, "tmp")
}

def filesEsika() {
  def apks = findFiles(glob: "tmp/esika/**/*.apk")
  def mappings = findFiles(glob: "tmp/esika/**/mapping.txt")
  def x = apks.length - 1
  return [apks[x].name, apks[x].path, mappings[0].path]
}

def filesEsikaBundle() {
  def aab = findFiles(glob: "tmp/esika/**/*.aab")
  def mappings = findFiles(glob: "tmp/esika/**/mapping.txt")
  def x = aab.length - 1
  return [aab[x].name, aab[x].path, mappings[0].path]
}


def filesLBel() {
  def apks = findFiles(glob: "tmp/lbel/**/*.apk")
  def mappings = findFiles(glob: "tmp/lbel/**/mapping.txt")
  def x = apks.length - 1
  return [apks[x].name, apks[x].path, mappings[0].path]
}

def filesLBelBundle() {
  def aab = findFiles(glob: "tmp/lbel/**/*.aab")
  def mappings = findFiles(glob: "tmp/lbel/**/mapping.txt")
  def x = aab.length - 1
  return [aab[x].name, aab[x].path, mappings[0].path]
}

