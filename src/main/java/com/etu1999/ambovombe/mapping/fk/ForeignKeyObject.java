package com.etu1999.ambovombe.mapping.fk;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.etu1999.ambovombe.core.exception.DAOException;
import com.etu1999.ambovombe.core.process.DAO;
import com.etu1999.ambovombe.mapping.annotation.data.ForeignKey;
import com.etu1999.ambovombe.utils.Misc;

import lombok.Getter;
import lombok.Setter;

public class ForeignKeyObject {
    Field field;
    Class<?> type;
    Method setter;

    @Getter @Setter
    ForeignKey annotation;
    @Getter @Setter
    Object value;      // La valeur de la clé etrangère
    @Getter
    boolean init;

    public ForeignKeyObject(ForeignKey annotation, Method setter, Field field, Object value){
        setAnnotation(annotation);
        this.setter = setter;
        this.field = field;
        this.type = field.getType();
        this.init = annotation.initialize();
        this.value = value;
    }

    public Object init(Object object, Connection con) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, DAOException, SQLException, ClassNotFoundException{
        boolean init_con = false;
        Object val = null;
        ForeignType ft = this.annotation.foreignType();
        if(con == null){
            con = new DAO().createConnection();
            init_con = true;
        }
        if(ft == ForeignType.ManyToOne){
            DAO foreign_object = (DAO) this.type.newInstance();
            val = foreign_object.findById(con, getValue());
        }else if(ft == ForeignType.OneToMany){
            if(List.class.isAssignableFrom(field.getType())){
                ParameterizedType pt = (ParameterizedType) field.getGenericType();
                Class<?> pt_cl = (Class<?>) pt.getActualTypeArguments()[0];
                DAO foreign = (DAO) pt_cl.newInstance();
                val = foreign.findWhere(con, String.format(" %s = %s", annotation.mappedBy(), Misc.convertForSql(getValue())));
            }
        }
        if(init_con)
            con.close();
        return setter.invoke(object, val);
    }

    public String getIdName(){
        return getAnnotation().mappedBy();
    }

    public ForeignType getForeignType(){
        return getAnnotation().foreignType();
    }
    
}