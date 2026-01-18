package com.devteria.identity_service.entity;

import lombok.Builder;

@Builder
public record MailBody(String[] to, String subject, String body) {
}
