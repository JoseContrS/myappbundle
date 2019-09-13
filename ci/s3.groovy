def upload(dir, buildType) {
  withAWS(credentials: "aws_apps_credentials", region: "us-west-2") {
    def date = new Date().format("yyyyMMdd")
    s3Upload(bucket: "belcorp-apps/${dir}/esika/${buildType}/${date}", includePathPattern: "**/*.aab", workingDir: "app/build/outputs/bundle/esika${buildType.capitalize()}", acl:"PublicRead")
    s3Upload(bucket: "belcorp-apps/${dir}/lbel/${buildType}/${date}", includePathPattern: "**/*.aab", workingDir: "app/build/outputs/bundle/lbel${buildType.capitalize()}", acl:"PublicRead")

    s3Upload(bucket: "belcorp-apps/${dir}/esika/${buildType}/${date}", includePathPattern: "**/mapping.txt", workingDir: "app/build/outputs/mapping/esika", acl:"PublicRead")
    s3Upload(bucket: "belcorp-apps/${dir}/lbel/${buildType}/${date}", includePathPattern: "**/mapping.txt", workingDir: "app/build/outputs/mapping/lbel", acl:"PublicRead")
  }
}

def download(dir, buildType, path) {
  withAWS(credentials: "aws_apps_credentials", region: "us-west-2") {
    def date = new Date().format("yyyyMMdd")
    s3Download(file:"${path}/esika/", bucket:"belcorp-apps", path:"${dir}/esika/${buildType}/${date}/", force:true)
    s3Download(file:"${path}/lbel/", bucket:"belcorp-apps", path:"${dir}/lbel/${buildType}/${date}/", force:true)
  }
}

def files(dir, buildType) {
  def arrFiles

  withAWS(credentials: "aws_apps_credentials", region: "us-west-2") {
    def date = new Date().format("yyyyMMdd")
    arrFiles = s3FindFiles(bucket:"belcorp-apps/${dir}/${buildType}/${date}", glob:"*.aab")
  }

  arrFiles
}

return this
