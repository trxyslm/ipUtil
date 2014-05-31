package com.onewaveinc.ip.ip.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentSkipListSet;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;

public class IPUtil {
	private static Log log = LogFactory.getLog(IPUtil.class);
	

	/**
	 * 从ip的字符串形式得到字节数组形式
	 * 
	 * @param ip
	 *            字符串形式的ip
	 * @return 字节数组形式的ip
	 */
	public static byte[] getIpByteArrayFromString(String ip) {
		byte[] ret = new byte[4];
		String tokens[] = ip.split("\\.");
		try {
			ret[0] = (byte) (Integer.parseInt(tokens[0]) & 0xFF);
			ret[1] = (byte) (Integer.parseInt(tokens[1]) & 0xFF);
			ret[2] = (byte) (Integer.parseInt(tokens[2]) & 0xFF);
			ret[3] = (byte) (Integer.parseInt(tokens[3]) & 0xFF);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return ret;
	}

	public static long IP2Long(String IP) {
		long[] ip = new long[4];
		String tokens[] = IP.split("\\.");
		if (tokens.length != 4)
			return -1;
		try {
			ip[0] = Long.parseLong(tokens[0]) << 24;
			ip[1] = Long.parseLong(tokens[1]) << 16;
			ip[2] = Long.parseLong(tokens[2]) << 8;
			ip[3] = Long.parseLong(tokens[3]);
			return ip[0] + ip[1] + ip[2] + ip[3];
		} catch (Exception e) {
			return -1;
		}
	}
	
	public static String IP2LongStr(String IP) {
		StringBuffer ipaddress = new StringBuffer(15);
		String tokens[] = IP.split("\\.");
		if (tokens.length != 4)
			return ipaddress.toString();
		try {
			ipaddress.append(Long.parseLong(tokens[0]) << 24);
			ipaddress.append(Long.parseLong(tokens[1]) << 16);
			ipaddress.append(Long.parseLong(tokens[2]) << 8);
			ipaddress.append(Long.parseLong(tokens[3]));
			
		} catch (Exception e) {
			
		}
		return ipaddress.toString();
	}

	public static String long2IP(long ip) {
		StringBuffer result  = new StringBuffer(15);
		result.append(ip >> 24).append('.');
		result.append((ip & 0x00FFFFFF) >> 16).append('.');
		result.append((ip & 0x0000FFFF) >> 8).append('.');
		result.append((ip & 0x000000FF));
		return result.toString();
	}

	/**
	 * 根据某种编码方式将字节数组转换成字符串
	 * 
	 * @param b
	 *            字节数组
	 * @param offset
	 *            要转换的起始位置
	 * @param len
	 *            要转换的长度
	 * @param encoding
	 *            编码方式
	 * @return 如果encoding不支持，返回一个缺省编码的字符串
	 */
	public static String getString(byte[] b, int offset, int len,
			String encoding) {
		try {
			return new String(b, offset, len, encoding);
		} catch (UnsupportedEncodingException e) {
			return new String(b, offset, len);
		}
	}

	/**
	 * @param ip
	 *            ip的字节数组形式
	 * @return 字符串形式的ip
	 */
	static StringBuilder sb = new StringBuilder(15);
	public static String getIpStringFromBytes(byte[] ip) {
		
		if (ip.length < 4) {
			return sb.toString();
		}
		sb.delete(0, sb.length());
		sb.append(ip[0] & 0xFF);
		sb.append('.');
		sb.append(ip[1] & 0xFF);
		sb.append('.');
		sb.append(ip[2] & 0xFF);
		sb.append('.');
		sb.append(ip[3] & 0xFF);
		return sb.toString();
	}
	
	/**
	 * 将ip段转化成掩码格式的ip
	 * @author SLM
	 * @param start
	 * @param end
	 * @return
	 */
	public static List<String> ip2Mask(long start, long end){
		long base = start;
		int step = 0;
		long thirtytwobits = 4294967295L;
		List<String> result = new ArrayList<String>();
		while(base <= end){
			step = 0;
			while((base | (1 << step)) != base){
				if((base | (((~0) & thirtytwobits) >> (31 - step))) > end){
					break;
				}
				step++;
			}
			result.add(long2IP(base) + "/" + (32 - step));
			base += 1 << step;
		}
		return result;
	}
	
	/**
	 * 合并IP段
	 * <br>把有交集的和相邻的合并为一个段
	 * <br>效率低,容易导致OOM,建议使用 {@link IPUtil.merge2} 方法
	 * @author SLM
	 * @param original
	 * @return
	 */
	@Deprecated
	public static List<String> merge(List<String> original){
		if(CollectionUtils.isNotEmpty(original)){
			final SortedSet<Long> set = new ConcurrentSkipListSet<Long>();
			List<String> dest = new ArrayList<String>();
			for(String str : original){
				String[] tmp = str.split("-");
				long start = Long.valueOf(tmp[0]);
				long end = Long.valueOf(tmp[1]);
				for(long i = start; i <= end; i++){
					set.add(i);
				}
			}
			
			long first = set.first();
			long pre = first;
			long cur = 0;
			Iterator<Long> iterator = set.iterator();
			while(iterator.hasNext()){
				cur = iterator.next();
				if(cur - pre > 1){
					dest.add(first + "-" + pre);
					first = cur;
				}
				pre = cur;
			}
			dest.add(first + "-" + set.last());
			return dest;
		}
		return null;
	}
	
	/**
	 * 合并IP段
	 * <br>把有交集的和相邻的合并为一个段
	 * @author SLM
	 * @param original
	 * @return
	 */
	public static List<String> merge2(List<String> original){
		if(CollectionUtils.isNotEmpty(original)){
			RangeSet<Long> rs = TreeRangeSet.create();
			for(String str : original){
				String[] tmp = str.split("-");
				long start = Long.valueOf(tmp[0]);
				long end = Long.valueOf(tmp[1]);
				if(start < end){
					rs.add(Range.closedOpen(start, end));
				}else if(start == end){
					rs.add(Range.closed(start, end));
				}else{
					log.error("非法的IP段：" + str);
				}
			}
			List<String> dest = new ArrayList<String>();
			Set<Range<Long>> ranges = rs.asRanges();
			Iterator<Range<Long>> iterator =  ranges.iterator();
			Range<Long> pre = iterator.next();
			dest.add(pre.lowerEndpoint() + "-" + pre.upperEndpoint());
			while(iterator.hasNext()){
				Range<Long> cur = iterator.next();
				if(cur.lowerEndpoint() - pre.upperEndpoint() == 1){
					pre = Range.open(pre.lowerEndpoint(), cur.upperEndpoint());
					dest.remove(dest.size() - 1);
					dest.add(pre.lowerEndpoint() + "-" + pre.upperEndpoint());
				}else{
					dest.add(cur.lowerEndpoint() + "-" + cur.upperEndpoint());
					pre = cur;
				}
			}
			return dest;
		}
		return null;
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
//		System.out.println(IP2Long("255.255.255.255"));
//		System.out.println(ip2Mask(IP2Long("192.168.1.1"), IP2Long("192.168.1.5")));
		List<String> lines = IOUtils.readLines(new FileInputStream("C:\\Users\\Administrator\\Desktop\\document\\华通\\ip_data\\IP地址数据库\\淘宝购买库.csv"), "GBK");
		List<String> orig = new ArrayList<String>();
		for(String str : lines){
			String[] tmp = str.split(",");
			if(tmp[5].equals("湖北")){
				orig.add(tmp[2] + "-" + tmp[3]);
			}
		}
		long b = System.currentTimeMillis();
		List<String> dest = merge2(orig);
		System.out.println("共 " + orig.size() + " 个IP段，合并后剩余 " + dest.size() + " 个， 共耗时 " + (System.currentTimeMillis() - b) + " ms");
		System.out.println(dest);
		//454033408-455081983, 717619200-717684735, 829947904-830078975, 830156800-830160895, 830177280-830210047, 835846144-835885055
		//2948399104-2948464639, 3025797120-3025829887,
	}
}
