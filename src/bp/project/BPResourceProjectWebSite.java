package bp.project;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import bp.cache.BPCacheDataFileSystem;
import bp.cache.BPTreeCacheNode;
import bp.format.BPFormatWebSiteLink;
import bp.res.BPResource;
import bp.res.BPResourceDir;
import bp.res.BPResourceFile;
import bp.res.BPResourceFileLocal;
import bp.res.BPResourceWebSiteLink;
import bp.util.IOUtil;
import bp.util.JSONUtil;
import bp.util.Std;

public class BPResourceProjectWebSite extends BPResourceProjectFile
{
	protected volatile List<String> m_wslinkfilenames = new ArrayList<String>();

	public BPResourceProjectWebSite(BPResourceDir dir)
	{
		super(dir, false);
	}

	public String getResType()
	{
		return "website project";
	}

	public String getProjectTypeName()
	{
		return "website";
	}

	public BPProjectItemFactory[] getItemFactories()
	{
		return new BPProjectItemFactory[] { new BPProjectItemWebSiteLinkFactory() };
	}

	public static class BPProjectItemWebSiteLinkFactory implements BPProjectItemFactory
	{
		public String getName()
		{
			return BPFormatWebSiteLink.FORMAT_WEBSITELINK;
		}

		public void create(Map<String, Object> params, BPResourceProject project, BPResource par)
		{
			String name = (String) params.get("name");
			BPResourceDir p = (BPResourceDir) par;
			if (p == null)
				p = (BPResourceDir) project;
			BPResourceFile f = (BPResourceFile) p.createChild(name + ".bpwslink", true);
			BPResourceWebSiteLink link = new BPResourceWebSiteLink(f);
			link.setMappedData(params);
			writeWebSiteLink(f, link);
		}

		public String getItemClassName()
		{
			return BPResourceWebSiteLink.class.getName();
		}
	}

	public final static BPResourceWebSiteLink readWebSiteLink(BPResourceFile res)
	{
		BPResourceWebSiteLink rc = null;
		BPResourceWebSiteLink wslink = new BPResourceWebSiteLink(res);
		if (wslink.readLinkFile())
			rc = wslink;
		return rc;
	}

	public final static boolean writeWebSiteLink(BPResourceFile res, BPResourceWebSiteLink link)
	{
		return res.useOutputStream((out) ->
		{
			try
			{
				String str = JSONUtil.encode(link.getMappedData(), 6);
				IOUtil.write(out, str.getBytes("utf-8"));
				return true;
			}
			catch (Exception e)
			{
				Std.err(e);
			}
			return false;
		});
	}

	public BPResource wrapResource(BPResource res)
	{
		BPResource rc = null;
		if (res.isFileSystem())
		{
			if (".bpwslink".equals(res.getExt()))
			{
				rc = readWebSiteLink((BPResourceFile) res);
			}
			else if (".bpprj".equalsIgnoreCase(res.getName()))
			{
				return null;
			}
		}
		if (rc == null)
		{
			rc = super.wrapResource(res);
		}
		return rc;
	}

	public void save(BPResource res)
	{
		if (res instanceof BPResourceWebSiteLink)
		{
			BPResourceWebSiteLink link = (BPResourceWebSiteLink) res;
			BPResourceFile raw = (BPResourceFile) link.getRawResource();
			writeWebSiteLink(raw, link);
		}
	}

	public void refreshByCache(BPTreeCacheNode<BPCacheDataFileSystem> root)
	{
		List<BPTreeCacheNode<?>> nodes = new ArrayList<BPTreeCacheNode<?>>();
		root.filter((node) -> ((BPCacheDataFileSystem) node.getValue()).getName().toLowerCase().endsWith(".bpwslink"), nodes);
		List<String> filenames = new ArrayList<String>();
		for (BPTreeCacheNode<?> node : nodes)
		{
			filenames.add(((BPCacheDataFileSystem) node.getValue()).getFullName());
		}
		m_wslinkfilenames = filenames;
	}

	public List<BPResourceWebSiteLink> listWebSiteLink()
	{
		List<BPResourceWebSiteLink> rc = new ArrayList<BPResourceWebSiteLink>();
		List<String> filenames = m_wslinkfilenames;
		for (String filename : filenames)
		{
			BPResourceFileLocal f = new BPResourceFileLocal(filename);
			if (f.exists() && f.isFile())
			{
				BPResourceWebSiteLink link = readWebSiteLink(f);
				rc.add(link);
			}
		}
		return rc;
	}
}
