package org.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        CrptApi api = new CrptApi(TimeUnit.MINUTES, 5);

        try {
            CrptApi.Document document = new CrptApi.Document();

            CrptApi.Document.Description description = new CrptApi.Document.Description();
            description.setParticipantInn("string");
            document.setDescription(description);
            document.setDoc_id("string");
            document.setDoc_status("string");
            document.setDoc_type("LP_INTRODUCE_GOODS");
            document.setImportRequest(true);
            document.setOwner_inn("string");
            document.setParticipant_inn("string");
            document.setProducer_inn("string");
            document.setProduction_date("2020-01-23");
            document.setProduction_type("string");

            List<CrptApi.Document.Product> products = new ArrayList<>();
            CrptApi.Document.Product product = new CrptApi.Document.Product();
            product.setCertificate_document("string");
            product.setCertificate_document_date("2020-01-23");
            product.setCertificate_document_number("string");
            product.setOwner_inn("string");
            product.setProducer_inn("string");
            product.setProduction_date("2020-01-23");
            product.setTnved_code("string");
            product.setUit_code("string");
            product.setUitu_code("string");
            products.add(product);
            document.setProducts(products);

            document.setReg_date("2020-01-23");
            document.setReg_number("string");

            api.makeDocument(document, "signature");
        } catch (IOException | InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }
}