package org.ddpush.service.broadCast.exeutor;

import java.math.BigDecimal;
import java.util.List;

import org.ddpush.service.broadCast.QueryCommand;

import com.sun.media.sound.InvalidDataException;

public interface QueryWorker {
	/**
	 * 为了简化计算，经纬度需要相乘的因子
	 */
	final static BigDecimal LON_MASK  = BaseExecutor.LON_MASK;
	final static BigDecimal LAT_MASK = BaseExecutor.LAT_MASK;
	public List<Object> executorQuery(final QueryCommand query)throws InvalidDataException;
}
