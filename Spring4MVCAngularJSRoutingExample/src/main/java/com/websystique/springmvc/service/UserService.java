package com.websystique.springmvc.service;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.websystique.springmvc.model.User;
public class UserService {
	public static JdbcTemplate getJdbcTemplate(){
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
	    dataSource.setDriverClassName("com.mysql.jdbc.Driver");
	    dataSource.setUrl("jdbc:mysql://mysql8.db4free.net:3307/udhari");
	    dataSource.setUsername("sdevhade");
	    dataSource.setPassword("sachin225");
	    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		return jdbcTemplate;
	}	
	public static String validateUser(User user){
		JdbcTemplate jdbcTemplate = UserService.getJdbcTemplate();
		String sql = "SELECT * FROM users where email_id = '"+user.getEmail_id()+"'";	
		List<Map<String, Object>> listContact = jdbcTemplate.queryForList(sql);		
		if(listContact.isEmpty())
			return "no_user";
		Map<String,Object> mp =  listContact.get(0);
		if(mp.get("password").toString().equals(user.getPassword().toString()))
			return "valid_user";
		else
			return "invalid_password";
	}	
	public static Boolean addUser(User user){
		String insert = "INSERT INTO users (email_id, name, password) VALUES (?, ?, ?)";
		JdbcTemplate jdbcTemplate = UserService.getJdbcTemplate();
		Object[] params = new Object[] { user.getEmail_id(), user.getFull_name(), user.getPassword() };
		int[] types = new int[] { Types.VARCHAR, Types.VARCHAR, Types.VARCHAR };
		int row = jdbcTemplate.update(insert, params, types);
		return row>0;
	}
	public static String addGroup(JsonObject obj){		
		String SQL = "INSERT INTO groups (name,  owner) VALUES(?,?)";
        KeyHolder holder = new GeneratedKeyHolder();
        UserService.getJdbcTemplate().update(new PreparedStatementCreator() { 
                        @Override
                        public PreparedStatement createPreparedStatement(Connection connection)
                                throws SQLException {
                            PreparedStatement ps = connection.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
                            ps.setString(1, obj.getString("group_name"));
                            ps.setString(2, obj.getString("owner"));
                            return ps;
                        }
                    }, holder);
        Long grpId = holder.getKey().longValue();
		JsonObjectBuilder job = Json.createObjectBuilder()
                .add("group_name", obj.getString("group_name"))
                .add("owner", obj.getString("owner")).add("group_id",grpId);
		JsonArrayBuilder builder = Json.createArrayBuilder();
		JsonArray members = obj.getJsonArray("members");
		java.util.Date today = new java.util.Date();
        for (JsonValue mb : members) {
            JsonObject js = (JsonObject) mb;
            String SQL2 = "INSERT INTO group_members (gm_name,  email_id, group_id, date_created) VALUES(?,?,?,?)";
            holder = new GeneratedKeyHolder();
            UserService.getJdbcTemplate().update(new PreparedStatementCreator() { 
                @Override
                public PreparedStatement createPreparedStatement(Connection connection)
                        throws SQLException {
                    PreparedStatement ps = connection.prepareStatement(SQL2, Statement.RETURN_GENERATED_KEYS);
                    ps.setString(1, js.getString("gm_name"));
                    ps.setString(2, js.getString("gm_email"));
                    ps.setLong(3, grpId);
                    ps.setTimestamp(4, new Timestamp(today.getTime()));
                    return ps;
                }
            }, holder);            
            builder.add(Json.createObjectBuilder().add("gm_name", js.getString("gm_name"))
                    .add("gm_email", js.getString("gm_email"))
                    .add("gm_id",holder.getKey().longValue() )
                    .add("group_id",grpId )
                    .add("date_created",today.getTime() )
                    .build());
        }
        JsonArray arr = builder.build();
        job.add("members", arr);
        JsonObject resp = job.build();
		return resp.toString();
	}
}
