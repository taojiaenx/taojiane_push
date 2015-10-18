package org.ddpush.service.broadCast.exeutor.mysql;

import java.util.List;

import org.ddpush.service.broadCast.QueryCommand;
import org.ddpush.service.broadCast.dao.BroadCastListHandler;
import org.ddpush.service.broadCast.exeutor.QueryWorker;

import com.ddpush.dao.DbHelper;
import com.sun.media.sound.InvalidDataException;

/**
 * mysql查询执行器
 * @author taojiaen
 *
 */
public class MysqlWorker implements QueryWorker{
	private static String _QUERY_SQL = "EXPLAIN PARTITIONS SELECT broadcast_id, author_uuid, broadcast_body, lat, lon, create_time from broadcast_storer where lat_floor >= ? AND lat_floor<=? AND  lon_floor >=? AND  lon_floor<=?   order by id desc limit ?, 10";
	private final static BroadCastListHandler handler = new BroadCastListHandler();
	private static int[] LON_RANGE_100 = new int[]{
		9,
		9,
		9,
		9,
		9,
		9,
		9,
		9,
		9,
		9,
		9,
		9,
		9,
		9,
		9,
		9,
		9,
		9,
		9,
		10,
		10,
		10,
		10,
		10,
		10,
		10,
		10,
		10,
		10,
		10,
		10,
		10,
		11,
		11,
		11,
		11,
		11,
		11,
		11,
		12,
		12,
		12,
		12,
		12,
		12,
		13,
		13,
		13,
		13,
		14,
		14,
		14,
		15,
		15,
		15,
		16,
		16,
		16,
		17,
		17,
		18
	};
	private static int[] LON_RANGE_200 = new int[]{
		18,
		18,
		18,
		18,
		18,
		18,
		18,
		18,
		18,
		18,
		18,
		18,
		18,
		18,
		19,
		19,
		19,
		19,
		19,
		19,
		19,
		19,
		19,
		20,
		20,
		20,
		20,
		20,
		20,
		21,
		21,
		21,
		21,
		21,
		22,
		22,
		22,
		22,
		23,
		23,
		23,
		24,
		24,
		25,
		25,
		25,
		26,
		26,
		27,
		27,
		28,
		29,
		29,
		30,
		31,
		31,
		32,
		33,
		34,
		35,
		36
	};
	private static int[] LON_RANGE_300 = new int[]{
		27,
		27,
		27,
		27,
		27,
		27,
		27,
		27,
		27,
		27,
		27,
		27,
		28,
		28,
		28,
		28,
		28,
		28,
		28,
		29,
		29,
		29,
		29,
		29,
		29,
		30,
		30,
		30,
		31,
		31,
		31,
		31,
		32,
		32,
		33,
		33,
		33,
		34,
		34,
		35,
		35,
		36,
		36,
		37,
		37,
		38,
		39,
		40,
		40,
		41,
		42,
		43,
		44,
		45,
		46,
		47,
		48,
		49,
		51,
		52,
		54
	};

	@Override
	public List<Object> executorQuery(QueryCommand query)
			throws InvalidDataException {
		if (query.getDistance() != 100 &&
				query.getDistance() != 200 && query.getDistance() != 300) {
			throw new InvalidDataException("这个距离不支持" + query.getDistance());
		}
		if (Math.abs(query.getLat()) > 60)
			throw new InvalidDataException("这个纬度不支持" + query.getLat());
		
		int lat_floor = (int) (LAT_MASK.doubleValue() * query.getLat());
		int lon_floor = (int) (LON_MASK.doubleValue() * query.getLon());
		int latRnage = getLatRange(query.getDistance());
		int lonRange = getLonRange((int) Math.abs(query.getLat()),query.getDistance());
		return 
			DbHelper.batchQeury(_QUERY_SQL, new Object[]{
					lat_floor - latRnage,
					lat_floor + latRnage,
					lon_floor - lonRange,
					lon_floor + lonRange,
					query.getPageIndx() * 10
			}, handler);
	}
	private int getLonRange(int lat, int distance) {
		switch(distance) {
		case 100:
			return LON_RANGE_100[lat];
		case 200:
			return LON_RANGE_200[lat];
		case 300:
			return LON_RANGE_300[lat]; 
		default: return 15;
		}
	}
	private int getLatRange(int distance) {
		switch(distance) {
		case 100:
			return 1;
		case 200:
			return 2;
		case 300:
			return 3; 
		default: return 1;
		}
	}

}
