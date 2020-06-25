package com.metodi.workforcemanagement.services;

public interface EmailService {

    void sendEmails(String[] to, String requester, String message);
}
