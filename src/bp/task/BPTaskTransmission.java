package bp.task;

public interface BPTaskTransmission<V> extends BPTask<V>
{
	public final static String CATEGORY_TRANSMISSION = "transmission";

	default String getCategory()
	{
		return CATEGORY_TRANSMISSION;
	}
	
	public static interface BPTaskFactoryTransmission extends BPTaskFactory
	{
		default String getCategory()
		{
			return CATEGORY_TRANSMISSION;
		}
	}
}
