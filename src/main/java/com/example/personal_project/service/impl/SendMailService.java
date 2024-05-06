package com.example.personal_project.service.impl;


import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SendMailService {
    @Value("${mailgun.domain.name}")
    private String DOMAIN_NAME;
    @Value("${mailgun.api.key}")
    private String API_KEY;

    //    public JsonNode sendEmail() throws UnirestException
    public JsonNode sendEmail() {
        try {
            String userVariables = "first_name=John&last_name=Smith&my_message_id=123";
            String recipientVariables = "{\"xppp3081@gmail.com\": {\"name\":\"Alice\", \"id\":1}}";
            HttpResponse<JsonNode> request = Unirest
                    .post("https://api.mailgun.net/v3/" + DOMAIN_NAME + "/messages")
                    .basicAuth("api", API_KEY)
                    .queryString("from", "xppp3082@gmail.com")
                    .queryString("to", "xppp3081@gmail.com")
                    .queryString("subject", "API test exception notification.")
                    .queryString("html", "<p>Just a test for ensuring everything is fine.</p>")
                    .queryString("user-variables", recipientVariables)
                    .queryString("v", userVariables)
//                    .queryString("text", "Your API has exceptions during automated testing: ")
                    .asJson();
            log.info(request.getBody().toString());
            return request.getBody();
        } catch (Exception e) {
            log.error("Error on sending mail with mailgun api : " + e.getMessage());
            return null;
        }

    }
}
//                .queryString("html", "<p>Just a test for ensuring everything is fine.</p>")
//                .queryString("text", "Your API has exceptions during automated testing: " + apiUrl + ".")