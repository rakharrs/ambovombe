package com.etu1999.ambovombe.core.connection;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.w3c.dom.Element;

import com.etu1999.ambovombe.core.parser.XMLReader;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
/**
 * @author rakharrs
 */
public class GConnection implements Serializable{

    private Connection connection = null;

    private String name;        //Unit-name
    private String driver;
    private String url;
    private String dbname;
    private String user;
    private String password;

    /**
     * retrieving the full url to the database
     * @return
     */
    public String getFullUrl(){
        return (getUrl()+"/"+getDbname()+"?user="+getUser()+"&password="+getPassword());
    }

    /**
     * Constructor of GConnection
     * @param e the connection element from the xml
     * @throws Exception
     */
    public GConnection(Element e) throws Exception{
        setName(        XMLReader.getElementValue(e, "unit-name"));
        setDriver(      XMLReader.getElementValue(e, "driver"));
        setUrl(         XMLReader.getElementValue(e, "url"));
        setDbname(      XMLReader.getElementValue(e, "dbname"));
        setUser(        XMLReader.getElementValue(e, "user"));
        setPassword(    XMLReader.getElementValue(e, "password"));
    }

    public Connection getConnection() throws ClassNotFoundException, SQLException{
        if(this.connection == null)
            return defineConnection();
        return this.connection;
    }

    /**
     * Creating a connection based on the attribute then
     * Defining the attribute connection from it
     * @return the defined connection
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public Connection defineConnection() 
    throws  ClassNotFoundException, 
            SQLException
    {
        Connection con = createConnection(false);
        setConnection(con);
        return con;
    }

    /**
     * Creating a connection based on the attribute then
     * Defining the attribute connection from it
     * @return the defined connection
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public Connection init()
    throws  ClassNotFoundException,
            SQLException
    {
        return defineConnection();
    }

    /**
     * Creating a connection based on the GConnection attribute(s)
     * @param autocommit
     * @return The created connection 
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public Connection createConnection(boolean autocommit) 
    throws  ClassNotFoundException, 
            SQLException
    {
        Class.forName(getDriver());
        Connection con = DriverManager.getConnection(getFullUrl());
        con.setAutoCommit(autocommit);
        return con;
    }

    /**
     * Sets this connection's auto-commit mode to the given state. 
     * If a connection is in auto-commit mode, then all its SQL statements 
     * will be executed and committed as individual transactions. 
     * Otherwise, its SQL statements are grouped into transactions that are terminated 
     * by a call to either the method commit or the method rollback. 
     * By default, new GConnections are not in autocommit mode.
     * @param autocommit
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public void setAutoCommit(boolean autocommit) throws SQLException, ClassNotFoundException{
        getConnection().setAutoCommit(autocommit);
    }

    /**
     * Releases this Connection object's database and JDBC resources 
     * immediately instead of waiting for them to be automatically released.
     *  Calling the method close on a Connection object that is 
        already closed is a no-op.
        It is strongly recommended that an application 
        explicitly commits or rolls back an active transaction prior 
        to calling the close method. If the close method is called and 
        there is an active transaction, the results are 
        implementation-defined.
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public void close() throws SQLException, ClassNotFoundException{
        getConnection().close();;
    }

    /**
     * Makes all changes made since the previous commit/rollback permanent 
     * and releases any database locks currently held by this Connection object. 
     * This method should be used only when auto-commit mode has been disabled.
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public void commit() throws SQLException, ClassNotFoundException{
        getConnection().commit();
    }
}
