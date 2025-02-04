package bp.format;

import java.util.function.Consumer;

public class BPFormatFactoryWebCFs implements BPFormatFactory
{
	public void register(Consumer<BPFormat> regfunc)
	{
		regfunc.accept(new BPFormatHTML());
		regfunc.accept(new BPFormatWebSiteLink());
		regfunc.accept(new BPFormatWebSiteOperation());
		regfunc.accept(new BPFormatWebSiteConsole());
	}
}
