package test.lnwazg;

import java.util.List;

public class User
{
    String name;
    
    String sex;
    
    int age;
    
    List<String> hobbies;
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getSex()
    {
        return sex;
    }
    
    public void setSex(String sex)
    {
        this.sex = sex;
    }
    
    public int getAge()
    {
        return age;
    }
    
    public void setAge(int age)
    {
        this.age = age;
    }
    
    public List<String> getHobbies()
    {
        return hobbies;
    }
    
    public void setHobbies(List<String> hobbies)
    {
        this.hobbies = hobbies;
    }

    @Override
    public String toString()
    {
        return "User [name=" + name + ", sex=" + sex + ", age=" + age + ", hobbies=" + hobbies + "]";
    }
    
    
}
