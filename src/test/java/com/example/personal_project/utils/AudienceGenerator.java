package com.example.personal_project.utils;

import com.example.personal_project.model.Audience;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.List;

public class AudienceGenerator {

    public static final String AUDIENCES_JSON = """
            {
                "data": [
                    {
                        "id": 3,
                        "name": "Bob Johnson",
                        "email": "xppp3082@arch.nctu.edu.tw",
                        "birthday": "2007-09-23",
                        "mailcount": 90,
                        "opencount": 70,
                        "clickcount": 16,
                        "company_id": 11,
                        "tags": [
                            {
                                "id": 4,
                                "name": "Customer",
                                "company_id": 11
                            },
                            {
                                "id": 100,
                                "name": "母親節",
                                "company_id": 11
                            }
                        ]
                    },
                    {
                        "id": 2,
                        "name": "Jane Smith",
                        "email": "xppp3082@gmail.com",
                        "birthday": "1995-05-15",
                        "mailcount": 90,
                        "opencount": 57,
                        "clickcount": 12,
                        "company_id": 11,
                        "tags": [
                            {
                                "id": 4,
                                "name": "Customer",
                                "company_id": 11
                            },
                            {
                                "id": 5,
                                "name": "KOL",
                                "company_id": 11
                            },
                            {
                                "id": 100,
                                "name": "母親節",
                                "company_id": 11
                            }
                        ]
                    }
                ]
            }
            """;

    public static List<Audience> getMockAudiences() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(new ClassPathResource("./test/audience.json").getFile(), new TypeReference<List<Audience>>() {
        });
    }
}
