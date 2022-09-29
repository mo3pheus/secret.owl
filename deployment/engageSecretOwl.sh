#!/bin/bash

source /etc/environment

echo "Fly secret-owl - take our message to its destination!"
java -jar /home/sanket/Documents/workspace/secret.owl/target/secret-owl-1.0-shaded.jar \
 --log.file.path /home/sanket/Documents/workspace/secret.owl/executionLogs/ \
 --destination.address tcp://3.219.5.247:1883 \
 --addressee griffindor/green \
 --secret.owl.sender.name yashraj \
 --secret.owl.sender.password y@5hr@j_h0gw@rt5_blu3 \
 --secret.owl.message /home/sanket/Documents/workspace/secret.owl/src/main/resources/message.txt

