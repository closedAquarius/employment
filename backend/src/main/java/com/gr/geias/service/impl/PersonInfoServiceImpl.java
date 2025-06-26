package com.gr.geias.service.impl;

import com.gr.geias.model.PersonInfo;
import com.gr.geias.repository.PersonInfoRepository;
import com.gr.geias.service.PersonInfoService;
import com.gr.geias.util.FaceUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 人员信息服务实现类
 */
@Service
public class PersonInfoServiceImpl implements PersonInfoService {
    
    @Autowired
    private PersonInfoRepository personInfoRepository;

    @Override
    public PersonInfo login(String username, String password) {
        return personInfoRepository.queryPerson(username, password);
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
} 