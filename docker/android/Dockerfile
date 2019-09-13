FROM openjdk:8
LABEL maintainer="Luis Diego <lfdiego7@gmail.com>"

ENV VERSION_SDK_TOOLS "4333796"
ENV VERSION_BUILD_TOOLS "27.0.3"
ENV VERSION_TARGET_SDK "27"
ENV ANDROID_HOME "/sdk"
ENV GRADLE_CACHE "/.gradle"
ENV PATH "$PATH:${ANDROID_HOME}/tools:${ANDROID_HOME}/tools/bin:${ANDROID_HOME}/platform-tools"

# Descargar Android SDK
RUN mkdir "$ANDROID_HOME" .android && \
    mkdir $HOME/.android && \
    touch $HOME/.android/repositories.cfg && \
    cd "$ANDROID_HOME" && \
    curl -o sdk.zip "https://dl.google.com/android/repository/sdk-tools-linux-${VERSION_SDK_TOOLS}.zip" && \
    unzip sdk.zip && \
    rm sdk.zip

# Aceptar Licensias
RUN yes | $ANDROID_HOME/tools/bin/sdkmanager --licenses
RUN yes | $ANDROID_HOME/tools/bin/sdkmanager "extras;android;m2repository" \
    "extras;google;google_play_services" \
    "extras;google;m2repository" \
    "extras;google;instantapps"

# Instalar Android Build Tools y Librerias
RUN $ANDROID_HOME/tools/bin/sdkmanager --update
RUN $ANDROID_HOME/tools/bin/sdkmanager "build-tools;${VERSION_BUILD_TOOLS}" \
    "platforms;android-${VERSION_TARGET_SDK}" \
    "platform-tools" \
    "tools"
RUN $ANDROID_HOME/tools/bin/sdkmanager "extras;android;m2repository" \
    "extras;google;google_play_services" \
    "extras;google;m2repository" \
    "extras;google;instantapps"

# Crear volumenes
VOLUME [ "${ANDROID_HOME}", "${GRADLE_CACHE}" ]

# Crear directorio principal
RUN mkdir /app
WORKDIR /app
