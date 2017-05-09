package com.yumi.android.sdk.ads.utils.assets;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * 使用dom方式进行xml解析虽然因为每次处理一个element都要从该根节点一直到element节点进行一遍遍历，性能上会有一些影响,但是好处是，
 * 此处对于所有的xml文件都适用
 * 
 * @author Administrator
 * 
 */
public final class XMLParser {
	public final static int LEAF_NODE_NORMAL = 0;
	public final static int LEAF_NODE_NO_VALUE = 1;
	public final static int LEAF_NODE_CDATA = 2;

	public static Map<String, Object> paraserXML(InputStream inputStream) {
		Map<String, Object> map = new HashMap<String, Object>();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		Document document = null;

		try {
			document = builder.parse(inputStream);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// 获取根节点
		Element root = document.getDocumentElement();

		NodeList nodeList = root.getChildNodes();

		// 遍历根节点之下的二层子节点，如果该子节点下没有子节点的话，直接将该子节点的值put到以根节点的node_name为key的map中
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);

			// 未指定具体的listNodeName
			doParaser(node, map, null);
		}

		try {
			inputStream.close();
		} catch (Exception e) {
		}
		return map;
	}

	/**
	 * 将解析xml的inputStream并标示出listNode，在碰到listNode节点以后，其下的数据，不管是不是叶子节点，
	 * 都将放到以listNodeName为key的的map之下
	 * 
	 * @param inputStream
	 * @param listNodeName
	 * @return
	 */
	public static Map<String, Object> paraserXML(InputStream inputStream,
			String listNodeName) {

		Map<String, Object> map = new HashMap<String, Object>();

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		Document document = null;

		try {
			document = builder.parse(inputStream);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// 获取根节点
		Element root = document.getDocumentElement();
		NodeList nodeList = root.getChildNodes();

		// 遍历根节点之下的二层子节点，如果该子节点下没有子节点的话，直接将该子节点的值put到以根节点的node_name为key的map中
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);

			// 指定了具体的listNodeName的情况
			doParaser(node, map, listNodeName);
		}

		return map;
	}

	private static void doParaser(Node node, Map<String, Object> map,
			String listNodeName) {
		// 只有类型是element的节点才会进行处理
		if (node.getNodeType() == Node.ELEMENT_NODE) {
			Element element = (Element) node;
			// 是他的子节点
			if (isElementChildOf(element, listNodeName)) {
				// 是叶子节点，节点形式<node_name>node_value</node_name>
				if ((element.getChildNodes().getLength() == 1 && element
						.getChildNodes().item(0).getNodeType() == Node.TEXT_NODE)) {
					putLeafIntoMapWithSpecified(element, map, LEAF_NODE_NORMAL,
							listNodeName);
				} else
				// 是叶子节点，且该节点形式如:"</node_name>"，也就是说该node没有值
				if (element.getChildNodes().getLength() == 0) {
					putLeafIntoMapWithSpecified(element, map, LEAF_NODE_NORMAL,
							listNodeName);
				} else
				// 是叶子节点，且该节点形式如："<node_name><![CDATA[ node_value ]]></node_name>"
				if (element.getChildNodes().getLength() == 1
						&& element.getChildNodes().item(0).getNodeType() == Node.CDATA_SECTION_NODE) {
					putLeafIntoMapWithSpecified(element, map, LEAF_NODE_NORMAL,
							listNodeName);
				}
				doParaser(element, map, listNodeName);
			} else {
				if ((element.getChildNodes().getLength() == 1 && element
						.getChildNodes().item(0).getNodeType() == Node.TEXT_NODE)) {
					putLeafIntoMapWithNoSpecified(element, map,
							LEAF_NODE_NORMAL);
				} else
				// 是叶子节点，且该节点形式如:"</node_name>"，也就是说该node没有值
				if (element.getChildNodes().getLength() == 0) {
					putLeafIntoMapWithNoSpecified(element, map,
							LEAF_NODE_NO_VALUE);
				} else
				// 是叶子节点，且该节点形式如："<node_name><![CDATA[ node_value ]]></node_name>"
				if (element.getChildNodes().getLength() == 1
						&& element.getChildNodes().item(0).getNodeType() == Node.CDATA_SECTION_NODE) {
					putLeafIntoMapWithNoSpecified(element, map, LEAF_NODE_CDATA);
				} else {
					doTravelWithNoSpecified(element, map);
				}
			}
		}
	}

	/**
	 * 判断element是否是某个node的子节点，该node的名称叫做nodeName
	 * 
	 * @param element
	 *            待验证的element
	 * @param nodeName
	 * @return
	 */
	private static boolean isElementChildOf(Element element, String nodeName) {
		if (nodeName == null) {
			return false;
		} else {
			for (Node node = element.getParentNode(); node != null; node = node
					.getParentNode()) {
				if (node.getNodeName().equals(nodeName)) {
					return true;

				}
			}
			return false;
		}
	}

	// 将该element的键值对放到以其parent的nodeName为key的
	@SuppressWarnings("unchecked")
	private static void putLeafIntoMapWithNoSpecified(Element element,
			Map<String, Object> rootMap, int type) {

		String key = element.getParentNode().getNodeName();
		String value = null;
		switch (type) {
		case LEAF_NODE_NORMAL:
		case LEAF_NODE_CDATA:
			value = element.getChildNodes().item(0).getNodeValue();
			break;
		case LEAF_NODE_NO_VALUE:
			value = "null";
			break;
		}

		// rootMap中有存储key标示的数据
		if (rootMap.get(key) != null) {

			// 在该key处保存的是一个List，说明key的孩子节点的结构都是一样的
			if (rootMap.get(key) instanceof List<?>) {
				List<Object> elementList = (List<Object>) rootMap.get(key);

				// 不是在最后一个基础上继续添加
				if (((Map<String, Object>) (elementList
						.get(elementList.size() - 1))).containsKey(element
						.getNodeName())) {

					elementList.add(buildMap(
							new String[] { element.getNodeName() },
							new String[] { value }));
				}
				// 在在最后一个基础上继续添加
				else {
					((Map<String, Object>) (elementList
							.get(elementList.size() - 1))).put(
							element.getNodeName(), value);

				}

			}

			// 该处保存的是一个map
			if (rootMap.get(key) instanceof Map<?, ?>) {

				Map<String, Object> elementMap = (Map<String, Object>) rootMap
						.get(key);
				// map中有保存该key
				if (elementMap.containsKey(element.getNodeName())) {
					rootMap.remove(key);
					List<Map<String, Object>> elementList = new ArrayList<Map<String, Object>>();
					elementList.add(elementMap);
					elementList.add(buildMap(
							new String[] { element.getNodeName() },
							new String[] { value }));
					rootMap.put(key, elementList);

				}

				// 没有保存该key
				else {
					elementMap.put(element.getNodeName(), value);
				}

			}

		} else {
			rootMap.put(
					key,
					buildMap(new String[] { element.getNodeName() },
							new String[] { value }));
		}

	}

	/**
	 * 该节点的父节点是listNodeName为名称的node
	 * 
	 * @param element
	 *            叶子节点
	 * @param rootMap
	 *            所有解析的xml数据的存放处
	 * @param type
	 *            叶子节点的类型
	 * @param listNodeName
	 *            父节点的名称
	 */
	@SuppressWarnings("unchecked")
	private static void putLeafIntoMapWithSpecified(Element element,
			Map<String, Object> rootMap, int type, String listNodeName) {
		String value = null;

		switch (type) {
		case LEAF_NODE_NORMAL:
		case LEAF_NODE_CDATA:
			value = element.getChildNodes().item(0).getNodeValue();
			break;
		case LEAF_NODE_NO_VALUE:
			value = "null";
			break;
		}

		String[] parentNames = getParentNames(element, listNodeName);

		Map<String, Object> tempMap = rootMap;

		for (int i = parentNames.length - 1; i >= 0; i--) {

			if (tempMap.get(parentNames[i]) == null) {
				tempMap.put(parentNames[i], new HashMap<String, Object>());
			}
			tempMap = (Map<String, Object>) tempMap.get(parentNames[i]);
		}

		// 已经有这个值了，那么就需要重新去new一个数据结构，然后将该值put到相对应的结构处
		if (tempMap.containsKey(element.getNodeName())) {

			Map<String, Object> map = new HashMap<String, Object>();

			for (int i = parentNames.length - 2; i >= 0; i--) {
				if (i == 0) {
					map.put(parentNames[i], value);
				} else {
					map.put(parentNames[i], new HashMap<String, Object>());
				}
			}

			Object listObject = rootMap.get(listNodeName);
			if (listObject instanceof Map<?, ?>) {
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				list.add((Map<String, Object>) listObject);
				list.add(map);

				rootMap.remove(listNodeName);
				rootMap.put(listNodeName, list);
			}

			if (listObject instanceof List<?>) {

				List<Map<String, Object>> list = (List<Map<String, Object>>) listObject;
				list.add(map);

			}

		} else {
			tempMap.put(element.getNodeName(), value);
		}

	}

	/**
	 * 例如: <info> <child> <name> 张三 </name> </child> </info>
	 * 如果element是name节点，而listNodeName是child的话，那么返回[child]
	 * 
	 * @param element
	 * @param listNodeName
	 * @return
	 */
	private static String[] getParentNames(Element element, String listNodeName) {
		List<String> list = new ArrayList<String>();
		for (String key = element.getParentNode().getNodeName(); !key
				.equals(listNodeName); key = element.getParentNode()
				.getNodeName()) {
			list.add(key);
		}
		list.add(listNodeName);
		return (String[]) list.toArray();
	}

	// 遍历该节点
	private static void doTravelWithNoSpecified(Element node,
			Map<String, Object> map) {
		for (int i = 0; i < node.getChildNodes().getLength(); i++) {
			Node tempNode = node.getChildNodes().item(i);

			doParaser(tempNode, map, null);
		}
	}

	/**
	 * 根据传递的键值对创建一个map并返回
	 * 
	 * @param keys
	 *            键
	 * @param values
	 *            值
	 * @return
	 */
	private static Map<String, Object> buildMap(String[] keys, String[] values) {
		Map<String, Object> map = new HashMap<String, Object>();
		for (int i = 0; i < keys.length; i++) {
			map.put(keys[i], values[i]);
		}
		return map;
	}

}
