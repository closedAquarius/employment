package com.guangge.Interview.util;

import com.guangge.Interview.data.Candidates;
import com.guangge.Interview.data.Resume;
import com.guangge.Interview.record.CandidateRecord;
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

    public static CandidateRecord toCandidateRecord(Candidates candidates) {
        return new CandidateRecord(
                candidates.getId(),
                candidates.getName(),
                candidates.getCv(),
                candidates.getEmail(),
                candidates.getBirth(),
                candidates.getStatus().toString(),
                candidates.getPictureUrl());
    }
}
