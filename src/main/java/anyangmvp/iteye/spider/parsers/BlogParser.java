/**
 *
 */
package anyangmvp.iteye.spider.parsers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.jsoup.HttpStatusException;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.alibaba.fastjson.JSON;

import anyangmvp.iteye.spider.utils.ConnectionUtil;

/**
 * 博客解析工具
 *
 * @author Stephen An
 * @date 2017年10月15日 上午10:55:26
 */
public class BlogParser {

	/**
	 * 根据iteye博客分类url地址，将获取到的所有博客链接json写入本地文件blogsJsonPath。
	 *
	 * @author Stephen An
	 * @date 2017年10月15日 上午9:57:29
	 * @param categoryUrl
	 *            博客分类链接url
	 * @param totalPages
	 *            总页码
	 * @param blogsJsonPath
	 *            将接续到的博客链接json保存本地的路径
	 * @return
	 */
	public static String writeBlogUrls2LocalJson(String categoryUrl,
			String localblogUrlsJson) {

		List<BlogEntity> blogList = new ArrayList<BlogEntity>();

		String blogBaseUrl = categoryUrl.substring(0,
				categoryUrl.indexOf("/category/"));
		String categoryUrlPage = categoryUrl;
		boolean getPageCountResult = false;
		int totalPages = 1;

		for (int i = 1; i <= totalPages; i++) {
			categoryUrlPage = categoryUrl + "?page=" + i;
			try {
				Document category = ConnectionUtil.instance(categoryUrlPage, 5)
						.get();

				if (!getPageCountResult) {
					//获取总页码
					totalPages = Integer
							.parseInt(category.select(".pagination>.next_page")
									.get(0).previousElementSibling().text());
					getPageCountResult = true;
				}

				Elements eles = category
						.select("div.blog_title a:matchesOwn(^[\\d\\(（]");
				eles.forEach(ele -> {
					{
						String title = ele.text().replaceAll("（", "(")
								.replaceAll("）", ")");
						String regexStr = "([\\(]?\\d+[\\)]?[\\.]?\\s*)";
						Matcher m = Pattern.compile(regexStr).matcher(title);
						StringBuffer sb = new StringBuffer();
						while (m.find()) {
							m.appendReplacement(sb, String.format("%03d. ",
									Integer.parseInt(m.group().replaceAll(
											"[\\(\\)\\.\\s#\\$]", ""))));
							break;
						}
						m.appendTail(sb);
						title = sb.toString();
						String blogUrl = blogBaseUrl + ele.attr("href");
						blogList.add(new BlogEntity(title, blogUrl));
					}
				});

				System.out.println("Page:" + i + " 获取成功 ...");

			} catch (IOException e) {
				e.printStackTrace();
				System.err.println("Page:" + i + " 获取异常，将重试 ...");
				i--;
			}

		}

		try {
			Collections.sort(blogList);
			JSON.writeJSONString(new FileWriter(localblogUrlsJson), blogList);
			//			System.out.println(JSON.toJSONString(blogList, true));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return JSON.toJSONString(blogList, true);
	}

	/**
	 * 根据博客地址，获取博客内容，并且写入到本地html文件
	 *
	 * @author Stephen An
	 * @date 2017年10月15日 上午10:36:01
	 * @param blogUrl
	 *            博客链接url
	 * @param blogTitle
	 *            博客标题，可以从writeBlogUrls2LocalJson()方法获取到的json取到
	 * @param localBlogsFolder
	 *            本地保存博客的文件夹路径
	 * @return
	 */
	public static boolean getBlogContent(String blogUrl, String blogTitle,
			String localBlogsFolder) {

		boolean success = false;

		try {
			Document blog = ConnectionUtil.instance(blogUrl, 5).get();

			// 删除无需显示的内容
			blog.select("div#nav_show_top_stop + script").remove();
			blog.getElementsByAttributeValueMatching("id",
					"(local|bottoms|footer|branding|branding|header|nav_show_top_stop)")
					.remove();
			blog.getElementsByAttributeValueMatching("class",
					"(h-entry|blog_nav|blog_bottom|boutique-curr-box|blog_comment)")
					.remove();
			blog.getElementsByAttributeValueMatching("src",
					".*(weiboshare\\.js|async_new\\.js|async_new\\.js|tracking\\.js|dup.baidustatic\\.com/js/ds\\.js|replace\\.min\\.js).*")
					.remove();

			// 重置width
			blog.getElementById("content").attr("style", "width:100%");
			blog.getElementById("main").attr("style", "width:96%");
			blog.getElementsByClass("blog_main").attr("style", "width:100%");

			// 重设页面显示title
			blog.select("title,h3 a")
					.forEach(element -> element.text(blogTitle));

			FileUtils
					.writeStringToFile(
							new File(localBlogsFolder,
									blogTitle.replaceAll(
											"[\\\\/:\\*\\?\\\"<>\\|]", "_")
											+ ".html"),
							blog.html(), Charset.forName("UTF-8"));

			success = true;

		} catch (SocketException e) {
			//			e.printStackTrace();
			System.err.println(blogTitle + " 获取失败 " + e.getMessage());
		} catch (HttpStatusException e) {
			//			e.printStackTrace();
			System.err.println(blogTitle + " 获取失败 " + e.getMessage());
		} catch (SocketTimeoutException e) {
			//			e.printStackTrace();
			System.err.println(blogTitle + " 获取失败 " + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
//			System.err.println("遇到致命错误跳过去！");
//			success = true;
		}

		return success;
	}

}
