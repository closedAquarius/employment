import pymysql


class Match(object):
    # 建立数据库链接
    def __init__(self):
        try:
            self.connect = pymysql.connect(host="127.0.0.1",
                                           port=3306,
                                           user="root",
                                           password="Liu050804",
                                           database="spiderdatabase",
                                           charset="utf8")
            self.cursor = self.connect.cursor(cursor=pymysql.cursors.DictCursor)
        except Exception as e:
            print(f"数据库连接错误: {e}")
            raise

    # 关闭链接
    def __del__(self):
        self.connect.close()
        self.cursor.close()

    # 薪资优先匹配
    def __salaryPriority(self, sql_0, sql_1, sql_2, sql_3, sql_4, sql_5,
                         city, area, experience, education, skill: list, salary):
        try:
            if len(skill) != 0:
                self.cursor.execute(sql_0,
                                    (city, area, experience, education,
                                     skill[0], skill[1], skill[2], skill[3], skill[4], salary))
                self.connect.commit()
                result_0 = self.cursor.fetchone()
                self.cursor.execute(sql_1, (city, experience, education,
                                            skill[0], skill[1], skill[2], skill[3], skill[4], salary))
                self.connect.commit()
                result_1 = self.cursor.fetchone()
            else:
                self.cursor.execute(sql_0, (city, area, experience, education, salary))
                self.connect.commit()
                result_0 = self.cursor.fetchone()
                self.cursor.execute(sql_1, (city, experience, education, salary))
                self.connect.commit()
                result_1 = self.cursor.fetchone()
            # 比较绝对值取最接近期望薪资的值
            if result_0 is not None and result_1 is not None:
                if abs(salary - result_0['avg_salary']) < abs(salary - result_1['avg_salary']):
                    return result_0
                else:
                    return result_1
            else:
                if len(skill) != 0:
                    self.cursor.execute(sql_2,
                                        (city, area, education,
                                         skill[0], skill[1], skill[2], skill[3], skill[4], salary))
                    self.connect.commit()
                    result_0 = self.cursor.fetchone()
                    self.cursor.execute(sql_3, (city, education,
                                                skill[0], skill[1], skill[2], skill[3], skill[4], salary))
                    self.connect.commit()
                    result_1 = self.cursor.fetchone()
                else:
                    self.cursor.execute(sql_2, (city, area, education, salary))
                    self.connect.commit()
                    result_0 = self.cursor.fetchone()
                    self.cursor.execute(sql_3, (city, education, salary))
                    self.connect.commit()
                    result_1 = self.cursor.fetchone()
                # 比较绝对值取最接近期望薪资的值
                if result_0 is not None and result_1 is not None:
                    if abs(salary - result_0['avg_salary']) < abs(salary - result_1['avg_salary']):
                        return result_0
                    else:
                        return result_1
                else:
                    if len(skill) != 0:
                        self.cursor.execute(sql_4,
                                            (city, area,
                                             skill[0], skill[1], skill[2], skill[3], skill[4], salary))
                        self.connect.commit()
                        result_0 = self.cursor.fetchone()
                        self.cursor.execute(sql_5, (city,
                                                    skill[0], skill[1], skill[2], skill[3], skill[4], salary))
                        self.connect.commit()
                        result_1 = self.cursor.fetchone()
                    else:
                        self.cursor.execute(sql_4, (city, area, salary))
                        self.connect.commit()
                        result_0 = self.cursor.fetchone()
                        self.cursor.execute(sql_5, (city, salary))
                        self.connect.commit()
                        result_1 = self.cursor.fetchone()
                    # 比较绝对值取最接近期望薪资的值
                    if result_0 is not None and result_1 is not None:
                        if abs(salary - result_0['avg_salary']) < abs(salary - result_1['avg_salary']):
                            return result_0
                        else:
                            return result_1
                    else:
                        # 无结果返回false
                        return False

        except Exception as e:
            self.connect.rollback()
            print(">>>读取数据失败 执行MySQL: {} 时出错：{}", e)

    # 地域优先匹配
    def __areaPriority(self, sql_0, sql_1, sql_2, sql_3, sql_4, sql_5,
                       city, area, experience, education, skill: list, salary):
        try:
            if len(skill) != 0:
                self.cursor.execute(sql_0,
                                    (city, area, experience, education,
                                     skill[0], skill[1], skill[2], skill[3], skill[4], salary))
                self.connect.commit()
                result_0 = self.cursor.fetchone()
                self.cursor.execute(sql_1, (
                    city, experience, education, skill[0], skill[1], skill[2], skill[3], skill[4], salary))
                self.connect.commit()
                result_1 = self.cursor.fetchone()
            else:
                self.cursor.execute(sql_0, (city, area, experience, education, salary))
                self.connect.commit()
                result_0 = self.cursor.fetchone()
                self.cursor.execute(sql_1, (city, experience, education, salary))
                self.connect.commit()
                result_1 = self.cursor.fetchone()
            if result_0 is not None:
                return result_0
            elif result_1 is not None:
                return result_1
            else:
                if len(skill) != 0:
                    self.cursor.execute(sql_2,
                                        (city, area, education,
                                         skill[0], skill[1], skill[2], skill[3], skill[4], salary))
                    self.connect.commit()
                    result_0 = self.cursor.fetchone()
                    self.cursor.execute(sql_3, (
                        city, education, skill[0], skill[1], skill[2], skill[3], skill[4], salary))
                    self.connect.commit()
                    result_1 = self.cursor.fetchone()
                else:
                    self.cursor.execute(sql_2, (city, area, education, salary))
                    self.connect.commit()
                    result_0 = self.cursor.fetchone()
                    self.cursor.execute(sql_3, (city, education, salary))
                    self.connect.commit()
                    result_1 = self.cursor.fetchone()
                if result_0 is not None:
                    return result_0
                elif result_1 is not None:
                    return result_1
                else:
                    if len(skill) != 0:
                        self.cursor.execute(sql_4,
                                            (city, area,
                                             skill[0], skill[1], skill[2], skill[3], skill[4], salary))
                        self.connect.commit()
                        result_0 = self.cursor.fetchone()
                        self.cursor.execute(sql_5, (
                            city, skill[0], skill[1], skill[2], skill[3], skill[4], salary))
                        self.connect.commit()
                        result_1 = self.cursor.fetchone()
                    else:
                        self.cursor.execute(sql_4, (city, area, salary))
                        self.connect.commit()
                        result_0 = self.cursor.fetchone()
                        self.cursor.execute(sql_5, (city, salary))
                        self.connect.commit()
                        result_1 = self.cursor.fetchone()
                    if result_0 is not None:
                        return result_0
                    elif result_1 is not None:
                        return result_1
                    else:
                        return False
        except Exception as e:
            self.connect.rollback()
            print(">>>读取数据失败 执行MySQL: {} 时出错：{}", e)

    # 按照语言选择sql语句
    @staticmethod
    def __sqlChoose(lan, skill: list):
        if len(skill) != 0:

            if lan == 'Java':
                sql_0 = '''select * from java_data 
                           where city = %s and area = %s and experience = %s and education = %s
                           and(skill like %s or skill like %s or skill like %s or skill like %s or skill like %s)
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_1 = '''select * from java_data 
                           where city = %s and experience = %s and education = %s
                           and(skill like %s or skill like %s or skill like %s or skill like %s or skill like %s)
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_2 = '''select * from java_data 
                           where city = %s and area = %s and education = %s
                           and(skill like %s or skill like %s or skill like %s or skill like %s or skill like %s)
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_3 = '''select * from java_data 
                           where city = %s and education = %s
                           and(skill like %s or skill like %s or skill like %s or skill like %s or skill like %s)
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_4 = '''select * from java_data 
                           where city = %s and area = %s 
                           and(skill like %s or skill like %s or skill like %s or skill like %s or skill like %s)
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_5 = '''select * from java_data 
                           where city = %s 
                           and(skill like %s or skill like %s or skill like %s or skill like %s or skill like %s)
                           order by abs( %s - avg_salary) LIMIT 1
                    '''

            elif lan == 'Python':
                sql_0 = '''select * from python_data 
                           where city = %s and area = %s and experience = %s and education = %s
                           and(skill like %s or skill like %s or skill like %s or skill like %s or skill like %s)
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_1 = '''select * from python_data 
                           where city = %s and experience = %s and education = %s
                           and(skill like %s or skill like %s or skill like %s or skill like %s or skill like %s)
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_2 = '''select * from python_data
                           where city = %s and area = %s and education = %s
                           and(skill like %s or skill like %s or skill like %s or skill like %s or skill like %s)
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_3 = '''select * from python_data
                           where city = %s and education = %s
                           and(skill like %s or skill like %s or skill like %s or skill like %s or skill like %s)
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_4 = '''select * from python_data
                           where city = %s and area = %s 
                           and(skill like %s or skill like %s or skill like %s or skill like %s or skill like %s)
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_5 = '''select * from python_data
                           where city = %s 
                           and(skill like %s or skill like %s or skill like %s or skill like %s or skill like %s)
                           order by abs( %s - avg_salary) LIMIT 1
                    '''

            elif lan == 'PHP':
                sql_0 = '''select * from php_data 
                           where city = %s and area = %s and experience = %s and education = %s
                           and(skill like %s or skill like %s or skill like %s or skill like %s or skill like %s)
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_1 = '''select * from php_data 
                           where city = %s and experience = %s and education = %s
                           and(skill like %s or skill like %s or skill like %s or skill like %s or skill like %s)
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_2 = '''select * from php_data 
                           where city = %s and area = %s and education = %s
                           and(skill like %s or skill like %s or skill like %s or skill like %s or skill like %s)
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_3 = '''select * from php_data 
                           where city = %s and education = %s
                           and(skill like %s or skill like %s or skill like %s or skill like %s or skill like %s)
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_4 = '''select * from php_data 
                           where city = %s and area = %s 
                           and(skill like %s or skill like %s or skill like %s or skill like %s or skill like %s)
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_5 = '''select * from php_data 
                           where city = %s 
                           and(skill like %s or skill like %s or skill like %s or skill like %s or skill like %s)
                           order by abs( %s - avg_salary) LIMIT 1
                    '''

            elif lan == 'C/C++':
                sql_0 = '''select * from c_data 
                           where city = %s and area = %s and experience = %s and education = %s
                           and(skill like %s or skill like %s or skill like %s or skill like %s or skill like %s)
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_1 = '''select * from c_data 
                           where city = %s and experience = %s and education = %s
                           and(skill like %s or skill like %s or skill like %s or skill like %s or skill like %s)
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_2 = '''select * from c_data 
                           where city = %s and area = %s and education = %s
                           and(skill like %s or skill like %s or skill like %s or skill like %s or skill like %s)
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_3 = '''select * from c_data 
                           where city = %s and education = %s
                           and(skill like %s or skill like %s or skill like %s or skill like %s or skill like %s)
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_4 = '''select * from c_data 
                           where city = %s and area = %s 
                           and(skill like %s or skill like %s or skill like %s or skill like %s or skill like %s)
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_5 = '''select * from c_data 
                           where city = %s 
                           and(skill like %s or skill like %s or skill like %s or skill like %s or skill like %s)
                           order by abs( %s - avg_salary) LIMIT 1
                    '''

            elif lan == 'C#/.NET':
                sql_0 = '''select * from c23_data 
                           where city = %s and area = %s and experience = %s and education = %s
                           and(skill like %s or skill like %s or skill like %s or skill like %s or skill like %s)
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_1 = '''select * from c23_data 
                           where city = %s and experience = %s and education = %s
                           and(skill like %s or skill like %s or skill like %s or skill like %s or skill like %s)
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_2 = '''select * from c23_data 
                           where city = %s and area = %s and education = %s
                           and(skill like %s or skill like %s or skill like %s or skill like %s or skill like %s)
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_3 = '''select * from c23_data 
                           where city = %s and education = %s
                           and(skill like %s or skill like %s or skill like %s or skill like %s or skill like %s)
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_4 = '''select * from c23_data 
                           where city = %s and area = %s 
                           and(skill like %s or skill like %s or skill like %s or skill like %s or skill like %s)
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_5 = '''select * from c23_data 
                           where city = %s 
                           and(skill like %s or skill like %s or skill like %s or skill like %s or skill like %s)
                           order by abs( %s - avg_salary) LIMIT 1
                    '''

            elif lan == 'Unity3D/UE':
                sql_0 = '''select * from u3due_data 
                           where city = %s and area = %s and experience = %s and education = %s
                           and(skill like %s or skill like %s or skill like %s or skill like %s or skill like %s)
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_1 = '''select * from u3due_data 
                           where city = %s and experience = %s and education = %s
                           and(skill like %s or skill like %s or skill like %s or skill like %s or skill like %s)
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_2 = '''select * from u3due_data 
                           where city = %s and area = %s and education = %s
                           and(skill like %s or skill like %s or skill like %s or skill like %s or skill like %s)
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_3 = '''select * from u3due_data 
                           where city = %s and education = %s
                           and(skill like %s or skill like %s or skill like %s or skill like %s or skill like %s)
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_4 = '''select * from u3due_data 
                           where city = %s and area = %s 
                           and(skill like %s or skill like %s or skill like %s or skill like %s or skill like %s)
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_5 = '''select * from u3due_data 
                           where city = %s 
                           and(skill like %s or skill like %s or skill like %s or skill like %s or skill like %s)
                           order by abs( %s - avg_salary) LIMIT 1
                    '''

            elif lan == 'IOS':
                sql_0 = '''select * from ios_data 
                           where city = %s and area = %s and experience = %s and education = %s
                           and(skill like %s or skill like %s or skill like %s or skill like %s or skill like %s)
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_1 = '''select * from ios_data 
                           where city = %s and experience = %s and education = %s
                           and(skill like %s or skill like %s or skill like %s or skill like %s or skill like %s)
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_2 = '''select * from ios_data 
                           where city = %s and area = %s and education = %s
                           and(skill like %s or skill like %s or skill like %s or skill like %s or skill like %s)
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_3 = '''select * from ios_data 
                           where city = %s and education = %s
                           and(skill like %s or skill like %s or skill like %s or skill like %s or skill like %s)
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_4 = '''select * from ios_data 
                           where city = %s and area = %s 
                           and(skill like %s or skill like %s or skill like %s or skill like %s or skill like %s)
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_5 = '''select * from ios_data 
                           where city = %s 
                           and(skill like %s or skill like %s or skill like %s or skill like %s or skill like %s)
                           order by abs( %s - avg_salary) LIMIT 1
                    '''

            elif lan == 'Android':
                sql_0 = '''select * from android_data 
                           where city = %s and area = %s and experience = %s and education = %s
                           and(skill like %s or skill like %s or skill like %s or skill like %s or skill like %s)
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_1 = '''select * from android_data 
                           where city = %s and experience = %s and education = %s
                           and(skill like %s or skill like %s or skill like %s or skill like %s or skill like %s)
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_2 = '''select * from android_data 
                           where city = %s and area = %s and education = %s
                           and(skill like %s or skill like %s or skill like %s or skill like %s or skill like %s)
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_3 = '''select * from android_data 
                           where city = %s and education = %s
                           and(skill like %s or skill like %s or skill like %s or skill like %s or skill like %s)
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_4 = '''select * from android_data 
                           where city = %s and area = %s 
                           and(skill like %s or skill like %s or skill like %s or skill like %s or skill like %s)
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_5 = '''select * from android_data 
                           where city = %s 
                           and(skill like %s or skill like %s or skill like %s or skill like %s or skill like %s)
                           order by abs( %s - avg_salary) LIMIT 1
                    '''

            elif lan == 'web前端':
                sql_0 = '''select * from web_data 
                           where city = %s and area = %s and experience = %s and education = %s
                           and(skill like %s or skill like %s or skill like %s or skill like %s or skill like %s)
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_1 = '''select * from web_data 
                           where city = %s and experience = %s and education = %s
                           and(skill like %s or skill like %s or skill like %s or skill like %s or skill like %s)
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_2 = '''select * from web_data 
                           where city = %s and area = %s and education = %s
                           and(skill like %s or skill like %s or skill like %s or skill like %s or skill like %s)
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_3 = '''select * from web_data 
                           where city = %s and education = %s
                           and(skill like %s or skill like %s or skill like %s or skill like %s or skill like %s)
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_4 = '''select * from web_data 
                           where city = %s and area = %s 
                           and(skill like %s or skill like %s or skill like %s or skill like %s or skill like %s)
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_5 = '''select * from web_data 
                           where city = %s 
                           and(skill like %s or skill like %s or skill like %s or skill like %s or skill like %s)
                           order by abs( %s - avg_salary) LIMIT 1
                    '''

            elif lan == '算法':
                sql_0 = '''select * from algorithm_data 
                           where city = %s and area = %s and experience = %s and education = %s
                           and(skill like %s or skill like %s or skill like %s or skill like %s or skill like %s)
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_1 = '''select * from algorithm_data 
                           where city = %s and experience = %s and education = %s
                           and(skill like %s or skill like %s or skill like %s or skill like %s or skill like %s)
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_2 = '''select * from algorithm_data 
                           where city = %s and area = %s and education = %s
                           and(skill like %s or skill like %s or skill like %s or skill like %s or skill like %s)
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_3 = '''select * from algorithm_data 
                           where city = %s and education = %s
                           and(skill like %s or skill like %s or skill like %s or skill like %s or skill like %s)
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_4 = '''select * from algorithm_data 
                           where city = %s and area = %s 
                           and(skill like %s or skill like %s or skill like %s or skill like %s or skill like %s)
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_5 = '''select * from algorithm_data 
                           where city = %s 
                           and(skill like %s or skill like %s or skill like %s or skill like %s or skill like %s)
                           order by abs( %s - avg_salary) LIMIT 1
                    '''

            elif lan == '测试':
                sql_0 = '''select * from test_data 
                           where city = %s and area = %s and experience = %s and education = %s
                           and(skill like %s or skill like %s or skill like %s or skill like %s or skill like %s)
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_1 = '''select * from test_data 
                           where city = %s and experience = %s and education = %s
                           and(skill like %s or skill like %s or skill like %s or skill like %s or skill like %s)
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_2 = '''select * from test_data 
                           where city = %s and area = %s and education = %s
                           and(skill like %s or skill like %s or skill like %s or skill like %s or skill like %s)
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_3 = '''select * from test_data 
                           where city = %s and education = %s
                           and(skill like %s or skill like %s or skill like %s or skill like %s or skill like %s)
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_4 = '''select * from test_data 
                           where city = %s and area = %s 
                           and(skill like %s or skill like %s or skill like %s or skill like %s or skill like %s)
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_5 = '''select * from test_data 
                           where city = %s 
                           and(skill like %s or skill like %s or skill like %s or skill like %s or skill like %s)
                           order by abs( %s - avg_salary) LIMIT 1
                    '''

            elif lan == '运维':
                sql_0 = '''select * from oam_data 
                           where city = %s and area = %s and experience = %s and education = %s
                           and(skill like %s or skill like %s or skill like %s or skill like %s or skill like %s)
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_1 = '''select * from oam_data 
                           where city = %s and experience = %s and education = %s
                           and(skill like %s or skill like %s or skill like %s or skill like %s or skill like %s)
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_2 = '''select * from oam_data 
                           where city = %s and area = %s and education = %s
                           and(skill like %s or skill like %s or skill like %s or skill like %s or skill like %s)
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_3 = '''select * from oam_data 
                           where city = %s and education = %s
                           and(skill like %s or skill like %s or skill like %s or skill like %s or skill like %s)
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_4 = '''select * from oam_data 
                           where city = %s and area = %s 
                           and(skill like %s or skill like %s or skill like %s or skill like %s or skill like %s)
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_5 = '''select * from oam_data 
                           where city = %s 
                           and(skill like %s or skill like %s or skill like %s or skill like %s or skill like %s)
                           order by abs( %s - avg_salary) LIMIT 1
                    '''

            else:
                return []
        else:
            if lan == 'Java':
                sql_0 = '''select * from java_data 
                           where city = %s and area = %s and experience = %s and education = %s
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_1 = '''select * from java_data 
                           where city = %s and experience = %s and education = %s
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_2 = '''select * from java_data 
                           where city = %s and area = %s and education = %s
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_3 = '''select * from java_data 
                           where city = %s and education = %s
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_4 = '''select * from java_data 
                           where city = %s and area = %s 
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_5 = '''select * from java_data 
                           where city = %s 
                           order by abs( %s - avg_salary) LIMIT 1
                    '''

            elif lan == 'Python':
                sql_0 = '''select * from python_data 
                           where city = %s and area = %s and experience = %s and education = %s
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_1 = '''select * from python_data 
                           where city = %s and experience = %s and education = %s
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_2 = '''select * from python_data 
                           where city = %s and area = %s and education = %s
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_3 = '''select * from python_data 
                           where city = %s and education = %s
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_4 = '''select * from python_data 
                           where city = %s and area = %s 
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_5 = '''select * from python_data 
                           where city = %s 
                           order by abs( %s - avg_salary) LIMIT 1
                    '''

            elif lan == 'PHP':
                sql_0 = '''select * from php_data 
                           where city = %s and area = %s and experience = %s and education = %s
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_1 = '''select * from php_data 
                           where city = %s and experience = %s and education = %s
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_2 = '''select * from php_data 
                           where city = %s and area = %s and education = %s
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_3 = '''select * from php_data 
                           where city = %s and education = %s
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_4 = '''select * from php_data 
                           where city = %s and area = %s 
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_5 = '''select * from php_data 
                           where city = %s 
                           order by abs( %s - avg_salary) LIMIT 1
                    '''

            elif lan == 'C/C++':
                sql_0 = '''select * from c_data 
                           where city = %s and area = %s and experience = %s and education = %s
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_1 = '''select * from c_data 
                           where city = %s and experience = %s and education = %s
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_2 = '''select * from c_data 
                           where city = %s and area = %s and education = %s
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_3 = '''select * from c_data 
                           where city = %s and education = %s
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_4 = '''select * from c_data 
                           where city = %s and area = %s 
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_5 = '''select * from c_data 
                           where city = %s 
                           order by abs( %s - avg_salary) LIMIT 1
                    '''

            elif lan == 'C#/.NET':
                sql_0 = '''select * from c23_data 
                           where city = %s and area = %s and experience = %s and education = %s
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_1 = '''select * from c23_data 
                           where city = %s and experience = %s and education = %s
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_2 = '''select * from c23_data 
                           where city = %s and area = %s and education = %s
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_3 = '''select * from c23_data 
                           where city = %s and education = %s
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_4 = '''select * from c23_data 
                           where city = %s and area = %s 
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_5 = '''select * from c23_data 
                           where city = %s 
                           order by abs( %s - avg_salary) LIMIT 1
                    '''

            elif lan == 'Unity3D/UE':
                sql_0 = '''select * from u3due_data 
                           where city = %s and area = %s and experience = %s and education = %s
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_1 = '''select * from u3due_data 
                           where city = %s and experience = %s and education = %s
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_2 = '''select * from u3due_data 
                           where city = %s and area = %s and education = %s
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_3 = '''select * from u3due_data 
                           where city = %s and education = %s
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_4 = '''select * from u3due_data 
                           where city = %s and area = %s 
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_5 = '''select * from u3due_data 
                           where city = %s 
                           order by abs( %s - avg_salary) LIMIT 1
                    '''

            elif lan == 'IOS':
                sql_0 = '''select * from ios_data 
                           where city = %s and area = %s and experience = %s and education = %s
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_1 = '''select * from ios_data 
                           where city = %s and experience = %s and education = %s
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_2 = '''select * from ios_data 
                           where city = %s and area = %s and education = %s
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_3 = '''select * from ios_data 
                           where city = %s and education = %s
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_4 = '''select * from ios_data 
                           where city = %s and area = %s 
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_5 = '''select * from ios_data 
                           where city = %s 
                           order by abs( %s - avg_salary) LIMIT 1
                    '''

            elif lan == 'Android':
                sql_0 = '''select * from android_data 
                           where city = %s and area = %s and experience = %s and education = %s
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_1 = '''select * from android_data 
                           where city = %s and experience = %s and education = %s
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_2 = '''select * from android_data 
                           where city = %s and area = %s and education = %s
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_3 = '''select * from android_data 
                           where city = %s and education = %s
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_4 = '''select * from android_data 
                           where city = %s and area = %s 
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_5 = '''select * from android_data 
                           where city = %s 
                           order by abs( %s - avg_salary) LIMIT 1
                    '''

            elif lan == 'web前端':
                sql_0 = '''select * from web_data 
                           where city = %s and area = %s and experience = %s and education = %s
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_1 = '''select * from web_data 
                           where city = %s and experience = %s and education = %s
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_2 = '''select * from web_data 
                           where city = %s and area = %s and education = %s
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_3 = '''select * from web_data 
                           where city = %s and education = %s
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_4 = '''select * from web_data 
                           where city = %s and area = %s 
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_5 = '''select * from web_data 
                           where city = %s 
                           order by abs( %s - avg_salary) LIMIT 1
                    '''

            elif lan == '算法':
                sql_0 = '''select * from algorithm_data 
                           where city = %s and area = %s and experience = %s and education = %s
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_1 = '''select * from algorithm_data 
                           where city = %s and experience = %s and education = %s
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_2 = '''select * from algorithm_data 
                           where city = %s and area = %s and education = %s
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_3 = '''select * from algorithm_data 
                           where city = %s and education = %s
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_4 = '''select * from algorithm_data 
                           where city = %s and area = %s 
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_5 = '''select * from algorithm_data 
                           where city = %s 
                           order by abs( %s - avg_salary) LIMIT 1
                    '''

            elif lan == '测试':
                sql_0 = '''select * from test_data 
                           where city = %s and area = %s and experience = %s and education = %s
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_1 = '''select * from test_data 
                           where city = %s and experience = %s and education = %s
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_2 = '''select * from test_data 
                           where city = %s and area = %s and education = %s
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_3 = '''select * from test_data 
                           where city = %s and education = %s
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_4 = '''select * from test_data 
                           where city = %s and area = %s 
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_5 = '''select * from test_data 
                           where city = %s 
                           order by abs( %s - avg_salary) LIMIT 1
                    '''

            elif lan == '运维':
                sql_0 = '''select * from oam_data 
                           where city = %s and area = %s and experience = %s and education = %s
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_1 = '''select * from oam_data 
                           where city = %s and experience = %s and education = %s
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_2 = '''select * from oam_data 
                           where city = %s and area = %s and education = %s
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_3 = '''select * from oam_data 
                           where city = %s and education = %s
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_4 = '''select * from oam_data 
                           where city = %s and area = %s 
                           order by abs( %s - avg_salary) LIMIT 1
                    '''
                sql_5 = '''select * from oam_data 
                           where city = %s 
                           order by abs( %s - avg_salary) LIMIT 1
                    '''

            else:
                return []
        return sql_0, sql_1, sql_2, sql_3, sql_4, sql_5

    # 岗位匹配算法
    def jobMach(self, lan, city, area, salary, education, experience, skill: list, priority):

        # 特殊处理。。。
        if education == '中技':
            experience = experience + '/中专'

        # 技能数大于5个则删除多余部分
        if len(skill) > 5:
            skill = skill[1:6]

        # 若要求技能数小于5个，则将缺少的部分填充为第一个元素
        while 0 < len(skill) < 5:
            skill.append(skill[0])

        if len(skill) != 0:
            skill[0] = "%" + skill[0] + "%"
            skill[1] = "%" + skill[1] + "%"
            skill[2] = "%" + skill[2] + "%"
            skill[3] = "%" + skill[3] + "%"
            skill[4] = "%" + skill[4] + "%"

        # 获取sql语句
        sql_list = self.__sqlChoose(lan, skill)
        sql_0, sql_1, sql_2, sql_3, sql_4, sql_5 = sql_list
        # 优先级
        if priority == 'salary':

            if sql_list is not None:
                jobData = self.__salaryPriority(sql_0=sql_0, sql_1=sql_1, sql_2=sql_2, sql_3=sql_3, sql_4=sql_4,
                                                sql_5=sql_5, city=city, area=area, experience=experience,
                                                education=education, skill=skill, salary=salary)
                return jobData
            else:
                return False
        # 地域优先情况
        else:

            if sql_list is not None:
                jobData = self.__areaPriority(sql_0=sql_0, sql_1=sql_1, sql_2=sql_2, sql_3=sql_3, sql_4=sql_4,
                                              sql_5=sql_5, city=city, area=area, experience=experience,
                                              education=education, skill=skill, salary=salary)
                return jobData
            else:
                return False
