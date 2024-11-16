#!/bin/bash

apk --no-cache add curl

KAFKA_CONNECT_URL='http://kafka-connect:8083/connectors'

echo -e "\nWaiting for Kafka Connect to start listening on kafka-connect ⏳"
while [ $(curl -s -o /dev/null -w %{http_code} $KAFKA_CONNECT_URL) -eq 000 ] ; do
  echo -e $(date) " Kafka Connect HTTP listener state: "$(curl -s -o /dev/null -w %{http_code} $KAFKA_CONNECT_URL)" (waiting for 200)"
  sleep 5
done
nc -vz kafka-connect 8083

echo -e "\nStart connectors loading"

CONNECTORS=(
  'book.sink.json'
  'book.sink.streaming.json'
  'book.source.json'
  'notification.sink.json'
  'user.sink.dlq-ce-json.json'
  'user.sink.dlq-unprocessed.json'
  'user.sink.json'
  'user.source.json'
  'user.source.streaming.json'
)

for connector in "${CONNECTORS[@]}"
do
  echo -e "\n\nCreating a connector: $connector..."
  while [ $(curl -s -o /dev/null -w %{http_code} -v -H 'Content-Type: application/json' -X POST --data @/usr/connectors/$connector $KAFKA_CONNECT_URL) -ne 201 ] ; do
    echo -e $(date) " repeat loading '$connector'"
    sleep 5
  done
  echo "Connector '$connector' loaded"
done

echo -e "\nAll connectors loaded"
