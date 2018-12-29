package org.ltc.chatserver.database;

import org.apache.commons.dbutils.QueryRunner;

import java.sql.Connection;
import java.sql.SQLException;

public class LetDBUtils {
    public static QueryRunner getRunner() {
        return new QueryRunner(LetConnectionPool.getDataSource());
    }

    public static void doSqlInTransaction(OnDoInTransaction doInTransaction) {
        Connection connection = null;
        try {
            connection = LetConnectionPool.getConnection();
            connection.setAutoCommit(false);
            QueryRunner queryRunner = LetDBUtils.getRunner();
            doInTransaction.doSql(connection,queryRunner);
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback();
            }catch (Exception e1){
                e1.printStackTrace();
            }
        }finally {
            if(connection!=null){
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                LetConnectionPool.getThreadLocal().set(null);
            }
        }
    }

    public interface OnDoInTransaction{
        void doSql(Connection connection, QueryRunner queryRunner);
    }
}
