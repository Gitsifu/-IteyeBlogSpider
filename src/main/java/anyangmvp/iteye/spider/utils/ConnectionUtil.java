/**
 *
 */
package anyangmvp.iteye.spider.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.alibaba.fastjson.JSON;

/**
 * @author Stephen An
 * @date 2017年10月7日 上午10:04:25
 */
public class ConnectionUtil {
	private static List<Proxy> proxyList = null;
	private static Proxy currentProxy = null;

	public static Connection instance(String url, Integer timeOutSeconds) {
		Connection conn = Jsoup.connect(url);
		if (timeOutSeconds != null) {
			conn.timeout(timeOutSeconds * 1000);
		}
		if (proxyList != null && !proxyList.isEmpty()) {
			if (currentProxy == null) {
				currentProxy = proxyList
						.get(new Random().nextInt(proxyList.size()));
			}
			conn.proxy(currentProxy.getHost(), currentProxy.getPort());
			System.out.println("Set proxy " + currentProxy);
		}
		// //浏览器可接受的MIME类型。
		conn.header("Host", url);
		conn.header("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:56.0) Gecko/20100101 Firefox/56.0");
		conn.header("Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		conn.header("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
		conn.header("Accept-Encoding", "gzip, deflate");
		// conn.header("Cookie", "_javaeye_cookie_id_=1507340624522263;
		// _javaeye3_session_=BAh7BjoPc2Vzc2lvbl9pZCIlZTEwYjZiMWY3MTEyY2RlMDNkNzU5ZDE3ODA0NjM4ODc%3D--e01d5f2a6427c6d86e502d60cfe841cab588e63d");
		conn.header("Connection", "keep-alive");
		return conn;
	}

	/**
	 * 该方法获取代理列表，<B>只需要执行一次</B>
	 *
	 * @author Stephen An
	 * @date 2017年10月7日 下午4:38:49
	 */
	public static void createDynamicProxy() {
		if (proxyList == null) {
			try {
				Elements els = ConnectionUtil
						.instance("http://www.xicidaili.com/wt/", 5).get()
						.getElementsByClass("odd");
				proxyList = new ArrayList<Proxy>();
				for (Element element : els) {
					String speed = element.child(6).child(0).attr("title");
					if (speed.charAt(0) > '2') {
						continue;
					}
					String host = element.child(1).text();
					String port = element.child(2).text();
					String city = element.child(3).text();
					Proxy proxy = new Proxy(host, Integer.parseInt(port), city,
							speed);
					proxyList.add(proxy);
				}
				System.out.println("动态代理信息获取成功:");
				System.out.println(JSON.toJSONString(proxyList) + "\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 重新更换代理地址，在下一次ConnectionUtil.instance()生效
	 *
	 * @author Stephen An
	 * @date 2017年10月7日 下午4:39:12
	 */
	public static void resetProxy() {
		currentProxy = null;
	}

	static class Proxy {
		private String host;
		private Integer port;
		private String city;
		private String speed;

		public String getHost() {
			return this.host;
		}

		public void setHost(String host) {
			this.host = host;
		}

		public Integer getPort() {
			return this.port;
		}

		public void setPort(Integer port) {
			this.port = port;
		}

		public String getCity() {
			return this.city;
		}

		public void setCity(String city) {
			this.city = city;
		}

		public String getSpeed() {
			return this.speed;
		}

		public void setSpeed(String speed) {
			this.speed = speed;
		}

		/**
		 * @param host
		 * @param port
		 * @param city
		 * @param speed
		 */
		public Proxy(String host, Integer port, String city, String speed) {
			this.host = host;
			this.port = port;
			this.city = city;
			this.speed = speed;
		}

		@Override
		public String toString() {
			return "Proxy [host=" + this.host + ", port=" + this.port
					+ ", city=" + this.city + ", speed=" + this.speed + "]";
		}

	}
}
