#!/bin/bash
# @author Copyright (C) 2024  Duy Quoc Nguyen <d.q.nguyen@smartblackbox.org>

# https://hub.docker.com/_/mysql/tags
VERSION="8.0"
DEFAULT_CONTAINER="mysql8"
DEFAULT_IMAGE="mysql"
CONTAINER=${CONTAINER:-$DEFAULT_CONTAINER}
IMAGE=${IMAGE:-$DEFAULT_IMAGE}
IMAGE="$DEFAULT_IMAGE:$VERSION"
IMAGE_NAME=${IMAGE}_${VERSION}

DEFAULT_DB_PORT=3306

DB_PORT=${DB_PORT:-$DEFAULT_DB_PORT}

exec="help"
stop_docker=false

user="pbuser"

while getopts "bhindsqc:e:" option
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
        q) stop_docker=true
        ;;
        d) detach="--detach" # Execute docker and MySQL in background with --detach
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
        echo "Usage: build-steps: -b -> -n -d -> -q"
        echo "  -b: build CONTAINER image '$CONTAINER', image: $IMAGE"
        echo "  -s: run container '$CONTAINER' shell only"
        echo "  -q: stop container '$CONTAINER'"
        echo "  -d: run '$CONTAINER' in the background"
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
    echo "  - DB_PORT       = $DB_PORT <-> $DEFAULT_DB_PORT"
}

function build_image {
    echo "Docker pull image: $IMAGE"

    docker pull $IMAGE

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

function check_docker_is_running {
    cnt=0
    found=false
    timeout=2

    echo "Starting timeout for $timeout seconds"

    while [[ "$found" == false ]] && [ $cnt -le $timeout ]
    do
        tag=$( docker ps --filter "name=$CONTAINER" | tail -n 1 )

        if [[ "$tag" == *"$CONTAINER"* ]]; then
            found=true
            result="Docker found and running"
            return
        else
            result="Docker not active yet!"
        fi

        echo "$result; Timeout passed: $cnt"

        if [ $found == false ]; then
            sleep 1
        fi
        cnt=$((cnt+1))
    done
    false
}

if [ $stop_docker == true ]; then
    if (is_docker_running $CONTAINER); then
        save_docker_state $IMAGE
        docker kill $CONTAINER || true
        echo "Docker container '$CONTAINER' stopped!"
    else
        echo "Docker container '$CONTAINER' already stopped!"
    fi
    delete_container
    exit 0
fi

case "$exec"
    in
    "build")
        build_image
        
        exit 0
    ;;
    "shell")
        entrypoint=${entrypoint:-/bin/bash}
        interactive="--interactive"
    ;;
    "help")
        show_help
    ;;
    "info")
        show_info
    ;;
esac

echo "$exec"

if [ "$exec" == "normal" ]; then

    delete_container
    
    echo "Running $CONTAINER using $IMAGE"
    
    # $DB_PORT can be used to map outside port to the internal port.
    # These variable are set by build.job.
    
    echo "Docker-port-mapping: DB_PORT       = $DB_PORT <-> $DEFAULT_DB_PORT"
    echo ""
    
    # TODO: need to change MYSQL_ROOT_PASSWORD password !!!
    password="super"
    
    echo "docker run --name $CONTAINER $detach $entrypoint -p $DB_PORT:$DEFAULT_DB_PORT -e MYSQL_ROOT_PASSWORD=$password $IMAGE"
    
    echo "Starting MySQL server may take a while, please wait 15 seconds then nmap localhost for port $DB_PORT"

    docker run \
        --name $CONTAINER \
        $detach \
        $entrypoint \
        -p $DB_PORT:$DEFAULT_DB_PORT \
        -e MYSQL_ROOT_PASSWORD=$password \
        $IMAGE
    
    if [ -z $detach ]; then
        save_docker_state $IMAGE
        
        delete_container
    fi

fi

