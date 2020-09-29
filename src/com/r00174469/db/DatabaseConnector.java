package com.r00174469.db;

import com.r00174469.ui.Interface;

import java.sql.*;


public class DatabaseConnector {
    private Connection con;
    private String url;
    private String user;
    private String password;

    public DatabaseConnector(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }
    public Object query(String statement, Object... parameters){
        ResultSet rs = null;
        int rows = -1;
        boolean update = false;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(url, user, password);
            PreparedStatement stmt = con.prepareStatement(statement);
            int counter = 1;
            for(Object obj : parameters){
                if(obj instanceof String){
                    stmt.setString(counter,(String)obj);
                }else if(obj instanceof Integer){
                    stmt.setInt(counter,(Integer)obj);
                }else if(obj instanceof Date){
                    stmt.setDate(counter,(Date)obj);
                }else if(obj instanceof Float){
                    stmt.setFloat(counter,(Float)obj);
                }
                counter++;
            }

            if(statement.toLowerCase().contains("insert") || statement.toLowerCase().contains("delete")){
                update = true;
                rows = stmt.executeUpdate();
                Interface.print("Updated "+ rows + " rows",1);
            }else {
                rs = stmt.executeQuery();
            }
            if(update){
                return rows;
            }else{
                return rs;
            }
        }
        catch (java.sql.SQLIntegrityConstraintViolationException foreignKeyException){
            return "FKError";
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }


}
