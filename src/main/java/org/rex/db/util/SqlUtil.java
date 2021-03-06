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
package org.rex.db.util;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.rex.db.Ps;
import org.rex.db.exception.DBException;
import org.rex.db.exception.DBRuntimeException;

/**
 * SQL utilities.
 * 
 * @version 1.0, 2016-04-18
 * @since Rexdb-1.0
 */
public class SqlUtil {
	
	private static final String PARAMETER_PREFIX = "#{";
	
	private static final String PARAMETER_SUFFIX = "}";
	
	private static final char PARAMETER = '?';
	
	private static final Map<String, String[]> sqlCache = new HashMap<String, String[]>();
	
	/**
	 * Validates the SQL and the prepared parameters.
	 */
	public static void validate(String sql, Ps ps) throws DBException{
		validate(sql, ps == null ? 0 : ps.getParameters().size());
	}
	
	public static void validate(String sql, int expectedParameterSize) throws DBException{
		int holderSize = SqlUtil.countParameterPlaceholders(sql, PARAMETER, '\'');
		if (holderSize != expectedParameterSize)
			throw new DBException("DB-S0001", sql, holderSize, expectedParameterSize);
	}
	
	/**
	 * Sets the designated parameter to SQL NULL.
	 */
	public static void setNull(PreparedStatement preparedStatement, int index) throws SQLException{
//		preparedStatement.setObject(index, null, Types.NULL);
		preparedStatement.setObject(index, null);
	}
	
	/**
	 * Sets the value of the designated parameter with the given object.
	 */
	public static void setParameter(PreparedStatement preparedStatement, int index, Object value) throws SQLException {
		if (value instanceof String) {
			preparedStatement.setString(index, (String) value);
			
		} else if(value instanceof Number){
			if (value instanceof Integer) {
				preparedStatement.setInt(index, (Integer) value);
			} else if (value instanceof Long) {
				preparedStatement.setLong(index, (Long) value);
			} else if (value instanceof Short) {
				preparedStatement.setShort(index, (Short) value);
			} else if (value instanceof Float) {
				preparedStatement.setFloat(index, (Float) value);
			} else if (value instanceof Double) {
				preparedStatement.setDouble(index, (Double) value);
			} else if (value instanceof Byte) {
				preparedStatement.setByte(index, (Byte) value);
			} else if (value instanceof BigDecimal) {
				preparedStatement.setBigDecimal(index, (BigDecimal) value);
			}else
				preparedStatement.setObject(index, value);
			
		}else if(value instanceof Date){
			if (value instanceof Timestamp) {
				preparedStatement.setTimestamp(index, (Timestamp) value);
			} else if (value instanceof java.sql.Date) {
				preparedStatement.setDate(index, (java.sql.Date) value);
			} else if (value instanceof Time) {
				preparedStatement.setTime(index, (Time) value);
			} else {
				preparedStatement.setTimestamp(index, new Timestamp(((Date) value).getTime()));
			}
			
		} else if (value != null && value.getClass().isArray() && value.getClass().getComponentType() == byte.class) {
			preparedStatement.setBytes(index, (byte[]) value);
		} else if (value instanceof Blob) {
			preparedStatement.setBlob(index, (Blob) value);
		} else if (value instanceof Clob) {
			preparedStatement.setClob(index, (Clob) value);
		} else {
			preparedStatement.setObject(index, value);
		}
	}
	
	/**
	 * Sets the value of the designated parameter with the given object as the given SQL type.
	 */
	public static void setParameter(PreparedStatement preparedStatement, int index, Object value, int sqlType) throws SQLException{
		switch (sqlType) {
			case Types.VARCHAR : 
				if(value instanceof String || value == null){
					preparedStatement.setString(index, (String)value);
					break;
				}
			case Types.BOOLEAN :
				if(value instanceof Boolean || value == null){
					preparedStatement.setBoolean(index, (Boolean)value);
					break;
				}
			case Types.NUMERIC :
				if(value instanceof BigDecimal || value == null){
					preparedStatement.setBigDecimal(index, (BigDecimal)value);
					break;
				}
			case Types.INTEGER :
				if(value instanceof Integer || value == null){
					preparedStatement.setInt(index, (Integer)value);
					break;
				}
			case Types.BIGINT :
				if(value instanceof Long || value == null){
					preparedStatement.setLong(index, (Long)value);
					break;
				}
			case Types.DOUBLE :
				if(value instanceof Double || value == null){
					preparedStatement.setDouble(index, (Double)value);
					break;
				}
			case Types.FLOAT :
				if(value instanceof Float || value == null){
					preparedStatement.setFloat(index, (Float)value);
					break;
				}
			case Types.SMALLINT :
				if(value instanceof Short || value == null){
					preparedStatement.setShort(index, (Short)value);
					break;
				}
			case Types.TINYINT :
				if(value instanceof Byte || value == null){
					preparedStatement.setByte(index, (Byte)value);
					break;
				}
			case Types.VARBINARY :
				if(value instanceof byte[] || value == null){
					preparedStatement.setBytes(index, (byte[])value);
					break;
				}
			case Types.BLOB :
				if(value instanceof Blob || value == null){
					preparedStatement.setBlob(index, (Blob)value);
					break;
				}
			case Types.CLOB :
				if(value instanceof Clob || value == null){
					preparedStatement.setClob(index, (Clob)value);
					break;
				}
			case Types.DATE:
				if(value == null || value instanceof java.sql.Date){
					preparedStatement.setDate(index, (java.sql.Date)value);
					break;
				}else if(value instanceof Date){
					preparedStatement.setDate(index, new java.sql.Date(((Date)value).getTime()));
					break;
				}
			case Types.TIMESTAMP:
				if(value == null || value instanceof Timestamp){
					preparedStatement.setTimestamp(index, (Timestamp)value);
					break;
				}else if(value instanceof Date){
					preparedStatement.setTimestamp(index, new Timestamp(((Date)value).getTime()));
					break;
				}
			case Types.TIME:
				if(value == null || value instanceof Time){
					preparedStatement.setTime(index, (Time)value);
					break;
				}else if(value instanceof Date){
					preparedStatement.setTime(index, new Time(((Date)value).getTime()));
					break;
				}
			default : 
//					preparedStatement.setObject(index, value, sqlType);
				preparedStatement.setObject(index, value);
		}
	}
	
	/**
	 * Parses the given SQL with the '${...}' parameter placeholders.
	 * @param sql the SQL to be analyzed.
	 * @return a string array, array[0] is the parsed SQL, array[1..n] are prepared parameters' keys.
	 */
	public static String[] parse(String sql) {
		if(!sqlCache.containsKey(sql)){
			StringBuilder builder = new StringBuilder();
			List<String> all = new ArrayList<String>();
			if (sql != null && sql.length() > 0) {
				char[] src = sql.toCharArray();
				int offset = 0;
				int start = sql.indexOf(PARAMETER_PREFIX, offset);
				while (start > -1) {
					if (start > 0 && src[start - 1] == '\\') {
						builder.append(src, offset, start - offset - 1).append(PARAMETER_PREFIX);
						offset = start + PARAMETER_PREFIX.length();
					} else {
						int end = sql.indexOf(PARAMETER_SUFFIX, start);
						if (end == -1) {
							builder.append(src, offset, src.length - offset);
							offset = src.length;
						} else {
							builder.append(src, offset, start - offset);
							offset = start + PARAMETER_PREFIX.length();
							String content = new String(src, offset, end - offset);
							all.add(content);
							builder.append(PARAMETER);
							offset = end + PARAMETER_SUFFIX.length();
						}
					}
					start = sql.indexOf(PARAMETER_PREFIX, offset);
				}
				if (offset < src.length) {
					builder.append(src, offset, src.length - offset);
				}
			}
			all.add(0, builder.toString());
			String[] parsed = all.toArray(new String[all.size()]);
			sqlCache.put(sql, parsed);
			return parsed;
		}
		return sqlCache.get(sql);
	}


	/**
	 * Returns total number of placeholders.
	 * @param str the text to check.
	 * @param marker placeholder.
	 * @param delim placeholders between the delimiters won't be calculated.
	 * @return
	 */
	public static int countParameterPlaceholders(String str, char marker, char delim) {
		int count = 0;
		if (str == null || "".equals(str) || '\0' == marker || '\0' == delim)
			return count;

		final int stateStart = 0;
		final int stateNormalChar = 1;
		final int stateMarker = 2;
		final int stateInDelim = 3;
		final int stateError = 4;

		int len = str.length();
		int index = 0;
		char ch;
		char lookahead = 0;

		int state = stateStart;
		while (index < len) {
			ch = 0 == index ? str.charAt(0) : index < len - 1 ? lookahead : str.charAt(index);
			lookahead = index < len - 1 ? str.charAt(index + 1) : 0;
			switch (state) {
				case stateStart :
					if (ch == delim)
						state = stateInDelim;
					else if (ch == marker && (index == len - 1 || Character.isWhitespace(str.charAt(index + 1)))) {
						state = stateMarker;
					}
					else
						state = stateNormalChar;
					break;
				case stateNormalChar :
					if (ch == delim) {
						state = stateInDelim;
					}
					else if (index < len - 1 && lookahead == marker) {
						state = stateMarker;
					}
					break;
				case stateMarker :
					++count;
					if (index < len - 1 && !Character.isWhitespace(lookahead) && lookahead != ',' && lookahead != ')')
						state = stateError;
					else
						state = stateNormalChar;
					break;
				case stateInDelim :
					if (index == len - 1)
						state = stateError;
					else if (ch == delim) {
						if (index < len - 1 && delim == lookahead) {
							if (index > len - 2)
								throw new DBRuntimeException("DB-USQ01", str);
							else {
								index += 1;
							}
						}
						else
							state = stateNormalChar;
					}
					break;
				case stateError :
					throw new DBRuntimeException("DB-USQ02", str);
				default :
					throw new DBRuntimeException("DB-USQ03", str);
			}
			++index;
		}

		return count;
	}
	
	/**
	 * Returns SQL type matches the object.
	 * 
	 * 1. String - Types.VARCHAR
	 * 2. int|Integer - Types.INTEGER
	 * 3. BigDecimal - Types.NUMERIC
	 * 4. long|Long - Types.BIGINT
	 * 5. float|Float - Types.FLOAT
	 * 6. double|Double - Types.DOUBLE
	 * 7. Date - Types.DATE
	 * 8. Time - Types.TIME
	 * 9. etc
	 */
	public static int getSqlType(Object param){
		int type;
		if(param==null) {
			type = Types.NULL;
		}
		else{
			if(param instanceof String) 
				type = Types.VARCHAR;
			else if(param instanceof Integer) 
				type = Types.INTEGER;
			else if(param instanceof Long) 
				type = Types.BIGINT;
			else if(param instanceof Float) 
				type = Types.FLOAT;
			else if(param instanceof Double) 
				type = Types.DOUBLE;
			else if(param instanceof Short) 
				type = Types.SMALLINT;
			else if(param instanceof BigDecimal) 
				type = Types.NUMERIC;
			else if(param instanceof Date)
				type = Types.TIMESTAMP;
			else if(param.getClass().isArray() && param.getClass().getComponentType() == Byte.class)
				type = Types.VARBINARY;
			else if(param instanceof Blob)
				type = Types.BLOB;
			else if(param instanceof Clob)
				type = Types.CLOB;
			else
				type = Types.OTHER;
		}

		return type;
	}
	
	/**
	 * Returns SQL type name by code.
	 * @param sqlType SQL type.
	 * @return SQL type name.
	 */
	public static String getNameByType(int sqlType){
		switch(sqlType){
			case Types.BIT: return "BIT";
			case Types.BIGINT: return "BIGINT";
			case Types.DECIMAL: return "DECIMAL";
			case Types.DOUBLE: return "DOUBLE";
			case Types.FLOAT: return "FLOAT";
			case Types.INTEGER: return "INTEGER";
			case Types.NUMERIC: return "NUMERIC";
			case Types.REAL: return "REAL";
			case Types.SMALLINT: return "SMALLINT";
			case Types.TINYINT: return "TINYINT";
			case Types.CHAR: return "CHAR";
			case Types.VARCHAR: return "VARCHAR";
			case Types.LONGVARCHAR: return "LONGVARCHAR";
			case Types.DATE: return "DATE";
			case Types.TIME: return "TIME";
			case Types.TIMESTAMP: return "TIMESTAMP";
			case Types.BINARY: return "BINARY";
			case Types.VARBINARY: return "VARBINARY";
			case Types.LONGVARBINARY: return "LONGVARBINARY";
			case Types.NULL: return "NULL";
			case Types.OTHER: return "OTHER";
			case Types.JAVA_OBJECT: return "JAVA_OBJECT";
			case Types.DISTINCT: return "DISTINCT";
			case Types.STRUCT: return "STRUCT";
			case Types.ARRAY: return "ARRAY";
			case Types.BLOB: return "BLOB";
			case Types.CLOB: return "CLOB";
			case Types.REF: return "REF";
			case Types.DATALINK: return "DATALINK";
			case Types.BOOLEAN: return "BOOLEAN";
//			case Types.ROWID: return "ROWID";
//			case Types.NCHAR: return "NCHAR";
//			case Types.NVARCHAR: return "NVARCHAR";
//			case Types.LONGNVARCHAR: return "LONGNVARCHAR";
//			case Types.NCLOB: return "NCLOB";
//			case Types.SQLXML: return "SQLXML";
			default: return "Unsupported";
		}
	}
}
