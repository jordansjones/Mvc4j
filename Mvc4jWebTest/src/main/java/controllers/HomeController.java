package controllers;

import filters.OneActionFilter;
import filters.ThreeActionFilter;
import filters.TwoActionFilter;
import nextmethod.web.mvc.ActionResult;
import nextmethod.web.mvc.Controller;
import nextmethod.web.mvc.annotations.Filter;
import nextmethod.web.mvc.annotations.Filters;

/**
 *
 */
public class HomeController extends Controller {

	@Filters({
		@Filter(impl = OneActionFilter.class, order = 1),
		@Filter(impl = TwoActionFilter.class, order = 2),
		@Filter(impl = ThreeActionFilter.class, order = 3)
	})
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
