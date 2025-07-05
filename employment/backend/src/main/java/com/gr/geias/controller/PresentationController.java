
package com.gr.geias.controller;

import com.gr.geias.dto.PresentationWithCompanyDTO;
import com.gr.geias.model.Presentation;
import com.gr.geias.service.PresentationService;
import com.gr.geias.service.PresentationSignupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/presentation")
public class PresentationController {

    @Autowired
    private PresentationService presentationService;

    @Autowired
    private PresentationSignupService presentationSignupService;
    /**
     * 公司用户可访问：申请举办一场宣讲会
     */
    @PostMapping("/apply")
    public ResponseEntity<String> applyPresentation(@RequestBody Presentation presentation) {
        // 你可以在此处添加登录用户校验和 enable_status 检查
        boolean success = presentationService.applyPresentation(presentation);
        if (success) {
            return ResponseEntity.ok("宣讲会申请成功，等待审核");
        } else {
            return ResponseEntity.status(400).body("宣讲会申请失败，可能存在时间或地点冲突");
        }
    }

    /**
     * 公司用户可访问：查看自己申请的所有宣讲会
     */
    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<Presentation>> getPresentationsByCompanyId(@PathVariable Integer companyId) {

        List<Presentation> list = presentationService.getPresentationsByCompanyId(companyId);
        return ResponseEntity.ok(list);
    }

    /**
     * 管理员可访问：获取所有整合公司信息宣讲会申请记录（可选按状态筛选）
     * 学生可访问：获取所有整合公司信息宣讲会申请记录（已通过）
     * @param status 可选，状态 0=待审核，1=通过，2=拒绝，null=全部
     */
    @GetMapping("/admin/presentations")
    public ResponseEntity<List<PresentationWithCompanyDTO>> getAllPresentations(
            @RequestParam(required = false) Integer status
    ) {
        System.out.println("status:" + status);
        List<PresentationWithCompanyDTO> list = presentationService.getAllPresentationsWithCompany(status);
        System.out.println("list:" + list);
        return ResponseEntity.ok(list);
    }

    /**
     * 管理员可访问：审批通过申请，带备注，审批前检测冲突
     */
    @PutMapping("/{presentationId}/approve")
    public ResponseEntity<String> approvePresentation(
            @PathVariable("presentationId") Integer presentationId,
            @RequestParam(required = false) String remark,
            @RequestParam String location  // 管理员审批时指定地点
    ) {
        boolean success = presentationService.approvePresentation(presentationId, remark, location);
        if (success) {
            return ResponseEntity.ok("审批通过成功");
        } else {
            return ResponseEntity.status(409).body("审批失败：时间地点冲突或状态不正确");
        }
    }

    /**
     * 管理员可访问：拒绝申请，带拒绝理由（备注）
     */
    @PutMapping("/{presentationId}/reject")
    public ResponseEntity<String> rejectPresentation(
            @PathVariable("presentationId") Integer presentationId,
            @RequestParam String remark  // 拒绝理由
    ) {
        boolean success = presentationService.rejectPresentation(presentationId, remark);
        if (success) {
            return ResponseEntity.ok("申请已拒绝");
        } else {
            return ResponseEntity.status(400).body("拒绝失败，可能状态不正确或记录不存在");
        }
    }

    /**
     * 学生报名宣讲会
     * @param presentationId 宣讲会ID
     * @param studentId 当前学生ID（可通过token或参数传）
     */
    @PostMapping("/{presentationId}/signup")
    public ResponseEntity<String> signupPresentation(
            @PathVariable Integer presentationId,
            @RequestParam Integer studentId
    ) {
        boolean success = presentationSignupService.signupPresentation(presentationId, studentId);
        if (success) {
            return ResponseEntity.ok("报名成功！");
        } else {
            return ResponseEntity.status(400).body("报名失败：可能已报名、已满或未审核通过");
        }
    }

    /**
     * 学生获取已报名宣讲会列表（含公司信息和类型）
     */
    @GetMapping("/student/signed")
    public ResponseEntity<List<PresentationWithCompanyDTO>> getSignedPresentations(@RequestParam Integer studentId) {
        return ResponseEntity.ok(presentationSignupService.getSignedPresentations(studentId));
    }

    /**
     * 学生获取未报名宣讲会列表（含公司信息和类型）
     */
    @GetMapping("/student/unsigned")
    public ResponseEntity<List<PresentationWithCompanyDTO>> getUnSignedPresentations(@RequestParam Integer studentId) {
        System.out.println("studentId:" + studentId);
        return ResponseEntity.ok(presentationSignupService.getUnSignedPresentations(studentId));
    }

    /**
     * 学生撤销申请
     */
    @DeleteMapping("/student/cancel")
    public ResponseEntity<String> cancelSignup(
            @RequestParam Integer studentId,
            @RequestParam Integer presentationId) {
        boolean success = presentationSignupService.cancelSignup(studentId, presentationId);
        if (success) {
            return ResponseEntity.ok("取消报名成功");
        } else {
            return ResponseEntity.badRequest().body("取消报名失败，可能报名记录不存在");
        }
    }

    @GetMapping("/specialty/{presentationId}")
    public ResponseEntity<List<Map<String, Object>>> getSpecialty(@PathVariable Integer presentationId) {
        return ResponseEntity.ok(presentationSignupService.getSpecialtyDistribution(presentationId));
    }

    @GetMapping("/class/{presentationId}")
    public ResponseEntity<List<Map<String, Object>>> getClass(@PathVariable Integer presentationId) {
        return ResponseEntity.ok(presentationSignupService.getClassDistribution(presentationId));
    }
}
