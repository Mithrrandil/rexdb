package org.rex.db.configuration.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.rex.db.util.ResourceUtil;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XMLEntityResolver implements EntityResolver {

	private static final Map<String, String> doctypeMap = new HashMap<String, String>();

	private static final String CONFIG_PUBLIC = "-//db.rex-soft.org//DTD Config 1.0//EN".toUpperCase(Locale.ENGLISH);
	private static final String CONFIG_SYSTEM = "http://db.rex-soft.org/dtd/rexdb-1-config.dtd".toUpperCase(Locale.ENGLISH);
	private static final String CONFIG_DTD = "org/rex/db/configuration/rexdb-1-config.dtd";

	static {
		doctypeMap.put(CONFIG_PUBLIC, CONFIG_DTD);
		doctypeMap.put(CONFIG_SYSTEM, CONFIG_DTD);
	}

	public InputSource resolveEntity(String publicId, String systemId) throws SAXException {

		if (publicId != null) {
			publicId = publicId.toUpperCase(Locale.ENGLISH);
		}
		if (systemId != null) {
			systemId = systemId.toUpperCase(Locale.ENGLISH);
		}

		InputSource source = null;
		try {
			String path = doctypeMap.get(publicId);
			source = getInputSource(path, source);
			if (source == null) {
				path = doctypeMap.get(systemId);
				source = getInputSource(path, source);
			}
		} catch (Exception e) {
			throw new SAXException(e.toString());
		}
		return source;
	}

	private InputSource getInputSource(String path, InputSource source) {
		if (path != null) {
			InputStream in;
			try {
				in = ResourceUtil.getResourceAsStream(path);
				source = new InputSource(in);
			} catch (IOException e) {
			}
		}
		return source;
	}

}