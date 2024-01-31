package com.etu1999.ambovombe.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.etu1999.ambovombe.core.exception.DAOException;
import com.etu1999.ambovombe.mapping.annotation.data.Column;
import com.etu1999.ambovombe.mapping.annotation.data.ForeignKey;
import com.etu1999.ambovombe.mapping.annotation.data.Id;
import com.etu1999.ambovombe.mapping.annotation.data.Table;
import com.etu1999.ambovombe.mapping.annotation.data.UnitSource;
import com.etu1999.ambovombe.mapping.annotation.misc.Inherit;
import com.etu1999.ambovombe.mapping.fk.ForeignType;

public class Dhelper {

    public static String getTableName(Object object){
        Class<?> cls = object.getClass();
        if(cls.isAnnotationPresent(Table.class)){
            String val = cls.getAnnotation(Table.class).value();
            if(!val.isEmpty())
                return val;
        }
        return object.getClass().getSimpleName().toLowerCase();
    }

    /**
     * Returns the fields annotated with @Column that are not null for the given
     * object.
     *
     * @param  object the object to get the fields for
     * @param  fields the fields to check for null values
     * @return the fields annotated with @Column that are not null
     */
    public static Field[] getNotNullFields(Object object, Field[] fields) {
        return Arrays.stream(fields)
                .filter(field -> {
                    field.setAccessible(true);
                    try {
                        return field.get(object) != null;
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toArray(Field[]::new);
    }

    /**
     * 
     * Returning the columns of the DAO object
     * @param object
     * @return
     * @throws DAOException
     */
    public static String getColumns(Object object) throws DAOException{
        List<Field> fields = getColDAFields(object);
        StringBuilder columns = new StringBuilder();
        System.out.println(fields.size());
        for (int i = 0; i < fields.size(); i++) {
            String columnName = getColumnName(fields.get(i));
            if(columnName != null){
                columns.append(columnName);
                if(i < fields.size() - 1)
                    columns.append(",");
            }
        }
        return columns.toString();
    }


    /**
     * Get the primary key field of the DAO Object
     * If return null, then it means that the object doesn't have any ID
     * @param object
     * @return
     * @throws DAOException
     */
    public static Field getIdField(Object object) throws DAOException{
        ArrayList<Field> fields = getDAFields(object);
        for (Field field : fields)
            if(field.isAnnotationPresent(Id.class))
                return field;
        throw new DAOException("The following object doesn't have any @Id");
    }

    public static String[] getFieldsName(Object obj){
        ArrayList<Field> fields = getDAFields(obj);
        String[] val = new String[fields.size()];
        for (int i = 0; i < val.length; i++)
            val[i] = fields.get(i).getName();
        return val;
    }

    public static String getColumnName(Field field) throws DAOException{
        if(field.isAnnotationPresent(Column.class)){
            Column column = field.getAnnotation(Column.class);
            if(column.value().isEmpty())
                return field.getName();
            else return column.value();
        }else if(field.isAnnotationPresent(ForeignKey.class)){
            ForeignKey fk = field.getAnnotation(ForeignKey.class);
            if(fk.foreignType() == ForeignType.OneToMany)
                return null;
            String reference = fk.mappedBy();
            if(reference.isEmpty())
                return field.getName();
            else return reference;
        }else throw new DAOException("The field : "+field.getName()+" is not a column");
    }

    /**
     * Get all the object's fields that are used in the database table
     * @param object the object that we want to get the fields
     * @return a List of those fields
     */
    public static ArrayList<Field> getDAFields(Object object){
        Field[] fields = object.getClass().getDeclaredFields();
        ArrayList<Field> val = new ArrayList<>();
        for (int i = 0; i < fields.length; i++)
            if( fields[i].isAnnotationPresent(Column.class) || 
                fields[i].isAnnotationPresent(ForeignKey.class))
                    val.add(fields[i]);
        return val;
    }

    public static ArrayList<Field> getColDAFields(Object object){
        Field[] fields = object.getClass().getDeclaredFields();
        ArrayList<Field> val = new ArrayList<>();
        for (int i = 0; i < fields.length; i++)
            if( fields[i].isAnnotationPresent(Column.class) || 
                (fields[i].isAnnotationPresent(ForeignKey.class) && 
                fields[i].getAnnotation(ForeignKey.class).foreignType() == ForeignType.ManyToOne))
                    val.add(fields[i]);
        return val;
    }

    /**
     * Getting the inherted field from the object class
     * @param object The object which sould be a DAO one
     */
    public static List<Field> getInheritedFields(Object object){
        Field[] fields = object.getClass().getDeclaredFields();
        ArrayList<Field> val = new ArrayList<>();
        for (int i = 0; i < fields.length; i++)
            if( fields[i].isAnnotationPresent(Inherit.class) && 
                fields[i].getAnnotation(Inherit.class).value())
                    val.add(fields[i]);
        return val;
    }

    /**
     * Getting the inherited field from the object superclass
     * @param object
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static List<Field> getSuperFields(Object object) throws InstantiationException, IllegalAccessException{
        Class<?> cls = object.getClass();
        ArrayList<Field> val = new ArrayList<>();
        while (cls.getSuperclass().isAnnotationPresent(UnitSource.class)) {
            cls = cls.getSuperclass();
            Object supero = cls.newInstance();
            val.addAll(getInheritedFields(object));
        }
        return val;
    }
}