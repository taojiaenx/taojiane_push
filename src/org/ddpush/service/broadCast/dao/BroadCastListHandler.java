package org.ddpush.service.broadCast.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.dbutils.handlers.AbstractListHandler;
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
		newCast.setBroadCastID(rs.getString(0));
		newCast.setAuthorUUID(rs.getString(1));
		newCast.setLat(rs.getBigDecimal(2));
		newCast.setLon(rs.getBigDecimal(3));
		newCast.setBody(rs.getString(4));
		newCast.setCreateDate(rs.getDate(5));
		return newCast;
	}

}
