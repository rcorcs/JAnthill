package dcc.ufmg.anthill.net;
/**
 * @author Rodrigo Caetano O. ROCHA
 * @date 27 July 2013
 */

import java.net.NetworkInterface;
import java.net.InetAddress;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.SocketException;

import java.util.Enumeration;

public class NetUtil {
	public static InetAddress getLocalInet4Address() throws SocketException {
		Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
		while (interfaces.hasMoreElements()){
			NetworkInterface current = interfaces.nextElement();
			//System.out.println(current);
			if (!current.isUp() || current.isLoopback() || current.isVirtual()) continue;
			Enumeration<InetAddress> addresses = current.getInetAddresses();
			while (addresses.hasMoreElements()){
				InetAddress current_addr = addresses.nextElement();
				if (current_addr.isLoopbackAddress()) continue;
				if (current_addr instanceof Inet4Address) return current_addr;
			}
		}
		return null;
	}

	public static InetAddress getLocalInet6Address() throws SocketException {
		Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
		while (interfaces.hasMoreElements()){
			NetworkInterface current = interfaces.nextElement();
			//System.out.println(current);
			if (!current.isUp() || current.isLoopback() || current.isVirtual()) continue;
			Enumeration<InetAddress> addresses = current.getInetAddresses();
			while (addresses.hasMoreElements()){
				InetAddress current_addr = addresses.nextElement();
				if (current_addr.isLoopbackAddress()) continue;
				if (current_addr instanceof Inet6Address) return current_addr;
			}
		}
		return null;
	}
}
