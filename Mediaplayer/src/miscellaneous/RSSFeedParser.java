package miscellaneous;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;
import android.util.Xml;

public class RSSFeedParser {

	// We don't use namespaces
	private static final String ns = null;

	public List<Item> parse(InputStream in) throws XmlPullParserException,
			IOException {
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in, null);
			parser.nextTag();
			return readFeed(parser);
		} finally {
			in.close();
		}
	}

	private List<Item> readFeed(XmlPullParser parser)
			throws XmlPullParserException, IOException {
		List<Item> entries = new ArrayList<Item>();

		parser.require(XmlPullParser.START_TAG, ns, "rss");
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name.equals("channel") || name.equals("atom-link"))
				continue;

			// Starts by looking for the item tag
			if (name.equals("item")) {
				entries.add(readItem(parser));
			} else {
				skip(parser);
			}
		}
		return entries;
	}

	public static class Item {
		public final String title;
		public final String link;
		public final String description;
		public final String content;

		private Item(String title, String description, String link,
				String content) {
			this.title = title;
			this.description = description;
			this.link = link;
			this.content = content;
		}
	}

	// Parses the contents of an item. If it encounters a title, summary, or
	// link tag, hands them off
	// to their respective "read" methods for processing. Otherwise, skips the
	// tag.
	private Item readItem(XmlPullParser parser) throws XmlPullParserException,
			IOException {
		parser.require(XmlPullParser.START_TAG, ns, "item");
		String title = null;
		String description = null;
		String link = null;
		String content = null;
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name.equals("title")) {
				title = readTitle(parser);
			} else if (name.equals("description")) {
				description = readDescription(parser);
			} else if (name.equals("link")) {
				link = readLink(parser);
			} else if (name.equals("content:encoded")) {
				content = readContent(parser);
			} else {
				skip(parser);
			}
		}
		return new Item(title, description, link, content);
	}

	// Processes title tags in the feed.
	private String readTitle(XmlPullParser parser) throws IOException,
			XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, ns, "title");
		String title = readText(parser);
		parser.require(XmlPullParser.END_TAG, ns, "title");
		return title;
	}

	// Processes link tags in the feed.
	private String readLink(XmlPullParser parser) throws IOException,
			XmlPullParserException {
		String link = "";
		parser.require(XmlPullParser.START_TAG, ns, "link");
		link = readText(parser);
		// String relType = parser.getAttributeValue(null, "rel");
		// if (tag.equals("link")) {
		// if (relType.equals("alternate")) {
		// link = parser.getAttributeValue(null, "href");
		// parser.nextTag();
		// }
		// }
		parser.require(XmlPullParser.END_TAG, ns, "link");
		return link;
	}

	// Processes summary tags in the feed.
	private String readDescription(XmlPullParser parser) throws IOException,
			XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, ns, "description");
		String summary = readText(parser);
		parser.require(XmlPullParser.END_TAG, ns, "description");
		return summary;
	}

	private String readContent(XmlPullParser parser) throws IOException,
			XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, ns, "content:encoded");
		String content = "";
		int token = parser.nextToken();
		while (token != XmlPullParser.CDSECT) {
			token = parser.nextToken();
		}
		while(token==XmlPullParser.CDSECT){
						content =content + parser.getText();
			token = parser.nextToken();

		}
		 parser.require(XmlPullParser.END_TAG, ns, "content:encoded");
		return content;
	}

	// For the tags title and summary, extracts their text values.
	private String readText(XmlPullParser parser) throws IOException,
			XmlPullParserException {
		String result = "";
		if (parser.next() == XmlPullParser.TEXT) {
			result = parser.getText();
			parser.nextTag();
		}
		return result;
	}

	private void skip(XmlPullParser parser) throws XmlPullParserException,
			IOException {
		if (parser.getEventType() != XmlPullParser.START_TAG) {
			throw new IllegalStateException();
		}
		int depth = 1;
		while (depth != 0) {
			switch (parser.next()) {
			case XmlPullParser.END_TAG:
				depth--;
				break;
			case XmlPullParser.START_TAG:
				depth++;
				break;
			}
		}
	}

}
