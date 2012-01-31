#!/bin/bash
java -Xmx512M -jar `dirname $0`/project/sbt-launch.jar "$@"
