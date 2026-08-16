package com.sidney.banking.lambda;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

public class SqsEventHandler implements RequestHandler<SQSEvent, Void> {

    private static final String TABLE_NAME =
            "banking-platform-idempotency";

    private static final DynamoDbClient dynamoDb =
            DynamoDbClient.builder()
                    .region(Region.US_EAST_1)
                    .build();

    @Override
    public Void handleRequest(SQSEvent event, Context context) {

        context.getLogger().log(
                "Quantidade de mensagens recebidas: "
                        + event.getRecords().size()
                        + "\n"
        );

        for (SQSEvent.SQSMessage message : event.getRecords()) {

            String eventId = message.getMessageId();

            context.getLogger().log(
                    "Processando eventId: "
                            + eventId
                            + "\n"
            );

            if (!registrarIdempotencia(eventId, message, context)) {

                context.getLogger().log(
                        "Evento duplicado ignorado: "
                                + eventId
                                + "\n"
                );

                continue;
            }

            context.getLogger().log(
                    "Evento recebido do SQS: "
                            + message.getBody()
                            + "\n"
            );

            context.getLogger().log(
                    "Evento processado com sucesso: "
                            + eventId
                            + "\n"
            );
        }

        return null;
    }

    private boolean registrarIdempotencia(
            String eventId,
            SQSEvent.SQSMessage message,
            Context context) {

        long processedAt = Instant.now().getEpochSecond();

        // Expira em 24 horas
        long expiresAt = Instant.now()
                .plusSeconds(86400)
                .getEpochSecond();

        Map<String, AttributeValue> item = new HashMap<>();

        item.put(
                "eventId",
                AttributeValue.builder()
                        .s(eventId)
                        .build()
        );

        item.put(
                "processedAt",
                AttributeValue.builder()
                        .n(String.valueOf(processedAt))
                        .build()
        );

        item.put(
                "expiresAt",
                AttributeValue.builder()
                        .n(String.valueOf(expiresAt))
                        .build()
        );

        item.put(
                "status",
                AttributeValue.builder()
                        .s("PROCESSED")
                        .build()
        );

        PutItemRequest request =
                PutItemRequest.builder()
                        .tableName(TABLE_NAME)
                        .item(item)
                        .conditionExpression(
                                "attribute_not_exists(eventId)"
                        )
                        .build();

        try {

            dynamoDb.putItem(request);

            context.getLogger().log(
                    "Idempotência registrada no DynamoDB: "
                            + eventId
                            + "\n"
            );

            return true;

        } catch (ConditionalCheckFailedException e) {

            return false;
        }
    }
}