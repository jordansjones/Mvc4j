package nextmethod.io;

import com.google.common.base.Joiner;
import nextmethod.base.SystemHelpers;

import javax.annotation.Nonnull;
import java.nio.file.FileSystems;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public final class Filesystem {

	private Filesystem() {}

	public static String getFileName(@Nonnull final String fileName) {
		checkNotNull(fileName);
		return FileSystems.getDefault().getPath(fileName).getFileName().toString();
	}

	private static final Joiner pathJoiner = Joiner.on(SystemHelpers.pathSeparator());

	public static String createFilePath(@Nonnull final String... parts) {
		checkArgument(parts != null && parts.length > 0);
		return pathJoiner.join(parts);
	}
}
