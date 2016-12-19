package org.colorcoding.tools.btulz.shell;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * 运行环境
 * 
 * @author Niuren.Zhu
 *
 */
public class Environment {

	/**
	 * 命名空间-模型
	 */
	public static final String NAMESPACE_BTULZ_MODELS = "http://colorcoding.org/btulz/models";

	/**
	 * 命名空间-变形金刚
	 */
	public static final String NAMESPACE_BTULZ_TRANSFORMERS = "http://colorcoding.org/btulz/transformers";

	/**
	 * 命名空间-执行计划
	 */
	public static final String NAMESPACE_BTULZ_ORCHESTRATION = "http://colorcoding.org/btulz/orchestration";

	/**
	 * 程序启动的目录（主要的配置文件目录）
	 * 
	 * @return
	 */
	public static String getStartupFolder() {
		try {
			File file = null;
			URL url = Thread.currentThread().getContextClassLoader().getResource("");
			String path = null;
			if (url != null) {
				URI uri = url.toURI();
				if (uri != null) {
					path = uri.getPath();
				}
				if (path == null) {
					path = url.getPath();
					if (path != null)
						path = java.net.URLDecoder.decode(path, "UTF-8");
				}
			}
			if (path != null) {
				if (path.split(":").length > 2) {
					path = path.substring(path.indexOf(":") + 1, path.length());
				}
				if (path.indexOf("!") > 0) {
					path = path.substring(0, path.indexOf("!"));
				}
			}
			if (path == null) {
				path = System.getProperty("user.dir");
			}
			file = new File(path);
			if (file.isFile()) {
				file = file.getParentFile();
			}
			if (file.getParentFile().isDirectory() && file.getParentFile().getName().equals("WEB-INF")) {
				// web路径
				file = file.getParentFile();
			}
			return file.getPath();
		} catch (URISyntaxException | UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 获取工作目录
	 * 
	 * @return
	 */
	public static String getWorkingFolder() {
		return getStartupFolder();
	}

	/**
	 * 获取资源地址
	 * 
	 * @param type
	 *            资源名称
	 * @return 统一格式（此对象避免路径的中文问题）
	 * @throws URISyntaxException
	 */
	public static URI getResource(String name) throws URISyntaxException {
		URL url = Thread.currentThread().getContextClassLoader().getResource(name);
		if (url == null) {
			return null;
		}
		return url.toURI();
	}

}
