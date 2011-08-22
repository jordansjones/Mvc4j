package nextmethod.web.mvc;

/**
 *
 */
public interface IAuthorizationFilter extends IMvcFilter {

	void onAuthorization(AuthorizationContext filterContext);
}
