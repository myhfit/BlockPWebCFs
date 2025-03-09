package bp.util;

public enum MIMEs
{
	TEXT("text/plain;charset=utf-8"), HTML_TEXT("text/html"), XML_TEXT("text/xml"), JSON_APPLICATION("application/json"), JSON_FILE("text/json"), BMP_IMAGE("image/bmp"), PNG_IMAGE("image/png"), JPEG_IMAGE("image/jpeg")

	;

	private String ext;

	MIMEs(String e)
	{
		this.ext = e;
	}

	public String getExt()
	{
		return ext;
	}
}
