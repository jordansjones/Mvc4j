package nextmethod.web.mvc;

import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import com.google.common.io.Closeables;
import nextmethod.Tuple;
import nextmethod.reflection.AssemblyInfo;
import nextmethod.reflection.ClassInfo;
import org.apache.commons.lang3.ClassUtils;
import org.objectweb.asm.ClassReader;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import javax.annotation.Nullable;
import javax.servlet.ServletContext;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

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
			if (s.endsWith(ClassPathType.Class.suffix())) {
				loadClassInfo(ctx, s);
			} else if (s.endsWith(ClassPathType.Jar.suffix())) {
				loadAssemblyInfo(ctx, s);
			} else {
				final Set<String> childPaths = Sets.newHashSet(ctx.getResourcePaths(s));
				if (childPaths != null && !childPaths.isEmpty()) {
					// TODO: Register Directory Path with VPU
					walkContextPaths(ctx, childPaths);
				} else if (s.endsWith("/")) {
					directories.add(s);
				} else {
					resources.add(s);
				}
			}
		}
	}

	private void loadClassInfo(final ServletContext ctx, final String path) {
		if (isIgnoredPackage.apply(path))
			return;
//		final ClassInfo<?> classInfo = processJavaResource(ctx, path, new ClassAnalyzer());
		final String classInfo = processJavaResource(ctx, path, createClassProcessor());
//		if (classInfo != null) {
//			classes.add(classInfo);
//		}
	}

	private void loadAssemblyInfo(final ServletContext ctx, final String path) {
		final AssemblyInfo assemblyInfo = processJavaResource(ctx, path, createJarProcessor());
		if (assemblyInfo != null) {
			assemblies.add(assemblyInfo);
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

	private Function<Tuple<String, InputStream>, String> createClassProcessor() {
		return new Function<Tuple<String, InputStream>, String>() {
			@Override
			public String apply(@Nullable final Tuple<String, InputStream> input) {
				if (input == null)
					return null;

				final MetaClassInfoBuilder classInfoBuilder = new MetaClassInfoBuilder();
				try {
//					final ClassParser classParser = new ClassParser(input.getItem2(), input.getItem1());
//					final JavaClass parse = classParser.parse();
//					parse.accept(metaClassInfo);
					final String item1 = input.getItem1();
					final String packageName = ClassUtils.getPackageName(item1);
					final StringWriter stringWriter = new StringWriter();
					final ClassReader reader = new ClassReader(input.getItem2());
					reader.accept(classInfoBuilder, 0);
//					reader.accept(new ASMifierClassVisitor(new PrintWriter(System.out)), 0);
					int x = 1;
				}
				catch (IOException e) {
					e.printStackTrace();
				}
				return null;
			}
		};
	}

	private Function<Tuple<String, InputStream>, AssemblyInfo> createJarProcessor() {
		return new Function<Tuple<String, InputStream>, AssemblyInfo>() {
			public AssemblyInfo apply(@Nullable final Tuple<String, InputStream> input) {
				if (input == null)
					return null;

				final AssemblyInfo assemblyInfo = new AssemblyInfo(input.getItem1());
				try {
					final JarInputStream jarStream = new JarInputStream(input.getItem2());
					JarEntry jarEntry;
					while ((jarEntry = jarStream.getNextJarEntry()) != null) {
						if (jarEntry.isDirectory())
							continue;

						final String name = jarEntry.getName();
						if (Strings.isNullOrEmpty(name) || !name.toLowerCase().endsWith("class"))
							continue;
						if (isIgnoredPackage.apply(name))
							continue;

						final String s = readJarEntry(name, jarStream);
						int x = 1;
//						final ClassInfo<?> classInfo = readJarEntry(name, jarStream);
//						if (classInfo != null) {
//							assemblyInfo.getEntries().add(classInfo);
//						}
					}
				}
				catch (IOException e) {
					e.printStackTrace();
				}

				return assemblyInfo;
			}
		};
	}

	private String readJarEntry(final String name, final JarInputStream jis) {
		ByteArrayOutputStream bos = null;
		ByteArrayInputStream bis = null;
		try {
			final int buffer = 2048;
			byte[] data = new byte[buffer];
			bos = new ByteArrayOutputStream();
			int count;
			while ((count = jis.read(data, 0, buffer)) != -1) {
				bos.write(data, 0, count);
			}
			bos.flush();
			bis = new ByteArrayInputStream(bos.toByteArray());
			final Function<Tuple<String, InputStream>, String> classProcessor = createClassProcessor();
			return classProcessor.apply(Tuple.<String, InputStream>of(name, bis));
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			Closeables.closeQuietly(bos);
		}
		return null;
	}

}
