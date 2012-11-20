package nextmethod.io;

import javax.annotation.Nonnull;
import java.nio.file.FileSystems;

import static com.google.common.base.Preconditions.checkNotNull;

public final class Filesystem {

	private Filesystem() {}

	public static String getFileName(@Nonnull final String fileName) {
		checkNotNull(fileName);
		return FileSystems.getDefault().getPath(fileName).getFileName().toString();
	}
}
