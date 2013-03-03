package nextmethod.web.razor.text;

public interface ITextBuffer {

	int getLength();
	int getPosition();
	void setPosition(int position);
	int read();
	int peek();

}
