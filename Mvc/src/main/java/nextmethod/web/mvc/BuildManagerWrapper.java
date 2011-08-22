package nextmethod.web.mvc;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import javax.inject.Inject;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 *
 */
class BuildManagerWrapper implements IBuildManager {

	@Inject
	private VirtualPathUtility vpUtil;

	@Override
	public Object createInstanceFromVirtualPath(final String virtualPath, final Class<?> requiredBaseType) {
		return null;
	}

	@Override
	public ImmutableCollection<Assembly> getReferencedAssemblies() {
		final ImmutableMultimap<ClassPathType, String> classPath = vpUtil.getClassPath();

		final ImmutableSet.Builder<Assembly> builder = ImmutableSet.builder();
		builder.add(createAssemblyFromRawPath(classPath.get(ClassPathType.Path)));
		builder.addAll(createAssemblyFromJars(classPath.get(ClassPathType.Jar)));

		return builder.build();
	}

	@Override
	public void readCachedFile(final String fileName) {
	}

	@Override
	public void createCachedFile(final String fileName) {
	}

	private void addAssemblyType(final Assembly assembly, final String type) {
		try {
			final Class<?> aClass = Class.forName(type);
			assembly.getEntries().add(AssemblyType.of(aClass));
		}
		catch (ClassNotFoundException e) {
			// Do Nothing
		}
	}

	private Assembly createAssemblyFromRawPath(final ImmutableCollection<String> paths) {
		final Assembly assembly = new Assembly(Assembly.Ungrouped);
		for (String path : paths) {
			final String typeName = VirtualPathUtility.normalizeClassEntry(path);
			addAssemblyType(assembly, typeName);
		}
		return assembly;
	}

	private Set<Assembly> createAssemblyFromJars(final ImmutableCollection<String> jars) {
		final Set<Assembly> assemblies = Sets.newHashSet();
		final FilenameFilter filter = vpUtil.createFileNameFilter(ClassPathType.Path.suffix());
		for (String jar : jars) {
			try {
				final JarFile jarFile = new JarFile(jar);
				final String name = jarFile.getName();
				final Assembly assembly = new Assembly(name);
				final Enumeration<JarEntry> entries = jarFile.entries();
				while (entries.hasMoreElements()) {
					JarEntry jarEntry = entries.nextElement();
					if (jarEntry.isDirectory())
						continue;

					final String entryName = jarEntry.getName();
					if (filter.accept(null, entryName)) {
						final String type = VirtualPathUtility.normalizeClassEntry(entryName);
						addAssemblyType(assembly, type);
					}
				}
			}
			catch (IOException ignored) {
			}
		}
		return assemblies;
	}
}
