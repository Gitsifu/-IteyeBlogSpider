/**
 *
 */
package anyangmvp.iteye.spider;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.alibaba.fastjson.JSON;

import anyangmvp.iteye.spider.parsers.BlogEntity;
import anyangmvp.iteye.spider.parsers.BlogParser;
import anyangmvp.iteye.spider.utils.ConnectionUtil;

/**
 * 批量获取博客HTML
 *
 * @author Stephen An
 * @date 2017年10月15日 上午11:19:43
 */
public class App {

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		//TODO 博客分类url
		String categoryUrl = "http://412887952-qq-com.iteye.com/category/356333";
		//TODO 设置博客列表保存本地的json文件路径
		String localblogUrlsJson = "D:\\SpringBoot\\blogUrls.json";
		//TODO 设置博客HTML保存本地的文件夹路径
		String localBlogsFolder = "D:\\SpringBoot\\";

		List<BlogEntity> blogs = new ArrayList<>();

		if (false) {
			//获取博客列表json
			String blogListJson = BlogParser
					.writeBlogUrls2LocalJson(categoryUrl, localblogUrlsJson);

			blogs = JSON.parseArray(blogListJson, BlogEntity.class);
		} else {
			try {
				blogs = JSON
						.parseArray(
								FileUtils.readFileToString(
										new File(localblogUrlsJson), "UTF-8"),
								BlogEntity.class);

				Collections.sort(blogs);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		//为防止服务器检测爬虫程序限制IP地址，获取动态代理，如果不需要动态代理可以注视掉。
		ConnectionUtil.createDynamicProxy();

		//连接失败重试次数
		int tryCount = 3;
		for (int i = 0; i < blogs.size(); i++) {
			BlogEntity blog = blogs.get(i);

			//将博客HTML写入本地文件夹
			boolean success = BlogParser.getBlogContent(blog.getBlogUrl(),
					blog.getTitle(), localBlogsFolder);

			System.out.println(blog.getTitle() + (success?" 获取成功":" 获取失败"));

			if (!success) {
				i--;
				if (tryCount-- < 2) {
					//如果获取失败三次，重新更换动态代理
					ConnectionUtil.resetProxy();
					tryCount = 3;
				}
			} else {
				tryCount = 3;
			}
		}

	}
}
