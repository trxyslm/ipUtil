package com.onewaveinc.ip.ip.service;

import org.apache.commons.lang.StringUtils;

/**
 * <p>类的详细说明</p>
 *
 * @author  Administrator
 * @version 1.00 2014-5-22 Administrator
 * <p>      9.99 2014-5-22 修改者姓名 修改内容说明</p>
 * @see     参考类1
 */

/**
 * @author Administrator
 * 
 */
public class TestDemo {

	public static void main(String[] args) {
		String content = "121.234.124.200/24";
		String[] contents = StringUtils.split(content, "/");
		String ip = contents[0];
		int mask = 0;
		if (contents.length > 1) {
			mask = Integer.parseInt(contents[1]);
		} else {
			mask = 24;
		}
		long start = ipToLong(ip); // 地址

		long number = ipCount(mask); // ip地址个数

		long lmask = maskToLong(mask); // 掩码

		String startIp = (longToIP(start & lmask));

		String endIp = (longToIP((start & lmask) + number));
		System.out.println(startIp);

		System.out.println(endIp);

	}

	// 计算出掩码指定的IP地址个数 如: 24

	public static long ipCount(int mask) {

		long number = 0;

		for (int i = 0; i < 32 - mask; i++) {

			number += Math.pow(2, i);

		}

		return number;

	}

	// 分割IP地址

	public static long[] splitIp(String ip) {

		long[] ipArray = new long[4];

		int position1 = ip.indexOf(".");

		int length = ip.length();

		if (length >= 7 && length < 16) {

			if (position1 > 0) {

				int position2 = ip.indexOf(".", position1 + 1);

				if (position2 > 0) {

					int position3 = ip.indexOf(".", position2 + 1);

					if (position3 > 0 && position3 < length - 1) {

						try {

							ipArray[0] = Long.parseLong(ip.substring(0,
									position1));

							ipArray[1] = Long.parseLong(ip.substring(
									position1 + 1, position2));

							ipArray[2] = Long.parseLong(ip.substring(
									position2 + 1, position3));

							ipArray[3] = Long.parseLong(ip
									.substring(position3 + 1));

						} catch (NumberFormatException e) {

							return null;

						}

					} else {

						return null;

					}

				} else {

					return null;

				}

			} else {

				return null;

			}

		} else {

			return null;

		}

		return ipArray;

	}

	// 将10.0.0.0形式的ip地址转换成10进制整数

	public static long ipToLong(String str) {

		long[] ip = splitIp(str);

		if (ip != null) {

			// ip=*256*256*256+ip2*256*256+ip3*256+ip4

			return (ip[0] << 24) + (ip[1] << 16) + (ip[2] << 8) + ip[3];

		} else {

			return 0;

		}

	}

	// 将10.0.0.0形式的掩码地址转换成10进制整数

	public static long maskToLong(int mask) {

		long longMask = 0;

		for (int i = 31; i >= 32 - mask; i--) {

			longMask += Math.pow(2, i);

		}

		return longMask;

	}

	// 将10进制整数形式转换成127.0.0.1形式的IP地址

	public static String longToIP(long longIp) {

		StringBuffer sb = new StringBuffer("");

		sb.append(String.valueOf(longIp >>> 24));// 右移24位

		sb.append(".");

		sb.append(String.valueOf((longIp & 0x00FFFFFF) >>> 16)); // 将高8位置0，然后右移16位

		sb.append(".");

		sb.append(String.valueOf((longIp & 0x0000FFFF) >>> 8));

		sb.append(".");

		sb.append(String.valueOf(longIp & 0x000000FF));

		return sb.toString();

	}
}
