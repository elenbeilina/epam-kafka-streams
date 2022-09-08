docker run --tty \
           --network localtest_default \
           -v /Users/aqua-len/IdeaProjects/epam-kafka-streams/localTest/data/test-data-1.txt:/test-data-1.txt \
           -v /Users/aqua-len/IdeaProjects/epam-kafka-streams/localTest/data/test-data-2.txt:/test-data-2.txt \
           confluentinc/cp-kafkacat \
           bash -c "cat /test-data-1.txt | kafkacat  \
           -b broker:29092 \
            -P -t task1-1 \
            -K:

            cat /test-data-2.txt | kafkacat  \
                       -b broker:29092 \
                        -P -t task2 \
                        -K:"
