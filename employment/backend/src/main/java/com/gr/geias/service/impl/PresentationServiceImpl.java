package com.gr.geias.service.impl;

import com.gr.geias.dto.PresentationWithCompanyDTO;
import com.gr.geias.model.Presentation;
import com.gr.geias.repository.PresentationRepository;
import com.gr.geias.service.PresentationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class PresentationServiceImpl implements PresentationService {

    @Autowired
    private PresentationRepository presentationMapper;

    @Override
    public boolean applyPresentation(Presentation presentation) {

        // 设置默认状态和时间
        presentation.setStatus(0); // 0 = 待审批
        presentation.setCreateTime(new Date());
        presentation.setUpdateTime(new Date());
        presentation.setSignupCount(0); // 初始报名人数为0

        // 插入数据库
        return presentationMapper.insertPresentation(presentation) > 0;
    }

    @Override
    public List<Presentation> getPresentationsByCompanyId(Integer companyId) {
        return presentationMapper.selectByCompanyId(companyId);
    }

    @Override
    public List<Presentation> getAllPresentations(Integer status) {
        return presentationMapper.selectAllPresentations(status);
    }

    @Override
    public boolean approvePresentation(Integer presentationId, String remark, String location) {
        Presentation p = presentationMapper.selectById(presentationId);
        if (p == null || p.getStatus() != 0) { // 只处理待审批
            return false;
        }
        // 检查冲突，排除当前记录
        int conflictCount = presentationMapper.checkConflictExcludeSelf(
                location, p.getStartTime(), p.getEndTime(), presentationId);
        if (conflictCount > 0) {
            return false;
        }
        // 更新状态、备注、地点、更新时间
        return presentationMapper.updateStatusRemarkLocation(
                presentationId, 1, remark, location) > 0;
    }

    @Override
    public boolean rejectPresentation(Integer presentationId, String remark) {
        Presentation p = presentationMapper.selectById(presentationId);
        if (p == null || p.getStatus() != 0) {
            return false;
        }
        // 状态更新为拒绝(2)，更新备注和更新时间
        return presentationMapper.updateStatusRemark(
                presentationId, 2, remark) > 0;
    }

    @Override
    public List<PresentationWithCompanyDTO> getAllPresentationsWithCompany(Integer status) {
        return presentationMapper.selectAllWithCompany(status);
    }
}
