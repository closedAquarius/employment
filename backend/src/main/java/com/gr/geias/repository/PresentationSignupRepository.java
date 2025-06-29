package com.gr.geias.repository;

import com.gr.geias.dto.PresentationWithCompanyDTO;
import com.gr.geias.model.Presentation;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface PresentationSignupRepository {

    Presentation selectPresentationById(@Param("presentationId") Integer presentationId);

    Integer checkAlreadySigned(@Param("presentationId") Integer presentationId,
                               @Param("studentId") Integer studentId);

    int insertSignup(@Param("presentationId") Integer presentationId,
                     @Param("studentId") Integer studentId);

    int incrementSignupCount(@Param("presentationId") Integer presentationId);

    List<PresentationWithCompanyDTO> getSignedPresentationsByStudent(@Param("studentId") Integer studentId);

    List<PresentationWithCompanyDTO> getUnSignedPresentationsByStudent(@Param("studentId") Integer studentId);

    int deleteByStudentAndPresentation(@Param("studentId") Integer studentId, @Param("presentationId") Integer presentationId);

    void decrementSignupCount(@Param("presentationId") Integer presentationId);

    List<Map<String, Object>> countBySpecialty(@Param("presentationId") Integer presentationId);
    List<Map<String, Object>> countByClass(@Param("presentationId") Integer presentationId);
}
