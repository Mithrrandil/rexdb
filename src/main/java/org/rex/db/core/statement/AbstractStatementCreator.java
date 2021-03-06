/**
 * Copyright 2016 the Rex-Soft Group.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.rex.db.core.statement;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

import org.rex.db.Ps;
import org.rex.db.configuration.Configuration;
import org.rex.db.exception.DBException;
import org.rex.db.logger.Logger;
import org.rex.db.logger.LoggerFactory;
import org.rex.db.util.SqlUtil;

/**
 * Basic statement creator.
 * 
 * @version 1.0, 2016-02-14
 * @since Rexdb-1.0
 */
public abstract class AbstractStatementCreator implements StatementCreator{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractStatementCreator.class);

	//----------Statement
	public Statement createStatement(Connection conn) throws DBException, SQLException{
		if(LOGGER.isDebugEnabled())
			LOGGER.debug("creating Statement of Connection[{0}].", conn.hashCode());
		
		return conn.createStatement();
	}
	
	//----------Callable Statement
	public CallableStatement createCallableStatement(Connection conn, String sql) throws SQLException {
		if(LOGGER.isDebugEnabled())
			LOGGER.debug("preparing CallableStatement for sql {0} of Connection[{1}].", sql, conn.hashCode());
		
		return conn.prepareCall(sql);
	}
	
	//----------Batch Statement
	public Statement createBatchStatement(Connection conn, String[] sql) throws SQLException {
		if(LOGGER.isDebugEnabled())
			LOGGER.debug("creating batch Statement for sqls {0} of Connection[{1}].", Arrays.toString(sql), conn.hashCode());
		
		Statement stmt = conn.createStatement();
		for (int i = 0; i < sql.length; i++) {
			stmt.addBatch(sql[i]);
		}
		return stmt;
	}

	
	//------------------Sql validate
	private static boolean isValidateSql() throws DBException{
		return Configuration.getCurrentConfiguration().isValidateSql();
	}

	/**
	 * Validates the given SQL before executing.
	 */
	private static void validateSql(String sql, int expectedParameterSize) throws DBException{
		SqlUtil.validate(sql, expectedParameterSize);
	}
	
	protected static void validateSql(String sql, Object[] parameterArray) throws DBException{
		if(isValidateSql())
			validateSql(sql, parameterArray == null ? 0 : parameterArray.length);
	}
	
	protected static void validateSql(String sql, Object[][] parameterArrays) throws DBException{
		if(isValidateSql()){
			for (int i = 0; i < parameterArrays.length; i++) 
				validateSql(sql, parameterArrays[i] == null ? 0 : parameterArrays[i].length);
		}
	}

	protected static void validateSql(String sql, Ps ps) throws DBException{
		if(isValidateSql())
			validateSql(sql, ps == null ? 0 : ps.getParameterSize());
	}
	
	protected static void validateSql(String sql, Ps[] ps) throws DBException{
		if(isValidateSql()){
			for (int i = 0; i < ps.length; i++) 
				validateSql(sql, ps[i] == null ? 0 : ps[i].getParameterSize());
		}
	}
	
	protected static void validateSql(String sql) throws DBException{
		if(isValidateSql()) validateSql(sql, 0);
	}
	
	protected static void validateSql(String[] sql) throws DBException{
		if(isValidateSql()){
			for (int i = 0; i < sql.length; i++) 
				validateSql(sql[i], 0);
		}
	}
}
