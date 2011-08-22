package nextmethod.web.mvc;


import com.google.common.base.Objects;

/**
 *
 */
public final class UrlParameter {

	public static final UrlParameter Optional = new UrlParameter();

	private UrlParameter() {
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return obj != null && UrlParameter.class.isInstance(obj);
	}
}
