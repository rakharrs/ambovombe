package com.etu1999.ambovombe.core.process;

import static com.etu1999.ambovombe.utils.Dhelper.getDAFields;
import static com.etu1999.ambovombe.utils.Dhelper.getIdField;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.etu1999.ambovombe.core.connection.GConnection;
import com.etu1999.ambovombe.core.connection.config.GConfiguration;
import com.etu1999.ambovombe.core.exception.DAOException;
import com.etu1999.ambovombe.core.process.config.PreProcess;
import com.etu1999.ambovombe.mapping.annotation.data.ForeignKey;
import com.etu1999.ambovombe.mapping.annotation.data.UnitSource;
import com.etu1999.ambovombe.mapping.fk.ForeignKeyObject;
import com.etu1999.ambovombe.mapping.fk.ForeignType;
import com.etu1999.ambovombe.utils.Dhelper;
import com.etu1999.ambovombe.utils.Misc;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

/**
 * Database access connection
 * @author rakharrs
 */
@Getter @Setter
class DAC implements Serializable{
    @JsonIgnore
    private GConnection connection;
    @JsonIgnore
    private Field fieldId;
    @JsonIgnore
    private String unitName;    // The database unit name from xml
    @JsonIgnore
    private String columns;     // Formatted as (id, name, ....)
    @JsonIgnore
    private String table;       // The table name
    @JsonIgnore
    private String id_column;
    @JsonIgnore
    private List<Field> inheritedFields;   // as the name indicate it
    @JsonIgnore
    private HashMap<String, ForeignKeyObject> foreignKeys = new HashMap<>();
    //private QueryForge queryBuilder = new QueryForge();
    
    DAC(){
        try {            
            GConfiguration configuartion = PreProcess.getConfiguration();
            Class<? extends DAC> cls = getClass();
            try {
                setFieldId(getIdField(this));
                setId_column(Dhelper.getColumnName(getFieldId()));
            } catch (DAOException e) {
                System.out.println(this.getClass().getName() + "- WARNING : no @Id");
            }
            
            setUnitName(cls.getAnnotation(UnitSource.class).value());
            setTable(Dhelper.getTableName(this));
            setInheritedFields(Dhelper.getSuperFields(cls));
            setColumns(Dhelper.getColumns(this));
            setConnection(configuartion.getConnections().get(getUnitName()));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Configuration error, verify database.xml");
        }
    }

    public Connection createConnection() throws ClassNotFoundException, SQLException{
        return getConnection().createConnection(false);
    }

    public int insert(){
        return 0;
    }

    /**
     * Create an ArrayList from the results of a SQL query
     * 
     * @param obj object that will be used to create the arraylist
     * @param rs  is the resultset
     * @return ArrayList containing the results of the query
     * @throws DAOException
     */
    public static <T> List<T> sqltoArray(Connection ocn, DAO obj, ResultSet rs) throws NoSuchMethodException,
            InvocationTargetException, InstantiationException, IllegalAccessException, SQLException, DAOException, IllegalArgumentException, SecurityException, ClassNotFoundException {
        ArrayList<Field> fields = getDAFields(obj);
        ArrayList<T> val = new ArrayList<T>();
        while (rs.next()) {
            T object = (T) createObject(ocn, rs, obj, fields);
            val.add(object);
        }
        return val;
    }

    /**
     * Create the object from the current resultset
     * @param rs             The result set
     * @param object         The object of return   
     * @param selectedFields List of the fields which will be setted
     */
    protected static DAO createObject(Connection con, ResultSet rs, DAO object, List<Field> selectedFields) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, DAOException, SQLException, ClassNotFoundException{
        DAO val = object.getClass().getConstructor().newInstance();
        for (Field field : selectedFields) {
            Field r_Field = field;
            if(field.isAnnotationPresent(ForeignKey.class))
                if(field.getAnnotation(ForeignKey.class).foreignType() == ForeignType.OneToMany)
                    r_Field = val.getFieldId();
            String columnName = Dhelper.getColumnName(r_Field);
            Object baseValue = rs.getObject(columnName);
            val.setFieldValue(con, field, baseValue);
        }
        return val;
    }

    /**
     * Sets the value of a specified field in 
     * the provided DAO (Data Access Object) instance.
     *
     * @param instancedObject                The instance of the DAO object.
     * @param field                          The field to be set.
     * @param value                          The value to be set for the specified field.
     * @return                               The DAO instance with the updated field value.
     */
    protected Object setFieldValue(Connection con, Field field, Object value)
            throws NoSuchMethodException, SecurityException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, InstantiationException, DAOException, SQLException, ClassNotFoundException {
        Class<?> fieldType = field.getType();
        if(field.isAnnotationPresent(ForeignKey.class)){
            ForeignKeyObject fko = new ForeignKeyObject(
                field.getAnnotation(ForeignKey.class),
                Misc.getSetter(this, field.getName(), fieldType),
                field,
                value);
            getForeignKeys().put(field.getName(), fko);
            if(fko.isInit())
                return fko.init(this, con);
            else return null;
        }
        Method setter = Misc.getSetter(this, field.getName(), fieldType);
        return (DAO) setter.invoke(this, value);
    }

    /**
     * Initialize the value of the desired foreign key
     * @param con       The Connection zhich zill be used to get the value of the foreign key
     * @param foreignKey_name The Property name annoted ForeignKey
     * @return The foreignKey attribute
     */
    public Object initForeignKey(Connection con, String foreignKey_name) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException, DAOException, SQLException{
        return getForeignKeys().get(foreignKey_name).init(this, con);
    }


    public static <T> List<T> sqltoArray(Connection con, String query, DAO obj) throws SQLException,
            InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, DAOException, IllegalArgumentException, SecurityException, ClassNotFoundException {
        Statement st = con.createStatement();
        System.out.println(query);
        ResultSet rs = st.executeQuery(query);
        List<T> val = sqltoArray(con, obj, rs);
        rs.close();
        st.close();
        return val;
    }

    

    /**
     * Executes an update SQL query on the specified database connection.
     * Executes the given SQL statement, which may be 
     * an INSERT, UPDATE, or DELETE statement or 
     * an SQL statement that returns nothing, such as an SQL DDL statement.
     * @param con   a valid Connection object to the database
     * @param query the SQL query to be executed for database update
     * @return      the number of rows affected by the update operation
     * @throws SQLException if a database access error occurs or the SQL statement
     *                      does not return a ResultSet (such as an SQL INSERT statement)
     */
    public int executeUpdate(Connection con, String query) throws SQLException{
        Statement st = con.createStatement();
        int val = st.executeUpdate(query);
        st.close();
        return val;
    }
}
