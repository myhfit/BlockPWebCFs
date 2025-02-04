package bp.ext;

public class BPExtensionLoaderWebCFs implements BPExtensionLoader
{
	public String getName()
	{
		return "WebCommonFormats";
	}

	public boolean isUI()
	{
		return false;
	}

	public String getUIType()
	{
		return null;
	}

	public String[] getParentExts()
	{
		return new String[] { "CommonFormats" };
	}

	public String[] getDependencies()
	{
		return null;
	}

	public void preload()
	{
		String handlers = System.getProperty("java.protocol.handler.pkgs");
		if (handlers == null)
		{
			handlers = "";
		}
		else if (handlers.length() > 0)
		{
			handlers += "|";
		}
		handlers += "bp.streamhandler";
		System.setProperty("java.protocol.handler.pkgs", handlers);
	}
}
