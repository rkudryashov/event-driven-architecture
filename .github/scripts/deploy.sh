#!/bin/bash

chmod +x ./init_vm.sh ./redeploy_docker_containers.sh ./redeploy_docker_containers_with_log.sh

if ! [ -x "$(command -v docker)" ]; then
  ./init_vm.sh
fi

echo "Remove existing cron jobs to avoid interfering with the next Docker Compose redeployment"
crontab -r

./redeploy_docker_containers.sh

# the cron job below is a workaround to prevent a VM from becoming unresponsive a few hours after redeployment
# maybe the problem is that after some time docker resources (volumes) eat up all the disk space
echo "Add cron job to redeploy project each six hours"
touch tmp_file
echo "0 0,6,12,18 * * * ~/event-driven-architecture/.github/scripts/redeploy_docker_containers_with_log.sh" > tmp_file
crontab tmp_file
rm tmp_file
