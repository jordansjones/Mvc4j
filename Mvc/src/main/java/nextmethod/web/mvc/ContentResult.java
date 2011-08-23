package nextmethod.web.mvc;

import com.google.common.base.Strings;
import nextmethod.web.IHttpResponse;

import java.nio.charset.Charset;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 */
public class ContentResult extends ActionResult {

	private String content;
	private Charset contentEncoding;
	private String contentType;

	public ContentResult() {
	}

	public ContentResult(final String content) {
		this.content = content;
	}

	@Override
	public void executeResult(final ControllerContext context) {
		checkNotNull(context);

		final IHttpResponse response = context.getHttpContext().getResponse();
		if (!Strings.isNullOrEmpty(contentType)) {
			response.setContentType(contentType);
		}

		if (contentEncoding != null) {
			response.setContentEncoding(contentEncoding.name());
		}

		if (content != null) {
			response.write(content);
		}
	}

	public String getContent() {
		return content;
	}

	public void setContent(final String content) {
		this.content = content;
	}

	public Charset getContentEncoding() {
		return contentEncoding;
	}

	public void setContentEncoding(final Charset contentEncoding) {
		this.contentEncoding = contentEncoding;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(final String contentType) {
		this.contentType = contentType;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (!(o instanceof ContentResult)) return false;

		final ContentResult that = (ContentResult) o;

		if (content != null ? !content.equals(that.content) : that.content != null) return false;
		if (contentEncoding != null ? !contentEncoding.equals(that.contentEncoding) : that.contentEncoding != null)
			return false;
		if (contentType != null ? !contentType.equals(that.contentType) : that.contentType != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = content != null ? content.hashCode() : 0;
		result = 31 * result + (contentEncoding != null ? contentEncoding.hashCode() : 0);
		result = 31 * result + (contentType != null ? contentType.hashCode() : 0);
		return result;
	}
}
