package org.zkoss.fiddle.core.utils;

import javax.servlet.ServletContext;

import org.zkoss.fiddle.model.Resource;
import org.zkoss.fiddle.model.api.IResource;
import org.zkoss.zk.ui.Executions;


public class ResourceFactory {

	public static Resource getDefaultResource(int type, String name) {

		if (IResource.TYPE_ZUL == type) {
			String template = readThenReaplce("/WEB-INF/_templates/index.zul", "\\$\\{pkg\\}",
					IResource.PACKAGE_TOKEN_ESCAPE);
			return new Resource(IResource.TYPE_ZUL, name, template);
		} else if (IResource.TYPE_JS == type) {
			return new Resource(IResource.TYPE_JS, name, "function hello(){alert('hello');}");
		} else if (IResource.TYPE_CSS == type) {
			return new Resource(IResource.TYPE_CSS, name, ".hello{ \n color:red; \n }");
		} else if (IResource.TYPE_HTML == type) {
			return (new Resource(IResource.TYPE_HTML, name,
					"<html>\n  <head>\n    <title>Hello</title>\n  </head>\n\n<body>\n    hello\n  </body>\n</html>"));
		} else if (IResource.TYPE_JAVA == type) {
			String clsName = name;
			if (clsName != null)
				clsName = name.replaceAll(".java", "");

			String template = readThenReaplce("/WEB-INF/_templates/TestComposer.java", "\\$\\{class-name\\}", clsName);
			return new Resource(IResource.TYPE_JAVA, name, template);
		} else
			return null;
	}

	public static Resource getDefaultResource(int type) {
		if (IResource.TYPE_ZUL == type) {
			Resource r = getDefaultResource(type, "index.zul");
			r.setCanDelete(false);
			return r;
		} else if (IResource.TYPE_JS == type)
			return getDefaultResource(type, "index.js");
		else if (IResource.TYPE_CSS == type)
			return getDefaultResource(type, "index.css");
		else if (IResource.TYPE_HTML == type)
			return getDefaultResource(type, "index.html");
		else if (IResource.TYPE_JAVA == type)
			return getDefaultResource(type, "TestComposer.java");
		else
			return null;
	}

	private static String readThenReaplce(String filePath, String token, String replaced) {

		ServletContext req = (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext();

		String template = FileUtil.readIfExist(req.getRealPath(filePath));
		if (token != null && template != null)
			template = template.replaceAll(token, replaced);
		return template;
	}

}
