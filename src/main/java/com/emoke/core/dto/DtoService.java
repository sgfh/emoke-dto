package com.emoke.core.dto;

import com.emoke.core.dto.annotation.Dto;

import com.emoke.core.dto.pojo.Person;
import com.emoke.core.dto.pojo.PersonDto;
import com.emoke.core.dto.pojo.Student;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 转换dto
 */
@Service
public class DtoService {


    /**
     * @param cls:需要转换的dto实体类
     * @param data:已经有值的对象，需要转t对象
     * @return List<E>
     */
    public <E, D> List<E> run(Class<E> cls, List<D> data) throws IllegalAccessException, InstantiationException {
        if (data == null)
            throw new RuntimeException("data can not be null");
        List<E> list = new ArrayList<>();
        for (D d : data) {
            E o = cls.newInstance();
            run(o, d);
            list.add(o);
        }
        return list;
    }

    /**
     * @param t:需要转换的dto实体类
     * @param d:已经有值的对象，需要转t对象
     */
    public <E, D> void run(E t, D d) {
        //获取泛型中的属性
        Field[] fields = t.getClass().getDeclaredFields();
        if (0 == fields.length)
            return;
        for (Field field : fields) {
            Dto dto = field.getAnnotation(Dto.class);
            if (dto == null)
                throw new RuntimeException("Dto annotation is not found");
            //查询出d中该class
            Class dClass = d.getClass();
            Field[] dFields = dClass.getDeclaredFields();
            for (Field dField : dFields) {
                if (dto.name().equals(dField.getName())) {
                    //找到该对应属性
                    if (isBaseType(dField)) {
                        //是基本类型,将d中的值注入到t中
                        setFieldValueByFieldName(field.getName(), t, getFieldValue(d, dField));
                    } else {
                        //该属性是实体类
                        Object pojo = Objects.requireNonNull(getFieldValue(d, dField));
                        Class cls = pojo.getClass();
                        Field[] pFields = cls.getDeclaredFields();
                        for (Field pField : pFields) {
                            if (dto.field().equals(pField.getName())) {
                                Object object = getFieldValue(pojo, pField);
                                //是基本类型,将d中的值注入到t中
                                setFieldValueByFieldName(field.getName(), t, object);
                            }
                        }
                    }
                }
            }
        }
    }


    /**
     * 判断object是否为基本类型
     *
     * @return boolean
     */
    private boolean isBaseType(Field field) {
        String type = field.getGenericType().toString();
        return type.equals("class java.lang.String") || type.equals("class java.lang.Integer") || type.equals("class java.lang.Double") || type.equals("class java.lang.Boolean") || type.equals("boolean") || type.equals("int") ||
                type.equals("long") || type.equals("double") || type.equals("float") || type.equals("class java.util.Date") || type.equals("class java.lang.Short");
    }

    /**
     * 查询get方法获取到的属性值
     */
    private Object getFieldValue(Object object, Field field) {
        try {
            Method m = object.getClass().getMethod(
                    "get" + getMethodName(field.getName()));
            return m.invoke(object);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private String getMethodName(String fildeName) {
        byte[] items = fildeName.getBytes();
        items[0] = (byte) ((char) items[0] - 'a' + 'A');
        return new String(items);
    }


    /**
     * 根据属性名设置属性值
     *
     * @param fieldName:属性名称
     * @param object:object
     */
    private void setFieldValueByFieldName(String fieldName, Object object, Object value) {
        try {
            // 获取obj类的字节文件对象
            Class c = object.getClass();
            // 获取该类的成员变量
            Field f = c.getDeclaredField(fieldName);
            // 取消语言访问检查
            f.setAccessible(true);
            // 给变量赋值
            f.set(object, value);
        } catch (Exception e) {
            //注入是比说明该属性不存在于子类中，此时需要向父类属性进行扫描
            setSuperField(object, fieldName, value);
        }
    }

    /**
     * 通过反射获取父类的值，并注入值
     */
    private void setSuperField(Object paramClass, String paramString, Object value) {
        try {
            Field field = paramClass.getClass().getSuperclass().getDeclaredField(paramString);
            field.setAccessible(true);
            field.set(paramClass, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        DtoService dtoService = new DtoService();
        Person person = new Person();
        Student student = new Student();
        student.setName("哈哈");
        person.setAge(14);
        person.setStudent(student);
        person.setP("cccc");
        person.setName("xxxxx");
        PersonDto personDto = new PersonDto();
        long start = System.currentTimeMillis();
        dtoService.run(personDto, person);

        System.out.print((System.currentTimeMillis() - start) + "========" + personDto.toString());
    }
}
