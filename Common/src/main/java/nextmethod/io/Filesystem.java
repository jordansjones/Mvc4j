package nextmethod.io;

import javax.annotation.Nonnull;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import static com.google.common.base.Preconditions.checkNotNull;

public final class Filesystem {

	private Filesystem() {}

	public static String getFileName(@Nonnull final String fileName) {
		checkNotNull(fileName);
		final Path path = FileSystems.getDefault().getPath(fileName);
		final Path fName = path.getFileName();
		return fName.toString();
	}
}
