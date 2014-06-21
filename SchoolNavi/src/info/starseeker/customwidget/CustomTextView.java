package info.starseeker.customwidget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class CustomTextView extends TextView
{
	private int maxLines;

	public CustomTextView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	@Override
	public void setMaxLines(int line)
	{
		super.setMaxLines(line);
		this.maxLines = line;
	}

	public int getMaxLine()
	{
		return this.maxLines;
	}
}