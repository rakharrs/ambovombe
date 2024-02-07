package com.etu1999.ambovombe.core.process.query;

import static com.etu1999.ambovombe.utils.Dhelper.getIdField;
import static com.etu1999.ambovombe.utils.Misc.convertForSql;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import com.etu1999.ambovombe.core.exception.DAOException;
import com.etu1999.ambovombe.core.process.DAO;
import com.etu1999.ambovombe.mapping.annotation.data.ForeignKey;
import com.etu1999.ambovombe.mapping.fk.ForeignType;
import com.etu1999.ambovombe.utils.Dhelper;
import com.etu1999.ambovombe.utils.Misc;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class QueryForge {


    public static String update(DAO object) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, DAOException{
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append(String.format("UPDATE %s SET ", object.getTable()));
        String[] fieldsName = Dhelper.getFieldsName(object);
        for (int i = 0; i < fieldsName.length; i++) {
            queryBuilder.append(String.format(
                "%s = %s", 
                fieldsName[i], 
                getFieldValue(object, fieldsName[i])));
            if(i < fieldsName.length - 1)
                queryBuilder.append(" , ");
        }
        String idField = object.getFieldId().getName();
        queryBuilder.append(String.format(" where %s = %s", idField, getFieldValue(object, idField)));
        System.out.println(queryBuilder.toString());
        return queryBuilder.toString();
    }

    public static String insert(DAO object) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append(String.format("insert into %s(%s) values(%s)",object.getTable(),object.getColumns(), getFieldValues(Dhelper.getDAFields(object), object)));
        System.out.println("insert Jean " + queryBuilder.toString());
    
        return queryBuilder.toString();
    }

    public static String getFieldValues(List<Field> fields, DAO object) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{
        StringBuilder queryBuilder = new StringBuilder();
        int k = 1;
        for (int i = 0; i < fields.size(); i++) {
            Object attrb = null;
            if(fields.get(i).isAnnotationPresent(ForeignKey.class)){
                ForeignKey fk = fields.get(i).getAnnotation(ForeignKey.class);
                if(fk.foreignType() == ForeignType.ManyToOne){
                    DAO foreign_obj = (DAO) Misc.getGetter(object, fields.get(i).getName()).invoke(object);
                    if(foreign_obj != null){
                        attrb = Misc.getGetter(foreign_obj, foreign_obj.getFieldId().getName()).invoke(foreign_obj);
                    }
                    queryBuilder.append(Misc.convertForSql(attrb));
                    if(i < fields.size() - k)
                        queryBuilder.append(",");
                }if(fk.foreignType() == ForeignType.OneToMany){
                    int lastindex = queryBuilder.length()-1;
                    if (queryBuilder.charAt(lastindex) == ',')
                        queryBuilder.deleteCharAt(lastindex);

                    k++;
                }
                continue;
            }else{
                if(attrb == null)
                    attrb = Misc.getGetter(object, fields.get(i).getName()).invoke(object);
    
                queryBuilder.append(Misc.convertForSql(attrb));
                if(i < (fields.size() - k))
                    queryBuilder.append(",");
            }
        }
        return queryBuilder.toString();
    }

    public static String getFieldValues(String[] fieldsNames, DAO object) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{
        //String[] fieldsNames = Dhelper.getFieldsName(object);
        StringBuilder queryBuilder = new StringBuilder();
        for (int i = 0; i < fieldsNames.length; i++) {
            Object attrb = Misc.getGetter(object,fieldsNames[i]).invoke(object);
            queryBuilder.append(Misc.convertForSql(attrb));
            if(i < fieldsNames.length - 1)
                queryBuilder.append(",");
        }
        return queryBuilder.toString();
    }

    public static String getFieldValue(DAO object, String fieldName) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{
        Object attrb = Misc.getGetter(object, fieldName).invoke(object);
        return Misc.convertForSql(attrb);
    }

    /**
     * Returning the select * from query
     * @param object
     * @return
     */
    public static String selectAll(DAO object){
        String columns = object.getColumns();
        String table = object.getTable();
        String id_column = object.getId_column();
        String query = String.format("select %s from %s order by %s", columns, table, id_column);
        return query;
    }

    /**
     * Returning the select query where Id
     * @param object
     * @param value
     * @return
     * @throws DAOException
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public static String selectById(DAO object, Object value) throws DAOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{
        Field id = Dhelper.getIdField(object);
        String idName = Dhelper.getColumnName(id);
        String id_column = object.getId_column();
        String query = String.format(
            "select %s from %s where %s = %s order by %s" ,
                    object.getColumns(),
                    object.getTable(),
                    idName,
                    convertForSql(value),
                    id_column);
        return query;
    }
    /**
     * Returning the select query with the predicat
     * @param object
     * @param predicat eg: Id = 'ID007', Name = 'Bond'...
     * @return
     */
    public static String selectWhere(DAO object, String predicat){
        String id_column = object.getId_column();
        String query = String.format(
            "select %s from %s where %s", 
            object.getColumns(),
            object.getTable(),
            predicat,
            id_column);
        return query;
    }

    // Test panjiachen.github.io template
}
