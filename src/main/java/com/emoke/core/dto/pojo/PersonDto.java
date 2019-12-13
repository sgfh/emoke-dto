package com.emoke.core.dto.pojo;

import com.emoke.core.dto.annotation.Dto;


public class PersonDto {
    @Dto(dtoClass = Person.class,name = "age",field = "age")
    private int age;
    @Dto(dtoClass = Person.class,name = "name",field = "name")
    private String name;
    @Dto(dtoClass = Person.class,name = "p",field = "p")
    private String p;
    @Dto(dtoClass = Student.class,name = "student",field = "name")
    private String student;

    public String getStudent() {
        return student;
    }

    public void setStudent(String student) {
        this.student = student;
    }

    @Override
    public String toString() {
        return "PersonDto{" +
                "age=" + age +
                ", name='" + name + '\'' +
                ", p='" + p + '\'' +
                ", student='" + student + '\'' +
                '}';
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getP() {
        return p;
    }

    public void setP(String p) {
        this.p = p;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
