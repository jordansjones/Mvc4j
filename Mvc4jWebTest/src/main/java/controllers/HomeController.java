package controllers;

import com.nextmethod.web.mvc.ActionResult;
import com.nextmethod.web.mvc.Controller;

/**
 *
 */
public class HomeController extends Controller {

	public ActionResult index() {
		return view();
	}

	public ActionResult about() {
		return view();
	}

	public void test() {
		int x = 1;
	}
}
