package anyangmvp.iteye.spider;

import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import anyangmvp.iteye.spider.parsers.BlogParser;
import anyangmvp.iteye.spider.utils.ConnectionUtil;

/**
 * 测试BlogParser功能
 *
 * @author Stephen An
 * @date 2017年10月15日 上午11:56:14
 */
public class BlogParserTest {

	@Test(enabled=true)
	public void testWriteBlogUrls2LocalJson() {
		String categoryUrl = "http://412887952-qq-com.iteye.com/category/356333";
		String localblogUrlsJson = "D:\\SpringBoot\\blogUrls.json";

		String blogListJson = BlogParser.writeBlogUrls2LocalJson(categoryUrl,
				localblogUrlsJson);

		System.out.println(blogListJson);
	}

	@Test(enabled=true)
	public void getBlogContent() {
		String localBlogsFolder = "D:\\SpringBoot\\";
		String blogUrl = "http://412887952-qq-com.iteye.com/blog/2390339";
		String blogTitle = "136. [视频]Spring Boot MyBatis升级篇-注解-分页查询";

		boolean getBlogResult = false;
		for (int i = 0; i < 3; i++) {
			getBlogResult = BlogParser.getBlogContent(blogUrl, blogTitle,
					localBlogsFolder);
			if (getBlogResult) {
				break;
			}
		}

		assertTrue(getBlogResult);
	}

	@BeforeClass(enabled=false)
	public void beforeClass() {
		//为防止服务器检测爬虫程序限制IP地址，获取动态代理，如果不需要动态代理可以注视掉。
		ConnectionUtil.createDynamicProxy();
	}

}
