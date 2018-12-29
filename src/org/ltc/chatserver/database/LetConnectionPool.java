package org.ltc.chatserver.database;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class LetConnectionPool {
    private static ComboPooledDataSource dataSource = new ComboPooledDataSource();
    private static ThreadLocal<Connection> threadLocal = new ThreadLocal<Connection>();
    static {
        try {
//            dataSource.setDriverClass("com.mysql.jdbc.Driver");
            dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/letchat");
            dataSource.setUser("root");
            dataSource.setPassword("12345678");
            dataSource.setMaxPoolSize(50);
            dataSource.setMinPoolSize(10);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static DataSource getDataSource(){
        return dataSource;
    }

    public static Connection getConnection() throws SQLException {
        Connection connection = threadLocal.get();
        if(connection == null){
            connection = dataSource.getConnection();
            threadLocal.set(connection);
            return connection;
        }
       return connection;
    }

    public static ThreadLocal<Connection> getThreadLocal(){
        return threadLocal;
    }

}
