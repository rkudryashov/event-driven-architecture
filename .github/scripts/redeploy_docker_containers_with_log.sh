#!/bin/bash

echo "------------------------------" >> /tmp/redeployment.log 2>&1
echo "Redeployment with logs started at $(date)" >> /tmp/redeployment.log 2>&1
~/event-driven-architecture/.github/scripts/redeploy_docker_containers.sh >> /tmp/redeployment.log 2>&1
