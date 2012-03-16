package controllers;

import filters.OneActionFilter;
import filters.ThreeActionFilter;
import filters.TwoActionFilter;
import nextmethod.web.mvc.ActionResult;
import nextmethod.web.mvc.Controller;
import nextmethod.web.mvc.annotations.Filter;
import nextmethod.web.mvc.annotations.Filters;

import java.util.Date;

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
		System.out.println("Home.index: " + new Date().toString());
		return view();
	}
	
	public ActionResult test(final String id) {
		return view();
	}

	public ActionResult about() {
		return view();
	}
}
