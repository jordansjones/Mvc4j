package nextmethod.web.mvc;

import nextmethod.web.HttpException;
import nextmethod.web.routing.RequestContext;

/**
 *
 */
public interface IController {

	void execute(RequestContext requestContext) throws HttpException;

}
