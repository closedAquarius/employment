package com.gr.geias.service.impl;

import com.github.promeg.pinyinhelper.Pinyin;
import com.gr.geias.dto.StudentExportExcel;
import com.gr.geias.dto.StudentImportExcel;
import com.gr.geias.model.PersonInfo;
import com.gr.geias.repository.PersonInfoRepository;
import com.gr.geias.service.CollegeService;
import com.gr.geias.service.PersonInfoService;
import com.gr.geias.util.FaceUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 人员信息服务实现类
 */
@Service
public class PersonInfoServiceImpl implements PersonInfoService {
    
    @Autowired
    private PersonInfoRepository personInfoRepository;
    @Autowired
    private CollegeService collegeService;

    @Override
    public PersonInfo login(String username, String password) {
        return personInfoRepository.queryPerson(username, password);
    }

    @Override
    public Boolean registerPerson(PersonInfo personInfo) {
        // 输入验证
        if (personInfo.getUsername() == null || personInfo.getUsername().isEmpty()) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        if (personInfo.getPassword() == null || personInfo.getPassword().isEmpty()) {
            throw new IllegalArgumentException("密码不能为空");
        }
        if (personInfo.getPersonName() == null || personInfo.getPersonName().isEmpty()) {
            throw new IllegalArgumentException("姓名不能为空");
        }
        if (personInfo.getEnableStatus() == null) {
            throw new IllegalArgumentException("用户权限不能为空");
        }
        // 检查用户名唯一性
        if (personInfoRepository.checkUsernameExists(personInfo.getUsername())) {
            throw new IllegalArgumentException("用户名已存在");
        }
        // 设置创建时间
        personInfo.setCreateTime(new java.util.Date());
        Integer result = personInfoRepository.insertPerson(personInfo);
        return result > 0;
    }

    @Override
    public PersonInfo getPersonById(Integer personId) {
        return personInfoRepository.queryPersonById(personId);
    }

    @Override
    public List<PersonInfo> getCollegePerson() {
        return personInfoRepository.queryCollegePerson();
    }

    @Override
    public List<PersonInfo> getPersonByCollegeId(Integer collegeId) {
        return personInfoRepository.queryPersonByCollegeId(collegeId);
    }

    @Override
    public Boolean insertPerson(PersonInfo personInfo) {
        Integer result = personInfoRepository.insertPerson(personInfo);
        return result > 0;
    }

    @Override
    public List<PersonInfo> getAllTeachers(int offset, int pageSize) {
        return personInfoRepository.selectAllTeachers(offset, pageSize);
    }

    @Override
    public int getAllTeachersCount() {
        return personInfoRepository.countAllTeachers();
    }


    @Override
    public Boolean updatePerson(PersonInfo personInfo) {
        try {
            Integer result = personInfoRepository.updatePerson(personInfo);
            return result > 0;
        } catch (Exception e) {
            throw new RuntimeException("更新用户信息失败", e);
        }
    }

    @Override
    public Boolean delPerson(Integer personId) {
        Integer result = personInfoRepository.delPersonById(personId);
        return result > 0;
    }

    @Override
    public List<PersonInfo> getPerson1() {
        return personInfoRepository.queryPerson1();
    }

    @Override
    public Boolean addFace(PersonInfo personInfo, String faceImage) {
        Integer personId = personInfo.getPersonId();
        try {
            JSONObject jsonObject = FaceUtil.addUser(faceImage, personId.toString());
            String errorMsg = jsonObject.getString("error_msg");
            JSONObject result = jsonObject.getJSONObject("result");
            String faceToken = result.getString("face_token");
            
            if (errorMsg.equals("SUCCESS")) {
                Integer updateResult = personInfoRepository.updatePersonById(personId, faceToken);
                if (updateResult > 0) {
                    personInfo.setFaceToken(faceToken);
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public PersonInfo checkFace(String image) {
        try {
            JSONObject search = FaceUtil.search(image);
            String errorMsg = search.getString("error_msg");
            
            if (errorMsg.equals("SUCCESS")) {
                JSONObject result = search.getJSONObject("result");
                JSONArray userList = result.getJSONArray("user_list");
                JSONObject jsonObject = userList.getJSONObject(0);
                double score = jsonObject.getDouble("score");
                String userId = jsonObject.getString("user_id");
                
                if (score > 90) {
                    return personInfoRepository.queryPersonById(Integer.parseInt(userId));
                } else {
                    return new PersonInfo();
                }
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<PersonInfo> getPersonByCollegeIdPage(Integer collegeId, int offset, int limit) {
        return personInfoRepository.queryPersonByCollegeIdPage(collegeId, offset, limit);
    }

    @Override
    public int getPersonByCollegeIdCount(Integer collegeId) {
        Integer count = personInfoRepository.queryPersonByCollegeIdCount(collegeId);
        return count == null ? 0 : count;
    }

    @Override
    public List<PersonInfo> getPerson1Page(int offset, int limit) {
        return personInfoRepository.queryPerson1Page(offset, limit);
    }

    @Override
    public int getPerson1Count() {
        Integer count = personInfoRepository.queryPerson1Count();
        return count == null ? 0 : count;
    }

    @Override
    public boolean insertStudentsBatch(List<PersonInfo> studentList) {
        if (studentList == null || studentList.isEmpty()) {
            return false;
        }
        int insertedCount = personInfoRepository.insertPersons(studentList);
        // 判断是否全部插入成功
        return insertedCount == studentList.size();
    }

    @Override
    public List<StudentExportExcel> getAllStudents() {
        return personInfoRepository.queryAllStudentsForExport();
    }

    /**
     * 将导入的Excel DTO列表转换为PersonInfo实体，填充默认字段，批量保存
     */
    @Override
    public boolean importStudents(List<StudentImportExcel> importList) {
        if (importList == null || importList.isEmpty()) {
            return false;
        }

        // 预加载学院映射（collegeName -> collegeId）
        Map<String, Integer> collegeMap = collegeService.getCollegeNameToIdMap();

        List<PersonInfo> personInfoList = new ArrayList<>();

        for (StudentImportExcel dto : importList) {
            PersonInfo p = new PersonInfo();
            p.setPersonName(dto.getPersonName());
            p.setCollegeName(dto.getCollegeName());

            Integer collegeId = collegeMap.get(dto.getCollegeName());
            if (collegeId == null) {
                // 学院名未匹配到，跳过或抛异常（根据需求）
                continue;
            }
            p.setCollegeId(collegeId);

            String pinyin = Pinyin.toPinyin(dto.getPersonName(), "").toLowerCase();
            String uuidSuffix1 = UUID.randomUUID().toString().replace("-", "").substring(0, 2);
            String uuidSuffix2 = UUID.randomUUID().toString().replace("-", "").substring(0, 4);
            String username = pinyin + uuidSuffix1;
            String password = pinyin + uuidSuffix2;
            p.setUsername(username);
            p.setPassword(password);
            p.setEnableStatus(0);
            p.setCreateTime(new Date());

            personInfoList.add(p);
        }

        // 批量保存
        return insertStudentsBatch(personInfoList);
    }
} 