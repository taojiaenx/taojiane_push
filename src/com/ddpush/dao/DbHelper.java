package com.ddpush.dao;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.apache.log4j.PropertyConfigurator;
import org.ddpush.im.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidDataSourceFactory;

public class DbHelper {
    /* 日志 */
     static final Logger log = LoggerFactory.getLogger(DbHelper.class);
    /* 数据源*/
    private final static Map<String, DataSource> _DATASOURCEMAP = new ConcurrentHashMap<>();
    /* 当前config的信息 */
    private static Map<String, Map<String, String>> config = null;

    /**
     * 初始化
     * 
     * @throws java.lang.IllegalAccessException 
     */
    private DbHelper() throws IllegalAccessException {
        throw new IllegalAccessException("格式错误");
    }

    /**
     * @param table 当前配置的列表项
     * @param dataNode 数据节点
     */
    public static String dbprefix(String table, String dataNode) {
        return config.get(dataNode).get("prefix") + table;
    }

    /**
     * @param table 数据配置
     * 
     * */
    public static String dbprefix(String table) {
        return dbprefix(table, "data1");
    }


    /**
     * 连接初始化
     * 
     * @throws java.lang.Exception 
     */
    public synchronized static void init() throws Exception {
        /* 导入配置文件形成的mapMAP */
        config = loadProerties();
        for (Map.Entry<String, Map<String, String>> row : config.entrySet()) {
            _DATASOURCEMAP.put(row.getKey(),
                    DruidDataSourceFactory.createDataSource(config.get(row.getKey())));
        }
         log.info("线程池初始化完毕..");
    }

    /**
     * 配置导入
     * 
     * 
     * @throws java.lang.NullPointerException 
     */
    private static Map<String, Map<String, String>> loadProerties() {
        final ResourceBundle rb = Config.read();
        final Map<String, String> re = convertResourceBundleToMap(rb);
        final Map<String, Map<String, String>> result = new HashMap<>();
        for (Map.Entry<String, String> row : re.entrySet()) {
            if (row.getKey().contains("-")) {
                String dataNode = row.getKey().split("-")[0];
                String dataKey = row.getKey().split("-")[1];
                String value = row.getValue();
                if (result.containsKey(dataNode)) {
                    result.get(dataNode).put(dataKey, value);
                } else {
                    Map<String, String> temp = new HashMap();
                    temp.put(dataKey, value);
                    result.put(dataNode, temp);
                }
            }
        }
        return result;
    }


    public static QueryRunner getQueryRunner() {
        return getQueryRunner("data1");
    }


    public static QueryRunner getQueryRunner(final String dataNode) {
        return new QueryRunner(DbHelper._DATASOURCEMAP.get(dataNode));
    }

    /**
     * 配置销毁
     * 
     * @throws SQLException 数据库连接异常
     */
    public static void destory() throws SQLException {

        for (Map.Entry<String, DataSource> row : _DATASOURCEMAP.entrySet()) {
            row.getValue().getConnection().close();
        }

        _DATASOURCEMAP.clear();

    }

    /**
     * @param resource bounde锟斤拷锟斤拷锟斤拷
     * 
     * @return 锟斤拷RB锟斤拷锟阶拷锟斤拷锟紿SHMAP锟侥斤拷锟�
     * 
     * @throws java.lang.NullPointerException 锟斤拷锟絩esource 为锟秸ｏ拷锟斤拷锟阶筹拷锟斤拷锟届常
     */
    private static Map<String, String> convertResourceBundleToMap(ResourceBundle resource) {
        if (resource == null) throw new NullPointerException("resourcebundle为空");
        Map<String, String> map = new HashMap<String, String>();
        Enumeration<String> keys = resource.getKeys();

        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            map.put(key, resource.getString(key));
        }

        return map;
    }


  

    /**
     * 数据库修改语句执行
     * @param sql
     * @param params
     * @return
     */
    public static int batchExec(String sql, Object[][] params) {
        QueryRunner qr = null;
        try {
            qr = DbHelper.getQueryRunner("data1");
            qr.batch(sql, params);
        } catch (SQLException e) {
            log.error("数据库修改\\插入语句执行错误", e);
            return -1;
        }
        return 1;
    }

    /**
     * 查询语句执行
     * @param sql
     * @param params
     * @return
     */
    public static List<Object[]> batchQeury(String sql, Object[] params) {
        QueryRunner qr = null;
        List<Object[]> ret = null;
        try {
            qr = DbHelper.getQueryRunner("data1");
            ret = qr.query(sql, params, new ArrayListHandler());
            return ret;
        } catch (Exception e) {
            log.error("数据库查询出错", e);
            if(ret != null) ret.clear();
            return null;
        }
    }
    
	public static void main(String[] args) throws Exception {
		PropertyConfigurator.configure("Log4j.properties");
		DbHelper.init();
		List<Object[]> ret = DbHelper.batchQeury(
				"select * from broadcast_storer", new Object[0][0]);
		System.out.println(ret.size());
		System.out
				.println(DbHelper
						.batchExec(
								"insert into broadcast_storer(`broadcast_id`,`broadcast_body`,`lat`,`lon`,`lat_floor`,`lon_floor`,`ipv4`) values(?,?,?,?,?,?,?)",
								new Object[][] { {
										UUID.randomUUID().toString(),
										"我中出就是现在",
										new BigDecimal(50.0f),
										33.4f,
										50,
										33,
										StringUtil
												.ip2int("255.255.255.255"),
										 } }));
	}
}
