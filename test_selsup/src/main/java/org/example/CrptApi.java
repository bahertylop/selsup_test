package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.Setter;

import javax.naming.InsufficientResourcesException;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class CrptApi {

    private final long timeInterval;
    private final int requestLimit;

    private final Gson gson;
    private final HttpClient httpClient;

    private final BlockingQueue<Instant> timesOfRequests;

    private final Object synchObject = new Object();

    public CrptApi(TimeUnit timeUnit, int requestLimit) {
        this.timeInterval = timeUnit.toMillis(1);
        this.requestLimit = requestLimit;

        this.gson = new GsonBuilder().create();
        this.httpClient = HttpClient.newHttpClient();

        this.timesOfRequests = new LinkedBlockingQueue<>();
    }

    private boolean canMakeRequest() {
        Instant timeNow = Instant.now();
        Instant maxEarlyRequest = timeNow.minusMillis(timeInterval + 1);

        timesOfRequests.removeIf(time -> time.isBefore(maxEarlyRequest));
        return timesOfRequests.size() < requestLimit;
    }

    public void makeDocument(Document document, String signature) throws IOException, InterruptedException {
        synchronized (synchObject) {
            while (true) {
                if (canMakeRequest()) {
                    timesOfRequests.add(Instant.now());
                    break;
                } else {
                    try {
                        Thread.sleep(timeInterval);
                    } catch (InterruptedException e) {

                    }

                }
            }
        }

        String documentJson = gson.toJson(document);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://ismp.crpt.ru/api/v3/lk/documents/create"))
                .header("Content-Type", "application/json")
                .header("Signature", signature)
                .POST(HttpRequest.BodyPublishers.ofString(documentJson))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Error, request status code: " + response.statusCode() + " Time: " + Instant.now());
        }

        System.out.println("Response: " + response.body() + " Time: " + Instant.now());
    }

    @Setter
    @Getter
    public static class Document {
        private Description description;
        private String doc_id;
        private String doc_status;
        private String doc_type;
        private boolean importRequest;
        private String owner_inn;
        private String participant_inn;
        private String producer_inn;
        private String production_date;
        private String production_type;
        private List<Product> products;
        private String reg_date;
        private String reg_number;

        @Getter
        @Setter
        public static class Description {
            private String participantInn;
        }

        @Getter
        @Setter
        public static class Product {
            private String certificate_document;
            private String certificate_document_date;
            private String certificate_document_number;
            private String owner_inn;
            private String producer_inn;
            private String production_date;
            private String tnved_code;
            private String uit_code;
            private String uitu_code;
        }
    }
}
