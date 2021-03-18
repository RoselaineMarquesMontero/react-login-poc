package com.liferay.sagov.oidc.accesstoken;

import static com.liferay.sagov.oidc.constants.OIDCTokenServiceConstants.ACCESS_TOKEN;
import static com.liferay.sagov.oidc.constants.OIDCTokenServiceConstants.CLIENT_ID_ATTR;
import static com.liferay.sagov.oidc.constants.OIDCTokenServiceConstants.SESSION_ATTR_JOINER_CHAR;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.sagov.oidc.providerinfo.service.impl.OIDCProviderInfoServiceImpl;

import org.osgi.service.component.annotations.Component;

@Component(
		immediate = true,
		property = {
			"osgi.http.whiteboard.context.path=/",
			"osgi.http.whiteboard.servlet.pattern=/oidc/access_token/*"
		},
		service = Servlet.class
	)
public class OIDCAccessTokenServlet extends HttpServlet{
	/**
	 * 
	 */
	private static final long serialVersionUID = -9164023656269894196L;

	@Override
	public void init() throws ServletException {
		if (_log.isInfoEnabled()) {
			_log.info("OIDC RefreshToken Servlet init");
		}

		super.init();
	}
 
	@Override
	protected void doGet(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, ServletException {
		_log.info("OIDC RefreshToken Servlet GET");
		
		_log.info("User ID: " + PortalUtil.getUserId(httpServletRequest));
		_log.info("Company ID: " + PortalUtil.getCompanyId(httpServletRequest));
		
		
		String accessToken = getToken(httpServletRequest, httpServletResponse);
		
		_writeJSONResponse(httpServletRequest, httpServletResponse, accessToken);
	}

    protected String getToken(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
       
        String accessToken = "";
        try {
                HttpSession session = PortalUtil.getOriginalServletRequest(httpServletRequest).getSession(false);

                String clientId = (String) session.getAttribute(CLIENT_ID_ATTR);

                _log.info("ClientId from session in SPA server side --> "+ clientId);

                if(Validator.isNotNull(clientId)) {
                    accessToken = (String) session.getAttribute(ACCESS_TOKEN+SESSION_ATTR_JOINER_CHAR+clientId);
                }
                
                _log.info("Access_token ---> " +accessToken);
                
            }catch (Exception e) {
                _log.warn(e.getMessage(), e);
    
                httpServletResponse.setStatus(
                    HttpServletResponse.SC_PRECONDITION_FAILED);
        }
        return accessToken;
	}

	
	private void _writeJSONResponse(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, String accessToken) {
		
		httpServletResponse.setCharacterEncoding(StringPool.UTF8);
		httpServletResponse.setContentType(ContentTypes.APPLICATION_JSON);
		httpServletResponse.setStatus(HttpServletResponse.SC_OK);

		try {
			
			ServletResponseUtil.write(
				httpServletResponse, _generateJSON(accessToken));
		}
		catch (Exception e) {
			_log.warn(e.getMessage(), e);

			httpServletResponse.setStatus(
				HttpServletResponse.SC_PRECONDITION_FAILED);
		}
	}
	
	private String _generateJSON(String access_token) {

		JSONObject jsonObject =  JSONFactoryUtil.createJSONObject();
		jsonObject.put(ACCESS_TOKEN, access_token);
		
		return jsonObject.toJSONString();
		
	}	

	private static final Log _log = LogFactoryUtil.getLog(OIDCProviderInfoServiceImpl.class);

}
