package com.example.personal_project.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MailHook {
    private String recipientMail;
    private String subject;
    private String mimeID;
    private String deliveryStatus;
}
