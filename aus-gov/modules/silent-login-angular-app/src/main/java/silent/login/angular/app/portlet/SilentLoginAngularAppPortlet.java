package silent.login.angular.app.portlet;

import java.io.IOException;

import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import com.liferay.frontend.js.loader.modules.extender.npm.NPMResolver;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import silent.login.angular.app.constants.SilentLoginAngularAppPortletKeys;

/**
 * @author roselainedefaria
 */
@Component(
	immediate = true,
	property = {
		"com.liferay.portlet.display-category=category.sample",
		"com.liferay.portlet.instanceable=true",
		"javax.portlet.init-param.template-path=/",
		"javax.portlet.init-param.view-template=/view.jsp",
		"javax.portlet.name=" + SilentLoginAngularAppPortletKeys.SilentLoginAngularApp,
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=power-user,user",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false"
	},
	service = Portlet.class
)
public class SilentLoginAngularAppPortlet extends MVCPortlet {
	
	@Override
	public void doView(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		renderRequest.setAttribute(
			"mainRequire",
			_npmResolver.resolveModuleName("silent-login-angular-app") + " as main");

			_log.info("Silent Login Angular working...");

		super.doView(renderRequest, renderResponse);
	}

	@Reference
	private NPMResolver _npmResolver;

	private static final Log _log = LogFactoryUtil.getLog(SilentLoginAngularAppPortlet.class);

}