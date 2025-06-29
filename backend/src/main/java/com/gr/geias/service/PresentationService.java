package com.gr.geias.service;

import com.gr.geias.dto.PresentationWithCompanyDTO;
import com.gr.geias.model.Presentation;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PresentationService {
    boolean applyPresentation(Presentation presentation);

    List<Presentation> getPresentationsByCompanyId(Integer companyId);

    List<Presentation> getAllPresentations(Integer status);

    boolean approvePresentation(Integer presentationId, String remark, String location);

    boolean rejectPresentation(Integer presentationId, String remark);

    List<PresentationWithCompanyDTO> getAllPresentationsWithCompany(Integer status);
}
