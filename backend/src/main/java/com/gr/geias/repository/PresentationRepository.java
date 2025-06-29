package com.gr.geias.repository;

import com.gr.geias.dto.PresentationWithCompanyDTO;
import com.gr.geias.model.Presentation;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface PresentationRepository {
    int insertPresentation(Presentation presentation);

    int checkConflict(@Param("location") String location,
                      @Param("startTime") Date startTime,
                      @Param("endTime") Date endTime);

    List<Presentation> selectByCompanyId(@Param("companyId") Integer companyId);

    List<Presentation> selectAllPresentations(@Param("status") Integer status);

    Presentation selectById(@Param("presentationId") Integer presentationId);

    int checkConflictExcludeSelf(@Param("location") String location,
                                 @Param("startTime") Date startTime,
                                 @Param("endTime") Date endTime,
                                 @Param("excludeId") Integer excludeId);

    int updateStatusRemarkLocation(@Param("presentationId") Integer presentationId,
                                   @Param("status") Integer status,
                                   @Param("remark") String remark,
                                   @Param("location") String location);

    int updateStatusRemark(@Param("presentationId") Integer presentationId,
                           @Param("status") Integer status,
                           @Param("remark") String remark);

    List<PresentationWithCompanyDTO> selectAllWithCompany(@Param("status") Integer status);
}
