package bp.env;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class BPEnvTransmission extends BPEnvCustom
{
	public final static String ENV_NAME_TRANSMISSION = "Transmission";
	public final static String ENVKEY_WORKDIR = "WORKDIR";

	public String getName()
	{
		return ENV_NAME_TRANSMISSION;
	}

	protected List<String> setupRawKeys()
	{
		return new CopyOnWriteArrayList<String>(new String[] { ENVKEY_WORKDIR });
	}
}