package com.guangge.Interview.client;

import com.guangge.Interview.assistant.record.InterViewRecord;
import com.guangge.Interview.mail.MailService;
import com.guangge.Interview.services.ResumeService;
import com.guangge.Interview.tools.WrittenTestTools;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.hilla.BrowserCallable;

import java.util.List;


@BrowserCallable
@AnonymousAllowed
public class ClientService {
    private final ResumeService resumeService;
    private final MailService mailService;

    public ClientService(ResumeService resumeService, MailService mailService) {
        this.resumeService = resumeService;
        this.mailService = mailService;
    }


    public List<InterViewRecord> getInterView() {return this.resumeService.getInterViews();}

    public void sendMail(String name) {
        this.mailService.sendMailForAttachment(name);
    }

    public List<InterViewRecord> findInterView(String question) {
        return this.resumeService.findInterViewsByQuestion(question);
    }
}
