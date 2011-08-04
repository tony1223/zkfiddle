package org.zkoss.fiddle.core.utils;

import javax.servlet.ServletContext;

import org.zkoss.fiddle.model.Resource;
import org.zkoss.zk.ui.Executions;


public class ResourceFactory {

	public static Resource getDefaultResource(int type, String name) {

		if (Resource.TYPE_ZUL == type) {
			String template = readThenReaplce("/WEB-INF/_templates/index.zul", "\\$\\{pkg\\}",
					Resource.PACKAGE_TOKEN_ESCAPE);
			return new Resource(Resource.TYPE_ZUL, name, template);
		} else if (Resource.TYPE_JS == type) {
			if(name != null && name.endsWith("dsp"))
				return new Resource(Resource.TYPE_JS, name, readTemplate("/WEB-INF/_templates/test.js.dsp"));
			else
				return new Resource(Resource.TYPE_JS, name, readTemplate("/WEB-INF/_templates/test.js"));
		} else if (Resource.TYPE_CSS == type) {
			if(name != null && name.endsWith("dsp"))
				return new Resource(Resource.TYPE_JS, name, readTemplate("/WEB-INF/_templates/test.css.dsp"));
			else
				return new Resource(Resource.TYPE_JS, name, readTemplate("/WEB-INF/_templates/test.css"));
		} else if (Resource.TYPE_HTML == type) {
			return (new Resource(Resource.TYPE_HTML, name,readTemplate("/WEB-INF/_templates/test.html")));
		} else if (Resource.TYPE_JAVA == type) {
			String clsName = name;
			if (clsName != null)
				clsName = name.replaceAll(".java", "");

			String template = readThenReaplce("/WEB-INF/_templates/TestComposer.java", "\\$\\{class-name\\}", clsName);
			return new Resource(Resource.TYPE_JAVA, name, template);
		}  else
			return null;
	}

	public static Resource getDefaultResource(int type) {
		if (Resource.TYPE_ZUL == type) {
			Resource r = getDefaultResource(type, "index.zul");
			r.setCanDelete(false);
			return r;
		} else if (Resource.TYPE_JS == type)
			return getDefaultResource(type, "index.js");
		else if (Resource.TYPE_CSS == type)
			return getDefaultResource(type, "index.css");
		else if (Resource.TYPE_HTML == type)
			return getDefaultResource(type, "index.html");
		else if (Resource.TYPE_JAVA == type)
			return getDefaultResource(type, "TestComposer.java");
		else
			return null;
	}
	private static String readTemplate(String filePath) {

		ServletContext req = (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext();

		return FileUtil.readIfExist(req.getRealPath(filePath));
	}

	private static String readThenReaplce(String filePath, String token, String replaced) {

		ServletContext req = (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext();

		String template = FileUtil.readIfExist(req.getRealPath(filePath));
		if (token != null && template != null)
			template = template.replaceAll(token, replaced);
		return template;
	}

}
