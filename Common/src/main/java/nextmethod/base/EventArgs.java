package nextmethod.base;

import java.io.Serializable;

public class EventArgs implements Serializable {

	private static final long serialVersionUID = 6499664006208050091L;

	public static final EventArgs Empty = new EventArgs();

	public EventArgs() {
	}
}
