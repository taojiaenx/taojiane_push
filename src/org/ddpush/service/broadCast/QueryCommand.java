package org.ddpush.service.broadCast;


/**
 * 命令体
 * @author taojiaen
 *
 */
public class QueryCommand {
	/**
	 * 包ID
	 */
	private String packageID;
	private double lat;
	private double lon;
	private int pageIndx;
	private int distance;
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLon() {
		return lon;
	}
	public void setLon(double lon) {
		this.lon = lon;
	}
	public int getPageIndx() {
		return pageIndx;
	}
	public void setPageIndx(int page) {
		this.pageIndx = page;
	}
	public int getDistance() {
		return distance;
	}
	public void setDistance(int distance) {
		this.distance = distance;
	}
	public String getPackageID() {
		return packageID;
	}
	public void setPackageID(String packageID) {
		this.packageID = packageID;
	}
	
}
