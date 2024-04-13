package com.example.personal_project.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MailgunWebhookEvent {
    private String event;
    private String messageId;
    private String recipient;
}
