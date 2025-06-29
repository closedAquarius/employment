package com.gr.geias.service.impl;

import com.gr.geias.dto.PresentationWithCompanyDTO;
import com.gr.geias.model.Presentation;
import com.gr.geias.repository.PresentationSignupRepository;
import com.gr.geias.service.PresentationSignupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class PresentationSignupServiceImpl implements PresentationSignupService {

    @Autowired
    private PresentationSignupRepository presentationSignupMapper;

    @Override
    public boolean signupPresentation(Integer presentationId, Integer studentId) {
        Presentation p = presentationSignupMapper.selectPresentationById(presentationId);
        if (p == null || !Objects.equals(p.getStatus(), 1)) {
            // 不存在或未审核通过
            return false;
        }

        // 是否已报名
        if (presentationSignupMapper.checkAlreadySigned(presentationId, studentId) > 0) {
            return false;
        }

        // 是否已满
        if (p.getSignupCount() >= p.getCapacity()) {
            return false;
        }

        // 插入报名记录
        int inserted = presentationSignupMapper.insertSignup(presentationId, studentId);
        if (inserted <= 0) {
            return false;
        }

        // 增加报名数
        return presentationSignupMapper.incrementSignupCount(presentationId) > 0;
    }

    @Override
    public List<PresentationWithCompanyDTO> getSignedPresentations(Integer studentId) {
        return presentationSignupMapper.getSignedPresentationsByStudent(studentId);
    }

    @Override
    public List<PresentationWithCompanyDTO> getUnSignedPresentations(Integer studentId) {
        return presentationSignupMapper.getUnSignedPresentationsByStudent(studentId);
    }

    @Override
    public boolean cancelSignup(Integer studentId, Integer presentationId) {
        // 先删除报名表中的记录
        int deleted = presentationSignupMapper.deleteByStudentAndPresentation(studentId, presentationId);
        if (deleted > 0) {
            // 成功删除后，更新宣讲会报名人数-1
            presentationSignupMapper.decrementSignupCount(presentationId);
            return true;
        }
        return false;
    }

    @Override
    public List<Map<String, Object>> getSpecialtyDistribution(Integer presentationId) {
        return presentationSignupMapper.countBySpecialty(presentationId);
    }

    @Override
    public List<Map<String, Object>> getClassDistribution(Integer presentationId) {
        return presentationSignupMapper.countByClass(presentationId);
    }
}
