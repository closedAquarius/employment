package com.gr.geias.controller;

import com.gr.geias.dto.ClassGradeAndSpecialty;
import com.gr.geias.dto.CollegeAndPerson;
import com.gr.geias.dto.SpecialtyAndCollege;
import com.gr.geias.model.ClassGrade;
import com.gr.geias.model.College;
import com.gr.geias.model.PersonInfo;
import com.gr.geias.model.Specialty;
import com.gr.geias.enums.EnableStatusEnums;
import com.gr.geias.service.*;
import com.gr.geias.util.PageMath;
import com.gr.geias.util.TokenUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/organizationcontroller")
public class OrganizationController {
    @Autowired
    CollegeService collegeService;
    @Autowired
    PersonInfoService personInfoService;
    @Autowired
    OrganizationNumService organizationNumService;
    @Autowired
    SpecialtyService specialtyService;
    @Autowired
    ClassGradeService classGradeService;

    /**
     * 返回学院"详细"信息 (2级权限)
     *
     * @param name
     * @return
     */
    @RequestMapping(value = "/getcollegelist", method = RequestMethod.GET)
    public Map<String, Object> getCollegeList(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "pageSize", defaultValue = "8") Integer pageSize,
            @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum){
        Map<String, Object> map = new HashMap<String, Object>(4);
        try {
            int rowIndex = PageMath.pageNumtoRowIndex(pageNum, pageSize);
            // 获取全部学院
            List<College> collegePageList = collegeService.getCollegePage(name, rowIndex, pageSize);
            int total = collegeService.getCollegeCount(name); // 总数

            List<CollegeAndPerson> list = new ArrayList<>();
            for (College college : collegePageList) {
                CollegeAndPerson collegeAndPerson = new CollegeAndPerson();
                collegeAndPerson.setCollege(college);
                PersonInfo personById = personInfoService.getPersonById(college.getAdminId());
                collegeAndPerson.setPersonInfo(personById);
                Integer count = organizationNumService.getcollegeCount(college.getCollegeId());
                collegeAndPerson.setSum(count);
                list.add(collegeAndPerson);
            }

            map.put("success", true);
            map.put("list", list);
            map.put("total", total);
        } catch (Exception e) {
            map.put("success", false);
            map.put("errMsg", "分页查询出错: " + e.getMessage());
        }

        return map;
    }
    /**
     * 查询所有空闲的学院管理员 enable_Status=1 and college_id = null 权限 2
     *
     * @param collegeadminId
     * @return
     */
    @RequestMapping(value = "/getcollegeadmin", method = RequestMethod.GET)
    public Map<String, Object> getCollegeAdmin(@RequestParam(value = "collegeadminId", required = false) Integer collegeadminId) {
        Map<String, Object> map = new HashMap<String, Object>(3);
        List<PersonInfo> collegePerson = personInfoService.getCollegePerson();
        if (collegePerson.size() > 0) {
            map.put("success", true);
            map.put("collegePerson", collegePerson);
            return map;
        } else {
            map.put("success", false);
            map.put("errMsg", "请保证至少有一名学院管理身份的闲置用户存在");
            return map;
        }
    }

    /**
     * 添加学院 权限 2
     *
     * @param collegeName
     * @param personId
     * @return
     */
    @RequestMapping(value = "/addcollege", method = RequestMethod.GET)
    public Map<String, Object> addCollege(@RequestParam("collegeName") String collegeName,
                                          @RequestParam("personId") Integer personId) {
        Map<String, Object> map = new HashMap<String, Object>(3);
        if (collegeName == null || collegeName.equals("") || personId == null || personId == 0) {
            map.put("success", false);
            map.put("errMsg", "输入信息错误");
        }
        College college = new College();
        college.setAdminId(personId);
        college.setCollegeName(collegeName);
        college.setCreateTime(new Date());
        try {
            Boolean aBoolean = collegeService.addCollege(college);
            map.put("success", true);
        } catch (Exception e) {
            map.put("success", false);
            map.put("errMsg", e.getMessage());
        }
        return map;
    }

    /**
     * 更新学院 权限2
     *
     * @param collegeName
     * @param personId
     * @param collegeId
     * @return
     */
    @RequestMapping(value = "/updatecollege", method = RequestMethod.GET)
    public Map<String, Object> updateCollege(@RequestParam(value = "collegeName", required = false) String collegeName,
                                             @RequestParam(value = "personId", required = false) Integer personId,
                                             @RequestParam("collegeId") Integer collegeId) {
        Map<String, Object> map = new HashMap<String, Object>(3);
        if (personId == null || personId == 0) {
            personId = null;
        }
        College college = new College();
        college.setCollegeId(collegeId);
        college.setCollegeName(collegeName);
        college.setAdminId(personId);
        try {
            Boolean aBoolean = collegeService.updateCollege(college);
            if (aBoolean) {
                map.put("success", true);
            }
        } catch (RuntimeException e) {
            map.put("success", false);
            map.put("errMsg", e.getMessage());
        }
        return map;
    }

    /**
     * 删除学院 权限2
     *
     * @param collegeId
     * @return
     */
    @RequestMapping(value = "/delcollege", method = RequestMethod.GET)
    public Map<String, Object> delCollege(@RequestParam("collegeId") Integer collegeId) {
        Map<String, Object> map = new HashMap<String, Object>(3);
        if (collegeId != null && collegeId != null) {
            try {
                collegeService.delCollege(collegeId);
                map.put("success", true);
            } catch (RuntimeException e) {
                map.put("success", false);
                map.put("errMsg", e.getMessage());
            }
        } else {
            map.put("success", false);
            map.put("errMsg", "信息错误");
        }
        return map;
    }

    /**
     * 根据用户权限获取学院列表(简化信息) 权限 1，2
     * @return
     */
    @RequestMapping(value = "/getcollegeinit", method = RequestMethod.GET)
    public Map<String, Object> getCollegeinit(@RequestHeader("Authorization") String token) {
        Map<String, Object> map = new HashMap<String, Object>(3);
        Claims claims = TokenUtil.extractClaims(token);
        Integer userId = (Integer) claims.get("userId");
        PersonInfo person = personInfoService.getPersonById(userId);
        List<College> college = null;
        if (person.getEnableStatus() == EnableStatusEnums.ADMINISTRATOR.getState()) {
            college = collegeService.getCollege(null);
        }
        if (college != null) {
            map.put("success", true);
            map.put("collegeList", college);
        } else {
            map.put("success", false);
            map.put("errMsg", "查找学院信息错误");
        }
        return map;
    }

    /**
     * 获取专业详细列表 权限 1，2
     *
     * @param collegeId
     * @return
     */
    @RequestMapping(value = "/getspecialty", method = RequestMethod.GET)
    public Map<String, Object> getSpecialty(
            @RequestParam(value = "collegeId", required = false) Integer collegeId,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "8") Integer pageSize,
            @RequestHeader("Authorization") String token) {

        Map<String, Object> map = new HashMap<>(4);
        int offset = (pageNum - 1) * pageSize;

        try {
            Claims claims = TokenUtil.extractClaims(token);
            Integer userId = (Integer) claims.get("userId");
            PersonInfo person = personInfoService.getPersonById(userId);

            EnableStatusEnums role = EnableStatusEnums.stateOf(person.getEnableStatus());
            if (role != EnableStatusEnums.ADMINISTRATOR) {
                map.put("success", false);
                map.put("errMsg", "无权限访问该接口");
                return map;
            }

            // 查专业分页
            List<Specialty> specialtyList = specialtyService.getSpecialtyPage(collegeId, name, offset, pageSize);

            // 拼装结果
            List<SpecialtyAndCollege> list = new ArrayList<>();
            for (Specialty specialty : specialtyList) {
                SpecialtyAndCollege sac = new SpecialtyAndCollege();
                sac.setSpecialty(specialty);

                // 根据专业的 collegeId 重新获取学院信息
                College college = collegeService.getCollegeById(specialty.getCollegeId());
                sac.setCollege(college);

                Integer count = organizationNumService.getspecialtyCount(specialty.getSpecialtyId());
                sac.setSum(count);

                list.add(sac);
            }

            map.put("success", true);
            map.put("List", list);
        } catch (Exception e) {
            map.put("success", false);
            map.put("errMsg", "Token无效或查询出错: " + e.getMessage());
        }

        return map;
    }

    /**
     * 添加专业 权限 1(特定权限，学院内部专业才行)，2
     *
     * @param specialtyName
     * @param collegeId
     * @return
     */
    @RequestMapping(value = "/addspecialty", method = RequestMethod.GET)
    public Map<String, Object> addSpecialty(@RequestParam("specialtyName") String specialtyName,
                                            @RequestParam("collegeId") Integer collegeId,
                                            @RequestHeader("Authorization") String token) {
        Map<String, Object> map = new HashMap<String, Object>(3);
        if (specialtyName == null || specialtyName.equals("") || collegeId == null || collegeId == 0) {
            map.put("success", false);
            map.put("errMsg", "输入信息错误！");
            return map;
        }
        Claims claims = TokenUtil.extractClaims(token);
        Integer userId = (Integer) claims.get("userId");
        PersonInfo person = personInfoService.getPersonById(userId);
        Specialty specialty = new Specialty();
        if (person.getEnableStatus()==EnableStatusEnums.ADMINISTRATOR.getState()){
            specialty.setCollegeId(collegeId);
        }
        specialty.setSpecialtyName(specialtyName);
        specialty.setCreateTime(new Date());
        try {
            specialtyService.addSpecialty(specialty);
            map.put("success", true);
        } catch (RuntimeException e) {
            map.put("success", false);
            map.put("errMsg", e.getMessage());
        }
        return map;
    }

    /**
     * 更新专业 权限1（特指学院内部专业），2
     *
     * @param specialtyId
     * @param specialtyName
     * @param collegeId
     * @return
     */
    @RequestMapping(value = "/updatespecialty", method = RequestMethod.GET)
    public Map<String, Object> updateSpecialty(@RequestParam("specialtyId") Integer specialtyId,
                                               @RequestParam(value = "specialtyName") String specialtyName,
                                               @RequestParam(value = "collegeId") Integer collegeId) {
        Map<String, Object> map = new HashMap<String, Object>(2);
        if (specialtyName == null || specialtyName.equals("") || collegeId == null || collegeId == 0) {
            map.put("success", false);
            map.put("errMsg", "输入信息错误");
            return map;
        }
        Specialty specialty = new Specialty();
        specialty.setSpecialtyId(specialtyId);
        specialty.setSpecialtyName(specialtyName);
        specialty.setCollegeId(collegeId);
        Boolean aBoolean = specialtyService.updateSpecialty(specialty);
        if (aBoolean) {
            map.put("success", true);
        } else {
            map.put("success", false);
            map.put("errMsg", "修改错误");
        }
        return map;
    }

    /**
     * 删除专业 权限 1（特指学院内部专业），2
     *
     * @param specialtyId
     * @return
     */
    @RequestMapping(value = "/delspecialty", method = RequestMethod.GET)
    public Map<String, Object> delSpecialty(@RequestParam("specialtyId") Integer specialtyId) {
        Map<String, Object> map = new HashMap<String, Object>(2);
        try {
            Boolean aBoolean = specialtyService.delSpecialty(specialtyId);
            if (aBoolean) {
                map.put("success", true);
            } else {
                map.put("success", false);
                map.put("errMsg", "非法操作");
            }
        } catch (RuntimeException e) {
            map.put("success", false);
            map.put("errMsg", e.getMessage());
        }
        return map;
    }

    /**
     * 获取专业简化列表 权限 1，2
     *
     * @param collegeId
     * @return
     */
    @RequestMapping(value = "/getspecialtyinit", method = RequestMethod.GET)
    public Map<String, Object> getSpecialty(@RequestParam("collegeId") Integer collegeId) {
        Map<String, Object> map = new HashMap<String, Object>(3);
        try {
            List<Specialty> specialty = specialtyService.getSpecialty(collegeId);
            map.put("success", true);
            map.put("specialtyList", specialty);
        } catch (Exception e) {
            map.put("success", false);
            map.put("errMsg", "获取专业失败");
        }
        return map;
    }

    /**
     * 获取班级详细信息 权限 1(特定)，2
     *
     * @param specialtyId
     * @return
     */
    @RequestMapping(value = "/getclassgrade", method = RequestMethod.GET)
    public Map<String, Object> getClassGrade(
            @RequestParam("specialtyId") Integer specialtyId,
            @RequestParam(value = "pageSize", defaultValue = "8") Integer pageSize,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        Map<String, Object> map = new HashMap<String, Object>(3);
        try {
            Specialty specialtyById = specialtyService.getSpecialtyById(specialtyId);

            int offset = (pageNum - 1) * pageSize;

            List<ClassGrade> classGradeList = classGradeService.getClassGradePage(specialtyId,  offset, pageSize);
            int total = classGradeService.getClassGradeCount(specialtyId);
            List<ClassGradeAndSpecialty> list = new ArrayList<ClassGradeAndSpecialty>();
            for (ClassGrade classGrade : classGradeList) {
                ClassGradeAndSpecialty classGradeAndSpecialty = new ClassGradeAndSpecialty();
                classGradeAndSpecialty.setClassGrade(classGrade);
                classGradeAndSpecialty.setSpecialty(specialtyById);
                Integer classGradeCount = organizationNumService.getClassGradeCount(classGrade.getClassId());
                classGradeAndSpecialty.setSum(classGradeCount);
                PersonInfo personById = personInfoService.getPersonById(classGrade.getAdminId());
                classGradeAndSpecialty.setPersonInfo(personById);
                list.add(classGradeAndSpecialty);
            }
            map.put("success", true);
            map.put("List", list);
            map.put("total", total);
        } catch (Exception e) {
            map.put("success", false);
            map.put("errMsg", "获取班级列表失败");
        }
        return map;
    }

    /**
     * 在添加和修改 班级的时候 加载属于这个学院的老师 权限 1，2
     *
     * @param collegeId
     * @return
     */
    @RequestMapping(value = "/getpersoninit", method = RequestMethod.GET)
    public Map<String, Object> getpersoninit(@RequestParam("collegeId") Integer collegeId, @RequestHeader("Authorization") String token) {
        Map<String, Object> map = new HashMap<String, Object>(3);
        Claims claims = TokenUtil.extractClaims(token);
        Integer userId = (Integer) claims.get("userId");
        PersonInfo person = personInfoService.getPersonById(userId);
        List<PersonInfo> personByCollegeId = null;
        if (person.getEnableStatus() == EnableStatusEnums.ADMINISTRATOR.getState()) {
            personByCollegeId = personInfoService.getPersonByCollegeId(collegeId);
        }
        if (personByCollegeId.size() > 0) {
            map.put("success", true);
            map.put("person", personByCollegeId);
            return map;
        } else {
            map.put("success", false);
            map.put("errMsg", "这个学院合适的没有老师，请添加老师后在重试！");
            return map;
        }
    }

    /**
     * 添加 班级 权限 1(添加的班级只能是自己的学院的)，2
     *
     * @param personId
     * @param classGradeName
     * @param collegeId
     * @param specialtyId
     * @param sum
     * @return
     */
    @RequestMapping(value = "/addclassgrade", method = RequestMethod.GET)
    public Map<String, Object> addClassGrade(@RequestParam("personId") Integer personId,
                                             @RequestParam("classGradeName") String classGradeName,
                                             @RequestParam("collegeId") Integer collegeId,
                                             @RequestParam("specialtyId") Integer specialtyId,
                                             @RequestParam("sum") Integer sum) {
        Map<String, Object> map = new HashMap<String, Object>(3);
        if (personId == null || classGradeName == null || collegeId == null || specialtyId == null || sum == null) {
            map.put("success", false);
            map.put("errMsg", "信息输入错误");
            return map;
        }
        if (personId == 0 || classGradeName.equals("") || collegeId == 0 || specialtyId == 0 || sum < 0) {
            map.put("success", false);
            map.put("errMsg", "信息输入错误");
            return map;
        }
        ClassGrade classGrade = new ClassGrade();
        classGrade.setCreateTime(new Date());
        classGrade.setAdminId(personId);
        classGrade.setClassName(classGradeName);
        classGrade.setSpecialtyId(specialtyId);
        try {
            Boolean aBoolean = classGradeService.addClassGrade(classGrade, sum, collegeId);
            if (aBoolean) {
                map.put("success", true);
            } else {
                map.put("success", false);
                map.put("errMsg", "添加出错");
            }
        } catch (RuntimeException e) {
            map.put("success", false);
            map.put("errMsg", e.getMessage());
        }
        return map;
    }

    /**
     * 更新 班级 权限 1(添加的班级只能是自己的学院的)，2
     *
     * @param classId
     * @param personId
     * @param classGradeName
     * @param collegeId
     * @param specialtyId
     * @param sum
     * @return
     */
    @RequestMapping(value = "/updateclassgrade", method = RequestMethod.GET)
    public Map<String, Object> updateClassGrade(@RequestParam("classId") Integer classId,
                                                @RequestParam("personId") Integer personId,
                                                @RequestParam("classGradeName") String classGradeName,
                                                @RequestParam("collegeId") Integer collegeId,
                                                @RequestParam("specialtyId") Integer specialtyId,
                                                @RequestParam("sum") Integer sum) {
        Map<String, Object> map = new HashMap<String, Object>(3);
        if (classId == null || personId == null || classGradeName == null || collegeId == null
                || specialtyId == null || sum == null) {
            map.put("success", false);
            map.put("errMsg", "信息输入错误!");
            return map;
        }
        if (classId == 0 || personId == 0 || classGradeName.equals("") || collegeId == 0
                || specialtyId == 0 || sum <= 0) {
            map.put("success", false);
            map.put("errMsg", "信息输入错误!");
            return map;
        }
        ClassGrade classGrade = new ClassGrade();
        classGrade.setAdminId(personId);
        classGrade.setClassName(classGradeName);
        classGrade.setSpecialtyId(specialtyId);
        classGrade.setClassId(classId);
        try {
            Boolean aBoolean = classGradeService.updateClassGrade(classGrade, sum, collegeId);
            if (aBoolean) {
                map.put("success", true);
            } else {
                map.put("success", false);
                map.put("errMsg", "修改错误");
            }
        } catch (RuntimeException e) {
            map.put("success", false);
            map.put("errMsg", e.getMessage());
        }
        return map;
    }

    /**
     * 删除班级 权限 1(特定)，2
     *
     * @param classId
     * @param specialtyId
     * @return
     */
    @RequestMapping(value = "/delclassgrade", method = RequestMethod.GET)
    public Map<String, Object> delClassGrade(@RequestParam("classId") Integer classId,
                                             @RequestParam("specialtyId") Integer specialtyId) {
        Map<String, Object> map = new HashMap<String, Object>(3);
        try {
            Boolean aBoolean = classGradeService.delClassGrade(classId);
            Specialty specialtyById = specialtyService.getSpecialtyById(specialtyId);
            if (aBoolean) {
                collegeService.getAndSetcount(specialtyById.getCollegeId());
                specialtyService.getAndSetSpecialtyCount(specialtyId);
                map.put("success", true);
            } else {
                map.put("success", false);
                map.put("errMsg", "删除出错");
            }
        } catch (RuntimeException e) {
            map.put("success", false);
            map.put("errMsg", e.getMessage());
        }
        return map;
    }

    /**
     * 获取辅导员 权限 1（特定），2
     *
     * @param collegeId
     * @return
     */
    @RequestMapping(value = "/getperson_0", method = RequestMethod.GET)
    public Map<String, Object> getperson_0(@RequestParam(value = "collegeId", required = false) Integer collegeId,
                                           @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                           @RequestParam(value = "pageSize", defaultValue = "8") Integer pageSize,
                                           @RequestHeader("Authorization") String token) {

        Map<String, Object> map = new HashMap<>(3);
        try {
            Claims claims = TokenUtil.extractClaims(token);
            Integer userId = (Integer) claims.get("userId");
            PersonInfo person = personInfoService.getPersonById(userId);

            List<PersonInfo> personInfoList = null;
            College college = null;

            int offset = (pageNum - 1) * pageSize;

//            if (person.getEnableStatus() == EnableStatusEnums.PREXY.getState()) {
//                // 院长只能查看自己学院的人
//                personInfoList = personInfoService.getPersonByCollegeId(person.getCollegeId());
//                List<College> collegeList = collegeService.getCollege(person.getPersonId());
//                if (collegeList == null || collegeList.isEmpty()) {
//                    map.put("success", false);
//                    map.put("errMsg", "没有数据");
//                    return map;
//                }
//                college = collegeList.get(0);
//            } else
                if (person.getEnableStatus() == EnableStatusEnums.ADMINISTRATOR.getState()) {
                // 管理员可以查看任意学院
                if (collegeId == null) {
                    List<College> collegeList = collegeService.getCollege(null);
                    if (collegeList == null || collegeList.isEmpty()) {
                        map.put("success", false);
                        map.put("errMsg", "没有数据");
                        return map;
                    }
                    college = collegeList.get(0);
                    personInfoList = personInfoService.getPersonByCollegeId(college.getCollegeId());
                } else {
                    college = collegeService.getCollegeById(collegeId);
                    personInfoList = personInfoService.getPersonByCollegeId(collegeId);
                }
            } else {
                map.put("success", false);
                map.put("errMsg", "无权限访问");
                return map;
            }
            // 查询总数
            int total = personInfoService.getPersonByCollegeIdCount(college == null ? collegeId : college.getCollegeId());


            map.put("success", true);
            map.put("personInfoList", personInfoList);
            map.put("college", college);
            map.put("total", total);
            return map;

        } catch (Exception e) {
            map.put("success", false);
            map.put("errMsg", "Token无效或已过期");
            return map;
        }
    }


    /**
     * 添加辅导员 权限1（特定），2
     *
     * @param personName
     * @param collegeId
     * @param password
     * @param username
     * @return
     */
    @RequestMapping(value = "/addperson_o", method = RequestMethod.GET)
    public Map<String, Object> addPerson_o(@RequestParam("personName") String personName,
                                           @RequestParam("collegeId") Integer collegeId,
                                           @RequestParam("password") String password,
                                           @RequestParam("username") String username) {
        Map<String, Object> map = new HashMap<String, Object>(3);
        PersonInfo personInfo = new PersonInfo();
        personInfo.setCollegeId(collegeId);
        personInfo.setEnableStatus(0);
        personInfo.setCreateTime(new Date());
        personInfo.setPassword(password);
        personInfo.setUsername(username);
        personInfo.setPersonName(personName);
        try {
            Boolean aBoolean = personInfoService.insertPerson(personInfo);
            if (aBoolean) {
                map.put("success", true);
            } else {
                map.put("success", false);
                map.put("errMsg", "添加失败");
            }
            return map;
        }catch (Exception e){
            map.put("success", false);
            map.put("errMsg", "登录名重复,请修改后重试");
            return map;
        }
    }

    /**
     * 获取用户密码 权限 1（特定），2
     *
     * @param personId
     * @return
     */
    @RequestMapping("/getpersonById")
    public Map<String, Object> getPersonInit(@RequestParam("personId") Integer personId, @RequestHeader("Authorization") String token) {
        Map<String, Object> map = new HashMap<String, Object>(3);
        PersonInfo person = personInfoService.getPersonById(personId);
        map.put("success", true);
        map.put("person", person);
        return map;
    }

    /**
     * 更新辅导员信息 权限 1（特定），2
     *
     * @param personId
     * @param personname
     * @param collegeId
     * @param username
     * @param password
     * @return
     */
    @RequestMapping("/updateperson_0")
    public Map<String, Object> updatePerson_0(@RequestParam("personId") Integer personId,
                                              @RequestParam("personname") String personname,
                                              @RequestParam("collegeId") Integer collegeId,
                                              @RequestParam("username") String username,
                                              @RequestParam("password") String password) {
        Map<String, Object> map = new HashMap<String, Object>(3);
        PersonInfo personInfo = new PersonInfo();
        List<ClassGrade> classGrade = classGradeService.getClassGrade(null, personId);
        PersonInfo personById = personInfoService.getPersonById(personId);
        if (!personById.getCollegeId().equals(collegeId)) {
            if (classGrade.size() == 0) {
                personInfo.setCollegeId(collegeId);
            } else {
                map.put("success", false);
                map.put("errMsg", "该用户还管理有该学院的班级不能修改所属学院");
                return map;
            }
        }
        personInfo.setUsername(username);
        personInfo.setPersonName(personname);
        personInfo.setPassword(password);
        personInfo.setPersonId(personId);
        try {
            Boolean aBoolean = personInfoService.updatePerson(personInfo);
            if (aBoolean) {
                map.put("success", true);
            } else {
                map.put("success", false);
                map.put("errMsg", "修改失败");

            }
        } catch (RuntimeException e) {
            map.put("success", false);
            map.put("errMsg", "登录名重复,请修改后重试");
        }
        return map;
    }

    /**
     * 删除辅导员 1（特定），2
     *
     * @param personId
     * @return
     */
    @RequestMapping("/delperson_0")
    public Map<String, Object> delPerson(@RequestParam("personId") Integer personId,
                                         @RequestHeader("Authorization") String token) {
        Map<String, Object> map = new HashMap<String, Object>(3);
        try {
            Boolean aBoolean = personInfoService.delPerson(personId);
            if (aBoolean) {
                map.put("success", true);
            } else {
                map.put("success", false);
                map.put("errMsg", "删除失败");
            }
        } catch (DataIntegrityViolationException e) {
            map.put("success", false);
            map.put("errMsg", "该人员还管理着班级，不能删除");
        }
        return map;
    }

    /**
     * 获取学院管理 权限2
     *
     * @return
     */
    @RequestMapping(value = "/getperson_1", method = RequestMethod.GET)
    public Map<String, Object> getperson_1(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "8") Integer pageSize) {
        Map<String, Object> map = new HashMap<>(4);

        try {
            int offset = (pageNum - 1) * pageSize;

            List<PersonInfo> personInfoList = personInfoService.getPerson1Page(offset, pageSize);

            List<CollegeAndPerson> list = new ArrayList<>();
            for (PersonInfo personInfo : personInfoList) {
                CollegeAndPerson collegeAndPerson = new CollegeAndPerson();
                collegeAndPerson.setPersonInfo(personInfo);
                List<College> collegeList = collegeService.getCollege(personInfo.getPersonId());
                College college = null;
                if (!collegeList.isEmpty()) {
                    college = collegeList.get(0);
                }
                collegeAndPerson.setCollege(college);
                list.add(collegeAndPerson);
            }

            // 查询总数
            int total = personInfoService.getPerson1Count();

            map.put("success", true);
            map.put("List", list);
            map.put("total", total);
        } catch (Exception e) {
            map.put("success", false);
            map.put("errMsg", "获取数据失败");
        }

        return map;
    }


    /**
     * 添加学院管理 权限2
     *
     * @param personName
     * @param password
     * @param username
     * @return
     */
    @RequestMapping(value = "/addperson_1", method = RequestMethod.GET)
    public Map<String, Object> addPerson_1(@RequestParam("personName") String personName,
                                           @RequestParam("password") String password,
                                           @RequestParam("username") String username) {
        Map<String, Object> map = new HashMap<String, Object>(3);
        PersonInfo personInfo = new PersonInfo();
        personInfo.setCollegeId(null);
        personInfo.setEnableStatus(1);
        personInfo.setCreateTime(new Date());
        personInfo.setPassword(password);
        personInfo.setUsername(username);
        personInfo.setPersonName(personName);
        try {
            Boolean aBoolean = personInfoService.insertPerson(personInfo);
            if (aBoolean) {
                map.put("success", true);
            } else {
                map.put("success", false);
                map.put("errMsg", "添加失败");
            }
            return map;
        }catch (Exception e){
            map.put("success", false);
            map.put("errMsg", "登录名重复,请修改后重试");
            return map;
        }
    }

    /**
     * 修改学院管理 权限2
     *
     * @param personId
     * @param personname
     * @param username
     * @param password
     * @return
     */
    @RequestMapping("/updateperson_1")
    public Map<String, Object> updatePerson_1(@RequestParam("personId") Integer personId,
                                              @RequestParam("personname") String personname,
                                              @RequestParam("username") String username,
                                              @RequestParam("password") String password) {
        Map<String, Object> map = new HashMap<String, Object>(3);
        PersonInfo personInfo = new PersonInfo();
        personInfo.setPersonId(personId);
        personInfo.setUsername(username);
        personInfo.setPassword(password);
        personInfo.setPersonName(personname);
        try {
            Boolean aBoolean = personInfoService.updatePerson(personInfo);
            if (aBoolean) {
                map.put("success", true);
            } else {
                map.put("success", false);
                map.put("errMsg", "更新失败");

            }
            return map;
        }catch (Exception e){
        map.put("success", false);
        map.put("errMsg", "登录名重复,请修改后重试");
        return map;
    }
    }

    /**
     * 删除学院管理 权限2
     *
     * @param personId
     * @return
     */
    @RequestMapping("/delperson_1")
    public Map<String, Object> delPerson_1(@RequestParam("personId") Integer personId,
                                           @RequestHeader("Authorization") String token) {
        Map<String, Object> map = new HashMap<String, Object>(3);
        try {
            Boolean aBoolean = personInfoService.delPerson(personId);
            if (aBoolean) {
                map.put("success", true);
            } else {
                map.put("success", false);
                map.put("errMsg", "删除失败");
            }
        } catch (DataIntegrityViolationException e) {
            map.put("success", false);
            map.put("errMsg", "该管理员还管理着学院，不能删除");
        }
        return map;
    }

    /**
     *  获取组织架构图所需数据 权限 1，2
     * @return
     */
    @RequestMapping("/getorganizationinfo")
    public Map<String,Object> getOrganizationInfo(){
        List<College> collegeList = collegeService.getCollege(null);
        Map<String,Object> school = new HashMap<String, Object>(4);
        school.put("name", "XXX大学");
        school.put("symbolSize", 100);
        school.put("draggable", true);
        school.put("value", 27);
        school.put("x", 0);
        school.put("y", 0);
        Map<String,Object> map = new HashMap<String, Object>(5);
        List<Map<String,Object>> data = new ArrayList<Map<String,Object>>();
        List<String> nameList = new ArrayList<String>();
        List<Map<String,Object>> links = new ArrayList<Map<String,Object>>();
        List<Map<String,Object>> categories = new ArrayList<Map<String,Object>>();
        data.add(school);
        for (int i = 0; i < collegeList.size(); i++) {
            Map<String,Object> collegeOrganization = new HashMap<String,Object>(7);
            Map<String,Object> collegeLinks = new HashMap<String,Object>(2);
            Map<String,Object> collegeCategories = new HashMap<String,Object>(1);
            College college = collegeList.get(i);
            collegeOrganization.put("name", college.getCollegeName());
            collegeOrganization.put("symbolSize", 60);
            collegeOrganization.put("draggable", true);
            collegeOrganization.put("category", college.getCollegeName());
            collegeOrganization.put("value", 3);
            collegeOrganization.put("x", 1);
            collegeOrganization.put("y", -125);
            data.add(collegeOrganization);
            collegeLinks.put("source", "XXX大学");
            collegeLinks.put("target", college.getCollegeName());
            links.add(collegeLinks);
            collegeCategories.put("name", college.getCollegeName());
            categories.add(collegeCategories);
            nameList.add(college.getCollegeName());
            List<Specialty> specialtyList = specialtyService.getSpecialty(college.getCollegeId());
            for (int j = 0; j <specialtyList.size(); j++) {
                Specialty specialty = specialtyList.get(j);
                Map<String,Object> specialtyOrganization = new HashMap<String,Object>(5);
                Map<String,Object> specialtyLinks = new HashMap<String,Object>(2);
                specialtyOrganization.put("name", specialty.getSpecialtyName());
                specialtyOrganization.put("symbolSize", 30);
                specialtyOrganization.put("draggable", true);
                specialtyOrganization.put("category", college.getCollegeName());
                specialtyOrganization.put("value", 1);
                specialtyOrganization.put("x", 1);
                specialtyOrganization.put("y", 100);
                data.add(specialtyOrganization);
                specialtyLinks.put("source", college.getCollegeName());
                specialtyLinks.put("target", specialty.getSpecialtyName());
                links.add(specialtyLinks);
                nameList.add(specialty.getSpecialtyName());
            }
        }
        map.put("success", true);
        map.put("data", data);
        map.put("links",links);
        map.put("categories",categories);
        map.put("nameList", nameList);
        return map;
    }
} 