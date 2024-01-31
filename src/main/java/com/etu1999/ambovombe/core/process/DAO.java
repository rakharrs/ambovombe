package com.etu1999.ambovombe.core.process;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.etu1999.ambovombe.core.exception.DAOException;
import com.etu1999.ambovombe.core.process.query.QueryForge;
import com.etu1999.ambovombe.mapping.annotation.misc.PrimaryKey;
import com.etu1999.ambovombe.utils.Dhelper;

import lombok.Getter;
import lombok.Setter;

/**
 * The DAO-ORM class
 * @author rakharrs
 */
@Getter @Setter
public class DAO extends DAC{

    public DAO(){
        super();
    }

    /**
     * Saves the current object's state to the database using the provided database connection.
     *
     * This method generates an INSERT query based on the object's fields and values using
     * the QueryForge.insert() method and executes it using the executeUpdate() method.
     *
     * @param con the valid Connection object to the database
     * @return the number of rows affected by the save operation
     * @throws IllegalAccessException if access to a field is not possible
     * @throws IllegalArgumentException if an illegal or inappropriate argument is passed
     * @throws InvocationTargetException if a method could not be invoked
     * @throws NoSuchMethodException if a requested method cannot be found
     * @throws SecurityException if a security violation is detected
     * @throws SQLException if a database access error occurs or the SQL statement
     *         does not return a ResultSet (such as an SQL INSERT statement)
     */
    public int save(Connection con) throws IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
            SecurityException, SQLException {
        return executeUpdate(con, QueryForge.insert(this));
    }

    /**
     * Updates the current object's state in the database using the provided database connection.
     *
     * This method generates an UPDATE query based on the object's fields and values using
     * the QueryForge.update() method and executes it using the executeUpdate() method.
     *
     * @param con the valid Connection object to the database
     * @return the number of rows affected by the update operation
     * @throws IllegalAccessException if access to a field is not possible
     * @throws IllegalArgumentException if an illegal or inappropriate argument is passed
     * @throws InvocationTargetException if a method could not be invoked
     * @throws NoSuchMethodException if a requested method cannot be found
     * @throws SecurityException if a security violation is detected
     * @throws SQLException if a database access error occurs or the SQL statement
     *         does not return a ResultSet (such as an SQL UPDATE statement)
     * @throws DAOException if an issue specific to the Data Access Object (DAO) layer occurs
     */
    public int update(Connection con) throws IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
            SecurityException, SQLException, DAOException {
        return executeUpdate(con, QueryForge.update(this));
    }


    public <T> List<T> findAll(Connection con) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, SQLException, DAOException, IllegalArgumentException, SecurityException, ClassNotFoundException{
        String query = QueryForge.selectAll(this);
        List<T> vql = DAC.sqltoArray(con, query, this);
        return vql;
    }

    public <T> T findById(Connection con, Object Id_value) throws DAOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, SQLException, IllegalArgumentException, SecurityException, ClassNotFoundException{
        String query = QueryForge.selectById(this, Id_value);
        List<T> vql = DAC.sqltoArray(con, query, this);
        try {
            return vql.get(0);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    public <T> List<T> findWhere(Connection con, String predicat) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, SQLException, DAOException, IllegalArgumentException, SecurityException, ClassNotFoundException{
        String query = QueryForge.selectWhere(this, predicat);
        System.out.println("findwherequery = "+ query);
        List<T> vql = DAC.sqltoArray(con, query, this);
        return vql;
    }

    /**
     * build a specific primary key for this object depending on this object primary key annotation
     * @param con the connection that will be used
     * @return the built primary key
     */
    public String buildPrimaryKey(Connection con)
            throws DAOException, SQLException {
        ArrayList<Field> fields = Dhelper.getDAFields(this);
        Field field = this.getFieldId();
        if(field.isAnnotationPresent(PrimaryKey.class) && field.getType() == String.class){
            PrimaryKey pk = field.getAnnotation(PrimaryKey.class);
            return constructPrimaryKey(con, pk);
        }throw new DAOException("No buildable primary key");
    }

    protected String constructPrimaryKey(Connection con, PrimaryKey pk)
            throws DAOException, SQLException {
        String val = "";
        String pkSeq = buildSequenceNb(con, pk);
        String prefix = pk.prefix();
        if(pk.prefix().isEmpty() || pk.prefix()==null)
            throw new DAOException("No buildable primary key");
        val+=prefix; val+=pkSeq;
        return val;
    }


    protected String buildSequenceNb(Connection con, PrimaryKey pk)
            throws DAOException, SQLException {
        StringBuilder pkSeq = new StringBuilder(new String());
        String seq = getSequence(con, pk);
        for (int i = 0; i < pk.length()-pk.prefix().length()-seq.length(); i++) pkSeq.append("0");
        for (int i = 0; i < seq.length(); i++) pkSeq.append(seq.charAt(i));
        return pkSeq.toString();
    }


    protected String getSequence(Connection con, PrimaryKey pk)
            throws DAOException, SQLException {
        if(pk.sequence().isEmpty() || pk.sequence()==null)
            throw new DAOException("No buildable primary key");
        return getSequence(con, pk.sequence());
    }

    public static String getSequence(Connection con, String sequence) 
            throws SQLException {
        Statement st = con.createStatement();
        String dbName = con.getMetaData().getDatabaseProductName();
        ResultSet rs = null;
        String val = null;
        switch (dbName) {
            case "PostgreSQL":
                rs = st.executeQuery("SELECT to_hex(nextval('"+sequence+"')) as nextval");
                break;
            default:
                //rs = st.executeQuery("SELECT "+ getGetSequence() + "as nextval from dual");
                break;
        }
        rs.next();
        val = rs.getString("nextval");
        rs.close(); st.close();
        return val;
    }
}
