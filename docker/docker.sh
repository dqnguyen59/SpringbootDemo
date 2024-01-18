#!/bin/bash
# @author Copyright (C) 2024  Duy Quoc Nguyen <d.q.nguyen@smartblackbox.org>

set -e

VERSION_MAJOR=1
VERSION_MINOR=0

# De variable $BUILD_NUMBER is automatically set by Jenkins.
# If building from local machine, then set the build number to 0-DEV.
BUILD_NUMBER=${BUILD_NUMBER:-"0-DEV"}
VERSION="${VERSION:-$VERSION_MAJOR.$VERSION_MINOR.$BUILD_NUMBER}"

DEFAULT_CONTAINER="springbootdemo2core"
DEFAULT_IMAGE="springbootdemo2core"
CONTAINER=${CONTAINER:-$DEFAULT_CONTAINER}
IMAGE=${IMAGE:-$DEFAULT_IMAGE}
IMAGE=$CONTAINER:$VERSION
IMAGE_NAME=${CONTAINER}_${VERSION}

DEFAULT_API_PORT=8888

API_PORT=${API_PORT:-$DEFAULT_API_PORT}

DEFAULT_MYSQL_IP_ADDRESS="localhost"
DEFAULT_MYSQL_PORT="3306"

MYSQL_IP_ADDRESS=${MYSQL_IP_ADDRESS:-$DEFAULT_MYSQL_IP_ADDRESS}
MYSQL_PORT=${MYSQL_PORT:-$DEFAULT_MYSQL_PORT}

exec="help"
stop_docker=false

user="pbuser"

while getopts "bhindrsuEqc:e:" option
do
    case "${option}"
        in
        b) exec="build"
        ;;
        h) exec="help"
        ;;
        i) exec="info"
        ;;
        n) exec="normal"
        ;;
        s) exec="shell"
        ;;
        u) exec="unit-test"
        ;;
        E) exec="export"
        ;;
        q) stop_docker=true
        ;;
        d) detach="--detach" # Execute docker and SpringbootDemo in background with --detach
        ;;
        r) user="root"
        ;;
        c) CONTAINER=${OPTARG}
        ;;
        e) entrypoint=${OPTARG}
        ;;
    esac
done

function show_help {
        echo "SpringbootDemo docker"
        echo ""
        echo "Usage: build-steps: -b -> -u -> -n -d -> -t -> -q"
        echo "  -b: build CONTAINER image '$CONTAINER', image: $IMAGE"
        echo "  -n: run container '$CONTAINER' & SpringbootDemo"
        echo "  -s: run container '$CONTAINER' shell only"
        echo "  -q: stop container '$CONTAINER'"
        echo "  -r: run as root"
        echo "  -d: run '$CONTAINER' in the background"
        echo "  -u: unit-test SpringbootDemo inside the container '$CONTAINER'"
        echo "  -t: test SNMP on the running container '$CONTAINER'"
        echo "  -p: test the SpringbootDemo performance inside the container '$CONTAINER'"
        echo "  -c {docker_container_name}: enter custom docker name, default is '$DEFAULT_CONTAINER'"
        echo "  -e {entrypoint}: enter custom entrypoint script file"
        echo "  -i: show info"
        echo "  -h: this help"
        echo ""
}

function show_info {
    echo "Docker-container: $CONTAINER"
    echo "Docker-image: $IMAGE"
    echo ""
    echo "Docker-port-mapping:"
    echo "  - API_PORT       = $API_PORT <-> $DEFAULT_API_PORT"
}

function build_image {
    echo "Docker build image: $IMAGE"

    # --progress=plain \

    docker build \
        --build-arg VERSION="$VERSION" \
        --no-cache \
        --tag $IMAGE \
        --file ./Dockerfile ..
}

function delete_container {
    # Make sure the container is deleted, if it exists.
    echo "Deleting container: $CONTAINER"
    echo "docker rm -f $CONTAINER >/dev/null 2>&1"
    docker rm -f $CONTAINER >/dev/null 2>&1
}

# Before stopping docker container/image, make sure changes in docker container/image is saved.
function save_docker_state {
    echo "Saving docker image: '$1'"
    docker commit $(docker ps -aql) $1
    echo "Docker image saved: '$1'"
}

function is_docker_running {
    tag=$( docker ps --filter "name=$1" | tail -n 1 )

    if [[ "$tag" == *"$1"* ]]; then
        return
    fi
    false
}

function check_springbootdemo_is_running {
    cnt=0
    found=false
    timeout=2

    echo "Starting timeout for $timeout seconds"

    while [[ "$found" == false ]] && [ $cnt -le $timeout ]
    do
        tag=$( docker ps --filter "name=$CONTAINER" | tail -n 1 )

        if [[ "$tag" == *"$CONTAINER"* ]]; then
            found=true
            result="SpringbootDemo found and running"
            return
        else
            result="SpringbootDemo not active yet!"
        fi

        echo "$result; Timeout passed: $cnt"

        if [ $found == false ]; then
            sleep 1
        fi
        cnt=$((cnt+1))
    done
    false
}

function test_snmp {
    ip_address=""

    if $check_springbootdemo_is_running; then
        # If global environment IP_ADDRESS variable is not set, then get it from docker container.
        ip_address=`docker inspect --format '{{ .NetworkSettings.IPAddress }}' $CONTAINER`
        #ip_address=${IP_ADDRESS:-`docker inspect --format '{{ .NetworkSettings.IPAddress }}' $CONTAINER`}

        if [ -z $ip_address ]; then
            echo "Container '$CONTAINER' ip_address: '$ip_address'"
            exit 1
        fi

        echo "Container '$CONTAINER' ip_address: '$ip_address'"

        cd ../packages/core/tests

        IP_ADDRESS=$ip_address API_PORT=$API_PORT ./test_mib.sh
        echo "Test SNMP Ready"

    else
        echo "Error: SpringbootDemo is not started or docker is not active!"
        exit 2
    fi

}

function export_docker {
    echo "Export docker image: '$IMAGE' to $NAME.gz"
    docker save $IMAGE | gzip > $IMAGE_NAME.gz
}

if [ $stop_docker == true ]; then
    if (is_docker_running $CONTAINER); then
        save_docker_state $IMAGE
        docker kill $CONTAINER || true
        echo "Docker container '$CONTAINER' stopped!"
    else
        echo "Docker container '$CONTAINER' already stopped!"
    fi
    exit 0
fi

case "$exec"
    in
    "build")
        build_image
    ;;
    "normal")
        entrypoint=${entrypoint:-run.sh}
    ;;
    "shell")
        entrypoint=${entrypoint:-/bin/bash}
        interactive="--interactive"
    ;;
    "unit-test")
        entrypoint=${entrypoint:-test.sh}
    ;;
    "export")
        export_docker
    ;;
    "help")
        show_help
    ;;
    "info")
        show_info
    ;;
esac

if [ -n "$entrypoint" ]; then
    delete_container

    echo "Running $CONTAINER using $IMAGE"

    # $API_PORT can be used to map outside port to the internal port.
    # $MONITOR_PORT can be used to map outside port to the internal port.
    # These variable are set by build.job.

    echo "Docker-port-mapping: API_PORT       = $API_PORT <-> $DEFAULT_API_PORT"
    echo ""
    echo "MYSQL_IP_ADDRESS:    $MYSQL_IP_ADDRESS"
    echo "MYSQL_PORT:          $MYSQL_PORT"
    echo ""

    echo "
    docker run
        --name $CONTAINER
        --tty
        -u $user
        $detach
        $interactive
        --entrypoint=$entrypoint
        -p $API_PORT:$DEFAULT_API_PORT
        -e MYSQL_IP_ADDRESS=$MYSQL_IP_ADDRESS
        -e MYSQL_PORT=$MYSQL_PORT
        $IMAGE
    "

    docker run \
        --name $CONTAINER \
        --tty \
        -u $user \
        $detach \
        $interactive \
        --entrypoint=$entrypoint \
        -p $API_PORT:$DEFAULT_API_PORT \
        -e MYSQL_IP_ADDRESS=$MYSQL_IP_ADDRESS \
        -e MYSQL_PORT=$MYSQL_PORT \
        $IMAGE

    if [ -n "$interactive" ]; then
        save_docker_state $IMAGE
        
        delete_container
    fi
fi
