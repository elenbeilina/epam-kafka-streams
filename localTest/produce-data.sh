docker run --tty \
           --network localtest_default \
           -v /Users/aqua-len/IdeaProjects/epam-kafka-streams/localTest/data/test-data.txt:/test-data.txt \
           confluentinc/cp-kafkacat \
           bash -c "cat /test-data.txt | kafkacat  \
           -b broker:29092 \
            -P -t main \
            -K:"
