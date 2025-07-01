import React, { useState } from 'react';
import {
  Button,
  TextField,
  TextArea,
  DatePicker,
  FormLayout,
  Card,
  HorizontalLayout,
  VerticalLayout,
  ComboBox
} from '@vaadin/react-components';

export const config: ViewConfig = {
  menu: {
    exclude: true,
  },
};

const ResumeForm = ({ onGenerate }) => {
  // 样式定义
  const styles = {
    formContainer: {
      maxWidth: '800px',
      margin: '0 auto',
      padding: '0 20px 20px'
    },
    divider: {
      height: '1px',
      backgroundColor: 'var(--lumo-contrast-10pct)',
      margin: '20px 0',
      border: 'none'
    },
    card: {
      marginBottom: '15px',
      padding: '15px',
      backgroundColor: 'var(--lumo-base-color)',
      borderRadius: 'var(--lumo-border-radius-m)'
    },
    sectionTitle: {
      marginBottom: '15px',
      color: 'var(--lumo-primary-text-color)',
      fontWeight: '500'
    },
    button: {
      padding: '8px 16px',
      fontSize: 'var(--lumo-font-size-s)'
    },
    formRow: {
      display: 'flex',
      gap: '15px',
      alignItems: 'flex-end',
      marginBottom: '15px'
    },
    formField: {
      flex: '1 1 auto'
    },
    jdTextArea: {
      width: '90%',
      padding: '10px',
      fontFamily: 'inherit',
      fontSize: 'var(--lumo-font-size-m)',
      minHeight: '80px',
      maxHeight: '100px',
    }
  };

  // 基本信息状态
  const [basicInfo, setBasicInfo] = useState({
    name: '',
    sex: '',
    birthDate: '',
    email: '',
    phone: '',
    jd: ''
  });

  // 教育背景状态
  const [educations, setEducations] = useState([{
    begin: '',
    end: '',
    university: '',
    major: '',
    degree: ''
  }]);

  // 工作经历状态
  const [works, setWorks] = useState([{
    begin: '',
    end: '',
    company: '',
    workContent: ''
  }]);

  // 项目经历状态
  const [projects, setProjects] = useState([{
    begin: '',
    end: '',
    name: '',
    content: '',
    skills: ''
  }]);

  // 处理基本信息变化
  const handleBasicInfoChange = (field) => (e) => {
    setBasicInfo({
      ...basicInfo,
      [field]: e.target.value
    });
  };

  // 处理日期变化
  const handleDateChange = (field) => (e) => {
    setBasicInfo({
      ...basicInfo,
      [field]: e.target.value
    });
  };

  // 处理教育背景变化
  const handleEducationChange = (index, field) => (e) => {
    const newEducations = [...educations];
    newEducations[index][field] = e.target.value;
    setEducations(newEducations);
  };

  // 处理工作经历变化
  const handleWorkChange = (index, field) => (e) => {
    const newWorks = [...works];
    newWorks[index][field] = e.target.value;
    setWorks(newWorks);
  };

  // 处理项目经历变化
  const handleProjectChange = (index, field) => (e) => {
    const newProjects = [...projects];
    newProjects[index][field] = e.target.value;
    setProjects(newProjects);
  };

  // 添加教育背景
  const addEducation = () => {
    setEducations([
      ...educations,
      { begin: '', end: '', university: '', major: '', degree: '' }
    ]);
  };

  // 删除教育背景
  const removeEducation = (index) => {
    const newEducations = [...educations];
    newEducations.splice(index, 1);
    setEducations(newEducations);
  };

  // 添加工作经历
  const addWork = () => {
    setWorks([
      ...works,
      { begin: '', end: '', company: '', workContent: '' }
    ]);
  };

  // 删除工作经历
  const removeWork = (index) => {
    const newWorks = [...works];
    newWorks.splice(index, 1);
    setWorks(newWorks);
  };

  // 添加项目经历
  const addProject = () => {
    setProjects([
      ...projects,
      { begin: '', end: '', name: '', content: '', skills: '' }
    ]);
  };

  // 删除项目经历
  const removeProject = (index) => {
    const newProjects = [...projects];
    newProjects.splice(index, 1);
    setProjects(newProjects);
  };

  // 生成简历
  const generateResume = async () => {
    const requestData = {
        ...basicInfo,
        educationRecords: educations,
        workExperienceRecords: works,
        projectExperienceRecords: projects
    };

    if (onGenerate) {
      onGenerate(requestData);
    }
  };

  return (
    <div style={styles.formContainer}>
      {/* 职位描述部分 */}
      <div>
        <h3 style={styles.sectionTitle}>职位描述 (JD)</h3>
        <Card style={styles.card}>
          <TextArea
            value={basicInfo.jd}
            onInput={handleBasicInfoChange('jd')}
            style={styles.jdTextArea}
            placeholder="请输入职位描述和要求..."
          />
        </Card>
      </div>

      <hr style={styles.divider} />

      {/* 基本信息部分 */}
      <div>
        <h3 style={styles.sectionTitle}>基本信息</h3>
        <Card style={styles.card}>
          <FormLayout>
            <div style={styles.formRow}>
              <TextField
                label="姓名"
                value={basicInfo.name}
                onInput={handleBasicInfoChange('name')}
                style={{ ...styles.formField, width: '200px'}}
              />
              <ComboBox
                label="性别"
                items={['男', '女']}
                value={basicInfo.sex}
                onValueChanged={(e) => setBasicInfo({...basicInfo, sex: e.detail.value})}
                style={{ ...styles.formField, width: '120px' }}
                allowCustomValue={false}
              />
            </div>

            <div style={styles.formRow}>
              <DatePicker
                label="出生日期"
                value={basicInfo.birthDate}
                onValueChanged={handleDateChange('birthDate')}
                style={{ ...styles.formField, width: '200px' }}
              />
              <TextField
                label="邮箱"
                value={basicInfo.email}
                onInput={handleBasicInfoChange('email')}
                style={{ ...styles.formField, width: '250px' }}
              />
            </div>

            <TextField
              label="电话"
              value={basicInfo.phone}
              onInput={handleBasicInfoChange('phone')}
              style={{ ...styles.formField, width: '250px' }}
            />
          </FormLayout>
        </Card>
      </div>

      <hr style={styles.divider} />

      {/* 教育背景部分 */}
      <div>
        <h3 style={styles.sectionTitle}>教育背景</h3>
        {educations.map((edu, index) => (
          <Card key={index} style={styles.card}>
            <FormLayout>
              <div style={styles.formRow}>
                <DatePicker
                  label="开始时间"
                  value={edu.begin}
                  onValueChanged={(e) => handleEducationChange(index, 'begin')({ target: { value: e.detail.value } })}
                  style={{ ...styles.formField, width: '150px' }}
                />
                <DatePicker
                  label="结束时间"
                  value={edu.end}
                  onValueChanged={(e) => handleEducationChange(index, 'end')({ target: { value: e.detail.value } })}
                  style={{ ...styles.formField, width: '150px' }}
                />
              </div>

              <div style={styles.formRow}>
                <TextField
                  label="学校"
                  value={edu.university}
                  onInput={handleEducationChange(index, 'university')}
                  style={{ ...styles.formField, width: '300px' }}
                />
                <TextField
                  label="专业"
                  value={edu.major}
                  onInput={handleEducationChange(index, 'major')}
                  style={{ ...styles.formField, width: '200px' }}
                />
              </div>

              <TextField
                label="学位"
                value={edu.degree}
                onInput={handleEducationChange(index, 'degree')}
                style={{ ...styles.formField, width: '150px' }}
              />

              <div style={{ textAlign: 'right', marginTop: '10px' }}>
                <Button
                  theme="error tertiary"
                  onClick={() => removeEducation(index)}
                  style={styles.button}
                >
                  删除此项
                </Button>
              </div>
            </FormLayout>
          </Card>
        ))}

        <div style={{ textAlign: 'right', marginTop: '10px' }}>
          <Button
            theme="primary"
            onClick={addEducation}
            style={styles.button}
          >
            添加教育背景
          </Button>
        </div>
      </div>

      <hr style={styles.divider} />

      {/* 工作经历部分 */}
      <div>
        <h3 style={styles.sectionTitle}>工作经历</h3>
        {works.map((work, index) => (
          <Card key={index} style={styles.card}>
            <FormLayout>
              <div style={styles.formRow}>
                <DatePicker
                  label="开始时间"
                  value={work.begin}
                  onValueChanged={(e) => handleWorkChange(index, 'begin')({ target: { value: e.detail.value } })}
                  style={{ ...styles.formField, width: '150px' }}
                />
                <DatePicker
                  label="结束时间"
                  value={work.end}
                  onValueChanged={(e) => handleWorkChange(index, 'end')({ target: { value: e.detail.value } })}
                  style={{ ...styles.formField, width: '150px' }}
                />
              </div>

              <TextField
                label="公司"
                value={work.company}
                onInput={handleWorkChange(index, 'company')}
                style={{ ...styles.formField, width: '300px' }}
              />

              <TextField
                label="工作内容"
                value={work.workContent}
                onInput={handleWorkChange(index, 'workContent')}
                style={{ ...styles.formField, width: '100%' }}
              />

              <div style={{ textAlign: 'right', marginTop: '10px' }}>
                <Button
                  theme="error tertiary"
                  onClick={() => removeWork(index)}
                  style={styles.button}
                >
                  删除此项
                </Button>
              </div>
            </FormLayout>
          </Card>
        ))}

        <div style={{ textAlign: 'right', marginTop: '10px' }}>
          <Button
            theme="primary"
            onClick={addWork}
            style={styles.button}
          >
            添加工作经历
          </Button>
        </div>
      </div>

      <hr style={styles.divider} />

      {/* 项目经历部分 */}
      <div>
        <h3 style={styles.sectionTitle}>项目经历</h3>
        {projects.map((project, index) => (
          <Card key={index} style={styles.card}>
            <FormLayout>
              <div style={styles.formRow}>
                <DatePicker
                  label="开始时间"
                  value={project.begin}
                  onValueChanged={(e) => handleProjectChange(index, 'begin')({ target: { value: e.detail.value } })}
                  style={{ ...styles.formField, width: '150px' }}
                />
                <DatePicker
                  label="结束时间"
                  value={project.end}
                  onValueChanged={(e) => handleProjectChange(index, 'end')({ target: { value: e.detail.value } })}
                  style={{ ...styles.formField, width: '150px' }}
                />
              </div>

              <TextField
                label="项目名称"
                value={project.name}
                onInput={handleProjectChange(index, 'name')}
                style={{ ...styles.formField, width: '300px' }}
              />

              <TextField
                label="项目内容"
                value={project.content}
                onInput={handleProjectChange(index, 'content')}
                style={{ ...styles.formField, width: '100%' }}
              />

              <TextField
                label="使用技术"
                value={project.skills}
                onInput={handleProjectChange(index, 'skills')}
                style={{ ...styles.formField, width: '100%' }}
              />

              <div style={{ textAlign: 'right', marginTop: '10px' }}>
                <Button
                  theme="error tertiary"
                  onClick={() => removeProject(index)}
                  style={styles.button}
                >
                  删除此项
                </Button>
              </div>
            </FormLayout>
          </Card>
        ))}

        <div style={{ textAlign: 'right', marginTop: '10px' }}>
          <Button
            theme="primary"
            onClick={addProject}
            style={styles.button}
          >
            添加项目经历
          </Button>
        </div>
      </div>

      {/* 生成简历按钮 */}
      <div style={{ textAlign: 'center', marginTop: '30px' }}>
        <Button
          theme="primary"
          onClick={generateResume}
          style={{ padding: '12px 24px' }}
        >
          生成简历
        </Button>
      </div>
    </div>
  );
};

export default ResumeForm;