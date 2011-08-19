package com.nextmethod.web.routing;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.nextmethod.OutParam;
import com.nextmethod.web.IHttpContext;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import static com.nextmethod.web.routing.Helpers.rvdHas;

/**
 * User: Jordan
 * Date: 8/7/11
 * Time: 8:00 PM
 */
class PatternParser {

	private static final char BEGIN_PLACEHOLDER = '{';
	private static final char END_PLACEHOLDER = '}';

	private final String url;

	PatternParser(final String url) {
		this.url = url;
		parseUrl();
	}

	PatternSegment[] segments;
	Map<String, Boolean> parameterNames;
	PatternToken[] tokens;
	int segmentCount;
	boolean haveSegmentWithCatchAll;

	void parseUrl() {

		final String url = this.url;
		parameterNames = Maps.newHashMap();

		if (!Strings.isNullOrEmpty(url)) {
			if (url.charAt(0) == '~' || url.charAt(0) == '/')
				throw new IllegalArgumentException("Url must not start with '~' or '/'");

			if (url.indexOf('?') >= 0)
				throw new IllegalArgumentException("Url must not contain '?'");
		} else {
			segments = new PatternSegment[0];
			tokens = new PatternToken[0];
			return;
		}

		final String[] parts = url.split("/");
		int partsCount = segmentCount = parts.length;
		final List<PatternToken> allTokens = Lists.newArrayList();
		PatternToken tempToken;

		segments = new PatternSegment[partsCount];

		for (int i = 0; i < parts.length; i++) {
			if (haveSegmentWithCatchAll) {
				throw new IllegalArgumentException("A catch-all parameter can only appear as the last segment of the route Url");
			}

			int catchAlls = 0;
			String part = parts[i];
			int partLength = part.length();
			List<PatternToken> tokens = Lists.newArrayList();

			if (partLength == 0 && i < partsCount - 1)
				throw new IllegalArgumentException("Consecutive Url segment separators '/' are not allowed");

			if (part.contains("{}"))
				throw new IllegalArgumentException("Empty Url parameter name is not allowed");

			if (i > 0)
				allTokens.add(null);

			if (part.indexOf(BEGIN_PLACEHOLDER) == -1 && part.indexOf(END_PLACEHOLDER) == -1) {
				// no placeholders, short-circuit it
				tempToken = new PatternToken(PatternTokenType.Literal, part);
				tokens.add(tempToken);
				allTokens.add(tempToken);
				segments[i] = new PatternSegment(true, tokens);
				continue;
			}

			String tmp;
			int from = 0, start;
			boolean allLiteral = true;
			while (from < partLength) {
				start = part.indexOf(BEGIN_PLACEHOLDER, from);
				if (start >= partLength - 2)
					throw new IllegalArgumentException(String.format("Unterminated Url parameter. It must contain a matching '%s'", END_PLACEHOLDER));

				if (start < 0) {
					if (part.indexOf(END_PLACEHOLDER, from) >= from)
						throw new IllegalArgumentException(String.format("Unmatched Url parameter closer '%s'. A corresponding '%s' must precede", END_PLACEHOLDER, BEGIN_PLACEHOLDER));

					tmp = part.substring(from);
					tempToken = new PatternToken(PatternTokenType.Literal, tmp);
					tokens.add(tempToken);
					allTokens.add(tempToken);
					from += tmp.length();
					break;
				}

				if (from == 0 && start > 0) {
					tempToken = new PatternToken(PatternTokenType.Literal, part.substring(0, start));
					tokens.add(tempToken);
					allTokens.add(tempToken);
				}

				int end = part.indexOf(END_PLACEHOLDER, start + 1);
				int next = part.indexOf(BEGIN_PLACEHOLDER, start + 1);

				if (end < 0 || next >= 0 && next < end)
					throw new IllegalArgumentException(String.format("Unterminated Url parameter. It must contain matching '%s'", END_PLACEHOLDER));
				if (next == (end + 1))
					throw new IllegalArgumentException("Two consecutive URL parameters are not allowed. Split into a different segment by '/', or a literal string.");

				if (next == -1)
					next = partLength;

				String token = part.substring(start + 1, end);
				PatternTokenType type;
				if (token.charAt(0) == '*') {
					catchAlls++;
					haveSegmentWithCatchAll = true;
					type = PatternTokenType.CatchAll;
					token = token.substring(1);
				} else {
					type = PatternTokenType.Standard;
				}

				if (!parameterNames.containsKey(token))
					parameterNames.put(token, true);

				tempToken = new PatternToken(type, token);
				tokens.add(tempToken);
				allTokens.add(tempToken);
				allLiteral = false;

				if (end < (partLength - 1)) {
					token = part.substring(end + 1, next);
					tempToken = new PatternToken(PatternTokenType.Literal, token);
					tokens.add(tempToken);
					allTokens.add(tempToken);
					end += token.length();
				}

				if (catchAlls > 1 || (catchAlls == 1 && tokens.size() > 1))
					throw new IllegalArgumentException("A path segment that contains more than one section, such as a literal section or a parameter, cannot contain a catch-all parameter.");

				from = end + 1;
			}

			segments[i] = new PatternSegment(allLiteral, tokens);
		}

		if (!allTokens.isEmpty())
			this.tokens = Iterables.toArray(allTokens, PatternToken.class);

		allTokens.clear();
	}

	RouteValueDictionary addDefaults(final RouteValueDictionary dict, final RouteValueDictionary defaults) {
		if (defaults != null && !defaults.isEmpty()) {
			for (Map.Entry<String, Object> entry : defaults.entrySet()) {
				final String key = entry.getKey();
				if (dict.containsKey(key))
					continue;
				dict.put(key, entry.getValue());
			}
		}

		return dict;
	}

	boolean matchSegment(int segIdex, int argsCount, String[] argSegs, List<PatternToken> tokens, RouteValueDictionary ret) {
		String pathSegment = argSegs[segIdex];
		int pathSegmentLength = pathSegment != null ? pathSegment.length() : -1;
		int startIndex = pathSegmentLength - 1;

		PatternTokenType tokenType;
		int tokensCount = tokens.size();
		PatternToken token;
		String tokenName;

		for (int tokenIndex = tokensCount - 1; tokenIndex > -1; tokenIndex--) {
			token = tokens.get(tokenIndex);
			if (startIndex < 0)
				return false;

			tokenType = token.getTokenType();
			tokenName = token.getName();

			if (segIdex > segmentCount - 1 || tokenType == PatternTokenType.CatchAll) {
				final StringBuilder builder = new StringBuilder();
				for (int j = segIdex; j < argsCount; j++) {
					if (j > segIdex)
						builder.append('/');
					builder.append(argSegs[j]);
				}

				ret.put(tokenName, builder.toString());
				break;
			}

			int scanIndex;
			if (tokenType == PatternTokenType.Literal) {
				int nameLen = tokenName.length();
				if (startIndex + 1 < nameLen)
					return false;

				scanIndex = startIndex - nameLen + 1;
				assert pathSegment != null;
				if (!pathSegment.substring(scanIndex, (scanIndex + nameLen)).equalsIgnoreCase(tokenName.substring(0, nameLen)))
					return false;
				startIndex = scanIndex - 1;
				continue;
			}

			// Standard Token
			int nextTokenIndex = tokenIndex - 1;
			if (nextTokenIndex < 0) {
				// First token
				assert pathSegment != null;
				ret.put(tokenName, pathSegment.substring(0, startIndex + 1));
				continue;
			}

			if (startIndex == 0)
				return false;

			PatternToken nextToken = tokens.get(nextTokenIndex);
			String nextTokenName = nextToken.getName();

			// Skip one char, since there can be no empty segments and if the
			// current token's value happens to be the same as preceding
			// literal text, we'll save some time and complexity
			scanIndex = startIndex - 1;
			assert pathSegment != null;
			int lastIndex = pathSegment.lastIndexOf(nextTokenName, scanIndex);
			if (lastIndex == -1)
				return false;

			lastIndex += nextTokenName.length() - 1;

			String sectionValue = pathSegment.substring(lastIndex + 1, startIndex + (startIndex - lastIndex));
			if (Strings.isNullOrEmpty(sectionValue))
				return false;

			ret.put(tokenName, sectionValue);
			startIndex = lastIndex;
		}

		return true;
	}

	public RouteValueDictionary match(final String path, final RouteValueDictionary defaults) {
		final RouteValueDictionary ret = new RouteValueDictionary();

		String url = this.url;
		String[] argSegs;
		int argsCount;

		if (Strings.isNullOrEmpty(path)) {
			argSegs = null;
			argsCount = 0;
		} else {
			if (url.compareTo(path) == 0 && url.indexOf(BEGIN_PLACEHOLDER) < 0)
				return addDefaults(ret, defaults);

			argSegs = path.split("/");
			argsCount = argSegs.length;

			if (Strings.isNullOrEmpty(argSegs[argsCount - 1]))
				argsCount--; // path ends with a trailing '/'
		}

		boolean haveDefaults = defaults != null && !defaults.isEmpty();

		if (argsCount == 1 && Strings.isNullOrEmpty(argSegs[0]))
			argsCount = 0;

		if (!haveDefaults && ((haveSegmentWithCatchAll && argsCount < segmentCount) || (!haveSegmentWithCatchAll && argsCount != segmentCount)))
			return null;

		int i = 0;

		for (PatternSegment segment : segments) {
			if (i >= argsCount)
				break;

			if (segment.isAllLiteral()) {
				assert argSegs != null;
				if (!argSegs[i].equalsIgnoreCase(segment.getTokens().get(0).getName()))
					return null;
				i++;
				continue;
			}

			if (!matchSegment(i, argsCount, argSegs, segment.getTokens(), ret))
				return null;

			i++;
		}

		// Check the remaining segments, if any, and see if they are required
		//
		// If a segment has more than one section (i.e. there's at least one
		// literal, then it cannot match defaults)
		//
		// All of the remaining segments must have all defaults provided and they
		// must not be literals or the match will fail.
		if (i < segmentCount) {
			if (!haveDefaults)
				return null;

			for (; i < segmentCount; i++) {
				final PatternSegment segment = segments[i];
				if (segment.isAllLiteral())
					return null;

				final List<PatternToken> tokens = segment.getTokens();
				if (tokens.size() != 1)
					return null;

				if (!defaults.containsKey(tokens.get(0).getName()))
					return null;
			}
		} else if (!haveSegmentWithCatchAll && argsCount > segmentCount) {
			return null;
		}

		return addDefaults(ret, defaults);
	}

	public String buildUrl(final Route route, final RequestContext requestContext, RouteValueDictionary userValues) {
		if (requestContext == null)
			return null;

		final RouteData routeData = requestContext.getRouteData();
		RouteValueDictionary defaultValues = route != null ? route.getDefaults() : null;
		RouteValueDictionary ambientValues = routeData.getValues();

		if (defaultValues != null && defaultValues.isEmpty())
			defaultValues = null;
		if (ambientValues != null && ambientValues.isEmpty())
			ambientValues = null;
		if (userValues != null && userValues.isEmpty())
			userValues = null;

		// Check Url parameters
		// It is allowed to take ambient values for required parameters if:
		//
		//  - there are no default values provided
		//  - the default values dictionary contains at least 1 required parameter value
		//
		boolean canTakeFromAmbient;
		if (defaultValues == null) {
			canTakeFromAmbient = true;
		} else {
			canTakeFromAmbient = false;
			for (Map.Entry<String, Boolean> entry : parameterNames.entrySet()) {
				if (defaultValues.containsKey(entry.getKey())) {
					canTakeFromAmbient = true;
					break;
				}
			}
		}

		boolean allMustBeInUserValues = false;
		for (Map.Entry<String, Boolean> entry : parameterNames.entrySet()) {
			final String key = entry.getKey();
			// Is the parameter required?
			if (defaultValues == null || !defaultValues.containsKey(key)) {
				// Yes, it is required (no value in defaults)
				// Has the user provided value for it?
				if (userValues == null || !userValues.containsKey(key)) {
					if (allMustBeInUserValues)
						return null; // partial override -> no match

					if (!canTakeFromAmbient || ambientValues == null || !ambientValues.containsKey(key))
						return null; // no value provided -> no match
				} else if (canTakeFromAmbient) {
					allMustBeInUserValues = true;
				}
			}
		}

		// Check for non-url parameters
		if (defaultValues != null) {
			for (Map.Entry<String, Object> entry : defaultValues.entrySet()) {
				final String key = entry.getKey();

				if (parameterNames.containsKey(key))
					continue;

				final OutParam<Object> outParam = OutParam.of(Object.class);
				// Has the user specified value for this parameter and, if
				// yes, is it the same as the one in defaults?
				if (userValues != null && userValues.tryGetValue(key, outParam)) {
					final Object parameterValue = outParam.get();
					Object defaultValue = entry.getValue();

					if ((defaultValue instanceof String) && (parameterValue instanceof String)) {
						final boolean defaultNullOrEmpty = Strings.isNullOrEmpty((String) defaultValue);
						final boolean paramNullOrEmpty = Strings.isNullOrEmpty((String) parameterValue);
						if (defaultNullOrEmpty != paramNullOrEmpty)
							return null; // different value -> no match
						if (!defaultNullOrEmpty && ((String) defaultValue).compareTo((String) parameterValue) != 0)
							return null; // different value -> no match
					} else if (!defaultValue.equals(parameterValue)) {
						return null; // different value -> no match
					}
				}
			}
		}

		// Check the constraints
		final RouteValueDictionary constraints = route != null ? route.getConstraints() : null;
		if (constraints != null && !constraints.isEmpty()) {
			final IHttpContext httpContext = requestContext.getHttpContext();
			final OutParam<Boolean> invalidConstraint = OutParam.of(false);

			for (Map.Entry<String, Object> entry : constraints.entrySet()) {
				if (!Route.processConstraintInternal(httpContext, route, entry.getValue(), entry.getKey(), userValues, RouteDirection.UrlGeneration, requestContext, invalidConstraint))
					return null; // constraint not met -> no match
			}
		}

		// It's a match, generate the Url
		final StringBuilder returnValue = new StringBuilder();
		boolean canTrim = true;

		// Going in reverse order, so that we can trim without much ado
		int tokensCount = tokens.length - 1;
		for (int i = tokensCount; i >= 0; i--) {
			final PatternToken token = tokens[i];
			if (token == null) {
				if (i < tokensCount && returnValue.length() > 0 && returnValue.charAt(0) != '/')
					returnValue.insert(0, '/');
				continue;
			}

			final String parameterName = token.getName();
			if (token.getTokenType() == PatternTokenType.Literal) {
				returnValue.insert(0, parameterName);
				continue;
			}

			Object tokenValue;

			if (userValues.containsKey(parameterName)) {
				tokenValue = userValues.get(parameterName);
				if (rvdHas(defaultValues, parameterName, tokenValue)) {
					canTrim = false;
					if (tokenValue != null)
						returnValue.insert(0, tokenValue.toString());
					continue;
				}

				if (!canTrim && tokenValue != null)
					returnValue.insert(0, tokenValue.toString());
				continue;
			}

			if (defaultValues.containsKey(parameterName)) {
				tokenValue = defaultValues.get(parameterName);
				if (ambientValues.containsKey(parameterName))
					tokenValue = ambientValues.get(parameterName);

				if (!canTrim && tokenValue != null)
					returnValue.insert(0, tokenValue.toString());
				continue;
			}

			canTrim = false;
			if (ambientValues.containsKey(parameterName)) {
				tokenValue = ambientValues.get(parameterName);
				if (tokenValue != null)
					returnValue.insert(0, tokenValue.toString());
			}
		}

		// All the values specified in userValues that aren't part of the original
		// Url, the constraints or defaults collections are treated as overflow
		// values - they are appended as query parameters to the Url
		if (userValues != null) {
			boolean first = true;
			final String characterEncoding = requestContext.getContentEncoding();
			for (Map.Entry<String, Object> entry : userValues.entrySet()) {
				final String parameterName = entry.getKey();
				if (parameterNames.containsKey(parameterName) || rvdHas(defaultValues, parameterName) || rvdHas(constraints, parameterName))
					continue;

				final Object parameterValue = entry.getValue();
				if (parameterValue == null)
					continue;

				if ((parameterValue instanceof String) && Strings.isNullOrEmpty((String) parameterValue))
					continue;

				if (first) {
					returnValue.append('?');
					first = false;
				} else {
					returnValue.append('&');
				}

				returnValue.append(urlEncode(parameterName, characterEncoding))
					.append('=');
				if (parameterValue != null)
					returnValue.append(urlEncode(parameterValue.toString(), characterEncoding));
			}
		}

		return returnValue.toString();
	}

	private static final String DEFAULT_CHARACTER_ENCODING = Charsets.UTF_8.name();

	private static String urlEncode(final String part, final String characterEncoding) {
		try {
			return URLEncoder.encode(part, characterEncoding);
		}
		catch (UnsupportedEncodingException e) {
			try {
				return URLEncoder.encode(part, DEFAULT_CHARACTER_ENCODING);
			}
			catch (UnsupportedEncodingException ignored) {
			}
		}
		return part;
	}

	public String getUrl() {
		return url;
	}
}
