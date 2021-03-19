FROM openjdk:8
ENV ANDROID_HOME /opt/android-sdk/linux
RUN mkdir -p ${ANDROID_HOME} && cd ${ANDROID_HOME} && wget -q https://dl.google.com/android/repository/sdk-tools-linux-3859397.zip -O android_tools.zip &&unzip android_tools.zip && rm android_tools.zip
RUN yes | /opt/android-sdk/linux/tools/bin/sdkmanager --licenses

COPY ./AREA /app
WORKDIR /app
RUN ["./gradlew", "assembleDebug"]
CMD ["cp", "app/build/outputs/apk/debug/app-debug.apk", "/mobile_app/client.apk"]