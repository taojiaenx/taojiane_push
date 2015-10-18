package org.ddpush.service.broadCast.dao;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.apache.commons.dbutils.handlers.AbstractListHandler;
import org.apache.commons.dbutils.handlers.ArrayHandler;
import org.ddpush.service.broadCast.BroadCast;

/**
 * 数据库解析器
 * @author taojiaen
 *
 */
public class BroadCastListHandler extends AbstractListHandler<BroadCast>{

	@Override
	protected BroadCast handleRow(ResultSet rs) throws SQLException {
		BroadCast newCast = new BroadCast();
		newCast.setBroadCastID(rs.getString(1));
		newCast.setAuthorUUID(rs.getString(2));
		newCast.setBody(rs.getString(3));
		newCast.setLat(rs.getBigDecimal(4));
		newCast.setLon(rs.getBigDecimal(5));
		newCast.setCreateDate(rs.getDate(6));
		return newCast;
	}

}
