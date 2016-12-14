package category_node;

import java.util.List;

public class CategoryNode {

	String catid;
	String fatherid, topic, title, description, page_count;
	List<String> pages;
	boolean isFather;
	

	public String getCatid() {
		return catid;
	}

	public void setCatid(String catid) {
		this.catid = catid;
	}

	public String getFatherid() {
		return fatherid;
	}

	public void setFatherid(String fatherid) {
		this.fatherid = fatherid;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPage_count() {
		return page_count;
	}

	public void setPage_count(String page_count) {
		this.page_count = page_count;
	}

	public List<String> getPages() {
		return pages;
	}

	public void setPages(List<String> pages) {
		this.pages = pages;
	}

	public boolean isFather() {
		return isFather;
	}

	public void setFather(boolean isFather) {
		this.isFather = isFather;
	}

	

}
