package nextmethod.web.mvc;

import com.google.common.base.Function;
import com.google.common.collect.Sets;
import nextmethod.Tuple;
import nextmethod.reflection.AssemblyInfo;
import nextmethod.reflection.ClassInfo;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 */
class ServletContextAnalyzer {

	private static final IgnoredPackagesPredicate isIgnoredPackage = new IgnoredPackagesPredicate();
	static final ServletContextAnalyzer Instance = new ServletContextAnalyzer();

	private final Set<String> directories;
	private final Set<String> resources;
	private final Set<ClassInfo<?>> classes;
	private final Set<AssemblyInfo> assemblies;

	ServletContextAnalyzer() {
		directories = Sets.newHashSet();
		resources = Sets.newHashSet();
		classes = Sets.newHashSet();
		assemblies = Sets.newHashSet();
	}

	static void analyze(final ServletContext ctx) {
		final Reflections reflections = new Reflections(new ConfigurationBuilder()
			.setScanners(
//				new SubTypesScanner(),
//				new TypeAnnotationsScanner(),
//				new TypesScanner()
				new ResourcesScanner()
			)
//			.addUrls(ClasspathHelper.forWebInfLib(ctx))
			.addUrls(ClasspathHelper.forClassLoader())
//			.filterInputsBy(new IgnoredPackagesPredicate())
			.useParallelExecutor(Runtime.getRuntime().availableProcessors())
		);
//		final Store store = reflections.getStore();
		Instance.walkContextPaths(ctx, ctx.getResourcePaths("/"));
	}

	// TODO: Parallelize this
	private void walkContextPaths(final ServletContext ctx, final Set<String> paths) {
		checkNotNull(ctx);
		for (String s : paths) {
//			if (s.endsWith(ClassPathType.Class.suffix())) {
//				loadClassInfo(ctx, s);
//			} else if (s.endsWith(ClassPathType.Jar.suffix())) {
//				loadAssemblyInfo(ctx, s);
//			} else {
//				final Set<String> childPaths = Sets.newHashSet(ctx.getResourcePaths(s));
//				if (childPaths != null && !childPaths.isEmpty()) {
//					// TODO: Register Directory Path with VPU
//					walkContextPaths(ctx, childPaths);
//				} else if (s.endsWith("/")) {
//					directories.add(s);
//				} else {
//					resources.add(s);
//				}
//			}
		}
	}

	private <T> T processJavaResource(final ServletContext ctx, final String resourcePath, final Function<Tuple<String, InputStream>, T> callback) {
		try (InputStream is = ctx.getResourceAsStream(resourcePath)) {
			final Path path = Paths.get(resourcePath);
			return checkNotNull(callback).apply(Tuple.of(path.getFileName().toString(), is));
		}
		catch (IOException e) {
			// TODO: Log this?
			e.printStackTrace();
		}
		return null;
	}

}
