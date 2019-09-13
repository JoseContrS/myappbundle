FROM ruby:latest

LABEL maintainer="Luis Diego <lfdiego7@gmail.com>"

ENV LC_ALL "en_US.UTF-8"
ENV LANGUAGE "en_US.UTF-8"
ENV LANG "en_US.UTF-8"
ENV HOME "/root"

USER root

RUN gem install fastlane -NV

RUN mkdir /app
WORKDIR /app
