package com.guangge.Interview.util;

import com.guangge.Interview.data.Resume;
import com.guangge.Interview.record.InterViewRecord;

public class Dto2Record {
    public static InterViewRecord toInterViewDetails(Resume resume){
        return new InterViewRecord(
                resume.getId().toString(),
                resume.getName(),
                resume.getScore(),
                resume.getInterViewStatus().toString(),
                resume.getEvaluate(),
                resume.getEmail(),
                resume.getMp3Path(),
                resume.getInterviewEvaluate()
        );
    }
}
