/**
 *
 */
package anyangmvp.iteye.spider.parsers;

/**
 * blog实体类
 *
 * @author Stephen An
 * @date 2017年10月15日 上午10:39:45
 */
public class BlogEntity implements Comparable<BlogEntity> {
	private String title;
	private String blogUrl;

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getBlogUrl() {
		return this.blogUrl;
	}

	public void setBlogUrl(String blogUrl) {
		this.blogUrl = blogUrl;
	}

	/**
	 * @param title
	 * @param blogUrl
	 */
	public BlogEntity(String title, String blogUrl) {
		this.title = title;
		this.blogUrl = blogUrl;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.blogUrl == null) ? 0 : this.blogUrl.hashCode());
		result = prime * result
				+ ((this.title == null) ? 0 : this.title.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		BlogEntity other = (BlogEntity) obj;
		if (this.blogUrl == null) {
			if (other.blogUrl != null) {
				return false;
			}
		} else if (!this.blogUrl.equals(other.blogUrl)) {
			return false;
		}
		if (this.title == null) {
			if (other.title != null) {
				return false;
			}
		} else if (!this.title.equals(other.title)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "BlogEntity [title=" + this.title + ", blogUrl=" + this.blogUrl
				+ "]";
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(BlogEntity o) {
		// {return Integer.parseInt(this.title.subSequence(0, 3).toString()) -
		// Integer.parseInt(o.title.subSequence(0, 3).toString());}
		return this.title.compareTo(o.getTitle());
	}

	/**
	 *
	 */
	public BlogEntity() {
		// TODO Auto-generated constructor stub
	}

}
