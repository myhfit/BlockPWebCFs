package bp.project;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import bp.res.BPResourceDir;

public class BPProjectFactoryWebSite implements BPProjectFactory
{
	public BPResourceProject create(String prjtype, BPResourceDir dir, Map<String, String> prjdata)
	{
		BPResourceProjectWebSite project = new BPResourceProjectWebSite(dir);
		if (prjdata.containsKey("name"))
			project.setName(prjdata.get("name"));
		if (prjdata.containsKey("path"))
			project.setPath(prjdata.get("path"));
		return project;
	}

	public Class<? extends BPResourceProject> getProjectClass()
	{
		return BPResourceProjectWebSite.class;
	}

	public List<String> getProjectTypes()
	{
		List<String> rc = new ArrayList<String>();
		rc.add("website");
		return rc;
	}

	public boolean canHandle(String prjtype)
	{
		return prjtype.equalsIgnoreCase("website");
	}

	public String getName()
	{
		return "WebSite Project";
	}
}
