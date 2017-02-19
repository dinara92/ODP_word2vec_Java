package hashmap;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import category_node.CategoryNode;
import page_node.PageNode;

public class DBtoHashmap {
	
	public Map<String, List<PageNode>> resultSetPageToList(ResultSet rs) throws SQLException {

		ResultSetMetaData md = rs.getMetaData();
		int columns = md.getColumnCount();

		Map<String, List<PageNode>> thisCategoryPagesMap = new HashMap<String, List<PageNode>>();

		PageNode page = null;
		String currentCategory;
		int count = 0;
		long millis = System.currentTimeMillis();
		while (rs.next()) {
			Map<String, Object> row = new HashMap<String, Object>(columns);
			for (int i = 1; i <= columns; ++i) {
				row.put(md.getColumnName(i), rs.getObject(i));
			}

			page = new PageNode();
			currentCategory = row.get("catid").toString();
			page.set_id(row.get("id").toString());
			page.setDescription(row.get("Title").toString() + " " + row.get("Description").toString());
			
			if (thisCategoryPagesMap.containsKey(currentCategory)) {
				thisCategoryPagesMap.get(currentCategory).add(page);
			} else {
				List<PageNode> allCategoryPages = new ArrayList<PageNode>();
				allCategoryPages.add(page);
				thisCategoryPagesMap.put(currentCategory, allCategoryPages);
			}
			count++;
		}
		System.out.println("added all " + count + " pages to list of size " + thisCategoryPagesMap.size());
		millis = System.currentTimeMillis() - millis;
		System.out.println("in " + millis + "ms");
		return thisCategoryPagesMap;
	}
	
	public Map<String, List<PageNode>> resultSetPageWithLinkToList(ResultSet rs) throws SQLException {

		ResultSetMetaData md = rs.getMetaData();
		int columns = md.getColumnCount();

		Map<String, List<PageNode>> thisCategoryPagesMap = new HashMap<String, List<PageNode>>();

		PageNode page = null;
		String currentCategory;
		int count = 0;
		long millis = System.currentTimeMillis();
		while (rs.next()) {
			Map<String, Object> row = new HashMap<String, Object>(columns);
			for (int i = 1; i <= columns; ++i) {
				row.put(md.getColumnName(i), rs.getObject(i));
			}

			page = new PageNode();
			currentCategory = row.get("catid").toString();
			page.set_id(row.get("id").toString());
			page.setPage_link(row.get("link").toString());
			page.setDescription(row.get("Title").toString() + " " + row.get("Description").toString());
			
			if (thisCategoryPagesMap.containsKey(currentCategory)) {
				thisCategoryPagesMap.get(currentCategory).add(page);
			} else {
				List<PageNode> allCategoryPages = new ArrayList<PageNode>();
				allCategoryPages.add(page);
				thisCategoryPagesMap.put(currentCategory, allCategoryPages);
			}
			count++;
		}
		System.out.println("added all " + count + " pages to list of size " + thisCategoryPagesMap.size());
		millis = System.currentTimeMillis() - millis;
		System.out.println("in " + millis + "ms");
		return thisCategoryPagesMap;
	}
	
	public Map<String, List<String>> resultSetCategoryToList(ResultSet rs) throws SQLException {
		ResultSetMetaData md = rs.getMetaData();
		int columns = md.getColumnCount();
		Map<String, List<String>> thisCategoryFieldsMap = new HashMap<String, List<String>>();

		int count = 0;
		long millis = System.currentTimeMillis();
		while (rs.next()) {

			Map<String, Object> row = new HashMap<String, Object>(columns);
			for (int i = 1; i <= columns; ++i) {
				row.put(md.getColumnName(i), rs.getObject(i));
			}
			CategoryNode category = new CategoryNode();

			category.setCatid(row.get("catid").toString());
			category.setFatherid(row.get("fatherid").toString());
			category.setTopic(row.get("Topic").toString());

			if (thisCategoryFieldsMap.containsKey(category.getCatid())) {
				thisCategoryFieldsMap.get(category.getCatid()).add(category.getTopic());
				thisCategoryFieldsMap.get(category.getCatid()).add(category.getFatherid());
			} else {
				List<String> allCategoryFields = new ArrayList<String>();
				allCategoryFields.add(category.getTopic());
				allCategoryFields.add(category.getFatherid());
				thisCategoryFieldsMap.put(category.getCatid(), allCategoryFields);
			}
			count++;
		}
		System.out.println("added all " + count +" categories to list of size " + thisCategoryFieldsMap.size());
		millis = System.currentTimeMillis() - millis;
		System.out.println("in " + millis + "ms");
		return thisCategoryFieldsMap;
	}

}
