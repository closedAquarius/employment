package com.gr.geias.service;

import com.gr.geias.dto.PresentationWithCompanyDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface PresentationSignupService {
    boolean signupPresentation(Integer presentationId, Integer studentId);

    List<PresentationWithCompanyDTO> getSignedPresentations(Integer studentId);
    List<PresentationWithCompanyDTO> getUnSignedPresentations(Integer studentId);
    boolean cancelSignup(Integer studentId, Integer presentationId);

    List<Map<String, Object>> getSpecialtyDistribution(Integer presentationId);
    List<Map<String, Object>> getClassDistribution(Integer presentationId);
}
