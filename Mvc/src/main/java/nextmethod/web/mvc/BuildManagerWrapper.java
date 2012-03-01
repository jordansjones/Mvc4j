package nextmethod.web.mvc;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import nextmethod.annotations.TODO;
import nextmethod.reflection.AssemblyInfo;

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
@TODO
class BuildManagerWrapper implements IBuildManager {

	@Inject
	private VirtualPathUtility vpUtil;

	@Inject
	private BuildManager buildManager;

	@Override
	public Object createInstanceFromVirtualPath(final String virtualPath, final Class<?> requiredBaseType) {
		return buildManager.createInstanceFromVirtualPath(virtualPath, requiredBaseType);
	}

	@Override
	public ImmutableCollection<AssemblyInfo> getReferencedAssemblies() {
		final ImmutableMultimap<ClassPathType, String> classPath = vpUtil.getClassPath();

		final ImmutableSet.Builder<AssemblyInfo> builder = ImmutableSet.builder();
		builder.add(createAssemblyFromRawPath(classPath.get(ClassPathType.Class)));
		builder.addAll(createAssemblyFromJars(classPath.get(ClassPathType.Jar)));
		builder.addAll(buildManager.getReferencedAssemblies());

		return builder.build();
//		return buildManager.getReferencedAssemblies();
	}

	@Override
	public void readCachedFile(final String fileName) {
		buildManager.readCachedFile(fileName);
	}

	@Override
	public void createCachedFile(final String fileName) {
		buildManager.createCachedFile(fileName);
	}

	private void addAssemblyType(final Assembly assembly, final String type) {
		try {
			final Class<?> aClass = Class.forName(type);
			assembly.getEntries().add(AssemblyType.of(aClass));
		}
		catch (NoClassDefFoundError | ClassNotFoundException ignored) {
		}
	}

	private AssemblyInfo createAssemblyFromRawPath(final ImmutableCollection<String> paths) {
		final Assembly assembly = new Assembly(MagicStrings.UngroupedAssemblyName);
		for (String path : paths) {
			final String typeName = VirtualPathUtility.normalizeClassEntry(path);
			addAssemblyType(assembly, typeName);
		}
		return assembly.asAssemblyInfo();
	}

	private Set<AssemblyInfo> createAssemblyFromJars(final ImmutableCollection<String> jars) {
		final Set<AssemblyInfo> assemblies = Sets.newHashSet();
		final FilenameFilter filter = vpUtil.createFileNameFilter(ClassPathType.Class.suffix());
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
				assemblies.add(assembly.asAssemblyInfo());
			}
			catch (IOException ignored) {
			}
		}
		return assemblies;
	}
}
