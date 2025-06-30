FROM ubuntu:latest
LABEL authors="swc"

ENTRYPOINT ["top", "-b"]
